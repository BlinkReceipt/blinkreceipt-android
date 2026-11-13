package com.blinkreceipt.digital.outlook

import android.app.Application
import android.util.Log
import com.actualplatform.receipt.core.InitializeCallback
import com.actualplatform.receipt.digital.ReceiptDigitalSdk

class BlinkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        ReceiptDigitalSdk.initialize(this, object : InitializeCallback {

            override fun onComplete() {
                Log.d(TAG, "initialize complete")
            }

            override fun onException(e: Throwable) {
                Log.d(TAG, "failed in initialize", e )
            }

        })
    }


    private companion object {
        const val TAG = "ImapApplication"
    }
}