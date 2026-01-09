package com.blinkreceipt.ocr.ui.feature.customscan

sealed class CustomScanAction {
    data object TakePicture: CustomScanAction()
    data object FinishScan: CustomScanAction()
}