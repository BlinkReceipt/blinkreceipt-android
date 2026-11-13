package com.blinkreceipt.digital.imap

import android.app.Application
import android.util.Log
import com.actualplatform.receipt.core.InitializeCallback
import com.actualplatform.receipt.digital.ReceiptDigitalSdk

class BlinkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        ReceiptDigitalSdk.initialize(this, object : InitializeCallback {

            override fun onComplete() {
                Log.d(TAG,  "ReceiptDigitalSdk initialized" )
            }

            override fun onException(throwable: Throwable) {
                Log.e(TAG, "failure in initialize", throwable )
            }

        })
    }

    private companion object {
        const val TAG = "ImapApplication"
    }

}