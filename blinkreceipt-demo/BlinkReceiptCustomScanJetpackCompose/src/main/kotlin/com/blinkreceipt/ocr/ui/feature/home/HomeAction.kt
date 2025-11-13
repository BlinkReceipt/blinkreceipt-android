package com.blinkreceipt.ocr.ui.feature.home

sealed class HomeAction {

    data object StartCamera: HomeAction()

    data object DisplayVersion: HomeAction()

}
