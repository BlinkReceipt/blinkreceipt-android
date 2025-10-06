package com.blinkreceipt.ocr;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.microblink.BlinkReceiptSdk;
import com.microblink.core.InitializeCallback;

public class BlinkApplication extends Application {

    private static final String TAG = "BlinkReceiptStaticScan";

    @Override
    public void onCreate() {
        super.onCreate();

        BlinkReceiptSdk.initialize(this, new InitializeCallback() {
            @Override
            public void onComplete() {
                Log.d(TAG, "BlinkReceiptSdk initialized!!!");
            }

            @Override
            public void onException(@NonNull Throwable throwable) {
                Log.e(TAG, "failure in initialize", throwable);
            }
        });
    }

    @Override
    public void onTerminate() {
        BlinkReceiptSdk.terminate();

        super.onTerminate();
    }

}
