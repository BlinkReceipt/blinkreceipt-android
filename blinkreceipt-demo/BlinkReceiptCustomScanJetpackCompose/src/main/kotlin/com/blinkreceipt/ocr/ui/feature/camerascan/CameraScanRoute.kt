package com.blinkreceipt.ocr.ui.feature.camerascan

import android.graphics.RectF
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.microblink.RecognizerView
import com.microblink.ScanOptions
import com.microblink.camera.hardware.orientation.Orientation
import com.microblink.camera.view.CameraAspectMode
import com.blinkreceipt.ocr.R
import com.blinkreceipt.ocr.ui.theme.BlinkReceiptDemoTheme

internal val LocalCameraScanAction = staticCompositionLocalOf<(CameraScanAction) -> Unit> { {} }

@Composable
fun CameraScanRoute(
    modifier: Modifier,
    viewModel: CameraScanViewModel = hiltViewModel(),
    onScanResults: ((String) -> Unit),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState: CameraScanUiState by viewModel.uiState.collectAsStateWithLifecycle()

    var displayTakePictureErrorDialogPrompt by remember {
        mutableStateOf(false)
    }

    var displayFinishScanErrorDialogPrompt by remember {
        mutableStateOf(false)
    }

    var recognizerViewInstance: RecognizerView? by remember {
        mutableStateOf(null)
    }


    LaunchedEffect(viewModel, lifecycleOwner) {
        viewModel.events.collect { event ->
            when (event) {
                is CameraScanEvent.OnTakePictureError -> {
                    displayTakePictureErrorDialogPrompt = true
                }
                is CameraScanEvent.OnTakePictureCaptured -> {
                    recognizerViewInstance?.confirmPicture(event.result)
                }
                is CameraScanEvent.OnFinishScanError -> {
                    displayFinishScanErrorDialogPrompt = true
                }
                is CameraScanEvent.OnFinishScanCompleted -> {
                    onScanResults(event.blinkreceiptId)
                }
            }
        }
    }

    CompositionLocalProvider(
        LocalCameraScanAction provides { viewModel.handleAction(it) }
    ) {
        val action = LocalCameraScanAction.current
        CameraScanContent(
            modifier = modifier.windowInsetsPadding(WindowInsets.systemBars),
            uiState = uiState,
            onInitializeRecognizerView = { recognizerView ->
                with(recognizerView) {
                    setMeteringAreas(arrayOf(RectF(0f, 0f, 1f, 1f)), true)

                    initialOrientation = Orientation.ORIENTATION_PORTRAIT
                    aspectMode = CameraAspectMode.ASPECT_FILL

                    recognizerCallback(viewModel.recognizerCallback)

                    initialize(ScanOptions.newBuilder().build())

                    lifecycle(lifecycleOwner)

                    recognizerViewInstance = recognizerView
                }
            },
            onClickTakePicture = {
                recognizerViewInstance?.takePicture(viewModel.takePictureCallback)
                action(CameraScanAction.TakePicture)
            },
            onClickFinishScan = {
                recognizerViewInstance?.finishedScanning()
                action(CameraScanAction.FinishScan)
            },
        )

        if(displayTakePictureErrorDialogPrompt) {
            val onDismiss = { displayTakePictureErrorDialogPrompt = false }
            AlertDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    Text(
                        text = stringResource(android.R.string.ok),
                        modifier = Modifier.clickable(
                            onClick = onDismiss,
                        )
                    )
                },
                text = {
                    Text(text = stringResource(R.string.camera_scan_dialog_take_picture_error_msg))
                }
            )
        }

        if(displayFinishScanErrorDialogPrompt) {
            val onDismiss = { displayFinishScanErrorDialogPrompt = false }
            AlertDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    Text(
                        text = stringResource(android.R.string.ok),
                        modifier = Modifier.clickable(
                            onClick = onDismiss,
                        )
                    )
                },
                text = {
                    Text(text = stringResource(R.string.camera_scan_dialog_scan_results_error_msg))
                }
            )
        }
    }
}

@Composable
internal fun CameraScanContent(
    modifier: Modifier,
    uiState: CameraScanUiState,
    onInitializeRecognizerView: (RecognizerView) -> Unit,
    onClickTakePicture: (() -> Unit) = {},
    onClickFinishScan: (() -> Unit) = {},
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        val context = LocalContext.current
        val recognizerView = remember {
            RecognizerView(context).apply(onInitializeRecognizerView)
        }

        @Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
        AndroidView(
            factory = { recognizerView },
            modifier = modifier.fillMaxSize(),
            onRelease = { view ->
                view.terminate()
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ElevatedButton(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                enabled = !uiState.isCapturingPicture,
                onClick = onClickTakePicture,
            ) {
                Text(stringResource(R.string.camera_scan_btn_take_picture))
            }

            ElevatedButton(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                enabled = !uiState.isProcessingScanResults,
                onClick = onClickFinishScan,
            ) {
                Text(stringResource(R.string.camera_scan_btn_finish_scan))
            }
        }
    }

}

@PreviewScreenSizes
@Composable
internal fun CameraScanContent_Preview() {
    BlinkReceiptDemoTheme {
        Surface {
            CameraScanContent(
                modifier = Modifier.fillMaxSize(),
                uiState = CameraScanUiState(
                    isCapturingPicture = false,
                    isProcessingScanResults = false,
                ),
                onInitializeRecognizerView = { },
            )
        }
    }
}