package com.blinkreceipt.ocr.presenter;

import java.util.List;

import androidx.annotation.Nullable;

import com.microblink.core.Product;
import com.microblink.core.ScanResults;

public final class MainPresenter {

    @Nullable
    public List<Product> products(@Nullable ScanResults data ) {
        if ( data != null ) {
            return data.products();
        }

        return null;
    }
}
