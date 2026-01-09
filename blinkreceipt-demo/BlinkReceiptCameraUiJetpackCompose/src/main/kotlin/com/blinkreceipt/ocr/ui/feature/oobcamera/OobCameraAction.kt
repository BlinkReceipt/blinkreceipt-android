package com.blinkreceipt.ocr.ui.feature.oobcamera

import com.microblink.camera.ui.CameraRecognizerResults

sealed class OobCameraAction {
    data class ReceiveRecognizerResults(
        val results: CameraRecognizerResults,
    ): OobCameraAction()
}
