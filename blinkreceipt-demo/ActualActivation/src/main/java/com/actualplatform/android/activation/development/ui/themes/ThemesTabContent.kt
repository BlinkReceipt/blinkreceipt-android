package com.actualplatform.android.activation.development.ui.themes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.actualplatform.activation.theming.ActivationColorDefaults
import com.actualplatform.activation.theming.ActivationTheme
import com.actualplatform.activation.theming.colors
import com.actualplatform.android.activation.development.R
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThemesTabContent(theme: ActivationTheme, onThemeChange: (ActivationTheme) -> Unit) {
    // `draftTheme()` is the dark-folded resolved theme provided by WithDraftTheme; we use it to
    // paint the editor chrome. The `theme` parameter is the still-mutating draft and is what we
    // write back via `onThemeChange`.
    val active = draftTheme()
    val primary = active.colors.primary.toComposeColor()
    val textPrimary = active.colors.textPrimary.toComposeColor()
    val textSecondary = active.colors.textSecondary.toComposeColor()
    val textInverse = active.colors.textInverse.toComposeColor()
    val textOnPrimary = active.colors.textOnPrimary.toComposeColor()
    val surface = active.colors.surface.toComposeColor()
    val border = active.colors.border.toComposeColor()

    Column(modifier = Modifier.fillMaxWidth()) {
        SubSectionHeader(stringResource(R.string.activations_themes_section_predefined_themes))
        ThemePresetGrid(theme = theme, onThemeChange = onThemeChange)
        Spacer(modifier = Modifier.height(16.dp))

        SubSectionHeader(stringResource(R.string.activations_themes_section_font))
        ThemeFontPicker(theme, onThemeChange)
        Spacer(modifier = Modifier.height(16.dp))

        SubSectionHeader(stringResource(R.string.activations_themes_section_colors))

        val darkEnabled = theme.darkColors != theme.lightColors
        var editingDark by remember(darkEnabled) { mutableStateOf(false) }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.activations_themes_label_override_dark),
                style = active.typography.bodyLarge,
                color = textPrimary,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = darkEnabled,
                onCheckedChange = { enabled ->
                    onThemeChange(
                        theme.copy(
                            darkColors =
                            if (enabled) ActivationColorDefaults.darkColors() else theme.lightColors,
                        ),
                    )
                },
                colors =
                SwitchDefaults.colors(
                    checkedThumbColor = textInverse,
                    checkedTrackColor = primary,
                    checkedBorderColor = primary,
                    uncheckedThumbColor = textInverse,
                    uncheckedTrackColor = textSecondary,
                    uncheckedBorderColor = textSecondary,
                ),
            )
        }

        if (darkEnabled) {
            Spacer(modifier = Modifier.height(8.dp))
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                val segmentColors =
                    SegmentedButtonDefaults.colors(
                        activeContainerColor = primary,
                        activeContentColor = textOnPrimary,
                        activeBorderColor = primary,
                        inactiveContainerColor = surface,
                        inactiveContentColor = textPrimary,
                        inactiveBorderColor = border,
                    )
                SegmentedButton(
                    selected = !editingDark,
                    onClick = { editingDark = false },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    colors = segmentColors,
                ) {
                    Text(
                        text = stringResource(R.string.activations_themes_label_light),
                        style = active.typography.labelLarge,
                    )
                }
                SegmentedButton(
                    selected = editingDark,
                    onClick = { editingDark = true },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    colors = segmentColors,
                ) {
                    Text(
                        text = stringResource(R.string.activations_themes_label_dark),
                        style = active.typography.labelLarge,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val editingColors = if (editingDark) theme.darkColors else theme.lightColors
        ColorsEditor(
            colors = editingColors,
            onColorsChange = { updated ->
                onThemeChange(
                    if (editingDark) {
                        theme.copy(darkColors = updated)
                    } else {
                        theme.copy(lightColors = updated)
                    },
                )
            },
        )

        Spacer(modifier = Modifier.height(16.dp))
        SubSectionHeader(stringResource(R.string.activations_themes_section_shapes))
        ShapesEditor(theme.shapes) { onThemeChange(theme.copy(shapes = it)) }

        Spacer(modifier = Modifier.height(16.dp))
        SubSectionHeader(stringResource(R.string.activations_themes_section_typography))
        TypographyEditor(theme.typography) { onThemeChange(theme.copy(typography = it)) }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ColorsEditor(
    colors: ActivationTheme.Colors,
    onColorsChange: (ActivationTheme.Colors) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        ColorPickerRow("primary", colors.primary) { onColorsChange(colors.copy(primary = it)) }
        ColorPickerRow("accent", colors.accent) { onColorsChange(colors.copy(accent = it)) }
        ColorPickerRow("secondary", colors.secondary) {
            onColorsChange(colors.copy(secondary = it))
        }
        ColorPickerRow("background", colors.background) {
            onColorsChange(colors.copy(background = it))
        }
        ColorPickerRow("surface", colors.surface) { onColorsChange(colors.copy(surface = it)) }
        ColorPickerRow("surfaceAccent", colors.surfaceAccent) {
            onColorsChange(colors.copy(surfaceAccent = it))
        }
        ColorPickerRow("surfaceBrand", colors.surfaceBrand) {
            onColorsChange(colors.copy(surfaceBrand = it))
        }
        ColorPickerRow("surfaceInverse", colors.surfaceInverse) {
            onColorsChange(colors.copy(surfaceInverse = it))
        }
        ColorPickerRow("textPrimary", colors.textPrimary) {
            onColorsChange(colors.copy(textPrimary = it))
        }
        ColorPickerRow("textSecondary", colors.textSecondary) {
            onColorsChange(colors.copy(textSecondary = it))
        }
        ColorPickerRow("textAccent", colors.textAccent) {
            onColorsChange(colors.copy(textAccent = it))
        }
        ColorPickerRow("textInverse", colors.textInverse) {
            onColorsChange(colors.copy(textInverse = it))
        }
        ColorPickerRow("textOnPrimary", colors.textOnPrimary) {
            onColorsChange(colors.copy(textOnPrimary = it))
        }
        ColorPickerRow("textOnSecondary", colors.textOnSecondary) {
            onColorsChange(colors.copy(textOnSecondary = it))
        }
        ColorPickerRow("success", colors.success) { onColorsChange(colors.copy(success = it)) }
        ColorPickerRow("error", colors.error) { onColorsChange(colors.copy(error = it)) }
        ColorPickerRow("warning", colors.warning) { onColorsChange(colors.copy(warning = it)) }
        ColorPickerRow("border", colors.border) { onColorsChange(colors.copy(border = it)) }
        ColorPickerRow("tagSurface", colors.tagSurface) {
            onColorsChange(colors.copy(tagSurface = it))
        }
        ColorPickerRow("tagText", colors.tagText) { onColorsChange(colors.copy(tagText = it)) }
    }
}

@Composable
private fun ShapesEditor(
    shapes: ActivationTheme.Shapes,
    onShapesChange: (ActivationTheme.Shapes) -> Unit,
) {
    val theme = draftTheme()
    val primary = theme.colors.primary.toComposeColor()
    val textPrimary = theme.colors.textPrimary.toComposeColor()
    val textSecondary = theme.colors.textSecondary.toComposeColor()
    val border = theme.colors.border.toComposeColor()
    var borderText by
        remember(shapes.borderWidthDp) { mutableStateOf(shapes.borderWidthDp.toString()) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "unit = ${shapes.unit.roundToInt()} dp",
            style = theme.typography.bodySmall,
            color = textSecondary,
        )
        Slider(
            value = shapes.unit,
            onValueChange = { onShapesChange(shapes.copy(unit = it)) },
            valueRange = 0f..32f,
            steps = 31,
            colors =
            SliderDefaults.colors(
                thumbColor = primary,
                activeTrackColor = primary,
                inactiveTrackColor = primary.copy(alpha = 0.24f),
            ),
        )
        Text(
            text = stringResource(R.string.activations_themes_section_shapes_instruction),
            style = theme.typography.bodySmall,
            color = textSecondary,
        )
        ShapePreviewRow(shapes)
        OutlinedTextField(
            value = borderText,
            onValueChange = { input ->
                val filtered = input.filter { c -> c.isDigit() || c == '.' }
                borderText = filtered
                filtered.toFloatOrNull()?.let { onShapesChange(shapes.copy(borderWidthDp = it)) }
            },
            label = {
                Text(
                    text = "borderWidthDp",
                    style = theme.typography.bodyMedium,
                    color = textSecondary,
                )
            },
            textStyle = theme.typography.bodyMedium,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(theme.shapes.sm),
            colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primary,
                unfocusedBorderColor = border,
                focusedLabelColor = primary,
                unfocusedLabelColor = textSecondary,
                cursorColor = primary,
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary,
            ),
        )
    }
}

@Composable
private fun ShapePreviewRow(shapes: ActivationTheme.Shapes) {
    val theme = draftTheme()
    val primary = theme.colors.primary.toComposeColor()
    val textSecondary = theme.colors.textSecondary.toComposeColor()
    data class Step(val label: String, val multiplier: Float, val isPill: Boolean = false)
    val steps =
        listOf(
            Step("xs", 0.5f),
            Step("sm", 1f),
            Step("md", 1.5f),
            Step("lg", 2f),
            Step("xl", 3f),
            Step("pill", 0f, isPill = true),
        )
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Bottom) {
        steps.forEach { step ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val shape =
                    if (step.isPill) {
                        CircleShape
                    } else {
                        RoundedCornerShape((shapes.unit * step.multiplier).dp)
                    }
                Box(modifier = Modifier.size(48.dp).background(primary, shape))
                Spacer(Modifier.height(4.dp))
                Text(text = step.label, style = theme.typography.labelSmall, color = textSecondary)
            }
        }
    }
}

@Composable
private fun TypographyEditor(
    typography: ActivationTheme.Typography,
    onTypographyChange: (ActivationTheme.Typography) -> Unit,
) {
    val theme = draftTheme()
    val primary = theme.colors.primary.toComposeColor()
    val textSecondary = theme.colors.textSecondary.toComposeColor()
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "sizeScaleFactor = ${typography.sizeScaleFactor.formatTwoDecimals()}",
            style = theme.typography.bodySmall,
            color = textSecondary,
        )
        Slider(
            value = typography.sizeScaleFactor,
            onValueChange = { onTypographyChange(typography.copy(sizeScaleFactor = it)) },
            valueRange = 0.6f..1.6f,
            steps = 19,
            colors =
            SliderDefaults.colors(
                thumbColor = primary,
                activeTrackColor = primary,
                inactiveTrackColor = primary.copy(alpha = 0.24f),
            ),
        )
        Text(
            text = "Multiplied into every text style's font size. 1.0 = design defaults.",
            style = theme.typography.bodySmall,
            color = textSecondary,
        )
        TypographyRampPreview()
    }
}

@Composable
private fun TypographyRampPreview() {
    val theme = draftTheme()
    val scale = theme.typography.sizeScaleFactor
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(theme.shapes.md),
        colors =
        CardDefaults.cardColors(
            containerColor = theme.colors.surfaceAccent.toComposeColor(),
            contentColor = theme.colors.textSecondary.toComposeColor(),
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            typographyRamp.forEach { sample ->
                Text(
                    text = "${sample.name} — The quick brown fox",
                    style =
                    theme.typography.bodyLarge.copy(
                        fontSize = (sample.baseSize * scale).sp,
                        lineHeight = (sample.baseLine * scale).sp,
                        fontWeight = sample.weight,
                    ),
                )
                Spacer(Modifier.height(2.dp))
            }
        }
    }
}

private data class ThemePreset(val label: String, val build: () -> ActivationTheme)

private val themePresets =
    listOf(
        ThemePreset("Default") { ActivationTheme() },
        ThemePreset("Magenta + Lime") {
            val colors =
                ActivationColorDefaults.lightColors(
                    primary = 0xFFFF0080,
                    secondary = 0xFFFF8000,
                    background = 0xFF9400D3,
                    surface = 0xFFFFE4E1,
                    surfaceAccent = 0xFFFFD700,
                    surfaceInverse = 0xFF8B0000,
                    textPrimary = 0xFF4B0082,
                    textSecondary = 0xFF800080,
                    textAccent = 0xFFFF1493,
                    textInverse = 0xFFFFFF00,
                    success = 0xFFFF00FF,
                    error = 0xFF00FF00,
                    warning = 0xFF00BFFF,
                    border = 0xFFFF6347,
                )
            ActivationTheme(lightColors = colors, darkColors = colors)
        },
        ThemePreset("Cyan + Neon Dark") {
            ActivationTheme(
                lightColors =
                ActivationColorDefaults.lightColors(
                    primary = 0xFF00CED1,
                    secondary = 0xFFFFD700,
                    background = 0xFF2F4F4F,
                    surface = 0xFFFFFACD,
                    surfaceAccent = 0xFFAFEEEE,
                    surfaceInverse = 0xFF800080,
                    textPrimary = 0xFF8B4513,
                    textSecondary = 0xFFD2691E,
                    textAccent = 0xFFFF4500,
                    textInverse = 0xFFADFF2F,
                    success = 0xFFFF1493,
                    error = 0xFF00FFFF,
                    warning = 0xFFBA55D3,
                    border = 0xFF8A2BE2,
                ),
                darkColors =
                ActivationColorDefaults.darkColors(
                    primary = 0xFF00FF7F,
                    secondary = 0xFFFF00FF,
                    background = 0xFF000000,
                    surface = 0xFF1A0033,
                    surfaceAccent = 0xFF330066,
                    surfaceInverse = 0xFFFFE4B5,
                    textPrimary = 0xFFFFD700,
                    textSecondary = 0xFF7FFFD4,
                    textAccent = 0xFFFFA500,
                    textInverse = 0xFF4B0082,
                    success = 0xFFFF6347,
                    error = 0xFF40E0D0,
                    warning = 0xFF9370DB,
                    border = 0xFFADFF2F,
                ),
            )
        },
    )

@Composable
private fun ThemePresetGrid(theme: ActivationTheme, onThemeChange: (ActivationTheme) -> Unit) {
    val active = draftTheme()
    val primary = active.colors.primary.toComposeColor()
    val textOnPrimary = active.colors.textOnPrimary.toComposeColor()
    val textPrimary = active.colors.textPrimary.toComposeColor()
    val surfaceAccent = active.colors.surfaceAccent.toComposeColor()
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        themePresets.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { preset ->
                    val presetTheme = preset.build()
                    val selected = theme == presetTheme
                    FilledTonalButton(
                        modifier = Modifier.weight(1f),
                        onClick = { onThemeChange(presetTheme) },
                        shape = RoundedCornerShape(active.shapes.sm),
                        colors =
                        ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (selected) primary else surfaceAccent,
                            contentColor = if (selected) textOnPrimary else textPrimary,
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

private data class ThemeFontOption(val label: String, val family: FontFamily?)

private val themeFontOptions =
    listOf(
        ThemeFontOption("System", null),
        ThemeFontOption("Serif", FontFamily.Serif),
        ThemeFontOption("Sans-Serif", FontFamily.SansSerif),
        ThemeFontOption("Monospace", FontFamily.Monospace),
        ThemeFontOption("Cursive", FontFamily.Cursive),
    )

@Composable
private fun ThemeFontPicker(theme: ActivationTheme, onThemeChange: (ActivationTheme) -> Unit) {
    val active = draftTheme()
    val primary = active.colors.primary.toComposeColor()
    val textOnPrimary = active.colors.textOnPrimary.toComposeColor()
    val textPrimary = active.colors.textPrimary.toComposeColor()
    val surfaceAccent = active.colors.surfaceAccent.toComposeColor()
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        themeFontOptions.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { option ->
                    val selected =
                        theme.typography.fontFamily?.toString() == option.family?.toString()
                    FilledTonalButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onThemeChange(
                                theme.copy(
                                    typography = theme.typography.copy(fontFamily = option.family),
                                ),
                            )
                        },
                        shape = RoundedCornerShape(active.shapes.sm),
                        colors =
                        ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (selected) primary else surfaceAccent,
                            contentColor = if (selected) textOnPrimary else textPrimary,
                        ),
                    ) {
                        Text(text = option.label, style = active.typography.labelLarge)
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

private data class TypographyRampSample(
    val name: String,
    val baseSize: Float,
    val baseLine: Float,
    val weight: FontWeight,
)

private val typographyRamp =
    listOf(
        TypographyRampSample("Display L", 57f, 64f, FontWeight.Normal),
        TypographyRampSample("Headline M", 28f, 36f, FontWeight.Normal),
        TypographyRampSample("Title L", 22f, 28f, FontWeight.Normal),
        TypographyRampSample("Body L", 16f, 24f, FontWeight.Normal),
        TypographyRampSample("Body M", 14f, 20f, FontWeight.Normal),
        TypographyRampSample("Label M", 12f, 16f, FontWeight.Medium),
    )

private fun Float.formatTwoDecimals(): String {
    val rounded = (this * 100f).roundToInt() / 100f
    return rounded.toString()
}
