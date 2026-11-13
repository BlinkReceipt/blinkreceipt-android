package com.actualplatform.android.activation.development.ui.themes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.actualplatform.activation.theming.colors

/**
 * A [ColorPickerRow] for a `Long?` appearance-color override. `null` shows [default] (the exact
 * color this element currently renders with, per `AppearanceBridge.kt`) labeled "(default)";
 * tapping the swatch sets an explicit override, and the trailing close icon (shown only when
 * overridden) clears it back to `null`.
 */
@Composable
internal fun NullableColorPickerRow(
    label: String,
    value: Long?,
    default: Long,
    onValueChange: (Long?) -> Unit,
) {
    val theme = draftTheme()
    val textSecondary = theme.colors.textSecondary.toComposeColor()

    ColorPickerRow(
        label = label,
        color = value ?: default,
        isDefault = value == null,
        trailingContent = {
            if (value != null) {
                IconButton(onClick = { onValueChange(null) }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Reset $label to default",
                        tint = textSecondary,
                    )
                }
            }
        },
        onColorChange = { onValueChange(it) },
    )
}
