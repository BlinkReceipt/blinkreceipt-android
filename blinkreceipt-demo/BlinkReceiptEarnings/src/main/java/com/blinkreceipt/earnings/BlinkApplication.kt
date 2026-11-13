package com.blinkreceipt.earnings

import android.app.Application
import android.util.Log
import com.actualplatform.receipt.core.InitializeCallback
import com.actualplatform.receipt.earnings.ReceiptEarningSdk

class BlinkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        ReceiptEarningSdk.initialize(this, object : InitializeCallback {

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