package com.blinkreceipt.digital.imap

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import com.microblink.digital.AutoScrapeClient

/**
 * Process-scoped state for the auto-scrape demo: the user's cadence preference, and the latest text
 * from the auto-scrape callbacks.
 *
 * The [AutoScrapeClient] itself is owned by [BlinkApplication] and outlives every Activity —
 * `close()` cancels the periodic schedule, so it cannot be Activity-scoped — which means neither
 * its callbacks nor its configuration can live in Activity state. Snapshot state is safe to write
 * from the background thread the callbacks arrive on, and MainActivity reads it from composition on
 * the main thread.
 *
 * This object deliberately holds no reference to the client (or to any Context): a client kept in a
 * static field trips lint's StaticFieldLeak, and routing interval changes back through the
 * Application keeps ownership in one place instead of two.
 */
object AutoScrapeController {

    private const val PREFS_NAME = "auto-scrape-demo"

    private const val KEY_INTERVAL_HOURS = "interval-hours"

    /** Latest text from the auto-scrape callbacks. */
    var latestResult: String? by mutableStateOf(null)
        private set

    /**
     * The interval the user picked, as persisted. This is the *requested* value: the SDK floors
     * anything under [AutoScrapeClient.MIN_INTERVAL_HOURS], so the cadence that actually runs is
     * [effectiveIntervalHours].
     */
    var requestedIntervalHours: Int by mutableIntStateOf(AutoScrapeClient.DEFAULT_INTERVAL_HOURS)
        private set

    /**
     * The cadence the SDK will really use, after its own clamping.
     *
     * This deliberately does not call `AutoScrapeClient.clampInterval()`, even though that is the
     * SDK's own helper for exactly this. In the published 2.3.0 artifact the companion object is
     * obfuscated to `AutoScrapeClient$a` while the field holding it is still named `Companion`, so
     * Kotlin compiles a read of a field named `a` that does not exist, and any call to a companion
     * *function* dies at runtime with NoSuchFieldError. The `const val` thresholds are unaffected —
     * Kotlin inlines them at the call site, so no companion lookup happens.
     *
     * Replicates the documented rule: anything under the minimum falls back to the minimum, and
     * everything at or above it passes through unchanged.
     */
    val effectiveIntervalHours: Int
        get() = if (requestedIntervalHours < AutoScrapeClient.MIN_INTERVAL_HOURS) {
            AutoScrapeClient.MIN_INTERVAL_HOURS
        } else {
            requestedIntervalHours
        }

    /**
     * Restores the persisted choice. Called straight from `Application.onCreate`, before and
     * independently of SDK initialization: the stored preference is the user's, not the SDK's, so
     * the selector must show it immediately rather than only after — or if — init succeeds.
     */
    fun load(context: Context) {
        requestedIntervalHours = prefs(context)
            .getInt(KEY_INTERVAL_HOURS, AutoScrapeClient.DEFAULT_INTERVAL_HOURS)
    }

    /**
     * Persists the user's choice and hands it to the Application to apply to the armed client.
     * Changing the interval while armed re-schedules the periodic work immediately; if nothing is
     * armed yet, the stored value is applied when the client is created.
     */
    fun select(context: Context, hours: Int) {
        prefs(context).edit { putInt(KEY_INTERVAL_HOURS, hours) }

        requestedIntervalHours = hours

        (context.applicationContext as? BlinkApplication)?.applyAutoScrapeInterval(hours)
    }

    fun publish(message: String) {
        latestResult = message
    }

    private fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
