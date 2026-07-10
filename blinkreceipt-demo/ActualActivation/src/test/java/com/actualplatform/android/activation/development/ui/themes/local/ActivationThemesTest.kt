package com.actualplatform.android.activation.development.ui.themes.local

import com.actualplatform.activation.theming.ActivationColorDefaults
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import com.actualplatform.activation.theming.ActivationTheme as ActivationThemeModel

/**
 * Serialization round-trip and legacy-payload tests for the local [ActivationTheme] model, run
 * against the production [themeStorageJson] configuration.
 */
public class ActivationThemesTest {

    @Test
    public fun `verify legacy colors payload missing new tokens decodes with derived defaults`() {
        val palette =
            ActivationColorDefaults.lightColors(
                primary = CUSTOM_PRIMARY,
                surfaceAccent = CUSTOM_SURFACE_ACCENT,
                textAccent = CUSTOM_TEXT_ACCENT,
                textInverse = CUSTOM_TEXT_INVERSE,
            )
        // darkColors == lightColors folds local darkColors to null, matching a legacy payload.
        val model = ActivationThemeModel(lightColors = palette, darkColors = palette)

        // Strip the six new-token keys from "colors" to simulate a payload persisted before
        // the tokens existed.
        val fullJson =
            themeStorageJson
                .parseToJsonElement(themeStorageJson.encodeToString(model.toLocal()))
                .jsonObject
        val legacyColors =
            JsonObject(fullJson.getValue("colors").jsonObject.filterKeys { it !in NEW_TOKEN_KEYS })
        val legacyJson = JsonObject(fullJson + ("colors" to legacyColors))

        val decoded = themeStorageJson.decodeFromString<ActivationTheme>(legacyJson.toString())

        val colors = decoded.colors
        assertEquals(CUSTOM_PRIMARY, colors.primary)
        assertEquals(CUSTOM_PRIMARY, colors.accent)
        assertEquals(CUSTOM_PRIMARY, colors.surfaceBrand)
        assertEquals(CUSTOM_TEXT_INVERSE, colors.textOnPrimary)
        assertEquals(CUSTOM_TEXT_INVERSE, colors.textOnSecondary)
        assertEquals(CUSTOM_SURFACE_ACCENT, colors.tagSurface)
        assertEquals(CUSTOM_TEXT_ACCENT, colors.tagText)
    }

    @Test
    public fun `verify explicit null darkColors decodes to null and toModel folds dark to light`() {
        val decoded = themeStorageJson.decodeFromString<ActivationTheme>("""{"darkColors":null}""")

        assertNull(decoded.darkColors)
        val model = decoded.toModel()
        assertEquals(model.lightColors, model.darkColors)
    }

    @Test
    public fun `verify missing darkColors key decodes to null`() {
        val decoded = themeStorageJson.decodeFromString<ActivationTheme>("{}")

        assertNull(decoded.darkColors)
    }

    @Test
    public fun `verify round trip preserves distinct light and dark palettes including new tokens`() {
        val model =
            ActivationThemeModel(
                lightColors =
                ActivationColorDefaults.lightColors(
                    primary = 0xFF111111L,
                    tagSurface = 0xFF333333L,
                ),
                darkColors = ActivationColorDefaults.darkColors(primary = 0xFF222222L),
            )

        val decoded =
            themeStorageJson.decodeFromString<ActivationTheme>(
                themeStorageJson.encodeToString(model.toLocal()),
            )

        assertEquals(model, decoded.toModel())
    }

    private companion object {
        const val CUSTOM_PRIMARY = 0xFFAABBCCL
        const val CUSTOM_SURFACE_ACCENT = 0xFF112233L
        const val CUSTOM_TEXT_ACCENT = 0xFF445566L
        const val CUSTOM_TEXT_INVERSE = 0xFF778899L
        val NEW_TOKEN_KEYS =
            setOf("accent", "surfaceBrand", "textOnPrimary", "textOnSecondary", "tagSurface", "tagText")
    }
}
