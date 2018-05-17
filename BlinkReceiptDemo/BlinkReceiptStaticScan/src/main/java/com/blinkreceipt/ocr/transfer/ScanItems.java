package com.blinkreceipt.ocr.transfer;

import android.support.annotation.Nullable;

import com.microblink.Media;
import com.microblink.ScanResults;

public final class ScanItems {

    @Nullable
    private ScanResults results;

    @Nullable
    private Media media;

    public ScanItems(@Nullable ScanResults results, @Nullable Media media ) {
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
        return "ScanItems{" +
                "results=" + results +
                ", media=" + media +
                '}';
    }
}
