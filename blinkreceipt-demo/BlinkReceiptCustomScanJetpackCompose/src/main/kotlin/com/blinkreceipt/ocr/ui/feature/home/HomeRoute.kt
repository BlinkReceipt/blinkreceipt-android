@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.blinkreceipt.ocr.ui.feature.home

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
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
    onNavigateToCustomCameraScan: () -> Unit,
    onNavigateToOobCameraScan: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var displayVersion: String? by rememberSaveable(lifecycleOwner) {
        mutableStateOf(null)
    }

    var displayPermissionDeniedDialogPrompt: Boolean by rememberSaveable(lifecycleOwner) {
        mutableStateOf(false)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onNavigateToCustomCameraScan()
        } else {
            // Permission denied
            displayPermissionDeniedDialogPrompt = true
        }
    }

    LaunchedEffect(viewModel, lifecycleOwner) {
        viewModel.events
            .collect { event ->
                when (event) {
                    is HomeEvent.OnStartCustomScan -> {
                        launcher.launch(Manifest.permission.CAMERA)
                    }
                    is HomeEvent.OnStartOobCameraScan -> {
                        onNavigateToOobCameraScan()
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

    val focusRequester = FocusRequester()
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    BackHandler(fabMenuExpanded) { fabMenuExpanded = false }

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

            FabActionMenu(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                fabMenuExpanded = fabMenuExpanded,
                focusRequester = focusRequester,
                onToggleFabMenuExpandedState = { fabMenuExpanded = it },
            )

        }
    }
}

@Composable
private fun BoxScope.FabActionMenu(
    modifier: Modifier,
    fabMenuExpanded: Boolean,
    focusRequester: FocusRequester = FocusRequester(),
    onToggleFabMenuExpandedState: (Boolean) -> Unit,
) {
    val action = LocalHomeAction.current

    val items =
        listOf(
            R.drawable.ic_camera to stringResource(R.string.home_fab_custom_scan),
            com.microblink.camera.ui.R.drawable.ic_camera_capture to stringResource(R.string.home_fab_oob_camera),
        )

    FloatingActionButtonMenu(
        modifier = modifier,
        expanded = fabMenuExpanded,
        button = {
            ToggleFloatingActionButton(
                modifier =
                    Modifier.semantics {
                        traversalIndex = -1f
                        stateDescription = if (fabMenuExpanded) "Expanded" else "Collapsed"
                        contentDescription = "Toggle menu"
                    }
                        .animateFloatingActionButton(
                            visible = true,
                            alignment = Alignment.BottomEnd,
                        )
                        .focusRequester(focusRequester),
                checked = fabMenuExpanded,
                onCheckedChange = { onToggleFabMenuExpandedState(!fabMenuExpanded) },
            ) {
                val iconPainterRes by remember {
                    derivedStateOf {
                        if (checkedProgress > 0.5f) android.R.drawable.ic_menu_close_clear_cancel else android.R.drawable.ic_input_add
                    }
                }
                Icon(
                    painter = painterResource(iconPainterRes),
                    contentDescription = null,
                    modifier = Modifier.animateIcon({ checkedProgress }),
                )
            }
        },
    ) {
        val labelCloseMenu = stringResource(R.string.home_fab_close_menu)
        items.forEachIndexed { i, item ->
            FloatingActionButtonMenuItem(
                modifier =
                    Modifier.semantics {
                        isTraversalGroup = true
                        // Add a custom a11y action to allow closing the menu when focusing
                        // the last menu item, since the close button comes before the first
                        // menu item in the traversal order.
                        if (i == items.size - 1) {
                            customActions =
                                listOf(
                                    CustomAccessibilityAction(
                                        label = labelCloseMenu,
                                        action = {
                                            onToggleFabMenuExpandedState(false)
                                            true
                                        },
                                    )
                                )
                        }
                    }
                        .then(
                            if (i == 0) {
                                Modifier.onKeyEvent {
                                    // Navigating back from the first item should go back to the
                                    // FAB menu button.
                                    if (
                                        it.type == KeyEventType.KeyDown &&
                                        (it.key == Key.DirectionUp ||
                                                (it.isShiftPressed && it.key == Key.Tab))
                                    ) {
                                        focusRequester.requestFocus()
                                        return@onKeyEvent true
                                    }
                                    return@onKeyEvent false
                                }
                            } else {
                                Modifier
                            }
                        ),
                onClick = {
                    when(item.first) {
                        R.drawable.ic_camera -> {
                            action(HomeAction.StartCustomScan)
                        }
                        com.microblink.camera.ui.R.drawable.ic_camera_capture -> {
                            action(HomeAction.StartOobCameraScan)
                        }
                    }

                    // Toggle FAB Expanded State
                    onToggleFabMenuExpandedState(false)
                },
                icon = {
                    Icon(
                        painter = painterResource(item.first),
                        contentDescription = item.second,
                    )
                },
                text = { Text(text = item.second) },
            )
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
