package com.blinkreceipt.ocr;

import androidx.annotation.Nullable;

public interface OnNullableCompleteListener<T> {

    void onComplete( @Nullable T response );

}
