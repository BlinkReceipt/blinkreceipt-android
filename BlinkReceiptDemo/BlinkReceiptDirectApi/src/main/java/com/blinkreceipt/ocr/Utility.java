package com.blinkreceipt.ocr;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collection;

public final class Utility {

    private static final String TAG = "Utility";

    private Utility() {
        super();
    }

    public static <T> boolean isNullOrEmpty(Collection<T> c) {
        return (c == null) || (c.size() == 0);
    }

    public static void cancel( @NonNull Cancelable ... cancelable ) {
        try {
            if ( cancelable.length > 0 ) {
                for ( Cancelable item : cancelable ) {
                    item.cancel();
                }
            }
        } catch ( Throwable e ) {
            Log.e( TAG, e.toString() );
        }
    }

}
