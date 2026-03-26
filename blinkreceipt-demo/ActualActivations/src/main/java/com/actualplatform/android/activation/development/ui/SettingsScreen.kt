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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.actualplatform.android.EngageClient
import com.actualplatform.android.R

@Stable
internal data class SettingsData(
    val email: String,
    val phone: String,
    val environment: String,
    val showHeader: Boolean,
    val testAds: Boolean,
    val testMode: Boolean,
    val currencyName: String,
    val scanReward: Int,
    val receiptMaxAgeDays: Int,
    val forcePlacements: Set<String>,
) {
    companion object {
        fun from(prefs: SharedPreferences): SettingsData = SettingsData(
            email = prefs.getString(ActivationsActivity.PREF_EMAIL, "") ?: "",
            phone = prefs.getString(ActivationsActivity.PREF_PHONE, "") ?: "",
            environment = prefs.getString(ActivationsActivity.PREF_ENVIRONMENT, "Production") ?: "Production",
            showHeader = prefs.getBoolean(ActivationsActivity.PREF_SHOW_HEADER, true),
            testAds = prefs.getBoolean(ActivationsActivity.PREF_TEST_ADS, true),
            testMode = prefs.getBoolean(ActivationsActivity.PREF_TEST_MODE, true),
            currencyName = prefs.getString(ActivationsActivity.PREF_REWARD_CURRENCY_NAME, ActivationsActivity.DEFAULT_CURRENCY_NAME) ?: ActivationsActivity.DEFAULT_CURRENCY_NAME,
            scanReward = prefs.getInt(ActivationsActivity.PREF_SCAN_REWARD, 0),
            receiptMaxAgeDays = prefs.getInt(ActivationsActivity.PREF_RECEIPT_MAX_AGE_DAYS, EngageClient.DEFAULT_RECEIPT_MAX_AGE_DAYS),
            forcePlacements = prefs.getStringSet(ActivationsActivity.PREF_FORCE_PLACEMENTS, emptySet()) ?: emptySet(),
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
            text = stringResource(R.string.engage_settings_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        SectionHeader(stringResource(R.string.engage_section_user_identity))
        SettingsSummaryRow(
            stringResource(R.string.main_engage_editText_hint_email),
            settings.email.ifEmpty { "—" })
        SettingsSummaryRow(
            stringResource(R.string.main_engage_editText_hint_phone),
            settings.phone.ifEmpty { "—" })

        SectionHeader(stringResource(R.string.engage_section_environment))
        SettingsSummaryRow(
            stringResource(R.string.engage_section_environment),
            settings.environment
        )

        SectionHeader(stringResource(R.string.engage_section_test_options))
        SettingsSummaryRow(
            stringResource(R.string.engage_label_test_ads),
            if (settings.testAds) "On" else "Off"
        )
        SettingsSummaryRow(
            stringResource(R.string.engage_label_test_mode),
            if (settings.testMode) "On" else "Off"
        )

        // Debug Placements
        if (settings.forcePlacements.isNotEmpty()) {
            SectionHeader(stringResource(R.string.engage_section_debug_placements))
            SettingsSummaryRow(
                stringResource(R.string.engage_label_force_placements),
                settings.forcePlacements.joinToString(", ")
            )
        }

        SectionHeader(stringResource(R.string.engage_section_receipt_validation))
        SettingsSummaryRow(
            stringResource(R.string.engage_label_receipt_max_age),
            "${settings.receiptMaxAgeDays} days"
        )

        SectionHeader(stringResource(R.string.engage_section_ui))
        SettingsSummaryRow(
            stringResource(R.string.engage_offers_show_header),
            if (settings.showHeader) "On" else "Off"
        )

        SectionHeader(stringResource(R.string.engage_section_reward_currency))
        SettingsSummaryRow(
            stringResource(R.string.engage_label_currency_name),
            settings.currencyName
        )
        if (settings.scanReward > 0) {
            SettingsSummaryRow(
                stringResource(R.string.engage_label_reward_points),
                settings.scanReward.toString()
            )
        }
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
                title = { Text(stringResource(R.string.engage_settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.engage_nav_back))
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
        mutableStateOf(initial.scanReward.let { if (it == 0) "" else it.toString() })
    }
    var receiptMaxAgeDays by remember { mutableStateOf(initial.receiptMaxAgeDays.toString()) }
    var rewardCurrencyName by remember { mutableStateOf(initial.currencyName) }
    var rewardPayoutPercentage by remember {
        mutableStateOf(
            prefs.getFloat(ActivationsActivity.PREF_REWARD_PAYOUT_PERCENTAGE, ActivationsActivity.DEFAULT_PAYOUT_PERCENTAGE.toFloat())
                .let { if (it == ActivationsActivity.DEFAULT_PAYOUT_PERCENTAGE.toFloat()) "" else it.toString() }
        )
    }
    var rewardIconBase64 by remember {
        mutableStateOf(prefs.getString(ActivationsActivity.PREF_REWARD_ICON_BASE64, null))
    }

    val invalidEmailErrorLabel = stringResource(R.string.engage_error_invalid_email)

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
            payoutPercentage = rewardPayoutPercentage,
            onPayoutPercentageChange = { rewardPayoutPercentage = it },
            iconBase64 = rewardIconBase64,
            onIconBase64Change = { rewardIconBase64 = it },
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val trimmedEmail = email.trim()
                if (trimmedEmail.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                    emailError = invalidEmailErrorLabel
                    return@Button
                }
                emailError = null

                prefs.edit()
                    .putString(ActivationsActivity.PREF_EMAIL, trimmedEmail)
                    .putString(ActivationsActivity.PREF_PHONE, phone.trim())
                    .putString(ActivationsActivity.PREF_ENVIRONMENT, environment)
                    .putBoolean(ActivationsActivity.PREF_TEST_ADS, testAds)
                    .putBoolean(ActivationsActivity.PREF_TEST_MODE, testMode)
                    .putBoolean(ActivationsActivity.PREF_SHOW_HEADER, showHeader)
                    .putStringSet(ActivationsActivity.PREF_FORCE_PLACEMENTS, forcePlacements)
                    .putInt(ActivationsActivity.PREF_SCAN_REWARD, scanReward.toIntOrNull() ?: 0)
                    .putInt(ActivationsActivity.PREF_RECEIPT_MAX_AGE_DAYS, receiptMaxAgeDays.toIntOrNull() ?: EngageClient.DEFAULT_RECEIPT_MAX_AGE_DAYS)
                    .putString(ActivationsActivity.PREF_REWARD_CURRENCY_NAME, rewardCurrencyName.trim())
                    .putFloat(ActivationsActivity.PREF_REWARD_PAYOUT_PERCENTAGE, rewardPayoutPercentage.toFloatOrNull() ?: ActivationsActivity.DEFAULT_PAYOUT_PERCENTAGE.toFloat())
                    .putString(ActivationsActivity.PREF_REWARD_ICON_BASE64, rewardIconBase64)
                    .apply()

                try {
                    ActivationsActivity.applySettings(context)
                    Toast.makeText(context, R.string.engage_toast_settings_saved, Toast.LENGTH_SHORT).show()
                    onBack()
                } catch (e: IllegalArgumentException) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.engage_button_save))
        }
    }
}
