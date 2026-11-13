package com.actualplatform.android.activation.development.ui.themes

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.actualplatform.activation.theming.ActivationTheme
import com.actualplatform.activation.theming.colors
import com.actualplatform.android.activation.development.R

// Editor-scoped theme local. Default throws so accidental reads outside `WithDraftTheme`
// surface immediately — same pattern as the SDK's internal `LocalActivationTheme`.
internal val LocalDraftActivationTheme =
    staticCompositionLocalOf<ActivationTheme> {
        error("LocalDraftActivationTheme not provided — wrap content in WithDraftTheme")
    }

@Composable
@ReadOnlyComposable
internal fun draftTheme(): ActivationTheme = LocalDraftActivationTheme.current

internal fun Long.toComposeColor(): Color = Color(this.toInt())

// Shape ramp derivers — mirror the (internal) ThemeBridge.kt naming so the editor reads the
// same scale-driven Dp values the SDK uses for its own surfaces.
internal val ActivationTheme.Shapes.xs: Dp
    get() = (unit * 0.5f).dp

internal val ActivationTheme.Shapes.sm: Dp
    get() = (unit * 1f).dp

internal val ActivationTheme.Shapes.md: Dp
    get() = (unit * 1.5f).dp

internal val ActivationTheme.Shapes.lg: Dp
    get() = (unit * 2f).dp

internal val ActivationTheme.Shapes.xl: Dp
    get() = (unit * 3f).dp

// Material-equivalent typography styles derived from ActivationTheme.Typography. Each multiplies
// fontSize and lineHeight by `sizeScaleFactor` and applies `fontFamily`. Baseline values match
// Material 3 design defaults so visual output stays close to what the editor currently shows when
// no scaling is applied.
private fun ActivationTheme.Typography.textStyle(
    fontSize: TextUnit,
    lineHeight: TextUnit,
    fontWeight: FontWeight,
): TextStyle =
    TextStyle(
        fontFamily = fontFamily,
        fontSize = fontSize * sizeScaleFactor,
        lineHeight = lineHeight * sizeScaleFactor,
        fontWeight = fontWeight,
    )

internal val ActivationTheme.Typography.titleLarge: TextStyle
    get() = textStyle(22.sp, 28.sp, FontWeight.Normal)

internal val ActivationTheme.Typography.titleMedium: TextStyle
    get() = textStyle(16.sp, 24.sp, FontWeight.Medium)

internal val ActivationTheme.Typography.titleSmall: TextStyle
    get() = textStyle(14.sp, 20.sp, FontWeight.Medium)

internal val ActivationTheme.Typography.bodyLarge: TextStyle
    get() = textStyle(16.sp, 24.sp, FontWeight.Normal)

internal val ActivationTheme.Typography.bodyMedium: TextStyle
    get() = textStyle(14.sp, 20.sp, FontWeight.Normal)

internal val ActivationTheme.Typography.bodySmall: TextStyle
    get() = textStyle(12.sp, 16.sp, FontWeight.Normal)

internal val ActivationTheme.Typography.labelLarge: TextStyle
    get() = textStyle(14.sp, 20.sp, FontWeight.Medium)

internal val ActivationTheme.Typography.labelMedium: TextStyle
    get() = textStyle(12.sp, 16.sp, FontWeight.Medium)

internal val ActivationTheme.Typography.labelSmall: TextStyle
    get() = textStyle(11.sp, 16.sp, FontWeight.Medium)

@Composable
internal fun CollapsibleSection(
    title: String,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit,
) {
    val theme = draftTheme()
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(vertical = 12.dp),
        ) {
            Text(
                text = title,
                style = theme.typography.titleMedium,
                color = theme.colors.textPrimary.toComposeColor(),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = if (expanded) "▲" else "▼",
                style = theme.typography.titleMedium,
                color = theme.colors.textSecondary.toComposeColor(),
            )
        }
        HorizontalDivider(color = theme.colors.border.toComposeColor())
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) { content() }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
internal fun SubSectionHeader(title: String) {
    val theme = draftTheme()
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = title,
        style = theme.typography.titleSmall,
        color = theme.colors.primary.toComposeColor(),
    )
    Spacer(modifier = Modifier.height(4.dp))
}

// -----------------------------------------------------------------------------
// Drawable byte-loading helpers shared by the icon-preset code paths.
//
// Vector drawables ship as XML and have to be rasterised to PNG bytes before
// being wrapped in `ActivationIcon.Bytes` (Coil only decodes raster formats).
// Raster drawables (PNG) ship as bytes already, so we just read them out of
// the resource bundle.
// -----------------------------------------------------------------------------

/**
 * Rasterises a vector drawable to PNG bytes so the resulting `ActivationIcon.Bytes` is
 * Coil-decodable and survives JSON serialization with structural equality.
 */
@Composable
internal fun rememberVectorPngBytes(@DrawableRes resId: Int): ByteArray {
    val vector = ImageVector.vectorResource(resId)
    val density = LocalDensity.current
    val painter = rememberVectorPainter(image = vector)
    return remember(vector, density) { rasterizeToPng(vector, painter, density) }
}

private fun rasterizeToPng(vector: ImageVector, painter: Painter, density: Density): ByteArray {
    val sizePx = with(density) { 48.dp.toPx() }.toInt().coerceAtLeast(1)
    val bitmap = ImageBitmap(sizePx, sizePx)
    val canvas = Canvas(bitmap)
    val drawScope = CanvasDrawScope()
    val size = Size(sizePx.toFloat(), sizePx.toFloat())
    drawScope.draw(
        density = density,
        layoutDirection = LayoutDirection.Ltr,
        canvas = canvas,
        size = size,
    ) {
        with(painter) { draw(size = size) }
    }
    // `vector` is referenced solely so the `remember(...)` key picks it up and any
    // future raster cache stays correctly invalidated.
    @Suppress("UNUSED_EXPRESSION") vector
    return bitmap.encodeToPngBytes()
}

/** Reads raw PNG/JPEG bytes for a raster drawable resource. */
@Composable
internal fun rememberRawBytes(@DrawableRes resId: Int): ByteArray? {
    val context = LocalContext.current
    return remember(resId) { context.resources.openRawResource(resId).use { it.readBytes() } }
}

/**
 * One curated drawable entry the editor's icon picker can offer. `Vector` entries get rasterised on
 * demand; `Raster` entries are decoded from the raw resource bytes.
 */
internal sealed class PickableIcon {

    data class Vector(@DrawableRes val resId: Int) : PickableIcon()

    data class Raster(@DrawableRes val resId: Int) : PickableIcon()
}

internal val pickableIcons: List<PickableIcon> =
    listOf(
        PickableIcon.Vector(R.drawable.ic_arrow_back),
        PickableIcon.Vector(R.drawable.ic_arrow_left),
        PickableIcon.Vector(R.drawable.ic_bolt),
        PickableIcon.Vector(R.drawable.ic_btn_clip),
        PickableIcon.Vector(R.drawable.ic_btn_clipped),
        PickableIcon.Vector(R.drawable.ic_camera),
        PickableIcon.Vector(R.drawable.ic_close),
        PickableIcon.Vector(R.drawable.ic_description),
        PickableIcon.Vector(R.drawable.ic_play_ad),
        PickableIcon.Vector(R.drawable.ic_play_arrow),
        PickableIcon.Vector(R.drawable.ic_proceed),
        PickableIcon.Vector(R.drawable.ic_right_arrow),
        PickableIcon.Vector(R.drawable.ic_warning),
        PickableIcon.Vector(R.drawable.more_horiz),
        PickableIcon.Raster(R.drawable.boost_icon),
        PickableIcon.Raster(R.drawable.ic_nice_scan),
        PickableIcon.Raster(R.drawable.ic_store_default),
        PickableIcon.Raster(R.drawable.image_dollar),
    )

@Composable
internal fun rememberPickableBytes(entry: PickableIcon): ByteArray? =
    when (entry) {
        is PickableIcon.Vector -> rememberVectorPngBytes(entry.resId)
        is PickableIcon.Raster -> rememberRawBytes(entry.resId)
    }
