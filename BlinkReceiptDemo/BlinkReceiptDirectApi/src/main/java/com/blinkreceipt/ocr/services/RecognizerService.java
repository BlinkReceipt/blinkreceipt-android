package com.blinkreceipt.ocr.services;

import android.graphics.Bitmap;

import com.blinkreceipt.ocr.Cancelable;
import com.blinkreceipt.ocr.OnNullableCompleteListener;
import com.blinkreceipt.ocr.transfer.RecognizerResults;
import com.microblink.CameraOrientation;
import com.microblink.ScanOptions;

import androidx.annotation.NonNull;

public interface RecognizerService extends Cancelable {

    void recognize(@NonNull ScanOptions options, @NonNull Bitmap bitmap, @NonNull CameraOrientation orientation,
                   @NonNull OnNullableCompleteListener<RecognizerResults> listener );
}
