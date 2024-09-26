package com.blinkreceipt.earnings

import android.app.Application
import android.util.Log
import com.microblink.core.InitializeCallback
import com.microblink.earnings.BlinkReceiptEarningSdk

class BlinkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BlinkReceiptEarningSdk.initialize(this, object : InitializeCallback {

            override fun onComplete() {
                Log.d(TAG, "initialize complete" )
            }

            override fun onException(e: Throwable) {
                Log.e(TAG, "failure in initialize", e )
            }

        })
    }

    private companion object {
        const val TAG = "EarningsApplication"
    }
}