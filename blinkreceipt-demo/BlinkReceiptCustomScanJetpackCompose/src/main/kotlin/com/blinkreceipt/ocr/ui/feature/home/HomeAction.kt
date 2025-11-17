package com.blinkreceipt.ocr.ui.feature.home

sealed class HomeAction {

    data object StartCustomScan: HomeAction()

    data object StartOobCameraScan: HomeAction()

    data object DisplayVersion: HomeAction()

}
