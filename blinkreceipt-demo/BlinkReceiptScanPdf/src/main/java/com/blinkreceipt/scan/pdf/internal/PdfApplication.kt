package com.blinkreceipt.scan.pdf.internal

import android.app.Application
import com.actualplatform.receipt.ReceiptSdk

class PdfApplication: Application() {

    override fun onTerminate() {
        ReceiptSdk.terminate()

        super.onTerminate()
    }
}