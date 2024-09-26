package com.blinkreceipt.linking;

import android.app.Application;

import androidx.annotation.NonNull;

import com.microblink.core.InitializeCallback;
import com.microblink.linking.BlinkReceiptLinkingSdk;

public class BlinkApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BlinkReceiptLinkingSdk.initialize(this, new InitializeCallback() {

            @Override
            public void onComplete() {
                LogcatManager.event().debug(() -> "account linking initialization complete");
            }

            @Override
            public void onException(@NonNull Throwable throwable) {
                LogcatManager.event().exception(() -> throwable);
            }

        });
    }
}
