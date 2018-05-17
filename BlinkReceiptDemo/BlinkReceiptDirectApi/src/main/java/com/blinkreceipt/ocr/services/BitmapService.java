package com.blinkreceipt.ocr.services;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.microblink.extensions.Cancelable;
import com.microblink.extensions.OnNullableCompleteListener;

public interface BitmapService extends Cancelable {

    void findByUri( @NonNull Uri uri, @NonNull OnNullableCompleteListener<Bitmap> listener );
}
