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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.actualplatform.activation.Activation
import com.actualplatform.activation.theming.ActivationAppearance
import com.actualplatform.activation.theming.colors
import com.actualplatform.android.activation.development.ui.themes.CollapsibleSection
import com.actualplatform.android.activation.development.ui.themes.ErrorModalColorsEditor
import com.actualplatform.android.activation.development.ui.themes.LoadingColorsEditor
import com.actualplatform.android.activation.development.ui.themes.LocalDraftActivationTheme
import com.actualplatform.android.activation.development.ui.themes.MissedEarningsColorsEditor
import com.actualplatform.android.activation.development.ui.themes.OffersWallColorsEditor
import com.actualplatform.android.activation.development.ui.themes.ReceiptSummaryColorsEditor

/**
 * Internal/QA tool to edit [ActivationAppearance] at runtime: per-screen labels and per-element
 * color overrides (one collapsible section per screen scope, mirroring `AppearanceBridge.kt`'s
 * grouping).
 *
 * Blank label fields become `null` so the SDK falls back to its bundled localized default. Color
 * rows follow the same contract: clearing an override (the trailing close icon) sets it back to
 * `null`, and each row's "default" preview is computed by calling the real `AppearanceBridge`
 * function against an empty `Colors()` — so this editor can never drift from what the SDK actually
 * falls back to. Keys not shown in the UI (reserved iOS-parity keys) are preserved unchanged
 * because each scope's whole `Colors` object is seeded from the live appearance and only `copy`d.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CustomizeAppearanceScreen(
    onApply: (ActivationAppearance) -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
) {
    var scanLabel by remember { mutableStateOf("") }
    var scanExtendedLabel by remember { mutableStateOf("") }

    // Per-screen appearance color groups — one state object per `ActivationAppearance` scope.
    var offersWallColors by remember { mutableStateOf(ActivationAppearance.OffersWall.Colors()) }
    var loadingColors by remember { mutableStateOf(ActivationAppearance.Loading.Colors()) }
    var errorModalColors by remember { mutableStateOf(ActivationAppearance.ErrorModal.Colors()) }
    var receiptSummaryColors by remember {
        mutableStateOf(ActivationAppearance.ReceiptSummary.Colors())
    }
    var missedEarningsColors by remember {
        mutableStateOf(ActivationAppearance.MissedEarnings.Colors())
    }

    // `Activation.appearance` may still be the SDK default when this composes — DataStore
    // hydration (kicked off in ActivationActivity.onCreate) reads asynchronously and can land
    // after navigation. Keep re-syncing from it until the user makes a local edit, rather than
    // taking a single `.first()` snapshot that could race hydration and cause Apply to persist
    // (and thus wipe) defaults over a real saved customization.
    var hasLocalEdits by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        snapshotFlow { Activation.appearance }.collect { appearance ->
            if (hasLocalEdits) return@collect
            scanLabel = appearance.offersWall.labels.scanLabel.orEmpty()
            scanExtendedLabel = appearance.offersWall.labels.scanExtendedLabel.orEmpty()
            offersWallColors = appearance.offersWall.colors
            loadingColors = appearance.loading.colors
            errorModalColors = appearance.errorModal.colors
            receiptSummaryColors = appearance.receiptSummary.colors
            missedEarningsColors = appearance.missedEarnings.colors
        }
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
                        hasLocalEdits = true
                        scanLabel = ""
                        scanExtendedLabel = ""
                        offersWallColors = ActivationAppearance.OffersWall.Colors()
                        loadingColors = ActivationAppearance.Loading.Colors()
                        errorModalColors = ActivationAppearance.ErrorModal.Colors()
                        receiptSummaryColors = ActivationAppearance.ReceiptSummary.Colors()
                        missedEarningsColors = ActivationAppearance.MissedEarnings.Colors()
                        onReset()
                    },
                    modifier = Modifier.weight(1f),
                ) { Text("Reset") }

                Button(
                    onClick = {
                        onApply(
                            ActivationAppearance(
                                offersWall = ActivationAppearance.OffersWall(
                                    colors = offersWallColors,
                                    labels = ActivationAppearance.OffersWall.Labels(
                                        scanLabel = scanLabel.ifBlank { null },
                                        scanExtendedLabel = scanExtendedLabel.ifBlank { null },
                                    ),
                                ),
                                loading = ActivationAppearance.Loading(colors = loadingColors),
                                errorModal =
                                ActivationAppearance.ErrorModal(colors = errorModalColors),
                                receiptSummary =
                                ActivationAppearance.ReceiptSummary(
                                    colors = receiptSummaryColors,
                                ),
                                missedEarnings =
                                ActivationAppearance.MissedEarnings(
                                    colors = missedEarningsColors,
                                ),
                            ),
                        )
                    },
                    modifier = Modifier.weight(1f),
                ) { Text("Apply") }
            }
        },
    ) { padding ->
        // `CollapsibleSection`/`ColorPickerRow` paint their chrome from `draftTheme()`; this screen
        // doesn't let you edit the theme, so it provides the live `Activation.theme` non-editably —
        // purely so appearance-color rows preview against the SDK's actual active palette.
        CompositionLocalProvider(LocalDraftActivationTheme provides Activation.theme) {
            val themeColors = Activation.theme.colors

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 8.dp),
            ) {
                CollapsibleSection(title = "Offers Wall", initiallyExpanded = true) {
                    OutlinedTextField(
                        value = scanLabel,
                        onValueChange = {
                            hasLocalEdits = true
                            scanLabel = it
                        },
                        label = { Text("scanLabel — FAB (collapsed)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                    )
                    OutlinedTextField(
                        value = scanExtendedLabel,
                        onValueChange = {
                            hasLocalEdits = true
                            scanExtendedLabel = it
                        },
                        label = { Text("scanExtendedLabel — FAB (extended)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                    )
                    OffersWallColorsEditor(
                        colors = offersWallColors,
                        theme = themeColors,
                        onColorsChange = {
                            hasLocalEdits = true
                            offersWallColors = it
                        },
                    )
                }

                CollapsibleSection(title = "Loading") {
                    LoadingColorsEditor(
                        colors = loadingColors,
                        theme = themeColors,
                        onColorsChange = {
                            hasLocalEdits = true
                            loadingColors = it
                        },
                    )
                }

                CollapsibleSection(title = "Error Modal") {
                    ErrorModalColorsEditor(
                        colors = errorModalColors,
                        theme = themeColors,
                        onColorsChange = {
                            hasLocalEdits = true
                            errorModalColors = it
                        },
                    )
                }

                CollapsibleSection(title = "Receipt Summary") {
                    ReceiptSummaryColorsEditor(
                        colors = receiptSummaryColors,
                        theme = themeColors,
                        onColorsChange = {
                            hasLocalEdits = true
                            receiptSummaryColors = it
                        },
                    )
                }

                CollapsibleSection(title = "Missed Earnings") {
                    MissedEarningsColorsEditor(
                        colors = missedEarningsColors,
                        theme = themeColors,
                        onColorsChange = {
                            hasLocalEdits = true
                            missedEarningsColors = it
                        },
                    )
                }
            }
        }
    }
}
