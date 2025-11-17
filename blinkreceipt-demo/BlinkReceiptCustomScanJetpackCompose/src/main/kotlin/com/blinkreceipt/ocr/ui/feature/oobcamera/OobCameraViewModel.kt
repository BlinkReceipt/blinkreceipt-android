package com.blinkreceipt.ocr.ui.feature.oobcamera

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blinkreceipt.ocr.models.toProductItems
import com.blinkreceipt.ocr.ui.feature.home.data.ProductItemsFileService
import com.microblink.camera.ui.CameraRecognizerResults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OobCameraViewModel @Inject constructor(
    private val productItemsFileService: ProductItemsFileService,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val _events: MutableSharedFlow<OobCameraEvent> = MutableSharedFlow()
    val events: SharedFlow<OobCameraEvent> = _events.asSharedFlow()

    fun handleAction(action: OobCameraAction) {
        viewModelScope.launch {
            when (action) {
                is OobCameraAction.ReceiveRecognizerResults -> {
                    when (val results = action.results) {
                        is CameraRecognizerResults.Finishing -> {
                            savedStateHandle[KEY_IS_PROCESSING_SCAN_RESULTS] = true
                            _events.emit(OobCameraEvent.OnFinishing)
                        }
                        is CameraRecognizerResults.Success -> {
                            runCatching {
                                val scanResults = requireNotNull(results.results) {
                                    "Scan results cannot be null"
                                }
                                val blinkreceiptId = scanResults.blinkReceiptId()
                                productItemsFileService.store(
                                    blinkReceiptId = blinkreceiptId,
                                    productItems = scanResults.toProductItems(),
                                )

                                _events.emit(
                                    OobCameraEvent.OnSuccess(blinkreceiptId)
                                )
                            }.onFailure {
                                it.printStackTrace()
                                _events.emit(
                                    OobCameraEvent.OnError
                                )
                            }

                            savedStateHandle[KEY_IS_PROCESSING_SCAN_RESULTS] = false
                        }
                        is CameraRecognizerResults.Exception -> {
                            savedStateHandle[KEY_IS_PROCESSING_SCAN_RESULTS] = false
                        }
                        is CameraRecognizerResults.Cancelled -> {
                            savedStateHandle[KEY_IS_PROCESSING_SCAN_RESULTS] = false
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val KEY_IS_PROCESSING_SCAN_RESULTS = "isProcessingScanResults"
    }

}