package com.blinkreceipt.ocr.ui.feature.camerascan

data class CameraScanUiState(
    val isCapturingPicture: Boolean = false,
    val isProcessingScanResults: Boolean = false,
)