package com.blinkreceipt.engage.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

internal sealed interface ActivationsRoute : NavKey {
    @Serializable
    data object Home : ActivationsRoute

    @Serializable
    data object OffersWall : ActivationsRoute

    @Serializable
    data object SettingsEditor : ActivationsRoute
}
