package com.actualplatform.android.activation.development.ui.themes.local

import androidx.compose.ui.text.font.FontFamily
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import com.actualplatform.activation.theming.ActivationIcon as ActivationIconModel
import com.actualplatform.activation.theming.ActivationTheme as ActivationThemeModel

@Serializable
internal data class ActivationTheme(
    val colors: Colors = Colors(),
    val darkColors: Colors? = null,
    val shapes: Shapes = Shapes(),
    val typography: Typography = Typography(),
    val icons: Icons = Icons(),
) {
    @Serializable
    internal data class Colors(
        val primary: Long = 0xFF0062F2L,
        val secondary: Long = 0xFF0062F2L,
        val background: Long = 0xFFFFFFFFL,
        val surface: Long = 0xFFFCFBFAL,
        val surfaceAccent: Long = 0xFFEBF2FEL,
        val surfaceInverse: Long = 0xFF262626L,
        val textPrimary: Long = 0xFF142641L,
        val textSecondary: Long = 0xFF9CA3AFL,
        val textAccent: Long = 0xFF004EC2L,
        val textInverse: Long = 0xFFFFFFFFL,
        val success: Long = 0xFF29CC6AL,
        val error: Long = 0xFFF43F5EL,
        val warning: Long = 0xFFFCA355L,
        val border: Long = 0xFFE5E7EBL,
        // New tokens default to their parent token (mirroring ActivationColorDefaults' cascade)
        // so payloads persisted before these fields existed decode with the values the SDK
        // would derive rather than resetting to a literal.
        val accent: Long = primary,
        val surfaceBrand: Long = primary,
        val textOnPrimary: Long = textInverse,
        val textOnSecondary: Long = textInverse,
        val tagSurface: Long = surfaceAccent,
        val tagText: Long = textAccent,
    )

    @Serializable
    internal data class Shapes(
        val unit: Float = 8f,
        val borderWidthDp: Float = 1f,
    )

    @Serializable
    internal data class Typography(
        val sizeScaleFactor: Float = 1.0f,
        val fontFamily: String? = null,
    )

    @Serializable
    internal data class Icons(
        val rewardIcon: ActivationIcon? = null,
        val backIcon: ActivationIcon? = null,
        val scanIcon: ActivationIcon? = null,
        val moreStoresIcon: ActivationIcon? = null,
        val moreItemsIcon: ActivationIcon? = null,
        val clipIcon: ActivationIcon? = null,
        val clippedIcon: ActivationIcon? = null,
        val defaultStoreIcon: ActivationIcon? = null,
        val errorIcon: ActivationIcon? = null,
        val claimIcon: ActivationIcon? = null,
        val missedEarningsIcon: ActivationIcon? = null,
        val boostIcon: ActivationIcon? = null,
        val niceScanIcon: ActivationIcon? = null,
        val editMissedEarning: ActivationIcon? = null,
        val submitMissedEarnings: ActivationIcon? = null,
        val addMissedEarnings: ActivationIcon? = null,
        val reportMissedEarnings: ActivationIcon? = null,
        val calendarMissedEarnings: ActivationIcon? = null,
        val submitMissedEarningsSuccess: ActivationIcon? = null,
        val submitMissedEarningsError: ActivationIcon? = null,
        val infoIcon: ActivationIcon? = null,
    )
}

private fun ActivationThemeModel.Colors.toLocal(): ActivationTheme.Colors =
    ActivationTheme.Colors(
        primary = primary,
        secondary = secondary,
        background = background,
        surface = surface,
        surfaceAccent = surfaceAccent,
        surfaceInverse = surfaceInverse,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        textAccent = textAccent,
        textInverse = textInverse,
        success = success,
        error = error,
        warning = warning,
        border = border,
        accent = accent,
        surfaceBrand = surfaceBrand,
        textOnPrimary = textOnPrimary,
        textOnSecondary = textOnSecondary,
        tagSurface = tagSurface,
        tagText = tagText,
    )

private fun ActivationTheme.Colors.toModel(): ActivationThemeModel.Colors =
    ActivationThemeModel.Colors(
        primary = primary,
        secondary = secondary,
        background = background,
        surface = surface,
        surfaceAccent = surfaceAccent,
        surfaceInverse = surfaceInverse,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        textAccent = textAccent,
        textInverse = textInverse,
        success = success,
        error = error,
        warning = warning,
        border = border,
        accent = accent,
        surfaceBrand = surfaceBrand,
        textOnPrimary = textOnPrimary,
        textOnSecondary = textOnSecondary,
        tagSurface = tagSurface,
        tagText = tagText,
    )

private fun ActivationThemeModel.Shapes.toLocal(): ActivationTheme.Shapes =
    ActivationTheme.Shapes(unit = unit, borderWidthDp = borderWidthDp)

private fun ActivationTheme.Shapes.toModel(): ActivationThemeModel.Shapes =
    ActivationThemeModel.Shapes(unit = unit, borderWidthDp = borderWidthDp)

private fun ActivationThemeModel.Typography.toLocal(): ActivationTheme.Typography =
    ActivationTheme.Typography(
        sizeScaleFactor = sizeScaleFactor,
        fontFamily = fontFamily?.toString(),
    )

private fun ActivationTheme.Typography.toModel(): ActivationThemeModel.Typography =
    ActivationThemeModel.Typography(
        sizeScaleFactor = sizeScaleFactor,
        fontFamily = fontFamily?.decodeFontFamilyOrNull(),
    )

private fun String.decodeFontFamilyOrNull(): FontFamily? =
    when (this) {
        FontFamily.Serif.toString() -> FontFamily.Serif
        FontFamily.Cursive.toString() -> FontFamily.Cursive
        FontFamily.Monospace.toString() -> FontFamily.Monospace
        FontFamily.SansSerif.toString() -> FontFamily.SansSerif
        FontFamily.Default.toString() -> FontFamily.Default
        else -> null
    }

internal fun ActivationThemeModel.toLocal(): ActivationTheme =
    ActivationTheme(
        colors = lightColors.toLocal(),
        darkColors = darkColors.takeIf { it != lightColors }?.toLocal(),
        shapes = shapes.toLocal(),
        typography = typography.toLocal(),
        icons = ActivationTheme.Icons(
            rewardIcon = icons.rewardIcon?.toLocal(),
            backIcon = icons.backIcon?.toLocal(),
            scanIcon = icons.scanIcon?.toLocal(),
            moreStoresIcon = icons.moreStoresIcon?.toLocal(),
            moreItemsIcon = icons.moreItemsIcon?.toLocal(),
            clipIcon = icons.clipIcon?.toLocal(),
            clippedIcon = icons.clippedIcon?.toLocal(),
            defaultStoreIcon = icons.defaultStoreIcon?.toLocal(),
            errorIcon = icons.errorIcon?.toLocal(),
            claimIcon = icons.claimIcon?.toLocal(),
            missedEarningsIcon = icons.missedEarningsIcon?.toLocal(),
            boostIcon = icons.boostIcon?.toLocal(),
            niceScanIcon = icons.niceScanIcon?.toLocal(),
            editMissedEarning = icons.editMissedEarning?.toLocal(),
            submitMissedEarnings = icons.submitMissedEarnings?.toLocal(),
            addMissedEarnings = icons.addMissedEarnings?.toLocal(),
            reportMissedEarnings = icons.reportMissedEarnings?.toLocal(),
            calendarMissedEarnings = icons.calendarMissedEarnings?.toLocal(),
            submitMissedEarningsSuccess = icons.submitMissedEarningsSuccess?.toLocal(),
            submitMissedEarningsError = icons.submitMissedEarningsError?.toLocal(),
            infoIcon = icons.infoIcon?.toLocal(),
        ),
    )

internal fun ActivationTheme.toModel(): ActivationThemeModel =
    ActivationThemeModel(
        lightColors = colors.toModel(),
        darkColors = darkColors?.toModel() ?: colors.toModel(),
        shapes = shapes.toModel(),
        typography = typography.toModel(),
        icons = ActivationThemeModel.Icons(
            rewardIcon = icons.rewardIcon?.toModel(),
            backIcon = icons.backIcon?.toModel(),
            scanIcon = icons.scanIcon?.toModel(),
            moreStoresIcon = icons.moreStoresIcon?.toModel(),
            moreItemsIcon = icons.moreItemsIcon?.toModel(),
            clipIcon = icons.clipIcon?.toModel(),
            clippedIcon = icons.clippedIcon?.toModel(),
            defaultStoreIcon = icons.defaultStoreIcon?.toModel(),
            errorIcon = icons.errorIcon?.toModel(),
            claimIcon = icons.claimIcon?.toModel(),
            missedEarningsIcon = icons.missedEarningsIcon?.toModel(),
            boostIcon = icons.boostIcon?.toModel(),
            niceScanIcon = icons.niceScanIcon?.toModel(),
            editMissedEarning = icons.editMissedEarning?.toModel(),
            submitMissedEarnings = icons.submitMissedEarnings?.toModel(),
            addMissedEarnings = icons.addMissedEarnings?.toModel(),
            reportMissedEarnings = icons.reportMissedEarnings?.toModel(),
            calendarMissedEarnings = icons.calendarMissedEarnings?.toModel(),
            submitMissedEarningsSuccess = icons.submitMissedEarningsSuccess?.toModel(),
            submitMissedEarningsError = icons.submitMissedEarningsError?.toModel(),
            infoIcon = icons.infoIcon?.toModel(),
        ),
    )

@Serializable
internal sealed class ActivationIcon {
    @Serializable
    @SerialName("bytes")
    internal data class Bytes(val base64: String, val tint: Long? = null) : ActivationIcon()

    @Serializable
    @SerialName("url")
    internal data class Url(val url: String, val tint: Long? = null) : ActivationIcon()
}

@OptIn(ExperimentalEncodingApi::class)
internal fun ActivationIconModel.toLocal(): ActivationIcon? =
    when (this) {
        is ActivationIconModel.Bytes -> ActivationIcon.Bytes(Base64.encode(data), tint)
        is ActivationIconModel.Url -> ActivationIcon.Url(url, tint)
        is ActivationIconModel.Vector -> null // Vector cannot round-trip through JSON; drop on persist.
    }

@OptIn(ExperimentalEncodingApi::class)
internal fun ActivationIcon.toModel(): ActivationIconModel =
    when (this) {
        is ActivationIcon.Bytes -> ActivationIconModel.Bytes(Base64.decode(base64), tint)
        is ActivationIcon.Url -> ActivationIconModel.Url(url, tint)
    }
