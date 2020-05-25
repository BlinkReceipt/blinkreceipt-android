package com.blinkreceipt.ocr;

import android.app.Application;

import com.microblink.BlinkReceiptSdk;

public class BlinkReceiptApplication extends Application {

    @Override
    public void onTerminate() {
        BlinkReceiptSdk.terminate();

        super.onTerminate();
    }

}
