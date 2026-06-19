package com.actualplatform.android.activation.development.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.actualplatform.activation.Activation
import com.actualplatform.activation.theming.ActivationAppearance

/**
 * Internal/QA tool to edit [ActivationAppearance] per-screen labels at runtime. Mirrors
 * [com.actualplatform.android.activation.development.ui.themes.ThemesAndIconEditorScreen] but
 * simplified to a flat list of labeled fields grouped by SDK screen. On Apply it merges the edited
 * labels into the current [ActivationAppearance] via copy; blank fields become `null` so the SDK
 * uses its bundled localized default. Fields not shown in the UI are preserved unchanged.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CustomizeAppearanceScreen(
    onApply: (ActivationAppearance) -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
) {
    val current = Activation.appearance
    var scanLabel by remember { mutableStateOf(current.offersWall.labels.scanLabel.orEmpty()) }
    var scanExtendedLabel by remember {
        mutableStateOf(current.offersWall.labels.scanExtendedLabel.orEmpty())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customize Appearance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        scanLabel = ""
                        scanExtendedLabel = ""
                        onReset()
                    },
                    modifier = Modifier.weight(1f),
                ) { Text("Reset") }

                Button(
                    onClick = {
                        val base = Activation.appearance
                        onApply(
                            base.copy(
                                offersWall = base.offersWall.copy(
                                    labels = base.offersWall.labels.copy(
                                        scanLabel = scanLabel.ifBlank { null },
                                        scanExtendedLabel = scanExtendedLabel.ifBlank { null },
                                    ),
                                ),
                            ),
                        )
                    },
                    modifier = Modifier.weight(1f),
                ) { Text("Apply") }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text(
                text = "Offers Wall",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            OutlinedTextField(
                value = scanLabel,
                onValueChange = { scanLabel = it },
                label = { Text("scanLabel — FAB (collapsed)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            )
            OutlinedTextField(
                value = scanExtendedLabel,
                onValueChange = { scanExtendedLabel = it },
                label = { Text("scanExtendedLabel — FAB (extended)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            )
        }
    }
}
