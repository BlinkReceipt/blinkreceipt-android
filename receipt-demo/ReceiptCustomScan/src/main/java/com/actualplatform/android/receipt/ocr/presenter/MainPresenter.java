package com.actualplatform.android.receipt.ocr.presenter;

import com.actualplatform.android.receipt.ocr.transfer.RecognizerResults;
import com.actualplatform.receipt.core.Product;
import com.actualplatform.receipt.core.ScanResults;

import java.util.List;

import androidx.annotation.Nullable;

public final class MainPresenter {

    @Nullable
    public List<Product> products(@Nullable RecognizerResults items ) {
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
