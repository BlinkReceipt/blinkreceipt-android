package com.actualplatform.android.receipt.surveys;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import com.actualplatform.receipt.core.InitializeCallback;
import com.actualplatform.receipt.surveys.ReceiptSurveysSdk;

public class ReceiptSurveysApplication extends Application  {

    private final String TAG = "ReceiptSurveysApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        ReceiptSurveysSdk.initialize(this, new InitializeCallback() {
            @Override
            public void onComplete() {
                Log.d(TAG, "ReceiptSurveysSdk initialized!!!");
            }

            @Override
            public void onException(@NonNull Throwable throwable) {
                Log.e(TAG, "failure in initialize", throwable);
            }
        });
    }
}
