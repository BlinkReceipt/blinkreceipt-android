@file:OptIn(ExperimentalMaterial3Api::class)

package com.blinkreceipt.ocr.ui.feature.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blinkreceipt.ocr.R
import com.blinkreceipt.ocr.ui.theme.BlinkReceiptDemoTheme

internal val LocalHomeAction = staticCompositionLocalOf<(HomeAction) -> Unit>(
    defaultFactory = { {} }
)

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCameraScan: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var displayVersion: String? by remember(lifecycleOwner) {
        mutableStateOf(null)
    }

    var displayPermissionDeniedDialogPrompt: Boolean by remember(lifecycleOwner) {
        mutableStateOf(false)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onNavigateToCameraScan()
        } else {
            // Permission denied
            displayPermissionDeniedDialogPrompt = true
        }
    }

    LaunchedEffect(lifecycleOwner) {
        viewModel.events
            .collect { event ->
                when (event) {
                    is HomeEvent.OnStartCamera -> {
                        launcher.launch(Manifest.permission.CAMERA)
                    }
                    is HomeEvent.OnDisplayVersion -> {
                        displayVersion = event.versionName
                    }
                }
            }
    }

    CompositionLocalProvider(
        LocalHomeAction provides { viewModel.handleAction(it) }
    ) {
        HomeContent(
            modifier = modifier.fillMaxSize(),
            uiState = uiState,
        )

        if (displayVersion != null) {
            val onDismiss = { displayVersion = null }
            AlertDialog(
                modifier = Modifier.wrapContentSize(),
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
                    Text(text = stringResource(R.string.home_sdk_version_dialog_message, displayVersion ?: ""))
                }
            )
        }

        if(displayPermissionDeniedDialogPrompt) {
            val onDismiss = { displayPermissionDeniedDialogPrompt = false }
            AlertDialog(
                modifier = Modifier.wrapContentSize(),
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
                    Text(text = stringResource(R.string.home_permission_denied_dialog_message))
                }
            )
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
) {
    val action = LocalHomeAction.current

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.home_topappbar_title))
                },
                actions = {
                    Icon(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .clickable(
                                onClick = {
                                    action(HomeAction.DisplayVersion)
                                },
                            )
                            .clipToBounds(),
                        painter = painterResource(android.R.drawable.ic_menu_info_details),
                        contentDescription = stringResource(R.string.home_topappbar_menu_sdk_version),
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if(uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                        .align(Alignment.Center),
                )
            }

            ExtendedFloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onClick = {
                    action(HomeAction.StartCamera)
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = stringResource(R.string.home_fab_open_camera)
                )
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun HomeContentPreview() {
    BlinkReceiptDemoTheme {
        Surface {
            HomeContent(
                uiState = HomeUiState(
                    isLoading = false,
                ),
            )
        }
    }
}
