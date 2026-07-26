package com.blinkreceipt.digital.imap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Every action the IMAP demo exposes, in the order the buttons are laid out. The label doubles as
 * the button text and as the test tag, so a test can find a button by the action it triggers.
 */
enum class ImapAction(val label: String) {
    MESSAGES("Message"),
    LOGIN("login"),
    LOGOUT("logout"),
    CLEAR("clear"),
    VERIFY("Verify"),
    DEBUG("debug"),
    REMOTE_MESSAGES("Remote"),
    MULTIPLE_REMOTE("Multiple Remote"),
    MULTIPLE_MESSAGES("Multiple Messages"),
    SINGLE_LOGOUT("Single Logout"),
    AUTO_SCRAPE("Auto-Scrape Now (QA)"),
}

@Immutable
data class ImapUiState(
    val results: String = "",
    val enabledActions: Set<ImapAction> = emptySet(),
    val credentialsVisible: Boolean = false,
)

const val ResultsTestTag: String = "results"

@Composable
fun ImapScreen(
    state: ImapUiState,
    onAction: (ImapAction) -> Unit,
    onCredentialsConfirmed: () -> Unit,
    onCredentialsDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ImapAction.entries.forEach { action ->
                Button(
                    onClick = { onAction(action) },
                    enabled = action in state.enabledActions,
                    modifier = Modifier.testTag(action.label),
                ) {
                    Text(text = action.label)
                }
            }

            Text(
                text = state.results,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .testTag(ResultsTestTag),
            )
        }
    }

    if (state.credentialsVisible) {
        CredentialsDialog(
            onConfirmed = onCredentialsConfirmed,
            onDismissed = onCredentialsDismissed,
        )
    }
}

/**
 * The provider field is presentational, exactly as it was in the XML dialog it replaces: the login
 * path below always builds `Credentials.None.Gmail()`, so nothing ever reads this value.
 */
@Composable
private fun CredentialsDialog(
    onConfirmed: () -> Unit,
    onDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var provider by rememberSaveable { mutableStateOf("GMAIL") }

    AlertDialog(
        onDismissRequest = onDismissed,
        confirmButton = {
            TextButton(onClick = onConfirmed) {
                Text(text = "Ok")
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDismissed) {
                Text(text = "Cancel")
            }
        },
        title = { Text(text = "Credentials") },
        text = {
            OutlinedTextField(
                value = provider,
                onValueChange = { provider = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                ),
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun ImapScreenReadyPreview() {
    ImapTheme {
        ImapScreen(
            state = ImapUiState(
                results = "ScanResults Size: 3",
                enabledActions = ImapAction.entries.toSet(),
            ),
            onAction = {},
            onCredentialsConfirmed = {},
            onCredentialsDismissed = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ImapScreenInitializingPreview() {
    ImapTheme {
        ImapScreen(
            state = ImapUiState(),
            onAction = {},
            onCredentialsConfirmed = {},
            onCredentialsDismissed = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ImapScreenCredentialsPreview() {
    ImapTheme {
        ImapScreen(
            state = ImapUiState(
                results = "Logging in...",
                enabledActions = ImapAction.entries.toSet(),
                credentialsVisible = true,
            ),
            onAction = {},
            onCredentialsConfirmed = {},
            onCredentialsDismissed = {},
        )
    }
}
