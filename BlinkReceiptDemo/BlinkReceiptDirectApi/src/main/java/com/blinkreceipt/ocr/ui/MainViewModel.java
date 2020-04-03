package com.blinkreceipt.ocr.ui;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;

import com.blinkreceipt.ocr.Utility;
import com.blinkreceipt.ocr.repositories.BitmapRepository;
import com.blinkreceipt.ocr.repositories.RecognizerRepository;
import com.blinkreceipt.ocr.transfer.RecognizeItem;
import com.blinkreceipt.ocr.transfer.RecognizerResults;
import com.microblink.EdgeDetectionConfiguration;
import com.microblink.FrameCharacteristics;
import com.microblink.ScanOptions;
import com.microblink.core.Retailer;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class MainViewModel extends AndroidViewModel {

    private BitmapRepository bitmapRepository;

    private RecognizerRepository recognizerRepository;

    private ScanOptions scanOptions;

    private MutableLiveData<Uri> uri = new MutableLiveData<>();

    private MutableLiveData<RecognizeItem> item = new MutableLiveData<>();

    private LiveData<RecognizerResults> results = Transformations.switchMap( item,
            new Function<RecognizeItem, LiveData<RecognizerResults>>() {

                @Override
                public LiveData<RecognizerResults> apply(RecognizeItem data ) {
                    if ( recognizerRepository == null ) {
                        recognizerRepository = new RecognizerRepository( getApplication() );
                    }

                    return recognizerRepository.recognize( data.options(), data.bitmap(), data.orientation() );
                }

            } );

    private LiveData<Bitmap> bitmap = Transformations.switchMap( uri,
            new Function<Uri, LiveData<Bitmap>>() {

                @Override
                public LiveData<Bitmap> apply( Uri data ) {
                    if ( bitmapRepository == null ) {
                        bitmapRepository = new BitmapRepository( getApplication() );
                    }

                    return bitmapRepository.findByUri( data );
                }

            } );

    public MainViewModel(@NonNull Application application ) {
        super( application );

        scanOptions = ScanOptions.newBuilder()
                .retailer( Retailer.UNKNOWN )
                .frameCharacteristics( FrameCharacteristics.newBuilder()
                        .externalStorage( false )
                        .build() )
                .edgeDetectionConfiguration( new EdgeDetectionConfiguration() )
                .build();
    }

    public LiveData<Bitmap> bitmap() {
        return bitmap;
    }

    LiveData<RecognizerResults> results() {
        return results;
    }

    void uri(@NonNull Uri uri) {
        this.uri.setValue( uri );
    }

    void recognizeItem(@NonNull RecognizeItem data) {
        item.setValue( data );
    }

    ScanOptions scanOptions() {
        return scanOptions;
    }

    @Override
    protected void onCleared() {
        Utility.cancel( bitmapRepository, recognizerRepository );

        super.onCleared();
    }
}
