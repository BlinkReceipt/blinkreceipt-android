package com.actualplatform.android.activation.development.ui.themes.local

import org.junit.Assert.assertEquals
import org.junit.Test
import com.actualplatform.activation.theming.ActivationAppearance as ActivationAppearanceModel

/**
 * Serialization round-trip and legacy-payload tests for the local [ActivationAppearance] model,
 * run against the production [themeStorageJson] configuration.
 */
public class ActivationAppearancesTest {

    @Test
    public fun `verify legacy labels only payload decodes and preserves labels`() {
        val legacy =
            """{"offersWall":{"labels":{"scanLabel":"Scan!","scanExtendedLabel":"Scan a receipt"}}}"""

        val model = themeStorageJson.decodeFromString<ActivationAppearance>(legacy).toModel()

        assertEquals("Scan!", model.offersWall.labels.scanLabel)
        assertEquals("Scan a receipt", model.offersWall.labels.scanExtendedLabel)
        assertEquals(ActivationAppearanceModel.OffersWall.Colors(), model.offersWall.colors)
        assertEquals(ActivationAppearanceModel.Loading.Colors(), model.loading.colors)
        assertEquals(ActivationAppearanceModel.ErrorModal.Colors(), model.errorModal.colors)
        assertEquals(ActivationAppearanceModel.ReceiptSummary.Colors(), model.receiptSummary.colors)
        assertEquals(ActivationAppearanceModel.MissedEarnings.Colors(), model.missedEarnings.colors)
    }

    @Test
    public fun `verify round trip preserves overrides per scope`() {
        val model =
            ActivationAppearanceModel(
                offersWall =
                ActivationAppearanceModel.OffersWall(
                    colors =
                    ActivationAppearanceModel.OffersWall.Colors(
                        offerWallBackground = 0xFF102030L,
                    ),
                    labels = ActivationAppearanceModel.OffersWall.Labels(scanLabel = "Scan!"),
                ),
                loading =
                ActivationAppearanceModel.Loading(
                    colors =
                    ActivationAppearanceModel.Loading.Colors(
                        adLoadingLoadingBarLabel = 0xFF112233L,
                    ),
                ),
                errorModal =
                ActivationAppearanceModel.ErrorModal(
                    colors =
                    ActivationAppearanceModel.ErrorModal.Colors(
                        errorModalBackground = 0xFF223344L,
                    ),
                ),
                receiptSummary =
                ActivationAppearanceModel.ReceiptSummary(
                    colors =
                    ActivationAppearanceModel.ReceiptSummary.Colors(
                        postScanHeaderBackground = 0xFF334455L,
                    ),
                ),
                missedEarnings =
                ActivationAppearanceModel.MissedEarnings(
                    colors =
                    ActivationAppearanceModel.MissedEarnings.Colors(
                        missedEarningsNavigationTitleLabel = 0xFF445566L,
                    ),
                ),
            )

        val decoded =
            themeStorageJson.decodeFromString<ActivationAppearance>(
                themeStorageJson.encodeToString(model.toLocal()),
            )

        assertEquals(model, decoded.toModel())
    }

    @Test
    public fun `verify default appearance round trips to default`() {
        val decoded =
            themeStorageJson.decodeFromString<ActivationAppearance>(
                themeStorageJson.encodeToString(ActivationAppearanceModel().toLocal()),
            )

        assertEquals(ActivationAppearanceModel(), decoded.toModel())
    }
}
