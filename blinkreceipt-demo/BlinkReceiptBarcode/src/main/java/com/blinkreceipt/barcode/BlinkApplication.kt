package com.blinkreceipt.barcode

import android.app.Application
import com.microblink.barcode.BlinkReceiptBarcodeSdk
import com.microblink.core.InitializeCallback

class BlinkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BlinkReceiptBarcodeSdk.initialize(this, object : InitializeCallback {

            override fun onComplete() {
                LogcatManager.event().debug {
                    "initialize complete"
                }
            }

            override fun onException(e: Throwable) {
                LogcatManager.event().exception { e }
            }

        })
    }
}