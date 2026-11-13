package com.blinkreceipt.ocr;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.actualplatform.receipt.ReceiptSdk;
import com.actualplatform.receipt.core.InitializeCallback;

public class BlinkApplication extends Application {

    private static final String TAG = "BlinkReceiptCustomScan";

    @Override
    public void onCreate() {
        super.onCreate();

        ReceiptSdk.initialize(this, new InitializeCallback() {
            @Override
            public void onComplete() {
                Log.d(TAG, "ReceiptSdk initialized!!!");
            }

            @Override
            public void onException(@NonNull Throwable throwable) {
                Log.e(TAG, "failure in initialize", throwable);
            }
        });
    }

    @Override
    public void onTerminate() {
        ReceiptSdk.terminate();

        super.onTerminate();
    }

}
