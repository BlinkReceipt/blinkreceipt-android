package com.blinkreceipt.ocr.ui.feature.camerascan

sealed class CameraScanEvent {

    data object OnTakePictureError: CameraScanEvent()

    data object OnFinishScanError: CameraScanEvent()

    data class OnFinishScanCompleted(
        val blinkreceiptId: String,
    ): CameraScanEvent()

}
