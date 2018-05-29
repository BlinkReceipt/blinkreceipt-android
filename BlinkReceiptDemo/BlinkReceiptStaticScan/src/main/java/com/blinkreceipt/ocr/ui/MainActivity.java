package com.blinkreceipt.ocr.ui;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.blinkreceipt.ocr.R;
import com.blinkreceipt.ocr.adapter.ProductsAdapter;
import com.blinkreceipt.ocr.presenter.MainPresenter;
import com.blinkreceipt.ocr.transfer.CameraScanItems;
import com.microblink.IntentUtils;
import com.microblink.Media;
import com.microblink.Product;
import com.microblink.ScanResults;
import com.microblink.extensions.Utility;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int PERMISSIONS_REQUEST_CODE = 1000;

    private static final int CAMERA_SCAN_REQUEST_CODE = 1001;

    private static final String[] requestPermissions = {
            Manifest.permission.CAMERA
    };

    private MainViewModel viewModel;

    private MainPresenter presenter;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_main );

        viewModel = ViewModelProviders.of( this ).get( MainViewModel.class );

        presenter = new MainPresenter();

        final RecyclerView recyclerView = findViewById(R.id.products);

        final ProductsAdapter adapter = new ProductsAdapter();

        LinearLayoutManager manager = new LinearLayoutManager( this );

        recyclerView.addItemDecoration( new DividerItemDecoration(this, manager.getOrientation() ) );

        recyclerView.setLayoutManager( manager );

        recyclerView.setAdapter( adapter );

        viewModel.scanItems().observe(this, new Observer<CameraScanItems>() {

            @Override
            public void onChanged( @Nullable CameraScanItems scanResult ) {
                if ( scanResult != null ) {
                    List<Product> products = presenter.products( scanResult );

                    if ( Utility.isNullOrEmpty( products ) ) {
                        Toast.makeText( MainActivity.this, R.string.no_products_found_on_receipt, Toast.LENGTH_SHORT ).show();

                       return;
                    }

                    adapter.addAll( products );

                }
            }

        } );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate( R.menu.main_menu, menu );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item ) {
        switch( item.getItemId() ) {
            case R.id.camera:
                askForPermissions();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );

        switch ( requestCode ) {
            case PERMISSIONS_REQUEST_CODE:
                startCameraScanForResult();

                break;
            case CAMERA_SCAN_REQUEST_CODE:
                switch ( resultCode ) {
                    case Activity.RESULT_OK:
                        ScanResults results = data.getParcelableExtra( IntentUtils.DATA_EXTRA );

                        Media media = data.getParcelableExtra( IntentUtils.MEDIA_EXTRA );

                        viewModel.scanItems( new CameraScanItems( results, media ) );

                        break;
                }

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );

        EasyPermissions.onRequestPermissionsResult( requestCode, permissions, grantResults, this );
    }

    @Override
    public void onPermissionsGranted( int requestCode, @NonNull List<String> permissions ) {
        startCameraScanForResult();
    }

    @Override
    public void onPermissionsDenied( int requestCode, @NonNull List<String> permissions ) {
        if ( EasyPermissions.somePermissionPermanentlyDenied( this, permissions ) ) {
            new AppSettingsDialog.Builder( this ).build().show();
        }
    }

    @AfterPermissionGranted( PERMISSIONS_REQUEST_CODE )
    private void askForPermissions() {
        if ( EasyPermissions.hasPermissions( this, requestPermissions ) ) {
            startCameraScanForResult();
        } else {
            EasyPermissions.requestPermissions(this, getString( R.string.permissions_rationale ),
                    PERMISSIONS_REQUEST_CODE, requestPermissions );
        }
    }

    private void startCameraScanForResult() {
        startActivityForResult( IntentUtils.cameraScan( this, viewModel.scanOptions() ), CAMERA_SCAN_REQUEST_CODE );
    }

}
