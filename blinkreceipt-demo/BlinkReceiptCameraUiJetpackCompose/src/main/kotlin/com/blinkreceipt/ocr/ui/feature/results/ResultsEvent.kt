package com.blinkreceipt.ocr.ui.feature.results

sealed class ResultsEvent {

    data object OnDismiss: ResultsEvent()

}