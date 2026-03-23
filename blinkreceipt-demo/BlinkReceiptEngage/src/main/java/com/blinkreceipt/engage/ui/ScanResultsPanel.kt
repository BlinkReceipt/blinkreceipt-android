package com.blinkreceipt.engage.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.blinkreceipt.engage.R
import com.google.gson.GsonBuilder
import com.microblink.core.ScanResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val prettyGson by lazy { GsonBuilder().setPrettyPrinting().create() }

@Composable
internal fun ScanResultsPanel(
    scanResults: ScanResults?,
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    if (scanResults == null && errorMessage == null) return

    var json by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(scanResults) {
        json = if (scanResults != null) {
            withContext(Dispatchers.Default) {
                runCatching { prettyGson.toJson(scanResults) }.getOrNull()
            }
        } else {
            null
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        errorMessage?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        scanResults?.let {
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.engage_scan_results_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (json != null) {
                Text(
                    text = json!!,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    text = "Serializing…",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
