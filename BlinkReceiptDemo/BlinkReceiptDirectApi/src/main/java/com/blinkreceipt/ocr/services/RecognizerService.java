package com.blinkreceipt.ocr.services;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.blinkreceipt.ocr.Cancelable;
import com.blinkreceipt.ocr.OnNullableCompleteListener;
import com.microblink.CameraOrientation;
import com.microblink.Media;
import com.microblink.ScanOptions;
import com.microblink.ScanResults;

public interface RecognizerService extends Cancelable {

    void recognize( @NonNull ScanOptions options, @NonNull Bitmap bitmap, @NonNull CameraOrientation orientation,
                    @NonNull OnNullableCompleteListener<Pair<ScanResults,Media>> listener );
}
