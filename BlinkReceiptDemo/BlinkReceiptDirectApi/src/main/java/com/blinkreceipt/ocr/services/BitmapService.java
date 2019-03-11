package com.blinkreceipt.ocr.services;

import android.graphics.Bitmap;
import android.net.Uri;

import com.blinkreceipt.ocr.Cancelable;
import com.blinkreceipt.ocr.OnNullableCompleteListener;

import androidx.annotation.NonNull;

public interface BitmapService extends Cancelable {

    void findByUri(@NonNull Uri uri, @NonNull OnNullableCompleteListener<Bitmap> listener );
}
