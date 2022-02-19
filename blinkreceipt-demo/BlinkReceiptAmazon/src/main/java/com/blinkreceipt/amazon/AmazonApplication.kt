package com.blinkreceipt.amazon

import android.app.Application
import com.microblink.core.InitializeCallback
import com.microblink.core.Timberland
import com.microblink.linking.BlinkReceiptLinkingSdk

class AmazonApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BlinkReceiptLinkingSdk.debug = true

        BlinkReceiptLinkingSdk.initialize(this, object : InitializeCallback {

            override fun onComplete() {
                Timberland.d("account linking initialized")
            }

            override fun onException(t: Throwable) {
                Timberland.e(t)
            }

        })
    }
}