package com.blinkreceipt.ocr.repositories;

import android.app.Application;
import android.graphics.Bitmap;

import com.blinkreceipt.ocr.AndroidRepository;
import com.blinkreceipt.ocr.Cancelable;
import com.blinkreceipt.ocr.services.RecognizerService;
import com.blinkreceipt.ocr.services.RecognizerServiceImpl;
import com.blinkreceipt.ocr.transfer.RecognizerResults;
import com.microblink.CameraOrientation;
import com.microblink.ScanOptions;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    public LiveData<RecognizerResults> recognize(@NonNull ScanOptions options, @NonNull Bitmap bitmap, @NonNull CameraOrientation orientation ) {
        final MutableLiveData<RecognizerResults> data = new MutableLiveData<>();

        //This is not on the main thread!
        service.recognize( options, bitmap, orientation, data::postValue);

        return data;
    }

    @Override
    public void cancel() {
        service.cancel();
    }
}
