package com.blinkreceipt.ocr.transfer;

import com.microblink.Media;
import com.microblink.core.ScanResults;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class RecognizerResults {

    @Nullable
    private ScanResults results;

    @Nullable
    private Media media;

    @Nullable
    private Throwable e;

    public RecognizerResults(@Nullable ScanResults results, @Nullable Media media ) {
       this( results, media, null );
    }

    public RecognizerResults(@NonNull Throwable e ) {
        this( null, null, e );
    }

    private RecognizerResults(@Nullable ScanResults results, @Nullable Media media, @Nullable Throwable e) {
        super();

        this.results = results;

        this.media = media;

        this.e = e;
    }

    @Nullable
    public ScanResults results() {
        return results;
    }

    @Nullable
    public Media media() {
        return media;
    }

    @Nullable
    public Throwable e() {
        return e;
    }

    public RecognizerResults e(@NonNull Throwable e ) {
        this.e = e;

        return this;
    }

    @Override
    public String toString() {
        return "RecognizerResults{" +
                "results=" + results +
                ", media=" + media +
                ", e=" + e +
                '}';
    }
}
