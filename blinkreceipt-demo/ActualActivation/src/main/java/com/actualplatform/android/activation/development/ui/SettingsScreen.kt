package com.actualplatform.android.activation.development.ui

import android.content.SharedPreferences
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.actualplatform.activation.ActivationClient
import com.actualplatform.activation.RewardCurrency
import com.actualplatform.activation.RewardCurrencyCodePosition
import com.actualplatform.activation.RewardCurrencyImageLocation
import com.actualplatform.activation.RewardCurrencyLabelStyle
import com.actualplatform.activation.RewardCurrencyMessagingTextStyle
import com.actualplatform.activation.RewardCurrencyRounding
import com.actualplatform.android.activation.development.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Formats a scan-reward value with a minimum of 2 decimal places, preserving any extra precision
 * (e.g. 10.0 → "10.00", 10.125 → "10.125"). Forces the US decimal separator so the rendered value
 * stays parseable by [String.toFloatOrNull] when used as an editable field's initial text.
 */
private val ScanRewardFormat = DecimalFormat("0.00######", DecimalFormatSymbols(Locale.US))

@Stable
internal data class SettingsData(
    val email: String,
    val phone: String,
    val environment: String,
    val showHeader: Boolean,
    val testAds: Boolean,
    val testMode: Boolean,
    val currencyName: String,
    val currencyCode: String,
    val userPayoutPercentage: Double,
    val currencyPerDollar: Double,
    val currencyCodePosition: RewardCurrencyCodePosition,
    val rewardCurrencyLabelStyle: RewardCurrencyLabelStyle,
    val rewardCurrencyMessagingStyle: RewardCurrencyMessagingTextStyle,
    val rewardRounding: RewardCurrencyRounding,
    val currencyImageLocations: Set<RewardCurrencyImageLocation>,
    val scanReward: Double,
    val receiptMaxAgeDays: Int,
    val forcePlacements: Set<String>,
) {
    companion object {
        const val DEFAULT_SCAN_REWARD = 10.0f

        fun from(prefs: SharedPreferences): SettingsData = SettingsData(
            email = prefs.getString(ActivationActivity.PREF_EMAIL, "") ?: "",
            phone = prefs.getString(ActivationActivity.PREF_PHONE, "") ?: "",
            environment = prefs.getString(ActivationActivity.PREF_ENVIRONMENT, "Production") ?: "Production",
            showHeader = prefs.getBoolean(ActivationActivity.PREF_SHOW_HEADER, true),
            testAds = prefs.getBoolean(ActivationActivity.PREF_TEST_ADS, true),
            testMode = prefs.getBoolean(ActivationActivity.PREF_TEST_MODE, true),
            currencyName = prefs.getString(ActivationActivity.PREF_REWARD_CURRENCY_NAME, ActivationActivity.DEFAULT_CURRENCY_NAME) ?: ActivationActivity.DEFAULT_CURRENCY_NAME,
            currencyCode = prefs.getString(ActivationActivity.PREF_REWARD_CURRENCY_CODE, "") ?: "",
            userPayoutPercentage = prefs.getFloat(ActivationActivity.PREF_REWARD_PAYOUT_PERCENTAGE, ActivationActivity.DEFAULT_PAYOUT_PERCENTAGE.toFloat()).toDouble(),
            currencyPerDollar = prefs.getFloat(ActivationActivity.PREF_REWARD_CURRENCY_PER_DOLLAR, ActivationActivity.DEFAULT_REWARD_CURRENCY_PER_DOLLAR.toFloat()).toDouble(),
            currencyCodePosition = prefs.getEnum(ActivationActivity.PREF_REWARD_CURRENCY_CODE_POSITION, ActivationActivity.DEFAULT_CURRENCY_CODE_POSITION),
            rewardCurrencyLabelStyle = prefs.getEnum(ActivationActivity.PREF_REWARD_CURRENCY_LABEL_STYLE, ActivationActivity.DEFAULT_REWARD_LABEL_STYLE),
            rewardCurrencyMessagingStyle = prefs.getEnum(ActivationActivity.PREF_REWARD_CURRENCY_MESSAGING_STYLE, ActivationActivity.DEFAULT_REWARD_MESSAGING_STYLE),
            rewardRounding = prefs.getEnum(ActivationActivity.PREF_REWARD_ROUNDING, ActivationActivity.DEFAULT_REWARD_ROUNDING),
            currencyImageLocations = prefs.getEnumSet(ActivationActivity.PREF_REWARD_CURRENCY_IMAGE_LOCATIONS, ActivationActivity.DEFAULT_CURRENCY_IMAGE_LOCATIONS),
            scanReward = prefs.getNumberAsFloat(ActivationActivity.PREF_SCAN_REWARD, DEFAULT_SCAN_REWARD).toDouble(),
            receiptMaxAgeDays = prefs.getInt(ActivationActivity.PREF_RECEIPT_MAX_AGE_DAYS, ActivationClient.DEFAULT_RECEIPT_MAX_AGE_DAYS),
            forcePlacements = prefs.getStringSet(ActivationActivity.PREF_FORCE_PLACEMENTS, emptySet()) ?: emptySet(),
        )
    }
}

/** Read-only settings summary — not a route, used inline on the home screen. */
@Composable
internal fun SettingsSummary(
    settings: SettingsData,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.activations_settings_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        SectionHeader(stringResource(R.string.activations_section_user_identity))
        SettingsSummaryRow(stringResource(R.string.activations_editText_hint_email), settings.email.ifEmpty { "—" })
        SettingsSummaryRow(stringResource(R.string.activations_editText_hint_phone), settings.phone.ifEmpty { "—" })

        SectionHeader(stringResource(R.string.activations_section_environment))
        SettingsSummaryRow(stringResource(R.string.activations_section_environment), settings.environment)

        SectionHeader(stringResource(R.string.activations_section_test_options))
        SettingsSummaryRow(stringResource(R.string.activations_label_test_ads), if (settings.testAds) "On" else "Off")
        SettingsSummaryRow(stringResource(R.string.activations_label_test_mode), if (settings.testMode) "On" else "Off")

        // Debug Placements
        if (settings.forcePlacements.isNotEmpty()) {
            SectionHeader(stringResource(R.string.activations_section_debug_placements))
            SettingsSummaryRow(stringResource(R.string.activations_label_force_placements), settings.forcePlacements.joinToString(", "))
        }

        SectionHeader(stringResource(R.string.activations_section_receipt_validation))
        SettingsSummaryRow(stringResource(R.string.activations_label_receipt_max_age), "${settings.receiptMaxAgeDays} days")

        SectionHeader(stringResource(R.string.activations_section_ui))
        SettingsSummaryRow(stringResource(R.string.activations_offers_show_header), if (settings.showHeader) "On" else "Off")

        SectionHeader(stringResource(R.string.activations_section_reward_currency))
        SettingsSummaryRow(stringResource(R.string.activations_label_currency_name), settings.currencyName)
        SettingsSummaryRow(stringResource(R.string.activations_label_currency_code), settings.currencyCode.ifEmpty { "—" })
        if (settings.scanReward > 0) {
            SettingsSummaryRow(stringResource(R.string.activations_label_reward_points), ScanRewardFormat.format(settings.scanReward))
        }
        SettingsSummaryRow(stringResource(R.string.activations_label_user_payout_percentage), "%.2f".format(settings.userPayoutPercentage))
        SettingsSummaryRow(stringResource(R.string.activations_label_currency_per_dollar), "%.2f".format(settings.currencyPerDollar))
        SettingsSummaryRow(stringResource(R.string.activations_label_currency_code_position), settings.currencyCodePosition.name)
        SettingsSummaryRow(stringResource(R.string.activations_label_reward_label_style), settings.rewardCurrencyLabelStyle.name)
        SettingsSummaryRow(stringResource(R.string.activations_label_reward_messaging_style), settings.rewardCurrencyMessagingStyle.name)
        SettingsSummaryRow(stringResource(R.string.activations_label_reward_rounding), settings.rewardRounding.name)
        SettingsSummaryRow(
            stringResource(R.string.activations_label_currency_image_locations),
            settings.currencyImageLocations.takeIf { it.isNotEmpty() }?.joinToString(", ") { it.name } ?: "—",
        )
    }
}

/** Full-screen settings editor — launched as a nav route. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsEditorScreen(
    prefs: SharedPreferences,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.activations_settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.activations_nav_back))
                    }
                },
            )
        },
    ) { padding ->
        EditModeContent(prefs, onBack, Modifier.padding(padding))
    }
}

@Composable
private fun EditModeContent(
    prefs: SharedPreferences,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val initial = remember { SettingsData.from(prefs) }

    var email by remember { mutableStateOf(initial.email) }
    var phone by remember { mutableStateOf(initial.phone) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var environment by remember { mutableStateOf(initial.environment) }
    var testAds by remember { mutableStateOf(initial.testAds) }
    var testMode by remember { mutableStateOf(initial.testMode) }
    var showHeader by remember { mutableStateOf(initial.showHeader) }

    var forcePlacements by remember { mutableStateOf(initial.forcePlacements) }
    var scanReward by remember {
        mutableStateOf(ScanRewardFormat.format(initial.scanReward))
    }
    var receiptMaxAgeDays by remember { mutableStateOf(initial.receiptMaxAgeDays.toString()) }
    var rewardCurrencyName by remember { mutableStateOf(initial.currencyName) }
    var rewardPayoutPercentage by remember {
        mutableStateOf(
            prefs.getFloat(ActivationActivity.PREF_REWARD_PAYOUT_PERCENTAGE, ActivationActivity.DEFAULT_PAYOUT_PERCENTAGE.toFloat())
                .let { if (it == ActivationActivity.DEFAULT_PAYOUT_PERCENTAGE.toFloat()) "" else it.toString() }
        )
    }
    var rewardCurrencyPerDollar by remember {
        mutableStateOf(
            prefs.getFloat(ActivationActivity.PREF_REWARD_CURRENCY_PER_DOLLAR, ActivationActivity.DEFAULT_REWARD_CURRENCY_PER_DOLLAR.toFloat())
                .let { if (it == ActivationActivity.DEFAULT_REWARD_CURRENCY_PER_DOLLAR.toFloat()) "" else it.toString() }
        )
    }
    var rewardCurrencyCode by remember { mutableStateOf(initial.currencyCode) }
    var currencyCodePosition by remember { mutableStateOf(initial.currencyCodePosition) }
    var rewardCurrencyLabelStyle by remember { mutableStateOf(initial.rewardCurrencyLabelStyle) }
    var rewardCurrencyMessagingStyle by remember { mutableStateOf(initial.rewardCurrencyMessagingStyle) }
    var rewardRounding by remember { mutableStateOf(initial.rewardRounding) }
    var currencyImageLocations by remember { mutableStateOf(initial.currencyImageLocations) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        UserIdentitySection(
            email = email,
            onEmailChange = {
                email = it
                emailError = null
            },
            phone = phone,
            onPhoneChange = { phone = it },
            emailError = emailError,
        )

        Spacer(modifier = Modifier.height(24.dp))

        EnvironmentSection(
            environment = environment,
            onEnvironmentChange = { environment = it },
        )

        Spacer(modifier = Modifier.height(24.dp))

        TestOptionsSection(
            testAds = testAds,
            onTestAdsChange = { testAds = it },
            testMode = testMode,
            onTestModeChange = { testMode = it },
        )

        Spacer(modifier = Modifier.height(24.dp))

        ReceiptValidationSection(
            receiptMaxAgeDays = receiptMaxAgeDays,
            onReceiptMaxAgeDaysChange = { receiptMaxAgeDays = it },
        )

        Spacer(modifier = Modifier.height(24.dp))

        UiSettingsSection(
            showHeader = showHeader,
            onShowHeaderChange = { showHeader = it },
        )

        Spacer(modifier = Modifier.height(24.dp))

        DebugPlacementsSection(
            forcePlacements = forcePlacements,
            onForcePlacementsChange = { forcePlacements = it },
        )

        Spacer(modifier = Modifier.height(24.dp))

        ScanRewardSection(
            scanReward = scanReward,
            onScanRewardChange = { scanReward = it },
        )

        Spacer(modifier = Modifier.height(24.dp))

        RewardCurrencySection(
            currencyName = rewardCurrencyName,
            onCurrencyNameChange = { rewardCurrencyName = it },
            currencyCode = rewardCurrencyCode,
            onCurrencyCodeChange = { rewardCurrencyCode = it },
            userPayoutPercentage = rewardPayoutPercentage,
            onUserPayoutPercentageChange = { rewardPayoutPercentage = it },
            currencyPerDollar = rewardCurrencyPerDollar,
            onCurrencyPerDollarChange = { rewardCurrencyPerDollar = it },
            currencyCodePosition = currencyCodePosition,
            onCurrencyCodePositionChange = { currencyCodePosition = it },
            rewardCurrencyLabelStyle = rewardCurrencyLabelStyle,
            onRewardCurrencyLabelStyleChange = { rewardCurrencyLabelStyle = it },
            rewardCurrencyMessagingStyle = rewardCurrencyMessagingStyle,
            onRewardCurrencyMessagingStyleChange = { rewardCurrencyMessagingStyle = it },
            rewardRounding = rewardRounding,
            onRewardRoundingChange = { rewardRounding = it },
            currencyImageLocations = currencyImageLocations,
            onCurrencyImageLocationsChange = { currencyImageLocations = it },
        )

        Spacer(modifier = Modifier.height(24.dp))

        val emailErrorLabel = stringResource(R.string.activations_error_invalid_email)
        Button(
            onClick = {
                val trimmedEmail = email.trim()
                if (trimmedEmail.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                    emailError = emailErrorLabel
                    return@Button
                }
                emailError = null

                prefs.edit()
                    .putString(ActivationActivity.PREF_EMAIL, trimmedEmail)
                    .putString(ActivationActivity.PREF_PHONE, phone.trim())
                    .putString(ActivationActivity.PREF_ENVIRONMENT, environment)
                    .putBoolean(ActivationActivity.PREF_TEST_ADS, testAds)
                    .putBoolean(ActivationActivity.PREF_TEST_MODE, testMode)
                    .putBoolean(ActivationActivity.PREF_SHOW_HEADER, showHeader)
                    .putStringSet(ActivationActivity.PREF_FORCE_PLACEMENTS, forcePlacements)
                    .putFloat(ActivationActivity.PREF_SCAN_REWARD, scanReward.toFloatOrNull() ?: SettingsData.DEFAULT_SCAN_REWARD)
                    .putInt(ActivationActivity.PREF_RECEIPT_MAX_AGE_DAYS, receiptMaxAgeDays.toIntOrNull() ?: ActivationClient.DEFAULT_RECEIPT_MAX_AGE_DAYS)
                    .putString(ActivationActivity.PREF_REWARD_CURRENCY_NAME, rewardCurrencyName.trim())
                    .putString(ActivationActivity.PREF_REWARD_CURRENCY_CODE, rewardCurrencyCode.trim())
                    .putFloat(ActivationActivity.PREF_REWARD_PAYOUT_PERCENTAGE, rewardPayoutPercentage.toFloatOrNull()?.coerceIn(
                        RewardCurrency.USER_PAYOUT_PERCENTAGE_MIN.toFloat(),
                        RewardCurrency.USER_PAYOUT_PERCENTAGE_MAX.toFloat(),
                    ) ?: ActivationActivity.DEFAULT_PAYOUT_PERCENTAGE.toFloat())
                    .putFloat(ActivationActivity.PREF_REWARD_CURRENCY_PER_DOLLAR, rewardCurrencyPerDollar.toFloatOrNull() ?: ActivationActivity.DEFAULT_REWARD_CURRENCY_PER_DOLLAR.toFloat())
                    .putString(ActivationActivity.PREF_REWARD_CURRENCY_CODE_POSITION, currencyCodePosition.name)
                    .putString(ActivationActivity.PREF_REWARD_CURRENCY_LABEL_STYLE, rewardCurrencyLabelStyle.name)
                    .putString(ActivationActivity.PREF_REWARD_CURRENCY_MESSAGING_STYLE, rewardCurrencyMessagingStyle.name)
                    .putString(ActivationActivity.PREF_REWARD_ROUNDING, rewardRounding.name)
                    .putStringSet(ActivationActivity.PREF_REWARD_CURRENCY_IMAGE_LOCATIONS, currencyImageLocations.map { it.name }.toSet())
                    .apply()

                try {
                    ActivationActivity.applySettings(context)
                    Toast.makeText(context, R.string.activations_toast_settings_saved, Toast.LENGTH_SHORT).show()
                    onBack()
                } catch (e: IllegalArgumentException) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.activations_button_save))
        }
    }
}
