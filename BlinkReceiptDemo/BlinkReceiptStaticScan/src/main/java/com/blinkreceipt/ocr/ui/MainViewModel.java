package com.blinkreceipt.ocr.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.blinkreceipt.ocr.transfer.RecognizerResults;
import com.microblink.EdgeDetectionConfiguration;
import com.microblink.FrameCharacteristics;
import com.microblink.Retailer;
import com.microblink.ScanOptions;

public class MainViewModel extends AndroidViewModel {

    private ScanOptions scanOptions;

    private MutableLiveData<RecognizerResults> scanItems = new MutableLiveData<>();

    public MainViewModel( @NonNull Application application ) {
        super( application );

        scanOptions = ScanOptions.newBuilder()
                .retailer( Retailer.UNKNOWN )
                .frameCharacteristics( FrameCharacteristics.newBuilder()
                        .externalStorage( false )
                        .build() )
                .edgeDetectionConfiguration( new EdgeDetectionConfiguration() )
                .build();
    }

    void scanItems(@NonNull RecognizerResults item) {
        scanItems.setValue( item );
    }

    LiveData<RecognizerResults> scanItems() {
        return scanItems;
    }

    ScanOptions scanOptions() {
        return scanOptions;
    }

}
