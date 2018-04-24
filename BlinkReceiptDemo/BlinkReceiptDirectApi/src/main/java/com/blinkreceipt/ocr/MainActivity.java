package com.blinkreceipt.ocr;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blinkreceipt.ocr.transfer.RecognizeItem;
import com.microblink.CameraOrientation;
import com.microblink.FrameCharacteristics;
import com.microblink.Media;
import com.microblink.Product;
import com.microblink.Retailer;
import com.microblink.ScanOptions;
import com.microblink.ScanResults;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int GALLERY_IMAGE_REQUEST = 1984;

    private static final String TAG = "MainActivity";

    private TextView results;

    private MainViewModel viewModel;

    private Button btnRecognize;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_main );

        viewModel = ViewModelProviders.of( this ).get( MainViewModel.class );

        results = findViewById( R.id.results );

        btnRecognize = findViewById( R.id.recognize );

        final ScanOptions options = ScanOptions.newBuilder()
                .retailer( Retailer.UNKNOWN )
                .frameCharacteristics( FrameCharacteristics.newBuilder()
                        .storeFrames( true )
                        .build() )
                .logoDetection( true )
                .build();

        viewModel.results().observe(this, new Observer<Pair<ScanResults, Media>>() {

            @Override
            public void onChanged( @Nullable Pair<ScanResults, Media> data ) {
                btnRecognize.setEnabled( true );

                if ( data == null ) {
                    results.setText( null );

                    Toast.makeText( MainActivity.this, R.string.scan_results_no_products_found, Toast.LENGTH_LONG ).show();

                    return;
                }

                final ScanResults scanResults = data.first;

                if ( scanResults == null ) {
                    results.setText( null );

                    Toast.makeText( MainActivity.this, R.string.scan_results_no_products_found, Toast.LENGTH_LONG ).show();

                    return;
                }

                Log.d( TAG, scanResults.toString() );

                List<Product> products = scanResults.products();

                if ( !Utility.isNullOrEmpty( products ) ) {
                    results.setText( getString( R.string.scan_results_products, products.size() ) );
                } else {
                    results.setText( R.string.scan_results_no_products_found );
                }
            }

        } );

        viewModel.bitmap().observe(this, new Observer<Bitmap>() {

            @Override
            public void onChanged( @Nullable Bitmap bitmap ) {
                if ( bitmap == null ) {
                    btnRecognize.setEnabled( true );

                    results.setText( null );

                    Toast.makeText( MainActivity.this, R.string.bitmap_not_found, Toast.LENGTH_LONG ).show();

                    return;
                }

                viewModel.recognizeItem( new RecognizeItem( options, bitmap, CameraOrientation.ORIENTATION_PORTRAIT ) );
            }

        } );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch ( requestCode ) {
            case GALLERY_IMAGE_REQUEST:
                switch ( resultCode ) {
                    case Activity.RESULT_OK:
                        try {
                            if ( data == null ) {
                                Toast.makeText( this, R.string.gallery_picture_exception, Toast.LENGTH_LONG ).show();

                                return;
                            }

                            results.setText( R.string.recognize_results_progress );

                            btnRecognize.setEnabled( false );

                            Uri uri = data.getData();

                            if ( uri == null ) {
                                Toast.makeText( this, R.string.gallery_picture_exception, Toast.LENGTH_LONG ).show();

                                return;
                            }

                            viewModel.uri( uri );
                        } catch ( Throwable e ) {
                            Log.e( TAG, e.toString() );

                            Toast.makeText( this, e.toString(), Toast.LENGTH_LONG ).show();

                            btnRecognize.setEnabled( true );
                        }

                        break;
                }

                break;
        }
    }

    public void onRecognizeClick(final @NonNull View view ) {
        results.setText( null );

        startActivityForResult( Intent.createChooser( new Intent()
                .setType( "image/*" )
                .setAction( Intent.ACTION_GET_CONTENT ),
                getString( R.string.gallery_picture_title ) ), GALLERY_IMAGE_REQUEST );
    }
}
