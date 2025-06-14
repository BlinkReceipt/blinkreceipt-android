package com.blinkreceipt.ocr.ui;

import com.blinkreceipt.ocr.transfer.RecognizerResults;
import com.microblink.EdgeDetectionConfiguration;
import com.microblink.FrameCharacteristics;
import com.microblink.ScanOptions;

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
                .detectBarcodes(true)
                .frameCharacteristics(FrameCharacteristics.newBuilder()
                        .externalStorage(false)
                        .storeFrames(true)
                        .build())
                .edgeDetectionConfiguration(EdgeDetectionConfiguration.newBuilder().build())
                .build();
    }

    void scanItems(@NonNull RecognizerResults item) {
        scanItems.setValue(item);
    }

    LiveData<RecognizerResults> scanItems() {
        return scanItems;
    }

    ScanOptions scanOptions() {
        return scanOptions;
    }

}
