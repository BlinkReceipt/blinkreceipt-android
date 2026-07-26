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
        autoScrapeClient = AutoScrapeClient(this)

        autoScrapeClient.begin(
            success = { credentials, result ->
                // Background thread. One call per scraped account, per run.
                Log.d(TAG, "auto-scrape ${credentials.username} $result")

                AutoScrapeResults.publish("Auto-scrape ${credentials.username}: $result")
            },
            failure = { throwable ->
                Log.e(TAG, "auto-scrape failed", throwable)

                AutoScrapeResults.publish("Auto-scrape failure: $throwable")
            }
        )
    }

    private companion object {
        const val TAG = "ImapApplication"
    }

}
