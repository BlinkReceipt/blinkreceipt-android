package com.blinkreceipt.digital.imap

import android.app.Application
import android.util.Log
import com.microblink.core.InitializeCallback
import com.microblink.digital.AutoScrapeClient
import com.microblink.digital.BlinkReceiptDigitalSdk

class BlinkApplication : Application() {

    // Held for the life of the process, never scoped to an Activity: close() cancels the periodic
    // schedule, so an Activity-scoped client would disarm auto-scrape every time the user leaves
    // the screen or the device rotates.
    private lateinit var autoScrapeClient: AutoScrapeClient

    override fun onCreate() {
        super.onCreate()

        // Restored before SDK init, and independent of whether it succeeds, so the interval
        // selector always shows the user's persisted choice rather than the 24h default.
        AutoScrapeController.load(this)

        BlinkReceiptDigitalSdk.initialize(this, object : InitializeCallback {

            override fun onComplete() {
                Log.d(TAG,  "BlinkReceiptDigitalSdk initialized" )

                armAutoScrape()
            }

            override fun onException(throwable: Throwable) {
                Log.e(TAG, "failure in initialize", throwable )
            }

        })
    }

    /**
     * Armed only once the SDK reports ready. Arming reconciles the schedule against linked-account
     * storage, and a reconcile that runs before the SDK is initialized sees no eligible accounts —
     * which cancels the periodic work instead of starting it.
     */
    private fun armAutoScrape() {
        autoScrapeClient = AutoScrapeClient(this).apply {
            // Applies the restored interval before the schedule is created, so a launch-time re-arm
            // doesn't silently revert to the 24h default.
            intervalHours = AutoScrapeController.requestedIntervalHours
        }

        autoScrapeClient.begin(
            success = { credentials, result ->
                // Background thread. One call per scraped account, per run.
                Log.d(TAG, "auto-scrape ${credentials.username} $result")

                AutoScrapeController.publish("Auto-scrape ${credentials.username}: $result")
            },
            failure = { throwable ->
                Log.e(TAG, "auto-scrape failed", throwable)

                AutoScrapeController.publish("Auto-scrape failure: $throwable")
            }
        )
    }

    /**
     * Pushes a new cadence onto the armed client. Called by [AutoScrapeController] when the user
     * picks an interval; a no-op until the client exists, in which case the stored value is applied
     * by [armAutoScrape] instead.
     */
    fun applyAutoScrapeInterval(hours: Int) {
        if (::autoScrapeClient.isInitialized) {
            autoScrapeClient.intervalHours = hours
        }
    }

    private companion object {
        const val TAG = "ImapApplication"
    }

}
