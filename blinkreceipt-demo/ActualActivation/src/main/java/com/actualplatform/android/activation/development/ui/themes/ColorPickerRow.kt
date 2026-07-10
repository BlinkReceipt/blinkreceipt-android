package com.actualplatform.android.activation.development.ui.themes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.actualplatform.activation.theming.colors
import com.actualplatform.android.activation.development.R
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
internal fun ColorPickerRow(
    label: String,
    color: Long,
    isDefault: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null,
    onColorChange: (Long) -> Unit,
) {
    val theme = draftTheme()
    val textPrimary = theme.colors.textPrimary.toComposeColor()
    val textSecondary = theme.colors.textSecondary.toComposeColor()
    val border = theme.colors.border.toComposeColor()
    var dialogOpen by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { dialogOpen = true }.padding(vertical = 6.dp),
    ) {
        // Swatch shape stays fixed (decorative, not tied to `theme.shapes.unit`).
        val swatchShape = RoundedCornerShape(4.dp)
        Box(
            modifier =
            Modifier.size(28.dp)
                .clip(swatchShape)
                .background(Color(color.toInt()))
                .border(width = 1.dp, color = border, shape = swatchShape),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = theme.typography.bodyMedium, color = textPrimary)
            Text(
                text = if (isDefault) "${formatHex(color)} (default)" else formatHex(color),
                style = theme.typography.bodySmall,
                color = textSecondary,
            )
        }
        trailingContent?.invoke()
    }

    if (dialogOpen) {
        ColorPickerDialog(
            initialColor = color,
            onDismiss = { dialogOpen = false },
            onColorChange = onColorChange,
        )
    }
}

@Composable
internal fun ColorPickerDialog(
    initialColor: Long,
    onDismiss: () -> Unit,
    onColorChange: (Long) -> Unit,
) {
    val theme = draftTheme()
    val primary = theme.colors.primary.toComposeColor()
    val surface = theme.colors.surface.toComposeColor()
    val textPrimary = theme.colors.textPrimary.toComposeColor()
    val textSecondary = theme.colors.textSecondary.toComposeColor()
    val border = theme.colors.border.toComposeColor()
    val errorColor = theme.colors.error.toComposeColor()
    val controller = rememberColorPickerController()
    var currentColor by remember { mutableLongStateOf(initialColor) }
    var hexInput by remember { mutableStateOf(formatHex(initialColor)) }
    var hexInputError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = surface,
        titleContentColor = textPrimary,
        textContentColor = textPrimary,
        shape = RoundedCornerShape(theme.shapes.lg),
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = primary),
            ) {
                Text(
                    text = stringResource(R.string.activations_themes_button_done),
                    style = theme.typography.labelLarge,
                )
            }
        },
        text = {
            Column {
                HsvColorPicker(
                    modifier = Modifier.fillMaxWidth().height(280.dp),
                    controller = controller,
                    initialColor = Color(initialColor.toInt()),
                    onColorChanged = { envelope ->
                        if (envelope.fromUser) {
                            val newColor = envelope.color.toArgb().toLong() and 0xFFFFFFFFL
                            currentColor = newColor
                            hexInput = formatHex(newColor)
                            hexInputError = false
                            onColorChange(newColor)
                        }
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                AlphaSlider(
                    modifier = Modifier.fillMaxWidth().height(28.dp),
                    controller = controller,
                )
                Spacer(modifier = Modifier.height(8.dp))
                BrightnessSlider(
                    modifier = Modifier.fillMaxWidth().height(28.dp),
                    controller = controller,
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Swatch shape stays fixed (decorative, not tied to `theme.shapes.unit`).
                    val swatchShape = RoundedCornerShape(4.dp)
                    Box(
                        modifier =
                        Modifier.size(36.dp)
                            .clip(swatchShape)
                            .background(Color(currentColor.toInt()))
                            .border(width = 1.dp, color = border, shape = swatchShape),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = hexInput,
                        onValueChange = { input ->
                            hexInput = input
                            val parsed = parseHex(input)
                            if (parsed != null) {
                                hexInputError = false
                                currentColor = parsed
                                controller.selectByColor(Color(parsed.toInt()), fromUser = false)
                                onColorChange(parsed)
                            } else {
                                hexInputError = true
                            }
                        },
                        label = {
                            Text(
                                text = "Hex",
                                style = theme.typography.bodyMedium,
                                color = textSecondary,
                            )
                        },
                        textStyle = theme.typography.bodyMedium,
                        isError = hexInputError,
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions =
                        androidx.compose.foundation.text.KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                        ),
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
                            errorBorderColor = errorColor,
                            errorLabelColor = errorColor,
                            errorTextColor = textPrimary,
                        ),
                    )
                }
            }
        },
    )
}

internal fun formatHex(color: Long): String =
    "#" + (color and 0xFFFFFFFFL).toString(16).uppercase().padStart(8, '0')

internal fun parseHex(input: String): Long? {
    val cleaned = input.trim().removePrefix("#")
    if (cleaned.length != 6 && cleaned.length != 8) return null
    return runCatching {
        val parsed = cleaned.toLong(16)
        if (cleaned.length == 6) 0xFF000000L or parsed else parsed
    }
        .getOrNull()
}
