package com.blinkreceipt.digital.imap;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import okio.BufferedSource;
import okio.Okio;

public final class IOUtils {

    private IOUtils() {
        super();

        throw new InstantiationError("IOUtils constructor can't be called!");
    }

    @Nullable
    public static String tryReadStream(@NonNull final InputStream stream) {
        try {
            final BufferedSource source = Okio.buffer(Okio.source(stream));

            String data = source.readUtf8();

            source.close();

            return data;
        } catch (IOException e) {
            Log.e("IOUtils", "failure in tryReadStream", e);

            return null;
        }
    }
}
