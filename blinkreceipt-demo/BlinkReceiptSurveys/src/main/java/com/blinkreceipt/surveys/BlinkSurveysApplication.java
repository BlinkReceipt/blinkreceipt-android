package com.blinkreceipt.surveys;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import com.actualplatform.receipt.core.InitializeCallback;
import com.actualplatform.receipt.surveys.ReceiptSurveysSdk;

public class BlinkSurveysApplication extends Application  {

    private final String TAG = "BlinkSurveysApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        ReceiptSurveysSdk.initialize(this, new InitializeCallback() {
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
