package com.blinkreceipt.ocr;

import android.support.annotation.NonNull;

public interface OnCompleteListener<T> {

    void onComplete( @NonNull T response );

}
