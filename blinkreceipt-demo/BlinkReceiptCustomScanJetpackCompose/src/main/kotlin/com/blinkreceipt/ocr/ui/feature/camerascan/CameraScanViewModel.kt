package com.blinkreceipt.ocr.ui.feature.camerascan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blinkreceipt.ocr.ui.feature.home.data.ProductItemsFileService
import com.blinkreceipt.ocr.ui.feature.results.models.toProductItems
import com.microblink.BitmapResult
import com.microblink.CameraCaptureListener
import com.microblink.CameraRecognizerCallback
import com.microblink.Media
import com.microblink.RecognizerResult
import com.microblink.core.ScanResults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CameraScanViewModel @Inject constructor(
    private val productItemsFileService: ProductItemsFileService,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val _isCapturingPicture = savedStateHandle.getStateFlow(KEY_IS_CAPTURING_PICTURE, false)
    private val _isProcessingScanResults = savedStateHandle.getStateFlow(KEY_IS_PROCESSING_SCAN_RESULTS, false)

    val uiState = combine(
        _isCapturingPicture,
        _isProcessingScanResults,
    ) { isCapturingPicture, isProcessingScanResults ->
        CameraScanUiState(
            isCapturingPicture = isCapturingPicture,
            isProcessingScanResults = isProcessingScanResults,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CameraScanUiState(),
    )

    private val _events = MutableSharedFlow<CameraScanEvent>()
    val events: SharedFlow<CameraScanEvent> = _events.asSharedFlow()

    val recognizerCallback = object: CameraRecognizerCallback {
        override fun onConfirmPicture(p0: File) {
            // ...
        }

        override fun onPermissionDenied() {
            viewModelScope.launch {
                _events.emit(CameraScanEvent.OnTakePictureError)
            }
        }

        override fun onPreviewStarted() {
            // ...
        }

        override fun onPreviewStopped() {
            // ...
        }

        override fun onException(p0: Throwable) {
            viewModelScope.launch {
                _events.emit(CameraScanEvent.OnTakePictureError)
            }
        }

        override fun onRecognizerDone(
            p0: ScanResults,
            p1: Media
        ) {
            viewModelScope.launch {
                savedStateHandle[KEY_IS_PROCESSING_SCAN_RESULTS] = false

                val blinkreceiptId = p0.blinkReceiptId()
                runCatching {
                    productItemsFileService.store(
                        blinkReceiptId = blinkreceiptId,
                        productItems = p0.toProductItems(),
                    )

                    _events.emit(
                        CameraScanEvent.OnFinishScanCompleted(blinkreceiptId)
                    )
                }.onFailure {
                    it.printStackTrace()
                    _events.emit(
                        CameraScanEvent.OnFinishScanError
                    )
                }
            }
        }

        override fun onRecognizerException(p0: Throwable) {
            viewModelScope.launch {
                _events.emit(CameraScanEvent.OnFinishScanError)
            }
        }

        override fun onRecognizerResultsChanged(p0: RecognizerResult) {
            // ...
        }
    }

    val takePictureCallback = object: CameraCaptureListener {
        override fun onCaptured(p0: BitmapResult) {
            // TODO::
        }

        override fun onException(p0: Throwable) {
            viewModelScope.launch {
                _events.emit(CameraScanEvent.OnTakePictureError)
            }
        }
    }

    fun handleAction(action: CameraScanAction) {
        viewModelScope.launch {
            when(action) {
                is CameraScanAction.TakePicture -> {
                    savedStateHandle[KEY_IS_CAPTURING_PICTURE] = true
                }
                is CameraScanAction.FinishScan -> {
                    savedStateHandle[KEY_IS_PROCESSING_SCAN_RESULTS] = true
                }
            }
        }
    }

    companion object {
        private const val KEY_IS_CAPTURING_PICTURE = "isCapturingPicture"
        private const val KEY_IS_PROCESSING_SCAN_RESULTS = "isProcessingScanResults"
    }

}