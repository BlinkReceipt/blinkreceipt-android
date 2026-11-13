package com.blinkreceipt.scan.pdf

import android.app.Application
import android.util.Log
import com.actualplatform.receipt.ReceiptSdk
import com.actualplatform.receipt.core.InitializeCallback

class BlinkApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        ReceiptSdk.initialize(this, object: InitializeCallback {
            override fun onComplete() {
                Log.d(TAG, "ReceiptSdk initialized!!!")
            }

            override fun onException(p0: Throwable) {
                Log.e(TAG, "failure in initialize", p0)
            }
        })
    }

    override fun onTerminate() {
        super.onTerminate()
        ReceiptSdk.terminate()
    }

    internal companion object {
        private const val TAG = "BlinkReceiptScanPdf"
    }

}