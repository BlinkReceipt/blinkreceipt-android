package com.actualplatform.android.activation.development.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.actualplatform.android.activation.development.R

@Composable
internal fun ScanRewardSection(
    scanReward: String,
    onScanRewardChange: (String) -> Unit,
) {
    Text(stringResource(R.string.activations_section_scan_reward), style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = scanReward,
        onValueChange = { input ->
            // Scan reward is a decimal (RewardPoint); keep the decimal point and allow at most one
            // so values like "10.0" survive editing instead of collapsing to "100".
            val filtered = input.filter { c -> c.isDigit() || c == '.' }
            if (filtered.count { it == '.' } <= 1) {
                onScanRewardChange(filtered)
            }
        },
        label = { Text(stringResource(R.string.activations_label_reward_points)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}
