package com.blinkreceipt.ocr;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.blinkreceipt.ocr.repositories.BitmapRepository;
import com.blinkreceipt.ocr.repositories.RecognizerRepository;
import com.blinkreceipt.ocr.transfer.RecognizeItem;
import com.microblink.Media;
import com.microblink.ScanResults;

public class MainViewModel extends AndroidViewModel {

    private BitmapRepository bitmapRepository;

    private RecognizerRepository recognizerRepository;

    private MutableLiveData<Uri> uri = new MutableLiveData<>();

    private MutableLiveData<RecognizeItem> item = new MutableLiveData<>();

    private LiveData<Pair<ScanResults,Media>> results = Transformations.switchMap( item,
            new Function<RecognizeItem, LiveData<Pair<ScanResults,Media>>>() {

                @Override
                public LiveData<Pair<ScanResults,Media>> apply( RecognizeItem data ) {
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
    }

    public LiveData<Bitmap> bitmap() {
        return bitmap;
    }

    public LiveData<Pair<ScanResults, Media>> results() {
        return results;
    }

    public void uri( @NonNull Uri uri ) {
        this.uri.setValue( uri );
    }

    public void recognizeItem( @NonNull RecognizeItem data ) {
        item.setValue( data );
    }

    @Override
    protected void onCleared() {
        Utility.cancel( bitmapRepository, recognizerRepository );

        super.onCleared();
    }
}
