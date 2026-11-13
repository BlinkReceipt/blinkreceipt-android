package com.blinkreceipt.ocr.ui;

import com.blinkreceipt.ocr.transfer.RecognizerResults;
import com.actualplatform.receipt.EdgeDetectionConfiguration;
import com.actualplatform.receipt.FrameCharacteristics;
import com.actualplatform.receipt.ScanOptions;
import com.actualplatform.receipt.core.Retailer;

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
                .detectDuplicates(true)
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
