package com.actualplatform.android.activation.development.ui.themes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import com.actualplatform.activation.theming.ActivationIcon
import com.actualplatform.activation.theming.colors

private val THUMB_SIZE = 48.dp

@Composable
internal fun IconPickerRow(
    label: String,
    icon: ActivationIcon?,
    onChange: (ActivationIcon?) -> Unit,
) {
    val theme = draftTheme()
    val primary = theme.colors.primary.toComposeColor()
    val textPrimary = theme.colors.textPrimary.toComposeColor()
    var showPicker by remember { mutableStateOf(false) }

    Text(text = label, style = theme.typography.bodyMedium, color = textPrimary)
    Spacer(modifier = Modifier.padding(top = 4.dp))

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        IconThumbnail(icon = icon, contentDescription = label)
        Spacer(modifier = Modifier.width(12.dp))
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = { showPicker = true },
            shape = RoundedCornerShape(theme.shapes.sm),
            border = BorderStroke(1.dp, primary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = primary),
        ) {
            Text("Pick Icon", style = theme.typography.labelLarge)
        }
        if (icon != null) {
            TextButton(
                onClick = { onChange(null) },
                colors = ButtonDefaults.textButtonColors(contentColor = primary),
            ) {
                Text("Clear", style = theme.typography.labelLarge)
            }
        }
    }

    Spacer(modifier = Modifier.padding(bottom = 8.dp))

    if (showPicker) {
        IconPickerDialog(
            onDismiss = { showPicker = false },
            onPick = { bytes ->
                onChange(ActivationIcon.Bytes(bytes))
                showPicker = false
            },
        )
    }
}

@Composable
private fun IconPickerDialog(onDismiss: () -> Unit, onPick: (ByteArray) -> Unit) {
    val theme = draftTheme()
    val primary = theme.colors.primary.toComposeColor()
    val surface = theme.colors.surface.toComposeColor()
    val textPrimary = theme.colors.textPrimary.toComposeColor()
    val border = theme.colors.border.toComposeColor()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = surface,
        titleContentColor = textPrimary,
        textContentColor = textPrimary,
        shape = RoundedCornerShape(theme.shapes.lg),
        title = { Text(text = "Pick Icon", style = theme.typography.titleLarge) },
        text = {
            // Decorative cell radius — stays fixed (not tied to `theme.shapes.unit`).
            val cellShape = RoundedCornerShape(6.dp)
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(pickableIcons) { entry ->
                    val bytes = rememberPickableBytes(entry)
                    val resId = when (entry) {
                        is PickableIcon.Vector -> entry.resId
                        is PickableIcon.Raster -> entry.resId
                    }
                    Box(
                        modifier =
                            Modifier.aspectRatio(1f)
                                .clip(cellShape)
                                .border(1.dp, border, cellShape)
                                .clickable(enabled = bytes != null) { bytes?.let(onPick) }
                                .padding(8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(resId),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = primary),
            ) {
                Text(text = "Cancel", style = theme.typography.labelLarge)
            }
        },
    )
}

@Composable
private fun IconThumbnail(icon: ActivationIcon?, contentDescription: String) {
    val theme = draftTheme()
    val border = theme.colors.border.toComposeColor()
    val emptyBg = theme.colors.surfaceAccent.toComposeColor().copy(alpha = 0.4f)
    // Thumbnail border-radius stays fixed (not tied to `theme.shapes.unit`) — it's decorative.
    val shape = RoundedCornerShape(6.dp)
    val borderModifier = Modifier.size(THUMB_SIZE).clip(shape).border(1.dp, border, shape)

    // Build a local ImageLoader so AsyncImage never touches Coil's singleton. The Activation SDK
    // claims the singleton inside OffersWallFlow via `setSingletonImageLoaderFactory { … }`, and
    // Coil's `SingletonImageLoader.setSafe` throws once the singleton has been resolved by any
    // prior `AsyncImage(model)` call. Using an explicit per-Row loader keeps the SDK's
    // registration path unblocked regardless of how many icons the editor previewed first.
    val context = LocalContext.current
    val imageLoader = remember(context) { ImageLoader.Builder(context).build() }

    when (icon) {
        is ActivationIcon.Bytes ->
            AsyncImage(
                model = icon.data,
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                imageLoader = imageLoader,
                modifier = borderModifier,
            )
        is ActivationIcon.Url ->
            AsyncImage(
                model = icon.url,
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                imageLoader = imageLoader,
                modifier = borderModifier,
            )
        is ActivationIcon.Vector ->
            Image(
                imageVector = icon.imageVector,
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                modifier = borderModifier,
            )
        null -> Box(modifier = borderModifier.background(emptyBg))
    }
}
