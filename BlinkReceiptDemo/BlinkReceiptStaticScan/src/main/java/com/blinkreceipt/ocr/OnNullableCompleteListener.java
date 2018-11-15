package com.blinkreceipt.ocr;

import android.support.annotation.Nullable;

public interface OnNullableCompleteListener<T> {

    void onComplete(@Nullable T response);

}
