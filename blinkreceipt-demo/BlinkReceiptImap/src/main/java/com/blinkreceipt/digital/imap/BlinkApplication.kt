package com.blinkreceipt.digital.imap

import android.app.Application
import android.util.Log
import com.microblink.core.InitializeCallback
import com.microblink.digital.BlinkReceiptDigitalSdk

class BlinkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BlinkReceiptDigitalSdk.initialize(this, object : InitializeCallback {

            override fun onComplete() {
                Log.d(TAG,  "BlinkReceiptDigitalSdk initialized" )
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