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
import com.actualplatform.android.R

@Composable
internal fun ScanRewardSection(
    scanReward: String,
    onScanRewardChange: (String) -> Unit,
) {
    Text(stringResource(R.string.engage_section_scan_reward), style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = scanReward,
        onValueChange = { onScanRewardChange(it.filter { c -> c.isDigit() }) },
        label = { Text(stringResource(R.string.engage_label_reward_points)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}
