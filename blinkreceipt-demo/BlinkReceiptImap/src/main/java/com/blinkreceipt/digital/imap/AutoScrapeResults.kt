package com.blinkreceipt.digital.imap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Process-scoped sink for auto-scrape callbacks.
 *
 * The [com.microblink.digital.AutoScrapeClient] is armed in [BlinkApplication] and outlives every
 * Activity, so its results cannot be written straight into an Activity's own state. Snapshot state
 * is safe to write from the background thread the callbacks arrive on, and MainActivity reads it
 * from composition on the main thread.
 */
object AutoScrapeResults {

    var latest: String? by mutableStateOf(null)
        private set

    fun publish(message: String) {
        latest = message
    }
}
