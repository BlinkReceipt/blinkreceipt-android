package com.blinkreceipt.digital.outlook

import android.app.Application
import com.microblink.core.InitializeCallback
import com.microblink.digital.BlinkReceiptDigitalSdk

class BlinkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BlinkReceiptDigitalSdk.initialize(this, object : InitializeCallback {

            override fun onComplete() {
                LogcatManager.event().debug {
                    "initialize complete"
                }
            }

            override fun onException(e: Throwable) {
                LogcatManager.event().exception{ e }
            }

        })
    }
}