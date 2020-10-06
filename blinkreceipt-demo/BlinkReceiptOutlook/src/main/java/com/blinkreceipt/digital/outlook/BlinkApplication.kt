package com.blinkreceipt.digital.outlook

import android.app.Application
import com.microblink.BlinkReceiptSdk
import com.microblink.core.InitializeCallback
import com.microblink.core.Timberland
import com.microblink.digital.BlinkReceiptDigitalSdk

class BlinkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BlinkReceiptDigitalSdk.debug(true)

        BlinkReceiptDigitalSdk.initialize(this, object : InitializeCallback {

            override fun onComplete() {
                Timberland.d("initialize complete")
            }

            override fun onException(e: Throwable) {
                Timberland.d("initialize exception $e")
            }

        })
    }

    override fun onTerminate() {
        BlinkReceiptSdk.terminate()
        super.onTerminate()
    }

}