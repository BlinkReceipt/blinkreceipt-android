package com.blinkreceipt.ocr;

import android.support.annotation.NonNull;
import android.util.Log;

import com.microblink.CameraRecognizerCallback;
import com.microblink.Media;
import com.microblink.RecognizerResult;
import com.microblink.ScanResults;

import java.io.File;

public class SimpleCameraRecognizerCallback implements CameraRecognizerCallback  {

    private static final String TAG = "CameraRecognizer";

    @Override
    public void onConfirmPicture(@NonNull File file) {
        Log.d( TAG, "onConfirmPicture: " + file.toString() );
    }

    @Override
    public void onPermissionDenied() {
        Log.d( TAG, "onPermissionDenied " );
    }

    @Override
    public void onPreviewStarted() {
        Log.d( TAG, "onPreviewStarted" );
    }

    @Override
    public void onPreviewStopped() {
        Log.d( TAG, "onPreviewStopped" );
    }

    @Override
    public void onException(@NonNull Throwable throwable) {
        Log.e( TAG, "onException: " + throwable.toString() );
    }

    @Override
    public void onRecognizerDone(@NonNull ScanResults scanResults, Media media) {
        Log.d( TAG, "onRecognizerDone");
    }

    @Override
    public void onRecognizerException(@NonNull Throwable throwable) {
        Log.e( TAG, "onRecognizerException: " + throwable.toString() );
    }

    @Override
    public void onRecognizerResultsChanged(@NonNull RecognizerResult recognizerResult) {
        Log.d( TAG, "onRecognizerResultsChanged" );
    }

}
