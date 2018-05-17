package com.blinkreceipt.ocr.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.blinkreceipt.ocr.services.BitmapService;
import com.blinkreceipt.ocr.services.BitmapServiceImpl;
import com.microblink.extensions.AndroidRepository;
import com.microblink.extensions.Cancelable;
import com.microblink.extensions.OnNullableCompleteListener;
import com.microblink.extensions.Utility;

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

    public LiveData<Bitmap> findByUri( @NonNull Uri uri ) {
        final MutableLiveData<Bitmap> data = new MutableLiveData<>();

        service.findByUri( uri, new OnNullableCompleteListener<Bitmap>() {

            @Override
            public void onComplete( @Nullable Bitmap bitmap ) {
                data.setValue( bitmap );
            }

        } );

        return data;
    }

    @Override
    public void cancel() {
        Utility.cancel( service );
    }
}
