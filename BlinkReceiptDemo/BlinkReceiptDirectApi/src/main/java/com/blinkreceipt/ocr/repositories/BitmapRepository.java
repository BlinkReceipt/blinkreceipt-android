package com.blinkreceipt.ocr.repositories;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;

import com.blinkreceipt.ocr.AndroidRepository;
import com.blinkreceipt.ocr.Cancelable;
import com.blinkreceipt.ocr.Utility;
import com.blinkreceipt.ocr.services.BitmapService;
import com.blinkreceipt.ocr.services.BitmapServiceImpl;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public final class BitmapRepository extends AndroidRepository implements Cancelable {

    @NonNull
    private BitmapService service;

    public BitmapRepository( @NonNull Application application ) {
        this( application, new BitmapServiceImpl( application ) );
    }

    private BitmapRepository(@NonNull Application application, @NonNull BitmapService service) {
        super( application );

        this.service = service;
    }

    public LiveData<Bitmap> findByUri(@NonNull Uri uri ) {
        final MutableLiveData<Bitmap> data = new MutableLiveData<>();

        service.findByUri( uri, data::setValue );

        return data;
    }

    @Override
    public void cancel() {
        Utility.cancel( service );
    }
}
