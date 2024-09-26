package com.blinkreceipt.ocr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microblink.core.FloatType;
import com.microblink.core.StringType;
import com.microblink.core.internal.StringUtils;

public final class TypeValueUtils {

    private static final float ZERO_FLOAT_VALUE = 0f;

    private TypeValueUtils() {
        super();
    }

    public static float value(@Nullable FloatType type, float optional) {
        return type != null ? type.value() : optional;
    }

    public static float value(@Nullable FloatType type) {
        return value(type, ZERO_FLOAT_VALUE);
    }


    @Nullable
    public static String value(@Nullable StringType type, @NonNull String optional) {
        String value = type != null ? type.value() : null;

        return !StringUtils.isNullOrEmpty(value) ? value : optional;
    }

    @NonNull
    public static String value(@Nullable StringType type) {
        String value = value(type, "");

        if (value == null) {
            return "";
        }

        return value;
    }
}

