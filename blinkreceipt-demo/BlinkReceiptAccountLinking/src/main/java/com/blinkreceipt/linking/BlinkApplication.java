package com.blinkreceipt.linking;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.actualplatform.receipt.core.InitializeCallback;
import com.actualplatform.receipt.linking.ReceiptLinkingSdk;

public class BlinkApplication extends Application {

    private final String TAG = "AccountLinking";

    @Override
    public void onCreate() {
        super.onCreate();

        ReceiptLinkingSdk.initialize(this, new InitializeCallback() {

            @Override
            public void onComplete() {
                Log.d(TAG, "account linking initialization complete");
            }

            @Override
            public void onException(@NonNull Throwable throwable) {
                Log.e(TAG,  "failure in initialize", throwable);
            }

        });
    }
}
