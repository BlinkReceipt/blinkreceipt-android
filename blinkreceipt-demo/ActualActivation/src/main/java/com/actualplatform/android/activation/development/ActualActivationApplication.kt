package com.actualplatform.android.activation.development

import android.app.Application
import com.actualplatform.android.activation.development.ui.ActivationActivity
import com.microblink.BlinkReceiptSdk
import com.microblink.core.InitializeCallback
import com.microblink.logcat.LogcatManager

class ActualActivationApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        BlinkReceiptSdk.initialize(this, object : InitializeCallback {
            override fun onComplete() {
                ActivationActivity.applySettings(this@ActualActivationApplication)
            }

            override fun onException(throwable: Throwable) {
                LogcatManager.event().exception { throwable }
            }
        })
    }

}