package com.blinkreceipt.ocr.services;

import com.microblink.CameraRecognizerCallback;
import com.microblink.Media;
import com.microblink.RecognizerResult;
import com.microblink.core.ScanResults;
import com.microblink.core.internal.Timberland;

import java.io.File;

import androidx.annotation.NonNull;

public class SimpleCameraRecognizerCallback implements CameraRecognizerCallback  {

    @Override
    public void onConfirmPicture(@NonNull File file) {
        Timberland.d( "onConfirmPicture: " + file.toString() );
    }

    @Override
    public void onPermissionDenied() {
        Timberland.d( "onPermissionDenied " );
    }

    @Override
    public void onPreviewStarted() {
        Timberland.d( "onPreviewStarted" );
    }

    @Override
    public void onPreviewStopped() {
        Timberland.d( "onPreviewStopped" );
    }

    @Override
    public void onException(@NonNull Throwable throwable) {
        Timberland.e( "onException: " + throwable.toString() );
    }

    @Override
    public void onRecognizerDone(@NonNull ScanResults scanResults, @NonNull Media media) {
        Timberland.d( "onRecognizerDone");
    }

    @Override
    public void onRecognizerException(@NonNull Throwable throwable) {
        Timberland.e( "onRecognizerException: " + throwable.toString() );
    }

    @Override
    public void onRecognizerResultsChanged(@NonNull RecognizerResult recognizerResult) {
        Timberland.d( "onRecognizerResultsChanged" );
    }

}
