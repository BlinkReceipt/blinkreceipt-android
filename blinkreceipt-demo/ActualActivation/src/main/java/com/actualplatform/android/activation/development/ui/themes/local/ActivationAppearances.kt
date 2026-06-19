package com.actualplatform.android.activation.development.ui.themes.local

import kotlinx.serialization.Serializable
import com.actualplatform.activation.theming.ActivationAppearance as ActivationAppearanceModel

@Serializable
internal data class ActivationAppearance(
    val offersWall: OffersWall = OffersWall(),
) {
    @Serializable
    internal data class OffersWall(
        val labels: Labels = Labels(),
    ) {
        @Serializable
        internal data class Labels(
            val scanLabel: String? = null,
            val scanExtendedLabel: String? = null,
        )
    }
}

internal fun ActivationAppearanceModel.toLocal(): ActivationAppearance =
    ActivationAppearance(
        offersWall = ActivationAppearance.OffersWall(
            labels = ActivationAppearance.OffersWall.Labels(
                scanLabel = offersWall.labels.scanLabel,
                scanExtendedLabel = offersWall.labels.scanExtendedLabel,
            ),
        ),
    )

internal fun ActivationAppearance.toModel(): ActivationAppearanceModel =
    ActivationAppearanceModel(
        offersWall = ActivationAppearanceModel.OffersWall(
            labels = ActivationAppearanceModel.OffersWall.Labels(
                scanLabel = offersWall.labels.scanLabel,
                scanExtendedLabel = offersWall.labels.scanExtendedLabel,
            ),
        ),
    )
