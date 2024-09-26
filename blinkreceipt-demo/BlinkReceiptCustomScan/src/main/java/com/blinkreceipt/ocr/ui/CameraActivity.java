package com.blinkreceipt.ocr.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blinkreceipt.ocr.R;
import com.microblink.BitmapResult;
import com.microblink.CameraCaptureListener;
import com.microblink.CameraRecognizerCallback;
import com.microblink.Media;
import com.microblink.RecognizerResult;
import com.microblink.RecognizerView;
import com.microblink.core.ScanResults;

import java.io.File;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

public class CameraActivity extends AppCompatActivity implements CameraRecognizerCallback {

    private RecognizerView recognizerView;

    private Button finishScan;

    private View torch;

    private boolean isTorchOn = false;

    private final String TAG = "CameraActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_scan);

        recognizerView = findViewById(R.id.recognizer);

        finishScan = findViewById(R.id.finish_scan);

        finishScan.setOnClickListener(view -> {
            try {
                Toast.makeText(getApplicationContext(), R.string.finishing, Toast.LENGTH_SHORT).show();

                view.setEnabled(false);

                recognizerView.finishedScanning();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        torch = findViewById(R.id.torch);

        torch.setOnClickListener(v -> recognizerView.setTorchState(!isTorchOn, success -> {
            if (success) {
                isTorchOn = !isTorchOn;
            }
        }));

        final Button captureFrame = findViewById(R.id.capture_photo);

        captureFrame.setOnClickListener(v -> recognizerView.takePicture(new CameraCaptureListener() {

            @Override
            public void onCaptured(@NonNull BitmapResult results) {
                recognizerView.confirmPicture(results);

                Toast.makeText(getApplicationContext(), R.string.captured_photo, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(@NonNull Throwable e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }

        }));

        RectF regionOfInterest = new RectF(.05f, .10f, .95f, .90f);

        recognizerView.recognizerCallback(this);

        recognizerView.scanRegion(regionOfInterest);

        recognizerView.setMeteringAreas(new RectF[]{
                regionOfInterest
        }, true);

        try {
            recognizerView.initialize(Objects.requireNonNull(getIntent()
                    .getParcelableExtra(MainActivity.SCAN_OPTIONS_EXTRA)));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();

            finish();
        }

        recognizerView.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (recognizerView != null) {
            recognizerView.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (recognizerView != null) {
            recognizerView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (recognizerView != null) {
            recognizerView.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (recognizerView != null) {
            recognizerView.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recognizerView != null) {
            try {
                recognizerView.destroy();
            } catch (Exception e) {
                Log.e(TAG, "failure in onDestroy", e);
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (recognizerView != null) {
            recognizerView.changeConfiguration(newConfig);
        }
    }

    @Override
    public void onRecognizerDone(@NonNull ScanResults results, @NonNull Media media) {
        finishScan.setEnabled(true);

        setResult(Activity.RESULT_OK, new Intent()
                .putExtra(MainActivity.DATA_EXTRA, results)
                .putExtra(MainActivity.MEDIA_EXTRA, media));

        finish();
    }

    @Override
    public void onRecognizerException(@NonNull Throwable throwable) {
        finishScan.setEnabled(true);

        Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecognizerResultsChanged(@NonNull RecognizerResult result) {
        Log.d(TAG, "results: " + result);
    }

    @Override
    public void onConfirmPicture(@NonNull File file) {
        Log.d(TAG, file.toString());
    }

    @Override
    public void onPermissionDenied() {
    }

    @Override
    public void onPreviewStarted() {
        if (recognizerView.isCameraTorchSupported()) {
            torch.setVisibility(View.VISIBLE);
        } else {
            torch.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPreviewStopped() {
    }

    @Override
    public void onException(@NonNull Throwable throwable) {
        Log.e(TAG, "failure in onException", throwable);

        Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_LONG).show();
    }

}
