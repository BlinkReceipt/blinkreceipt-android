package com.actualplatform.android.activation.development.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.actualplatform.activation.Activation
import com.actualplatform.activation.ActivationClient
import com.actualplatform.activation.OffersWall
import com.actualplatform.activation.RewardCurrency
import com.actualplatform.activation.RewardCurrencyCodePosition
import com.actualplatform.activation.RewardCurrencyImageLocation
import com.actualplatform.activation.RewardCurrencyLabelStyle
import com.actualplatform.activation.RewardCurrencyMessagingTextStyle
import com.actualplatform.activation.RewardCurrencyRounding
import com.actualplatform.activation.RewardPoint
import com.actualplatform.activation.Rewards
import com.actualplatform.activation.ScanReward
import com.actualplatform.activation.models.PlacementLayout
import com.actualplatform.activation.networking.HttpEnvironment
import com.actualplatform.activation.networking.TestOptions
import com.actualplatform.android.activation.development.R
import com.actualplatform.android.activation.development.ui.themes.ThemesAndIconEditorScreen
import com.actualplatform.android.activation.development.ui.themes.local.ThemesAndIconsStorage
import com.microblink.ScanOptions
import com.microblink.camera.ui.CameraCharacteristics
import com.microblink.camera.ui.CameraRecognizerContract
import com.microblink.camera.ui.CameraRecognizerOptions
import com.microblink.camera.ui.CameraRecognizerResults
import com.microblink.logcat.LogcatManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.ByteString.Companion.encodeUtf8
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// File-scope delegate so the DataStore is a process-wide singleton keyed by the .preferences_pb
// file. Declaring this inside the Activity creates a fresh DataStore per instance, and DataStore
// throws "multiple DataStores active for the same file" if a prior instance's scope hasn't been
// cancelled yet (e.g. across configuration changes or process restarts).
private val Context.themeDatastore by
    preferencesDataStore(name = ActivationActivity.PREFS_THEME_NAME)

internal class ActivationActivity : ComponentActivity() {

    private val scanOptions: ScanOptions by lazy {
        // This is the Activation demo, so the defensive fallback enables activation. Since MON-1124
        // moved the flag into ScanOptions, a missing OPTIONS_KEY would otherwise default activation()
        // to false and silently skip the post-scan activation flow.
        intent.parcelable(OPTIONS_KEY) ?: ScanOptions.newBuilder().activation(true).build()
    }

    private val cameraCharacteristics: CameraCharacteristics by lazy {
        intent.parcelable(CAMERA_CHARACTERISTICS) ?: CameraCharacteristics.Builder().build()
    }

    private val rewardsPref by lazy {
        getSharedPreferences(
            REWARDS_PREF_NAME,
            Context.MODE_PRIVATE
        )
    }

    private val rewardEventsFlow: Flow<List<Rewards>> = callbackFlow {
        val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { p0, p1 ->
            p0 ?: run {
                LogcatManager.event().debug { "prefChangeListener:: p0 is null " }
                trySend(emptyList())
                return@OnSharedPreferenceChangeListener
            }
            if (p0.all.isEmpty()) {
                trySend(emptyList())
                return@OnSharedPreferenceChangeListener
            }
            p1 ?: return@OnSharedPreferenceChangeListener

            trySend(
                p0.all.mapNotNull { entry ->
                    return@mapNotNull p0.getString(entry.key, null)?.let { strValue ->
                        runCatching {
                            Json.decodeFromString(RewardsData.serializer(), strValue)
                        }.onFailure {
                            LogcatManager.event().exception { it }
                        }.getOrNull()
                    }
                }.sortedBy { it.dateCollectedMs }
                    .map { it.toModel() }
            )
        }

        rewardsPref.registerOnSharedPreferenceChangeListener(prefChangeListener)
        awaitClose {
            rewardsPref.unregisterOnSharedPreferenceChangeListener(prefChangeListener)
        }
    }

    private val totalRewardsEarnedFlow: Flow<Double> =
        rewardEventsFlow.map { it.sumOf { reward -> reward.amount.value } }

    private val prefs by lazy {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val themeStorage by lazy {
        ThemesAndIconsStorage(themeDatastore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Listen to DataStore-backed theme updates. Each emission propagates to the live SDK by
        // assigning `Activation.theme` to SDK defaults.
        // The flow fires on
        // app start (first hydration), after each `onApply` (writes new JSON), and after `onReset`
        // (clear → DataStore re-emits an empty Preferences → flow falls back to the baseline).
        lifecycleScope.launch {
            themeStorage.themes()
                .distinctUntilChanged()
                .flowWithLifecycle(lifecycle)
                .collect { Activation.theme = it }
        }

        setContent {
            val rewardEvents by rewardEventsFlow.collectAsState(emptyList())
            val totalRewardsEarned by totalRewardsEarnedFlow.collectAsState(0.0)

            MaterialTheme {
                val backStack = rememberNavBackStack(ActivationRoute.Home)
                var refreshKey by remember { mutableIntStateOf(0) }

                var lastScanResults by remember {
                    mutableStateOf<com.microblink.core.ScanResults?>(
                        null
                    )
                }
                var lastScanError by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    ActivationClient.instance.rewards.collect { reward ->
                        LogcatManager.event()
                            .debug(TAG) { "Reward received: type=${reward::class.simpleName}, brid=${reward.blinkReceiptId}, amount=${reward.amount}, totalBefore=$totalRewardsEarned, totalAfter=${totalRewardsEarned + reward.amount.value}, eventCount=${rewardEvents.size + 1}" }
                        rewardsPref.edit(true) {
                            putString(reward.blinkReceiptId, Json.encodeToString(reward.toData()))
                        }
                    }
                }

                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                    ),
                    transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                    popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                    entryProvider = entryProvider {
                        entry<ActivationRoute.OffersWall> { _ ->
                            val launcher = rememberLauncherForActivityResult(
                                contract = CameraRecognizerContract(),
                            ) { result ->
                                LogcatManager.event()
                                    .debug(TAG) { "OffersWall::launcher callback -> $result" }
                                when (result) {
                                    is CameraRecognizerResults.Success -> {
                                        LogcatManager.event()
                                            .debug(TAG) { "OffersWall::onScanResult -> Success, blinkReceiptId=${result.results?.blinkReceiptId()}" }
                                        lastScanResults = result.results
                                        lastScanError = null
                                    }

                                    is CameraRecognizerResults.Exception -> {
                                        LogcatManager.event()
                                            .debug(TAG) { "OffersWall::onScanResult -> Exception: ${result.exception}" }
                                        lastScanError = result.exception.message ?: "Unknown error"
                                        lastScanResults = null
                                    }

                                    CameraRecognizerResults.Cancelled -> {
                                        LogcatManager.event()
                                            .debug(TAG) { "OffersWall::onScanResult -> Cancelled" }
                                    }
                                }
                            }

                            val showHeader = prefs.getBoolean(PREF_SHOW_HEADER, true)

                            OffersWall(
                                onScanReceipt = {
                                    LogcatManager.event()
                                        .debug(TAG) { "OffersWall::onScanReceipt -> launching camera" }
                                    lastScanResults = null
                                    lastScanError = null
                                    launcher.launch(
                                        CameraRecognizerOptions.Builder()
                                            .options(scanOptions)
                                            .characteristics(cameraCharacteristics)
                                            .build()
                                    )
                                },
                                onDismiss = if (showHeader) {
                                    {
                                        LogcatManager.event().debug(TAG) { "OffersWall::onDismiss" }
                                        backStack.removeLastOrNull()
                                    }
                                } else null,
                            )
                        }

                        entry<ActivationRoute.SettingsEditor> { _ ->
                            SettingsEditorScreen(
                                prefs = prefs,
                                onBack = {
                                    refreshKey++
                                    backStack.removeLastOrNull()
                                },
                            )
                        }

                        entry<ActivationRoute.ThemesAndAppearanceEditor> { _ ->
                            ThemesAndIconEditorScreen(
                                onApply = { updatedTheme ->
                                    // Persistence first — the storage flow above re-emits the new
                                    // theme and assigns the live SDK globals
                                    // through the single source of truth.
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        themeStorage.save(updatedTheme)
                                    }

                                    Toast.makeText(
                                        applicationContext,
                                        R.string.activations_toast_themes_applied,
                                        Toast.LENGTH_SHORT,
                                    ).show()

                                    backStack.removeLastOrNull()
                                },
                                onReset = {
                                    // Clear DataStore and restore SDK defaults immediately. The
                                    // storage flow will also re-emit the baseline theme when the
                                    // backing preferences change.
                                    lifecycleScope.launch { themeStorage.clear() }
                                },
                                onBack = { backStack.removeLastOrNull() },
                            )
                        }

                        entry<ActivationRoute.Home> { _ ->
                            val settings = remember(refreshKey) { SettingsData.from(prefs) }

                            val scanLauncher = rememberLauncherForActivityResult(
                                contract = CameraRecognizerContract(),
                            ) { result ->
                                LogcatManager.event()
                                    .debug(TAG) { "ScanReceipt::launcher callback -> $result" }
                                when (result) {
                                    is CameraRecognizerResults.Success -> {
                                        LogcatManager.event()
                                            .debug(TAG) { "ScanReceipt::onScanResult -> Success, blinkReceiptId=${result.results?.blinkReceiptId()}" }
                                        lastScanResults = result.results
                                        lastScanError = null
                                    }

                                    is CameraRecognizerResults.Exception -> {
                                        LogcatManager.event()
                                            .debug(TAG) { "ScanReceipt::onScanResult -> Exception: ${result.exception}" }
                                        lastScanError = result.exception.message ?: "Unknown error"
                                        lastScanResults = null

                                        backStack.add(ActivationRoute.OffersWall)
                                    }

                                    CameraRecognizerResults.Cancelled -> {
                                        LogcatManager.event()
                                            .debug(TAG) { "ScanReceipt::onScanResult -> Cancelled" }
                                    }
                                }
                            }

                            HomeScreen(
                                settings = settings,
                                lastScanResults = lastScanResults,
                                lastScanError = lastScanError,
                                totalRewardsEarned = totalRewardsEarned,
                                rewardEvents = rewardEvents,
                                onOffersWallClick = {
                                    lastScanResults = null
                                    lastScanError = null

                                    if (settings.email.isEmpty() && settings.phone.isEmpty()) {
                                        backStack.add(ActivationRoute.SettingsEditor)
                                    } else {
                                        backStack.add(ActivationRoute.OffersWall)
                                    }
                                },
                                onScanReceiptClick = {
                                    if (settings.email.isEmpty() && settings.phone.isEmpty()) {
                                        backStack.add(ActivationRoute.SettingsEditor)
                                    } else {
                                        LogcatManager.event()
                                            .debug(TAG) { "ScanReceipt -> launching camera with activation=true" }
                                        lastScanResults = null
                                        lastScanError = null

                                        scanLauncher.launch(
                                            CameraRecognizerOptions.Builder()
                                                .options(scanOptions)
                                                .characteristics(cameraCharacteristics)
                                                .build()
                                        )
                                    }
                                },
                                onSettingsClick = { backStack.add(ActivationRoute.SettingsEditor) },
                                onThemesClick = { backStack.add(ActivationRoute.ThemesAndAppearanceEditor) },
                                onClearRewards = {
                                    LogcatManager.event().debug { "onClearRewards()" }
                                    rewardsPref.edit(true) { clear() }
                                },
                                onBack = { finish() },
                            )
                        }
                    },
                )
            }
        }
    }

    companion object {
        internal const val OPTIONS_KEY: String = "SCAN_OPTIONS_KEY"
        internal const val CAMERA_CHARACTERISTICS: String = "CAMERA_CHARACTERISTICS"
        internal const val PREFS_NAME = "activation_prefs"
        internal const val PREFS_THEME_NAME = "activation_themes_prefs"
        internal const val PREF_ENVIRONMENT = "activation_environment"
        internal const val PREF_TEST_ADS = "activation_test_ads"
        internal const val PREF_TEST_MODE = "activation_test_mode"
        internal const val PREF_EMAIL = "email_address"
        internal const val PREF_PHONE = "phone_number"
        internal const val PREF_FORCE_PLACEMENTS = "activation_force_placements"
        internal const val PREF_SCAN_REWARD = "activation_scan_reward"
        internal const val PREF_REWARD_CURRENCY_NAME = "activation_reward_currency_name"
        internal const val PREF_REWARD_CURRENCY_CODE = "activation_reward_currency_code"
        internal const val PREF_REWARD_PAYOUT_PERCENTAGE = "activation_reward_payout_percentage"
        internal const val PREF_REWARD_CURRENCY_PER_DOLLAR = "activation_reward_currency_per_dollar"
        internal const val PREF_REWARD_CURRENCY_CODE_POSITION = "activation_reward_currency_code_position"
        internal const val PREF_REWARD_CURRENCY_LABEL_STYLE = "activation_reward_currency_label_style"
        internal const val PREF_REWARD_CURRENCY_MESSAGING_STYLE = "activation_reward_currency_messaging_style"
        internal const val PREF_REWARD_ROUNDING = "activation_reward_rounding"
        internal const val PREF_REWARD_CURRENCY_IMAGE_LOCATIONS = "activation_reward_currency_image_locations"
        internal const val PREF_SHOW_HEADER = "activation_show_header"

        internal const val DEFAULT_CURRENCY_NAME = "Points"
        // Reward defaults mirror RewardCurrency.default() so a fresh install matches the SDK baseline.
        // NOTE: payout is the new 0.4–1.0 userPayoutPercentage scale (was the legacy 40–100).
        internal const val DEFAULT_PAYOUT_PERCENTAGE = RewardCurrency.DEFAULT_USER_PAYOUT_PERCENTAGE
        internal const val DEFAULT_REWARD_CURRENCY_PER_DOLLAR = RewardCurrency.DEFAULT_CURRENCY_PER_DOLLAR
        internal val DEFAULT_CURRENCY_CODE_POSITION = RewardCurrencyCodePosition.Leading
        internal val DEFAULT_REWARD_LABEL_STYLE = RewardCurrencyLabelStyle.CurrencyImage
        internal val DEFAULT_REWARD_MESSAGING_STYLE = RewardCurrencyMessagingTextStyle.CurrencyName
        internal val DEFAULT_REWARD_ROUNDING = RewardCurrencyRounding.Whole
        internal val DEFAULT_CURRENCY_IMAGE_LOCATIONS: Set<RewardCurrencyImageLocation> =
            RewardCurrencyImageLocation.ALL

        internal const val REWARDS_PREF_NAME = "activation_rewards_prefs"

        private const val TAG = "ActivationsActivity"

        private fun applyToClient(
            envName: String,
            testAds: Boolean,
            testMode: Boolean,
            email: String,
            phone: String,
            forcePlacements: Set<String>,
            rewardCurrencyName: String,
            rewardCurrencyCode: String,
            rewardPayoutPercentage: Double,
            rewardCurrencyPerDollar: Double,
            rewardCurrencyCodePosition: RewardCurrencyCodePosition,
            rewardCurrencyLabelStyle: RewardCurrencyLabelStyle,
            rewardCurrencyMessagingStyle: RewardCurrencyMessagingTextStyle,
            rewardRounding: RewardCurrencyRounding,
            rewardCurrencyImageLocations: Set<RewardCurrencyImageLocation>,
            scanRewardPoints: RewardPoint,
        ) {
            with(ActivationClient.instance) {
                environment = when (envName) {
                    "Development" -> HttpEnvironment.Development
                    "Staging" -> HttpEnvironment.Staging
                    else -> HttpEnvironment.Production
                }

                testOptions = buildSet {
                    if (testAds) add(TestOptions.Ads)
                    if (testMode) add(TestOptions.Test)
                }

                placements = forcePlacements.mapNotNull { name ->
                    runCatching { PlacementLayout.valueOf(name) }.getOrNull()
                }.toSet()

                hashedEmail =
                    email.takeIf { it.isNotEmpty() }?.encodeUtf8()?.sha256()?.hex().also { hash ->
                        if (hash.isNullOrEmpty()) {
                            LogcatManager.event().debug(TAG) { "hashedEmail is empty" }
                        } else {
                            LogcatManager.event().debug(TAG) { "hashedEmail = $hash" }
                        }
                    }

                hashedPhone =
                    phone.takeIf { it.isNotEmpty() }?.encodeUtf8()?.sha256()?.hex().also { hash ->
                        if (hash.isNullOrEmpty()) {
                            LogcatManager.event().debug(TAG) { "hashedPhone is empty" }
                        } else {
                            LogcatManager.event().debug(TAG) { "hashedPhone = $hash" }
                        }
                    }

                // RewardCurrency.default() normalizes for us: trims/truncates the name, blank code → null,
                // clamps payout to 0.4–1.0, and falls back CurrencyCode → CurrencyName when code is null.
                this.rewardCurrency = RewardCurrency.default(
                    currencyName = rewardCurrencyName.ifEmpty { DEFAULT_CURRENCY_NAME },
                    currencyCode = rewardCurrencyCode.ifBlank { null },
                    currencyCodePosition = rewardCurrencyCodePosition,
                    currencyPerDollar = rewardCurrencyPerDollar,
                    userPayoutPercentage = rewardPayoutPercentage,
                    rewardCurrencyLabelStyle = rewardCurrencyLabelStyle,
                    rewardCurrencyMessagingTextStyle = rewardCurrencyMessagingStyle,
                    rewardRounding = rewardRounding,
                    currencyImageLocations = rewardCurrencyImageLocations,
                )
                this.scanReward =
                    if (!scanRewardPoints.isZero()) ScanReward(scanRewardPoints) else null
                LogcatManager.event().debug(TAG) { "scanReward = ${scanRewardPoints.value}" }
            }
        }

        @JvmStatic
        fun applySettings(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            applyToClient(
                envName = prefs.getString(PREF_ENVIRONMENT, "Production") ?: "Production",
                testAds = prefs.getBoolean(PREF_TEST_ADS, true),
                testMode = prefs.getBoolean(PREF_TEST_MODE, true),
                email = prefs.getString(PREF_EMAIL, "") ?: "",
                phone = prefs.getString(PREF_PHONE, "") ?: "",
                forcePlacements = prefs.getStringSet(PREF_FORCE_PLACEMENTS, emptySet())
                    ?: emptySet(),
                rewardCurrencyName = prefs.getString(
                    PREF_REWARD_CURRENCY_NAME,
                    DEFAULT_CURRENCY_NAME
                ) ?: DEFAULT_CURRENCY_NAME,
                rewardCurrencyCode = prefs.getString(PREF_REWARD_CURRENCY_CODE, "") ?: "",
                rewardPayoutPercentage = prefs.getFloat(
                    PREF_REWARD_PAYOUT_PERCENTAGE,
                    DEFAULT_PAYOUT_PERCENTAGE.toFloat()
                ).toDouble(),
                rewardCurrencyPerDollar = prefs.getFloat(
                    PREF_REWARD_CURRENCY_PER_DOLLAR,
                    DEFAULT_REWARD_CURRENCY_PER_DOLLAR.toFloat()
                ).toDouble(),
                rewardCurrencyCodePosition = prefs.getEnum(
                    PREF_REWARD_CURRENCY_CODE_POSITION,
                    DEFAULT_CURRENCY_CODE_POSITION
                ),
                rewardCurrencyLabelStyle = prefs.getEnum(
                    PREF_REWARD_CURRENCY_LABEL_STYLE,
                    DEFAULT_REWARD_LABEL_STYLE
                ),
                rewardCurrencyMessagingStyle = prefs.getEnum(
                    PREF_REWARD_CURRENCY_MESSAGING_STYLE,
                    DEFAULT_REWARD_MESSAGING_STYLE
                ),
                rewardRounding = prefs.getEnum(PREF_REWARD_ROUNDING, DEFAULT_REWARD_ROUNDING),
                rewardCurrencyImageLocations = prefs.getEnumSet(
                    PREF_REWARD_CURRENCY_IMAGE_LOCATIONS,
                    DEFAULT_CURRENCY_IMAGE_LOCATIONS
                ),
                scanRewardPoints = RewardPoint(prefs.getNumberAsFloat(PREF_SCAN_REWARD, SettingsData.DEFAULT_SCAN_REWARD).toDouble()),
            )
        }

    }
}

@Serializable
private data class RewardsData(
    val type: Type,
    val amount: Double,
    val blinkReceiptId: String,
    val dateCollectedMs: Long,
) {
    enum class Type {
        ScanFinished,
        Promo,
        Boost,
    }
}

@OptIn(ExperimentalTime::class)
private fun Rewards.toData(): RewardsData =
    RewardsData(
        type = when (this) {
            is Rewards.ScanFinished -> RewardsData.Type.ScanFinished
            is Rewards.Promotion -> RewardsData.Type.Promo
            is Rewards.Boost -> RewardsData.Type.Boost
        },
        amount = this.amount.value,
        blinkReceiptId = this.blinkReceiptId,
        dateCollectedMs = Clock.System.now().toEpochMilliseconds(),
    )

private fun RewardsData.toModel(): Rewards =
    when (this.type) {
        RewardsData.Type.ScanFinished -> Rewards.ScanFinished(this.blinkReceiptId, RewardPoint(this.amount))
        RewardsData.Type.Promo -> Rewards.Promotion(this.blinkReceiptId, RewardPoint(this.amount))
        RewardsData.Type.Boost -> Rewards.Boost(this.blinkReceiptId, RewardPoint(this.amount))
    }

/** Reads an enum pref stored by [Enum.name], falling back to [default] for missing/unknown values. */
internal inline fun <reified T : Enum<T>> SharedPreferences.getEnum(key: String, default: T): T =
    getString(key, null)?.let { name -> runCatching { enumValueOf<T>(name) }.getOrNull() } ?: default

/**
 * Reads a Set of enums stored by [Enum.name]. Falls back to [default] only when the key is unset, so
 * an intentionally-empty selection persists as empty rather than snapping back to the default.
 */
internal inline fun <reified T : Enum<T>> SharedPreferences.getEnumSet(
    key: String,
    default: Set<T>,
): Set<T> =
    getStringSet(key, null)
        ?.mapNotNull { name -> runCatching { enumValueOf<T>(name) }.getOrNull() }
        ?.toSet()
        ?: default

/**
 * Reads a numeric pref as [Float], tolerating a value persisted under a different numeric type by an
 * older app version — e.g. [ActivationActivity.PREF_SCAN_REWARD] was previously written with
 * `putInt`. A typed getter (`getFloat`) throws `ClassCastException` when the stored type differs, so
 * read the raw value via [SharedPreferences.getAll] and coerce. The value migrates to a Float the
 * next time settings are saved.
 */
internal fun SharedPreferences.getNumberAsFloat(key: String, default: Float): Float =
    when (val value = all[key]) {
        is Number -> value.toFloat()
        is String -> value.toFloatOrNull() ?: default
        else -> default
    }

