package com.blinkreceipt.scan.pdf.internal

import android.app.Application
import com.microblink.BlinkReceiptSdk
import com.microblink.core.Events
import com.microblink.core.LoggingEvents
import logcat.LogPriority

class PdfApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        events = LoggingEvents(LogPriority.DEBUG)
    }

    override fun onTerminate() {
        BlinkReceiptSdk.terminate()

        super.onTerminate()
    }

    companion object {

        @JvmStatic
        var events:Events =  Events.NONE

    }
}