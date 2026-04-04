package com.actualplatform.android.activation.development.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

internal sealed interface ActivationRoute : NavKey {
    @Serializable
    data object Home : ActivationRoute

    @Serializable
    data object OffersWall : ActivationRoute

    @Serializable
    data object SettingsEditor : ActivationRoute
}
