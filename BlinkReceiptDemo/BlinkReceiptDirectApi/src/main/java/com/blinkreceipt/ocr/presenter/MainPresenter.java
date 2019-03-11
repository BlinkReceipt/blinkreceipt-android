package com.blinkreceipt.ocr.presenter;

import com.microblink.Product;
import com.microblink.ScanResults;

import java.util.List;

import androidx.annotation.Nullable;

public final class MainPresenter {

    @Nullable
    public List<Product> products( @Nullable ScanResults data ) {
        if ( data != null ) {
            return data.products();
        }

        return null;
    }
}
