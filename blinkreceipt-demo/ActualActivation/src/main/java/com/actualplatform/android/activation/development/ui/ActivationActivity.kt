package com.actualplatform.android.activation.development.ui

import android.content.Context
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.activity.compose.rememberLauncherForActivityResult
import com.actualplatform.activation.ActivationClient
import com.actualplatform.activation.OffersWall
import com.actualplatform.activation.Rewards
import com.actualplatform.activation.ScanReward
import com.microblink.camera.ui.CameraRecognizerContract
import com.microblink.camera.ui.CameraRecognizerOptions
import com.microblink.camera.ui.CameraRecognizerResults
import com.actualplatform.activation.models.PlacementLayout
import com.actualplatform.activation.networking.HttpEnvironment
import com.actualplatform.activation.networking.TestOptions
import com.microblink.ScanOptions
import com.microblink.camera.ui.CameraCharacteristics
import com.microblink.camera.ui.internal.parcelable
import com.microblink.core.ScanResults
import com.microblink.logcat.LogcatManager
import okio.ByteString.Companion.encodeUtf8

internal class ActivationActivity : ComponentActivity() {

    private val scanOptions: ScanOptions by lazy {
        intent.parcelable(OPTIONS_KEY) ?: ScanOptions.newBuilder().build()
    }

    private val cameraCharacteristics: CameraCharacteristics by lazy {
        intent.parcelable(CAMERA_CHARACTERISTICS) ?: CameraCharacteristics.Builder().build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        setContent {
            MaterialTheme {
                val backStack = rememberNavBackStack(ActivationRoute.Home)
                var refreshKey by remember { mutableIntStateOf(0) }

                var lastScanResults by remember { mutableStateOf<ScanResults?>(null) }
                var lastScanError by remember { mutableStateOf<String?>(null) }

                var totalRewardsEarned by remember { mutableFloatStateOf(0f) }
                val rewardEvents = remember { mutableStateListOf<Rewards>() }

                LaunchedEffect(Unit) {
                    ActivationClient.instance.rewards.collect { reward ->
                        LogcatManager.event().debug(TAG) { "Reward received: type=${reward::class.simpleName}, amount=${reward.amount}, totalBefore=$totalRewardsEarned, totalAfter=${totalRewardsEarned + reward.amount}, eventCount=${rewardEvents.size + 1}" }
                        totalRewardsEarned += reward.amount
                        rewardEvents.add(reward)
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
                                LogcatManager.event().debug(TAG) { "OffersWall::launcher callback -> $result" }
                                when (result) {
                                    is CameraRecognizerResults.Success -> {
                                        LogcatManager.event().debug(TAG) { "OffersWall::onScanResult -> Success, blinkReceiptId=${result.results?.blinkReceiptId()}" }
                                        lastScanResults = result.results
                                        lastScanError = null
                                        // Don't pop — let the OffersWall SDK process scan results internally
                                        // (loading screen, receipt summary, etc.). Only pop on onDismiss.
                                    }
                                    is CameraRecognizerResults.Exception -> {
                                        LogcatManager.event().debug(TAG) { "OffersWall::onScanResult -> Exception: ${result.exception}" }
                                        lastScanError = result.exception.message ?: "Unknown error"
                                        lastScanResults = null
                                    }
                                    CameraRecognizerResults.Cancelled -> {
                                        LogcatManager.event().debug(TAG) { "OffersWall::onScanResult -> Cancelled" }
                                    }
                                }
                            }

                            val showHeader = prefs.getBoolean(PREF_SHOW_HEADER, true)

                            OffersWall(
                                onScanReceipt = {
                                    LogcatManager.event().debug(TAG) { "OffersWall::onScanReceipt -> launching camera" }
                                    lastScanResults = null
                                    lastScanError = null
                                    launcher.launch(
                                        CameraRecognizerOptions.Builder()
                                            .options(scanOptions)
                                            .characteristics(cameraCharacteristics)
                                            .activation(true)
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

                        entry<ActivationRoute.Home> { _ ->
                            val settings = remember(refreshKey) { SettingsData.Companion.from(prefs) }

                            HomeScreen(
                                settings = settings,
                                lastScanResults = lastScanResults,
                                lastScanError = lastScanError,
                                totalRewardsEarned = totalRewardsEarned,
                                rewardEvents = rewardEvents,
                                onOffersWallClick = {
                                    lastScanResults = null
                                    lastScanError = null
                                    totalRewardsEarned = 0f
                                    rewardEvents.clear()
                                    val route: NavKey =
                                        if (settings.email.isEmpty() && settings.phone.isEmpty()) ActivationRoute.SettingsEditor else ActivationRoute.OffersWall
                                    backStack.add(route)
                                },
                                onSettingsClick = { backStack.add(ActivationRoute.SettingsEditor) },
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
        internal const val PREFS_NAME = "engage_prefs"
        internal const val PREF_ENVIRONMENT = "engage_environment"
        internal const val PREF_TEST_ADS = "engage_test_ads"
        internal const val PREF_TEST_MODE = "engage_test_mode"
        internal const val PREF_EMAIL = "email_address"
        internal const val PREF_PHONE = "phone_number"
        internal const val PREF_FORCE_PLACEMENTS = "engage_force_placements"
        internal const val PREF_SCAN_REWARD = "engage_scan_reward"
        internal const val PREF_REWARD_CURRENCY_NAME = "engage_reward_currency_name"
        internal const val PREF_REWARD_PAYOUT_PERCENTAGE = "engage_reward_payout_percentage"
        internal const val PREF_REWARD_ICON_BASE64 = "engage_reward_icon_base64"
        internal const val PREF_SHOW_HEADER = "engage_show_header"
        internal const val DEFAULT_CURRENCY_NAME = "Points"
        internal const val DEFAULT_PAYOUT_PERCENTAGE = 100.0
        private const val TAG = "ActivationsActivity"

        private fun applyToClient(
            envName: String,
            testAds: Boolean,
            testMode: Boolean,
            email: String,
            phone: String,
            forcePlacements: Set<String>,
            rewardCurrencyName: String,
            rewardPayoutPercentage: Double,
            rewardIconBase64: String?,
            scanReward: Int,
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

                hashedEmail = email.takeIf { it.isNotEmpty() }?.encodeUtf8()?.sha256()?.hex().also { hash ->
                    if (hash.isNullOrEmpty()) {
                        LogcatManager.event().debug(TAG) { "hashedEmail is empty" }
                    } else {
                        LogcatManager.event().debug(TAG) { "hashedEmail = $hash" }
                    }
                }

                hashedPhone = phone.takeIf { it.isNotEmpty() }?.encodeUtf8()?.sha256()?.hex().also { hash ->
                    if (hash.isNullOrEmpty()) {
                        LogcatManager.event().debug(TAG) { "hashedPhone is empty" }
                    } else {
                        LogcatManager.event().debug(TAG) { "hashedPhone = $hash" }
                    }
                }

                this.rewardCurrencyName = rewardCurrencyName.ifEmpty { DEFAULT_CURRENCY_NAME }
                this.rewardPayoutPercentage = rewardPayoutPercentage
                this.rewardIcon = rewardIconBase64?.takeIf { it.isNotEmpty() }?.let {
                    runCatching { Base64.decode(it, Base64.DEFAULT) }.getOrNull()
                }
                this.scanReward = ScanReward(scanReward)
            }
        }

        @JvmStatic
        fun applySettings(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            applyToClient(
                envName = prefs.getString(PREF_ENVIRONMENT, "Production") ?: "Production",
                testAds = prefs.getBoolean(PREF_TEST_ADS, true),
                testMode = prefs.getBoolean(PREF_TEST_MODE, true),
                email = prefs.getString(PREF_EMAIL, "") ?: "",
                phone = prefs.getString(PREF_PHONE, "") ?: "",
                forcePlacements = prefs.getStringSet(PREF_FORCE_PLACEMENTS, emptySet()) ?: emptySet(),
                rewardCurrencyName = prefs.getString(PREF_REWARD_CURRENCY_NAME, DEFAULT_CURRENCY_NAME) ?: DEFAULT_CURRENCY_NAME,
                rewardPayoutPercentage = prefs.getFloat(PREF_REWARD_PAYOUT_PERCENTAGE, DEFAULT_PAYOUT_PERCENTAGE.toFloat()).toDouble(),
                rewardIconBase64 = prefs.getString(PREF_REWARD_ICON_BASE64, null),
                scanReward = prefs.getInt(PREF_SCAN_REWARD, 0),
            )
        }
    }
}
