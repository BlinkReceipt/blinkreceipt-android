package com.blinkreceipt.ocr;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;

public class AndroidRepository {

    @SuppressLint("StaticFieldLeak")
    private final Application application;

    public AndroidRepository(@NonNull Application application) {
        this.application = application;
    }

    @NonNull
    public <T extends Application> T application() {
        //noinspection unchecked
        return (T) application;
    }

}
