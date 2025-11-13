package com.blinkreceipt.ocr.ui.feature.camerascan

sealed class CameraScanAction {
    data object TakePicture: CameraScanAction()
    data object FinishScan: CameraScanAction()
}