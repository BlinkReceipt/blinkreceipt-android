package com.blinkreceipt.ocr.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.blinkreceipt.ocr.AndroidRepository;
import com.blinkreceipt.ocr.Cancelable;
import com.blinkreceipt.ocr.OnNullableCompleteListener;
import com.blinkreceipt.ocr.services.RecognizerService;
import com.blinkreceipt.ocr.services.RecognizerServiceImpl;
import com.microblink.CameraOrientation;
import com.microblink.Media;
import com.microblink.ScanOptions;
import com.microblink.ScanResults;

public final class RecognizerRepository extends AndroidRepository implements Cancelable {

    @NonNull
    private RecognizerService service;

    public RecognizerRepository( @NonNull Application application ) {
        this( application, new RecognizerServiceImpl( application ) );
    }

    private RecognizerRepository(@NonNull Application application, @NonNull RecognizerService service) {
        super( application );

        this.service = service;
    }

    public LiveData<Pair<ScanResults,Media>> recognize( @NonNull ScanOptions options, @NonNull Bitmap bitmap, @NonNull CameraOrientation orientation ) {
        final MutableLiveData<Pair<ScanResults,Media>> data = new MutableLiveData<>();

        service.recognize( options, bitmap, orientation, new OnNullableCompleteListener<Pair<ScanResults, Media>>() {

            @Override
            public void onComplete( @Nullable Pair<ScanResults, Media> response ) {
                //This is not on the main thread!

                data.postValue( response );
            }

        } );

        return data;
    }

    @Override
    public void cancel() {
        service.cancel();
    }
}
