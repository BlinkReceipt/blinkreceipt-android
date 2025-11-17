package com.blinkreceipt.ocr.ui

import kotlinx.serialization.Serializable

sealed class MainDestinations {
    @Serializable
    data object Home: MainDestinations()

    @Serializable
    data object CustomScan: MainDestinations()

    @Serializable
    data object OobCameraScan: MainDestinations()

    @Serializable
    data class Results(
        val blinkreceiptId: String,
    ): MainDestinations() {
        companion object {
            const val KEY_BLINKRECEIPT_ID = "blinkreceiptId"
        }
    }
}