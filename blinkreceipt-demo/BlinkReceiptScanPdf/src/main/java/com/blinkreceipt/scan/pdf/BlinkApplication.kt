package com.blinkreceipt.scan.pdf

import android.app.Application
import android.util.Log
import com.microblink.BlinkReceiptSdk
import com.microblink.core.InitializeCallback

class BlinkApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        BlinkReceiptSdk.initialize(this, object: InitializeCallback {
            override fun onComplete() {
                Log.d(TAG, "BlinkReceiptSdk initialized!!!")
            }

            override fun onException(p0: Throwable) {
                Log.e(TAG, "failure in initialize", p0)
            }
        })
    }

    override fun onTerminate() {
        super.onTerminate()
        BlinkReceiptSdk.terminate()
    }

    internal companion object {
        private const val TAG = "BlinkReceiptScanPdf"
    }

}