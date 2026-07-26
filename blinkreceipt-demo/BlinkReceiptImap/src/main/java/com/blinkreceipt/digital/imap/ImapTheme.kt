package com.blinkreceipt.digital.imap

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Mirrors res/values/colors.xml, which the XML app theme (AppTheme) still uses. That theme is
// light-only and its ActionBar is always shown, so this scheme is light-only too — a dark Compose
// body under a permanently light ActionBar would not match.
private val ImapColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC5),
)

@Composable
fun ImapTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ImapColorScheme,
        content = content,
    )
}
