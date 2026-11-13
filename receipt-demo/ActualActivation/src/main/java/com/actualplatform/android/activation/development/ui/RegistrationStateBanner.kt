package com.actualplatform.android.activation.development.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.actualplatform.activation.ActivationClient
import com.actualplatform.activation.RegistrationState

@Composable
internal fun RegistrationStateBanner(modifier: Modifier = Modifier) {
    val state by ActivationClient.instance.registrationState.collectAsStateWithLifecycle()

    val label: String
    val bgColor: Color
    val textColor: Color
    when (val current = state) {
        is RegistrationState.Idle -> {
            label = "⚪ Registration: Idle"
            bgColor = MaterialTheme.colorScheme.surfaceVariant
            textColor = MaterialTheme.colorScheme.onSurfaceVariant
        }
        is RegistrationState.Registering -> {
            label = "🟡 Registration: Registering…"
            bgColor = MaterialTheme.colorScheme.secondaryContainer
            textColor = MaterialTheme.colorScheme.onSecondaryContainer
        }
        is RegistrationState.Registered -> {
            label = "🟢 Registration: Registered (clientId=${current.clientId})"
            bgColor = MaterialTheme.colorScheme.primaryContainer
            textColor = MaterialTheme.colorScheme.onPrimaryContainer
        }
        is RegistrationState.Exception -> {
            label = "🔴 Registration: Failed (${current.exception})"
            bgColor = MaterialTheme.colorScheme.errorContainer
            textColor = MaterialTheme.colorScheme.onErrorContainer
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            color = textColor,
            softWrap = true,
        )
    }
}
