package com.blinkreceipt.ocr.ui.feature.home

sealed class HomeEvent {

    data object OnStartCustomScan: HomeEvent()

    data object OnStartOobCameraScan: HomeEvent()

    data class OnDisplayVersion(val versionName: String): HomeEvent()

}