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
internal fun UiSettingsSection(
    showHeader: Boolean,
    onShowHeaderChange: (Boolean) -> Unit,
) {
    Text(stringResource(R.string.activations_section_ui), style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.activations_offers_show_header), modifier = Modifier.weight(1f))
        Switch(
            checked = showHeader,
            onCheckedChange = onShowHeaderChange,
        )
    }
}
