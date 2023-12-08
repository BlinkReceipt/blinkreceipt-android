package com.blinkreceipt.scan.pdf.internal

import android.app.Application
import com.microblink.BlinkReceiptSdk

class PdfApplication: Application() {

    override fun onTerminate() {
        BlinkReceiptSdk.terminate()

        super.onTerminate()
    }
}