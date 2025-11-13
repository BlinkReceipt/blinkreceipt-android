package com.blinkreceipt.ocr.ui.feature.home

sealed class HomeEvent {

    data object OnStartCamera: HomeEvent()

    data class OnDisplayVersion(val versionName: String): HomeEvent()

}