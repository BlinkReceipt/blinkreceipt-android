package com.blinkreceipt.surveys;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import com.microblink.core.InitializeCallback;
import com.microblink.surveys.BlinkReceiptSurveysSdk;

public class BlinkSurveysApplication extends Application  {

    private final String TAG = "BlinkSurveysApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        BlinkReceiptSurveysSdk.initialize(this, new InitializeCallback() {
            @Override
            public void onComplete() {
                Log.d(TAG, "BlinkReceiptSurveySdk initialized!!!");
            }

            @Override
            public void onException(@NonNull Throwable throwable) {
                Log.e(TAG, "failure in initialize", throwable);
            }
        });
    }
}
