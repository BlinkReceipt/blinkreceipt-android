package com.blinkreceipt.barcode

import android.app.Application
import android.util.Log
import com.microblink.barcode.BlinkReceiptBarcodeSdk
import com.microblink.core.InitializeCallback

class BlinkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BlinkReceiptBarcodeSdk.initialize(this, object : InitializeCallback {

            override fun onComplete() {
                Log.d(TAG, "initialize complete" )
            }

            override fun onException(e: Throwable) {
                Log.e(TAG, "failed in initialize", e )
            }

        })
    }

    private companion object {
        const val TAG = "BarcodeApplication"
    }
}