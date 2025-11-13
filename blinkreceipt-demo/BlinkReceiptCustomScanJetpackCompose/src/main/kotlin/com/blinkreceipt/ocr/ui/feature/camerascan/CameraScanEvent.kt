package com.blinkreceipt.ocr.ui.feature.camerascan

import com.microblink.BitmapResult

sealed class CameraScanEvent {

    data object OnTakePictureError: CameraScanEvent()

    data class OnTakePictureCaptured(
        val result: BitmapResult,
    ): CameraScanEvent()

    data object OnFinishScanError: CameraScanEvent()

    data class OnFinishScanCompleted(
        val blinkreceiptId: String,
    ): CameraScanEvent()

}
