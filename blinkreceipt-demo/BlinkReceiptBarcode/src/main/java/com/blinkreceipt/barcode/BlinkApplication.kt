package com.blinkreceipt.barcode

import android.app.Application
import com.microblink.barcode.BlinkReceiptBarcodeSdk
import com.microblink.core.InitializeCallback
import com.microblink.core.Timberland

class BlinkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BlinkReceiptBarcodeSdk.debug(true)

        BlinkReceiptBarcodeSdk.initialize(this, object : InitializeCallback {

            override fun onComplete() {
                Timberland.d("initialize complete")
            }

            override fun onException(e: Throwable) {
                Timberland.d("initialize exception $e")
            }

        })
    }
}