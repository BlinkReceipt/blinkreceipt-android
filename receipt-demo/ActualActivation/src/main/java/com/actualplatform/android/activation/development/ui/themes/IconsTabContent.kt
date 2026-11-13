package com.actualplatform.android.activation.development.ui.themes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.actualplatform.activation.theming.ActivationIcon
import com.actualplatform.activation.theming.ActivationTheme
import com.actualplatform.activation.theming.colors
import com.actualplatform.android.activation.development.R
import kotlin.random.Random

@Composable
internal fun IconsTabContent(theme: ActivationTheme, onThemeChange: (ActivationTheme) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SubSectionHeader(stringResource(R.string.activations_themes_section_predefined_icons))
        IconPresetGrid(
            icons = theme.icons,
            onIconsChange = { onThemeChange(theme.copy(icons = it)) },
        )
        Spacer(modifier = Modifier.height(16.dp))

        IconPickerRow(
            label = "rewardIcon",
            icon = theme.icons.rewardIcon,
            onChange = { onThemeChange(theme.copy(icons = theme.icons.copy(rewardIcon = it))) },
        )

        Spacer(modifier = Modifier.height(16.dp))

        CollapsibleSection(
            title = stringResource(R.string.activations_themes_section_offers_wall),
            initiallyExpanded = true,
        ) {
            IconPickerRow("backIcon", theme.icons.backIcon) {
                onThemeChange(theme.copy(icons = theme.icons.copy(backIcon = it)))
            }
            IconPickerRow("scanIcon", theme.icons.scanIcon) {
                onThemeChange(theme.copy(icons = theme.icons.copy(scanIcon = it)))
            }
            IconPickerRow("moreStoresIcon", theme.icons.moreStoresIcon) {
                onThemeChange(theme.copy(icons = theme.icons.copy(moreStoresIcon = it)))
            }
            IconPickerRow("moreItemsIcon", theme.icons.moreItemsIcon) {
                onThemeChange(theme.copy(icons = theme.icons.copy(moreItemsIcon = it)))
            }
            IconPickerRow("clipIcon", theme.icons.clipIcon) {
                onThemeChange(theme.copy(icons = theme.icons.copy(clipIcon = it)))
            }
            IconPickerRow("clippedIcon", theme.icons.clippedIcon) {
                onThemeChange(theme.copy(icons = theme.icons.copy(clippedIcon = it)))
            }
            IconPickerRow("defaultStoreIcon", theme.icons.defaultStoreIcon) {
                onThemeChange(theme.copy(icons = theme.icons.copy(defaultStoreIcon = it)))
            }
        }

        CollapsibleSection(title = stringResource(R.string.activations_themes_section_loading)) {}

        CollapsibleSection(title = stringResource(R.string.activations_themes_section_error_modal)) {
            IconPickerRow(
                label = "errorIcon",
                icon = theme.icons.errorIcon,
                onChange = { onThemeChange(theme.copy(icons = theme.icons.copy(errorIcon = it))) },
            )
        }

        CollapsibleSection(title = stringResource(R.string.activations_themes_section_receipt_summary)) {
            IconPickerRow("claimIcon", theme.icons.claimIcon) {
                onThemeChange(theme.copy(icons = theme.icons.copy(claimIcon = it)))
            }
            IconPickerRow("missedEarningsIcon", theme.icons.missedEarningsIcon) {
                onThemeChange(theme.copy(icons = theme.icons.copy(missedEarningsIcon = it)))
            }
            IconPickerRow("boostIcon", theme.icons.boostIcon) {
                onThemeChange(theme.copy(icons = theme.icons.copy(boostIcon = it)))
            }
            IconPickerRow("niceScanIcon", theme.icons.niceScanIcon) {
                onThemeChange(theme.copy(icons = theme.icons.copy(niceScanIcon = it)))
            }
            IconPickerRow("infoIcon", theme.icons.infoIcon) {
                onThemeChange(theme.copy(icons = theme.icons.copy(infoIcon = it)))
            }
        }

        CollapsibleSection(
            title = stringResource(R.string.activations_themes_section_missed_earnings),
        ) {
            IconPickerRow("editMissedEarning", theme.icons.editMissedEarning) {
                onThemeChange(theme.copy(icons = theme.icons.copy(editMissedEarning = it)))
            }
            IconPickerRow("submitMissedEarnings", theme.icons.submitMissedEarnings) {
                onThemeChange(theme.copy(icons = theme.icons.copy(submitMissedEarnings = it)))
            }
            IconPickerRow("addMissedEarnings", theme.icons.addMissedEarnings) {
                onThemeChange(theme.copy(icons = theme.icons.copy(addMissedEarnings = it)))
            }
            IconPickerRow("reportMissedEarnings", theme.icons.reportMissedEarnings) {
                onThemeChange(theme.copy(icons = theme.icons.copy(reportMissedEarnings = it)))
            }
            IconPickerRow("calendarMissedEarnings", theme.icons.calendarMissedEarnings) {
                onThemeChange(theme.copy(icons = theme.icons.copy(calendarMissedEarnings = it)))
            }
            IconPickerRow(
                "submitMissedEarningsSuccess",
                theme.icons.submitMissedEarningsSuccess,
            ) {
                onThemeChange(
                    theme.copy(icons = theme.icons.copy(submitMissedEarningsSuccess = it)),
                )
            }
            IconPickerRow("submitMissedEarningsError", theme.icons.submitMissedEarningsError) {
                onThemeChange(theme.copy(icons = theme.icons.copy(submitMissedEarningsError = it)))
            }
        }
    }
}

private data class IconPreset(val label: String, val seed: Long)

private val iconPresets =
    listOf(IconPreset("Random A", 1L), IconPreset("Random B", 7L), IconPreset("Random C", 42L))

@Composable
private fun IconPresetGrid(
    icons: ActivationTheme.Icons,
    onIconsChange: (ActivationTheme.Icons) -> Unit,
) {
    val active = draftTheme()
    val primary = active.colors.primary.toComposeColor()
    val textInverse = active.colors.textInverse.toComposeColor()
    val textPrimary = active.colors.textPrimary.toComposeColor()
    val surfaceAccent = active.colors.surfaceAccent.toComposeColor()

    // Materialise bytes for every pool entry once. `rememberPickableBytes` returns null
    // until raster reads finish; we gate the click on a full set being available.
    val poolBytes: List<ByteArray?> = pickableIcons.map { rememberPickableBytes(it) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        iconPresets.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { preset ->
                    val presetIcons = buildPresetIcons(preset.seed, poolBytes)
                    val selected = presetIcons != null && icons == presetIcons
                    FilledTonalButton(
                        modifier = Modifier.weight(1f),
                        enabled = presetIcons != null,
                        onClick = { presetIcons?.let(onIconsChange) },
                        shape = RoundedCornerShape(active.shapes.sm),
                        colors =
                        ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (selected) primary else surfaceAccent,
                            contentColor = if (selected) textInverse else textPrimary,
                        ),
                    ) {
                        Text(text = preset.label, style = active.typography.labelLarge)
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

private fun buildPresetIcons(seed: Long, poolBytes: List<ByteArray?>): ActivationTheme.Icons? {
    val rng = Random(seed)
    val picks = List(21) { poolBytes[rng.nextInt(poolBytes.size)] ?: return null }
    fun ic(i: Int) = ActivationIcon.Bytes(picks[i])
    return ActivationTheme.Icons(
        rewardIcon = ic(0),
        backIcon = ic(1),
        scanIcon = ic(2),
        moreStoresIcon = ic(3),
        moreItemsIcon = ic(4),
        clipIcon = ic(5),
        clippedIcon = ic(6),
        defaultStoreIcon = ic(7),
        errorIcon = ic(8),
        claimIcon = ic(9),
        missedEarningsIcon = ic(10),
        boostIcon = ic(11),
        niceScanIcon = ic(12),
        editMissedEarning = ic(13),
        submitMissedEarnings = ic(14),
        addMissedEarnings = ic(15),
        reportMissedEarnings = ic(16),
        calendarMissedEarnings = ic(17),
        submitMissedEarningsSuccess = ic(18),
        submitMissedEarningsError = ic(19),
        infoIcon = ic(20),
    )
}
