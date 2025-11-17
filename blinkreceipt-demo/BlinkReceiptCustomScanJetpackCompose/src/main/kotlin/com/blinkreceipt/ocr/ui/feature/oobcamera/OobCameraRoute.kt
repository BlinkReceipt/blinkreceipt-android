package com.blinkreceipt.ocr.ui.feature.oobcamera

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.core.os.bundleOf
import androidx.fragment.compose.AndroidFragment
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.blinkreceipt.ocr.R
import com.blinkreceipt.ocr.ui.theme.BlinkReceiptDemoTheme
import com.microblink.ScanOptions
import com.microblink.camera.ui.CameraCharacteristics
import com.microblink.camera.ui.CameraRecognizerFragment
import com.microblink.camera.ui.CameraRecognizerResults
import com.microblink.camera.ui.ScanCharacteristics
import com.microblink.camera.ui.TooltipCharacteristics
import com.microblink.camera.ui.internal.parcelable

internal val LocalOobScanAction = staticCompositionLocalOf<(OobCameraAction) -> Unit> { {} }

@Composable
fun OobCameraRoute(
    modifier: Modifier,
    viewModel: OobCameraViewModel = hiltViewModel(),
    onScanResults: ((String) -> Unit),
    onCancelled: (() -> Unit),
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var displayFinishingScanProgressDialogPrompt by rememberSaveable {
        mutableStateOf(false)
    }

    var displayFinishScanErrorDialogPrompt by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(viewModel, lifecycleOwner) {
        viewModel.events
            .collect { event ->
                when (event) {
                    is OobCameraEvent.OnCancelled -> {
                        onCancelled()
                    }

                    is OobCameraEvent.OnError -> {
                        displayFinishScanErrorDialogPrompt = true
                    }

                    is OobCameraEvent.OnFinishing -> {
                        displayFinishingScanProgressDialogPrompt = true
                    }

                    is OobCameraEvent.OnSuccess -> {
                        onScanResults(event.blinkreceiptId)
                    }
                }
            }
    }

    CompositionLocalProvider(
        LocalOobScanAction provides { viewModel.handleAction(it) },
    ) {
        OobCameraContent(
            modifier = modifier.windowInsetsPadding(WindowInsets.systemBars),
        )

        if (displayFinishingScanProgressDialogPrompt) {
            val onDismiss = { displayFinishingScanProgressDialogPrompt = false }
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
                    Text(text = stringResource(R.string.oob_camera_scan_dialog_finishing_msg))
                }
            )
        }

        if (displayFinishScanErrorDialogPrompt) {
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
                    Text(text = stringResource(R.string.oob_camera_scan_dialog_error_msg))
                }
            )
        }
    }
}

@Composable
internal fun OobCameraContent(
    modifier: Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val action = LocalOobScanAction.current

    if(LocalInspectionMode.current) {
        Text(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
            text = "OobCameraContent",
            textAlign = TextAlign.Center,
        )
        return
    }

    AndroidFragment<CameraRecognizerFragment>(
        modifier = modifier.fillMaxSize(),
        arguments = bundleOf(
            CameraRecognizerFragment.OPTIONS to ScanOptions
                .newBuilder()
                .logoDetection(true)
                .build(),
            CameraRecognizerFragment.CAMERA_CHARACTERISTICS to CameraCharacteristics.Builder()
                .cameraPermission(true)
                .scanCharacteristics(
                    ScanCharacteristics.Builder()
                        .date(true)
                        .merchant(true)
                        .total(true)
                        .build()
                )
                .tooltipCharacteristics(
                    TooltipCharacteristics.Builder()
                        .displayTooltips(true)
                        .build()
                )
                .build(),
        ),
    ) { fragment ->
        fragment.parentFragmentManager
            .setFragmentResultListener(
                CameraRecognizerFragment.SCAN_SESSION_RESULTS_KEY,
                lifecycleOwner,
            ) { _, bundle ->
                bundle.parcelable<CameraRecognizerResults>(CameraRecognizerFragment.SCAN_RESULTS_KEY)
                    ?.let { results ->
                        action(OobCameraAction.ReceiveRecognizerResults(results))
                    } ?: kotlin.run {
                    action(OobCameraAction.ReceiveRecognizerResults(CameraRecognizerResults.Cancelled))
                }
            }
    }
}

@Composable
@PreviewScreenSizes
internal fun OobCameraContent_Preview() {
    BlinkReceiptDemoTheme {
        Surface {
            OobCameraContent(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
