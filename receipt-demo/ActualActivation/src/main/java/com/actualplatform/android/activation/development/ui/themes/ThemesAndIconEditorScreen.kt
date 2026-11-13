package com.actualplatform.android.activation.development.ui.themes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.actualplatform.activation.Activation
import com.actualplatform.activation.theming.ActivationTheme
import com.actualplatform.activation.theming.colors
import com.actualplatform.android.activation.development.R
import kotlinx.coroutines.launch

/**
 * Developer screen for live-editing themes and icons of [ActivationTheme]. The callback let the
 * host commit the result however it wants — typically by assigning the `Activation.theme` directly.
 *
 * State is held in memory only; navigating away discards unapplied edits. The Reset button restores
 * the working draft to defaults and asks the host to do the same on the live SDK.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThemesAndIconEditorScreen(
    onApply: (ActivationTheme) -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
) {
    var draftTheme by remember { mutableStateOf(Activation.theme) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showResetDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val resetMessage = stringResource(R.string.activations_themes_snackbar_reset)

    val tabs =
        listOf(
            stringResource(R.string.activations_themes_tab_themes),
            stringResource(R.string.activations_themes_tab_icons),
        )

    WithDraftTheme(theme = draftTheme) {
        val theme = draftTheme()
        val primary = theme.colors.primary.toComposeColor()
        val surface = theme.colors.surface.toComposeColor()
        val background = theme.colors.background.toComposeColor()
        val textPrimary = theme.colors.textPrimary.toComposeColor()
        val textSecondary = theme.colors.textSecondary.toComposeColor()
        val textInverse = theme.colors.textInverse.toComposeColor()
        val textOnPrimary = theme.colors.textOnPrimary.toComposeColor()
        val surfaceInverse = theme.colors.surfaceInverse.toComposeColor()

        Scaffold(
            containerColor = background,
            contentColor = textPrimary,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.activations_themes_title),
                            style = theme.typography.titleLarge,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Text(
                                text = "←",
                                style = theme.typography.titleLarge,
                                color = textPrimary,
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = { showResetDialog = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = primary),
                        ) {
                            Text(
                                text = stringResource(R.string.activations_themes_menu_reset),
                                style = theme.typography.labelLarge,
                            )
                        }
                    },
                    colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = surface,
                        titleContentColor = textPrimary,
                        navigationIconContentColor = textPrimary,
                        actionIconContentColor = textSecondary,
                    ),
                )
            },
            bottomBar = {
                Surface(color = surface, contentColor = textPrimary, tonalElevation = 4.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = { onApply(draftTheme) },
                            modifier = Modifier.fillMaxWidth(),
                            colors =
                            ButtonDefaults.buttonColors(
                                containerColor = primary,
                                contentColor = textOnPrimary,
                            ),
                            shape = RoundedCornerShape(theme.shapes.sm),
                        ) {
                            Text(
                                text = stringResource(R.string.activations_themes_button_apply),
                                style = theme.typography.labelLarge,
                            )
                        }
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = surfaceInverse,
                        contentColor = textInverse,
                        actionColor = primary,
                        shape = RoundedCornerShape(theme.shapes.sm),
                    )
                }
            },
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = surface,
                    contentColor = textPrimary,
                    indicator = {
                        // `this: TabIndicatorScope` — the scope exposes
                        // `Modifier.tabIndicatorOffset(selectedTabIndex)` as a member extension.
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(selectedTabIndex = selectedTab),
                            color = primary,
                        )
                    },
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            selectedContentColor = primary,
                            unselectedContentColor = textSecondary,
                            text = { Text(text = title, style = theme.typography.labelLarge) },
                        )
                    }
                }

                // Each tab owns its own scroll state so switching tabs doesn't carry an
                // unrelated scroll offset over from the other tab.
                val themesScroll = rememberScrollState()
                val iconsScroll = rememberScrollState()
                when (selectedTab) {
                    0 ->
                        Column(
                            modifier =
                            Modifier.fillMaxSize().verticalScroll(themesScroll).padding(16.dp),
                        ) {
                            ThemesTabContent(
                                theme = draftTheme,
                                onThemeChange = { draftTheme = it },
                            )
                        }
                    1 ->
                        Column(
                            modifier =
                            Modifier.fillMaxSize()
                                .verticalScroll(iconsScroll)
                                .padding(16.dp),
                        ) {
                            IconsTabContent(theme = draftTheme, onThemeChange = { draftTheme = it })
                        }
                }
            }
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                containerColor = surface,
                iconContentColor = primary,
                titleContentColor = textPrimary,
                textContentColor = textSecondary,
                shape = RoundedCornerShape(theme.shapes.lg),
                title = {
                    Text(
                        text = stringResource(R.string.activations_themes_reset_dialog_title),
                        style = theme.typography.titleLarge,
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.activations_themes_reset_dialog_message),
                        style = theme.typography.bodyMedium,
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showResetDialog = false
                            // Show the snackbar BEFORE invoking onReset. ActivationActivity.kt's
                            // onReset does not pop the back stack, so the editor stays mounted and
                            // the snackbar renders for its full duration.
                            scope.launch { snackbarHostState.showSnackbar(resetMessage) }
                            onReset()
                            draftTheme = ActivationTheme()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = primary),
                    ) {
                        Text(
                            text = stringResource(R.string.activations_themes_reset_dialog_confirm),
                            style = theme.typography.labelLarge,
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showResetDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = primary),
                    ) {
                        Text(
                            text = stringResource(R.string.activations_themes_reset_dialog_dismiss),
                            style = theme.typography.labelLarge,
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun WithDraftTheme(theme: ActivationTheme, content: @Composable () -> Unit) {
    val isDark = isSystemInDarkTheme()
    // Same dark-resolution pattern as the SDK's ActivationThemeProvider — resolve the active
    // palette once and fold it into both slots so descendants never need to branch.
    val resolved =
        remember(theme, isDark) {
            val active = if (isDark) theme.darkColors else theme.lightColors
            theme.copy(lightColors = active, darkColors = active)
        }
    CompositionLocalProvider(LocalDraftActivationTheme provides resolved) { content() }
}

@Composable
@Preview
internal fun ThemesAndIconEditorScreenPreview() {
    ThemesAndIconEditorScreen(onApply = { _ -> }, onReset = {}, onBack = {})
}
