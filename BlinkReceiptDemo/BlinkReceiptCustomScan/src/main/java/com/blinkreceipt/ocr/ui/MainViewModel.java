package com.blinkreceipt.ocr.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.blinkreceipt.ocr.transfer.CameraScanItems;
import com.microblink.EdgeDetectionConfiguration;
import com.microblink.FrameCharacteristics;
import com.microblink.Retailer;
import com.microblink.ScanOptions;

public class MainViewModel extends AndroidViewModel {

    private ScanOptions scanOptions;

    private MutableLiveData<CameraScanItems> scanItems = new MutableLiveData<>();

    public MainViewModel( @NonNull Application application ) {
        super( application );

        scanOptions = ScanOptions.newBuilder()
                .retailer( Retailer.UNKNOWN )
                .frameCharacteristics( FrameCharacteristics.newBuilder()
                        .externalStorage( false )
                        .build() )
                .edgeDetectionConfiguration( EdgeDetectionConfiguration.defaults() )
                .build();
    }

    public void scanItems( @NonNull CameraScanItems item ) {
        scanItems.setValue( item );
    }

    public LiveData<CameraScanItems> scanItems() {
        return scanItems;
    }

    public ScanOptions scanOptions() {
        return scanOptions;
    }

}
