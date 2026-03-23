package com.blinkreceipt.engage

import android.app.Application
import com.blinkreceipt.engage.ui.ActivationsActivity
import com.microblink.BlinkReceiptSdk
import com.microblink.core.InitializeCallback
import com.microblink.logcat.LogcatManager

class BlinkReceiptApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        BlinkReceiptSdk.initialize(this, object : InitializeCallback {
            override fun onComplete() {
                ActivationsActivity.applySettings(this@BlinkReceiptApplication)
            }

            override fun onException(throwable: Throwable) {
                LogcatManager.event().exception { throwable }
            }
        })
    }

}