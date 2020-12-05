package com.microblink.blinkreceiptsurveys;

import android.app.Application;
import androidx.annotation.NonNull;
import com.microblink.core.InitializeCallback;
import com.microblink.core.Timberland;
import com.microblink.surveys.BlinkReceiptSurveysSdk;

public class BlinkSurveysApplication extends Application  {

    @Override
    public void onCreate() {
        super.onCreate();

        BlinkReceiptSurveysSdk.initialize(this, new InitializeCallback() {
            @Override
            public void onComplete() {
                Timberland.d("BlinkReceiptSurveySdk initialized!!!");
            }

            @Override
            public void onException(@NonNull Throwable throwable) {
                Timberland.e(throwable);
            }
        });
    }
}
