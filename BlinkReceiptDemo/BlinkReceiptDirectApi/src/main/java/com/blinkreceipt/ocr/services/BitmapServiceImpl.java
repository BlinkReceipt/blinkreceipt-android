package com.blinkreceipt.ocr.services;

import android.app.Application;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.microblink.extensions.OnNullableCompleteListener;

public final class BitmapServiceImpl implements BitmapService {

    private static final String TAG = "BitmapServiceImpl";

    private AsyncTask<String,Void,Bitmap> task;

    @NonNull
    private Application application;

    public BitmapServiceImpl( @NonNull Application application ) {
        this.application = application;
    }

    @Override
    public void findByUri( @NonNull Uri uri, @NonNull OnNullableCompleteListener<Bitmap> listener ) {
        cancel();

        try {
            new BitmapTask( application, listener ).execute( uri );
        } catch ( Throwable e ) {
            Log.e( TAG, e.toString() );

            listener.onComplete( null );
        }
    }

    @Override
    public void cancel() {
        try {
            if ( task != null && task.getStatus() == AsyncTask.Status.RUNNING ) {
                task.cancel( true );
            }

            task = null;
        } catch ( Throwable e ) {
            Log.e( TAG, e.toString() );
        }
    }

    private static final class BitmapTask extends AsyncTask<Uri,Void,Bitmap> {

        @NonNull
        private Application application;

        @NonNull
        private OnNullableCompleteListener<Bitmap> listener;

        BitmapTask(@NonNull Application application, @NonNull OnNullableCompleteListener<Bitmap> listener) {
            this.application = application;

            this.listener = listener;
        }

        @Override
        protected void onPostExecute( Bitmap bitmap ) {
            listener.onComplete( bitmap );
        }

        @Override
        protected Bitmap doInBackground( Uri... parameters ) {
            if ( isCancelled() ) {
                return null;
            }

            try {
                final ContentResolver contentResolver = application.getContentResolver();

                return MediaStore.Images.Media.getBitmap( contentResolver, parameters[ 0 ] );
            } catch ( Throwable e ) {
                Log.e( TAG, e.toString() );
            }

            return null;
        }

    }
}
