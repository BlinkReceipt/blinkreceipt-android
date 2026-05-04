package com.actualplatform.android.activation.development.ui

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.actualplatform.activation.Rewards
import com.actualplatform.android.activation.development.R
import com.microblink.core.ScanResults
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    settings: SettingsData,
    lastScanResults: ScanResults?,
    lastScanError: String?,
    totalRewardsEarned: Float,
    rewardEvents: List<Rewards>,
    onOffersWallClick: () -> Unit,
    onScanReceiptClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.activation_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(
                            R.string.activation_nav_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            // Read-only settings summary
            SettingsSummary(settings = settings)

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Button(
                onClick = onOffersWallClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.activation_offers_browser_button))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onScanReceiptClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.activation_scan_receipt))
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = onSettingsClick, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.activation_settings_title))
            }

            // Rewards Earned
            if (rewardEvents.isNotEmpty()) {
                SectionHeader(stringResource(R.string.activation_section_rewards))
                SettingsSummaryRow(
                    stringResource(R.string.activation_label_total_rewards),
                    String.format(Locale.US, "%.2f %s", totalRewardsEarned, settings.currencyName),
                )
                rewardEvents.forEachIndexed { index, reward ->
                    val label = when (reward) {
                        is Rewards.ScanFinished -> "Scan"
                        is Rewards.Promotion -> "Promotion"
                        is Rewards.Boost -> "Boost"
                    }
                    Text(
                        text = "${index + 1}. $label: +${String.format(Locale.US, "%.2f", reward.amount)}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp, bottom = 2.dp),
                    )
                }
            }

            // Scan Results
            ScanResultsPanel(
                scanResults = lastScanResults,
                errorMessage = lastScanError,
            )
        }
    }
}
