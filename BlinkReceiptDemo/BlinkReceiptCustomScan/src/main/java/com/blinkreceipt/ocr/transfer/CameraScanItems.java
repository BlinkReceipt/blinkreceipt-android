package com.blinkreceipt.ocr.transfer;

import android.support.annotation.Nullable;

import com.microblink.Media;
import com.microblink.ScanResults;

public final class CameraScanItems {

    @Nullable
    private ScanResults results;

    @Nullable
    private Media media;

    public CameraScanItems(@Nullable ScanResults results, @Nullable Media media ) {
        super();

        this.results = results;

        this.media = media;
    }

    @Nullable
    public ScanResults results() {
        return results;
    }

    @Nullable
    public Media media() {
        return media;
    }

    @Override
    public String toString() {
        return "CameraScanItems{" +
                "results=" + results +
                ", media=" + media +
                '}';
    }

}
