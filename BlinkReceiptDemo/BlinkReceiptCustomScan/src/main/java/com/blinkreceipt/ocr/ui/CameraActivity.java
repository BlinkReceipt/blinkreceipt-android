package com.blinkreceipt.ocr.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blinkreceipt.ocr.R;
import com.microblink.BitmapResult;
import com.microblink.CameraCaptureListener;
import com.microblink.CameraRecognizerCallback;
import com.microblink.IntentUtils;
import com.microblink.Media;
import com.microblink.RecognizerCompatibility;
import com.microblink.RecognizerResult;
import com.microblink.RecognizerView;
import com.microblink.ScanOptions;
import com.microblink.ScanResults;
import com.microblink.camera.hardware.SuccessCallback;

import java.io.File;

public class CameraActivity extends AppCompatActivity implements CameraRecognizerCallback {

    private static final String TAG = "CameraActivity";

    private RecognizerView recognizerView;

    private Button finishScan;

    private View torch;

    private boolean isTorchOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( R.layout.activity_camera_scan );

        recognizerView = findViewById( R.id.recognizer );

        finishScan = findViewById( R.id.finish_scan );

        finishScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    Toast.makeText( CameraActivity.this, R.string.finishing, Toast.LENGTH_SHORT ).show();

                    view.setEnabled( false );

                    recognizerView.finishedScanning();
                } catch ( Exception e ) {
                    Toast.makeText( CameraActivity.this, e.toString(), Toast.LENGTH_SHORT ).show();
                }
            }

        } );

        torch = findViewById( R.id.torch );

        torch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                recognizerView.setTorchState( !isTorchOn, new SuccessCallback() {

                    @Override
                    public void onOperationDone(boolean success) {
                        if ( success ) {
                            isTorchOn = !isTorchOn;
                        }
                    }

                } );
            }
        });

        final Button captureFrame = findViewById( R.id.capture_photo );

        captureFrame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                recognizerView.takePicture( new CameraCaptureListener() {

                    @Override
                    public void onCaptured( @NonNull BitmapResult results ) {
                        recognizerView.confirmPicture( results );

                        Toast.makeText( CameraActivity.this, R.string.captured_photo, Toast.LENGTH_SHORT ).show();
                    }

                    @Override
                    public void onException(@NonNull Exception e) {
                        Toast.makeText( CameraActivity.this, e.toString(), Toast.LENGTH_LONG ).show();
                    }

                } );
            }
        } );

        RectF regionOfInterest = RecognizerCompatibility.defaultRegionOfInterest();

        recognizerView.recognizerCallback( this );

        recognizerView.scanRegion( regionOfInterest );

        recognizerView.setMeteringAreas( new RectF[] {
                regionOfInterest
        }, true );

        try {
            recognizerView.initialize(( ScanOptions) getIntent().getParcelableExtra( MainActivity.SCAN_OPTIONS_EXTRA ) );
        } catch ( Exception e ) {
            Toast.makeText( this, e.toString(), Toast.LENGTH_LONG ).show();

            finish();
        }

        recognizerView.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        if ( recognizerView != null ) {
            recognizerView.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if ( recognizerView != null ) {
            recognizerView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if ( recognizerView != null ) {
            recognizerView.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if ( recognizerView != null ) {
            recognizerView.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if ( recognizerView != null ) {
           try {
               recognizerView.destroy();
           } catch ( Exception e ) {
               Log.e( TAG, e.toString() );
           }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if ( recognizerView != null ) {
            recognizerView.changeConfiguration(newConfig);
        }
    }

    @Override
    public void onRecognizerDone( @NonNull ScanResults results, @NonNull Media media ) {
        finishScan.setEnabled( true );

        setResult( Activity.RESULT_OK, new Intent()
                .putExtra( IntentUtils.DATA_EXTRA, results )
                .putExtra( IntentUtils.MEDIA_EXTRA, media ) );

        finish();
    }

    @Override
    public void onRecognizerException( @NonNull Throwable throwable ) {
        Log.e( TAG, throwable.toString() );

        finishScan.setEnabled( true );

        Toast.makeText( this, throwable.toString(), Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onRecognizerResultsChanged( @NonNull RecognizerResult result ) {
        Log.d( TAG, "results: " + result.toString() );
    }

    @Override
    public void onConfirmPicture( @NonNull File file ) {
        Log.d( TAG, file.toString() );
    }

    @Override
    public void onPermissionDenied() { }

    @Override
    public void onPreviewStarted() {
        if ( recognizerView.isCameraTorchSupported() ) {
            torch.setVisibility( View.VISIBLE );
        } else {
            torch.setVisibility( View.GONE );
        }
    }

    @Override
    public void onPreviewStopped() { }

    @Override
    public void onException( @NonNull Throwable throwable ) {
        Log.e( TAG, throwable.toString() );

        Toast.makeText( this, throwable.toString(), Toast.LENGTH_LONG ).show();
    }

}
