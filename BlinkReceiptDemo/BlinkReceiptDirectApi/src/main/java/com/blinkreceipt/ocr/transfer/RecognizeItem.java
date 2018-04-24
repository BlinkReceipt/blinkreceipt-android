package com.blinkreceipt.ocr.transfer;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.microblink.CameraOrientation;
import com.microblink.ScanOptions;

public final class RecognizeItem {

    @NonNull
    private ScanOptions options;

    @NonNull
    private Bitmap bitmap;

    @NonNull
    private CameraOrientation orientation;

    public RecognizeItem(@NonNull ScanOptions options, @NonNull Bitmap bitmap, @NonNull CameraOrientation orientation) {
        this.options = options;

        this.bitmap = bitmap;

        this.orientation = orientation;
    }

    @NonNull
    public ScanOptions options() {
        return options;
    }

    @NonNull
    public Bitmap bitmap() {
        return bitmap;
    }

    @NonNull
    public CameraOrientation orientation() {
        return orientation;
    }

    @Override
    public String toString() {
        return "RecognizeItem{" +
                "options=" + options +
                ", bitmap=" + bitmap +
                ", orientation=" + orientation +
                '}';
    }
}
