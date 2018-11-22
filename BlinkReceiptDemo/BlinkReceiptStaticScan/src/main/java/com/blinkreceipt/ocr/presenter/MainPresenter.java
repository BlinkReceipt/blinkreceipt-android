package com.blinkreceipt.ocr.presenter;

import android.support.annotation.Nullable;

import com.blinkreceipt.ocr.transfer.RecognizerResults;
import com.microblink.Product;
import com.microblink.ScanResults;

import java.util.List;

public final class MainPresenter {

    @Nullable
    public List<Product> products( @Nullable RecognizerResults items ) {
        if ( items != null ) {
            ScanResults results = items.results();

            return results != null ? results.products() : null;
        }

        return null;
    }

    public boolean exception( @Nullable RecognizerResults results ) {
        return results != null && results.e() != null;
    }
}
