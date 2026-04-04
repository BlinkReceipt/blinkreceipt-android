package com.actualplatform.android.activation.development.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.actualplatform.android.activation.development.R

@Composable
internal fun TestOptionsSection(
    testAds: Boolean,
    onTestAdsChange: (Boolean) -> Unit,
    testMode: Boolean,
    onTestModeChange: (Boolean) -> Unit,
) {
    Text(stringResource(R.string.engage_section_test_options), style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.engage_label_test_ads), modifier = Modifier.weight(1f))
        Switch(
            checked = testAds,
            onCheckedChange = onTestAdsChange,
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.engage_label_test_mode), modifier = Modifier.weight(1f))
        Switch(
            checked = testMode,
            onCheckedChange = onTestModeChange,
        )
    }
}
