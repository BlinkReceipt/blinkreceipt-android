package com.blinkreceipt.digital.imap

import android.app.Application
import com.microblink.core.InitializeCallback
import com.microblink.core.Timberland
import com.microblink.digital.BlinkReceiptDigitalSdk

class BlinkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BlinkReceiptDigitalSdk.debug(false)

        BlinkReceiptDigitalSdk.initialize(this, object : InitializeCallback {

            override fun onComplete() {
                Timberland.d("initialize complete")
            }

            override fun onException(e: Throwable) {
                Timberland.d("initialize exception $e")
            }

        })
    }

}