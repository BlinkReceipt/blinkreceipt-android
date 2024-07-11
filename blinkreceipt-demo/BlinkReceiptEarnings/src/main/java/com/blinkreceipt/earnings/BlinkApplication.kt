package com.blinkreceipt.earnings

import android.app.Application
import com.microblink.core.InitializeCallback
import com.microblink.core.Timberland
import com.microblink.earnings.BlinkReceiptEarningSdk

class BlinkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BlinkReceiptEarningSdk.initialize(this, object : InitializeCallback {

            override fun onComplete() {
                Timberland.d("initialize complete")
            }

            override fun onException(e: Throwable) {
                Timberland.d("initialize exception $e")
            }

        })
    }
}