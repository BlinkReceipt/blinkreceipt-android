package com.blinkreceipt.ocr.ui;

import android.app.Application;

import com.blinkreceipt.ocr.transfer.RecognizerResults;
import com.microblink.EdgeDetectionConfiguration;
import com.microblink.FrameCharacteristics;
import com.microblink.ScanOptions;
import com.microblink.core.Retailer;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {

    private final ScanOptions scanOptions;

    private final MutableLiveData<RecognizerResults> scanItems = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);

        scanOptions = ScanOptions.newBuilder()
                .retailer(Retailer.UNKNOWN)
                .frameCharacteristics(FrameCharacteristics.newBuilder()
                        .externalStorage(false)
                        .build())
                .edgeDetectionConfiguration(EdgeDetectionConfiguration.newBuilder().build())
                .build();
    }

    public void scanItems(@NonNull RecognizerResults item) {
        scanItems.setValue(item);
    }

    public LiveData<RecognizerResults> scanItems() {
        return scanItems;
    }

    public ScanOptions scanOptions() {
        return scanOptions;
    }

}
