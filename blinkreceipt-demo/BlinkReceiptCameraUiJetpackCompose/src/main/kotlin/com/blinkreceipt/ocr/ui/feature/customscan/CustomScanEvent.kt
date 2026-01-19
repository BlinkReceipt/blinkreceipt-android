package com.blinkreceipt.ocr.ui.feature.customscan

import com.microblink.BitmapResult

sealed class CustomScanEvent {

    data object OnTakePictureError: CustomScanEvent()

    data class OnTakePictureCaptured(
        val result: BitmapResult,
    ): CustomScanEvent()

    data object OnFinishingScan: CustomScanEvent()

    data object OnFinishScanError: CustomScanEvent()

    data class OnFinishScanCompleted(
        val blinkreceiptId: String,
    ): CustomScanEvent()

}
