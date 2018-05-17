package com.microblink.extensions;

import android.support.annotation.NonNull;

public interface OnCompleteListener<T> {

    void onComplete( @NonNull T response );
}
