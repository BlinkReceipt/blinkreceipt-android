package com.blinkreceipt.digital.imap

import android.app.Application
import com.microblink.core.InitializeCallback
import com.microblink.digital.BlinkReceiptDigitalSdk

class BlinkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BlinkReceiptDigitalSdk.initialize(this, object : InitializeCallback {

            fun onComplete() {
                LogcatManager.event().debug { "BlinkReceiptDigitalSdk initialized!!!" }
            }

            fun onException(throwable: Throwable) {
                LogcatManager.event().exception { throwable }
            }

        })
    }

}