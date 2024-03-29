package com.blinkreceipt.ocr.ui;

import com.blinkreceipt.ocr.transfer.RecognizerResults;
import com.microblink.EdgeDetectionConfiguration;
import com.microblink.FrameCharacteristics;
import com.microblink.ScanOptions;
import com.microblink.core.Retailer;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private final ScanOptions scanOptions;

    private final MutableLiveData<RecognizerResults> scanItems = new MutableLiveData<>();

    public MainViewModel() {
        super();

        scanOptions = ScanOptions.newBuilder()
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
