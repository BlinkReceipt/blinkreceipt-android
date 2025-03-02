package com.blinkreceipt.linking;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.microblink.core.InitializeCallback;
import com.microblink.linking.BlinkReceiptLinkingSdk;

public class BlinkApplication extends Application {

    private final String TAG = "AccountLinking";

    @Override
    public void onCreate() {
        super.onCreate();

        BlinkReceiptLinkingSdk.initialize(this, new InitializeCallback() {

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
