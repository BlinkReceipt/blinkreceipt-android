package com.blinkreceipt.ocr.services;

import android.app.Application;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.blinkreceipt.ocr.OnNullableCompleteListener;
import com.blinkreceipt.ocr.transfer.RecognizerResults;
import com.microblink.CameraOrientation;
import com.microblink.Media;
import com.microblink.Recognizer;
import com.microblink.ScanOptions;
import com.microblink.ScanResults;

public final class RecognizerServiceImpl implements RecognizerService {

    private static final String TAG = "RecognizerServiceImpl";

    @NonNull
    private Application application;

    public RecognizerServiceImpl( @NonNull Application application ) {
        this.application = application;
    }

    @Override
    public void recognize(@NonNull ScanOptions options, @NonNull Bitmap bitmap, @NonNull CameraOrientation orientation,
                          final @NonNull OnNullableCompleteListener<RecognizerResults> listener  ) {
        try {
            Recognizer.getInstance().initialize( options );

            Recognizer.getInstance().recognize( application, new SimpleCameraRecognizerCallback() {

                        @Override
                        public void onRecognizerDone(@NonNull final ScanResults results, @NonNull final Media media ) {
                            super.onRecognizerDone( results, media );

                            listener.onComplete( new RecognizerResults( results, media ) );
                        }

                        @Override
                        public void onRecognizerException( @NonNull final Throwable throwable ) {
                            super.onRecognizerException( throwable );

                            Log.e( TAG, throwable.toString() );

                            listener.onComplete( new RecognizerResults( throwable ) );
                        }
                    }, orientation, bitmap ) ;
        } catch ( Throwable e ) {
            Log.e( TAG, e.toString() );

            listener.onComplete( new RecognizerResults( e ) );
        }
    }

    @Override
    public void cancel() { }
}
