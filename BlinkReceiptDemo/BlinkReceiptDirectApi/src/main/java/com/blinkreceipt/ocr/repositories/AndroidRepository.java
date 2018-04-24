package com.blinkreceipt.ocr.repositories;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.annotation.NonNull;

public class AndroidRepository {

    @SuppressLint("StaticFieldLeak")
    private final Application application;

    AndroidRepository(@NonNull Application application) {
        this.application = application;
    }

    @NonNull
    public <T extends Application> T getApplication() {
        //noinspection unchecked
        return (T) application;
    }

}
