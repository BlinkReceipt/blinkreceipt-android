package com.blinkreceipt.ocr.services;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.microblink.CameraOrientation;
import com.microblink.Media;
import com.microblink.ScanOptions;
import com.microblink.ScanResults;
import com.microblink.extensions.Cancelable;
import com.microblink.extensions.OnNullableCompleteListener;

public interface RecognizerService extends Cancelable {

    void recognize( @NonNull ScanOptions options, @NonNull Bitmap bitmap, @NonNull CameraOrientation orientation,
                    @NonNull OnNullableCompleteListener<Pair<ScanResults,Media>> listener );
}
