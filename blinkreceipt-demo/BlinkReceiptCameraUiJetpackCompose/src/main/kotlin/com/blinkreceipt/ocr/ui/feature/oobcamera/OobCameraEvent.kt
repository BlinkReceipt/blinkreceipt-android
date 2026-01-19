package com.blinkreceipt.ocr.ui.feature.oobcamera

sealed class OobCameraEvent {

    data object OnError: OobCameraEvent()

    data object OnCancelled: OobCameraEvent()

    data class OnSuccess(
        val blinkreceiptId: String,
    ): OobCameraEvent()

}