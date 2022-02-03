package com.blinkreceipt.ocr.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.blinkreceipt.ocr.R;
import com.blinkreceipt.ocr.Utility;
import com.blinkreceipt.ocr.adapter.ProductsAdapter;
import com.blinkreceipt.ocr.presenter.MainPresenter;
import com.blinkreceipt.ocr.transfer.RecognizerResults;
import com.microblink.BlinkReceiptSdk;
import com.microblink.camera.ui.RecognizerViewActivity;
import com.microblink.camera.ui.ScanSessionResults;
import com.microblink.camera.ui.ScanUIConfiguration;
import com.microblink.camera.ui.ScanUIConfigurationKt;
import com.microblink.camera.ui.internal.ReceiptDataConfigOption;
import com.microblink.core.Product;
import com.microblink.core.internal.CollectionUtils;

import java.util.List;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int PERMISSIONS_REQUEST_CODE = 1000;

    private static final String[] requestPermissions = {
            Manifest.permission.CAMERA
    };

    private MainViewModel viewModel;

    private MainPresenter presenter;

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Intent data = result.getData();

        if (data != null && result.getResultCode() == Activity.RESULT_OK) {
            ScanSessionResults.ScanResults scanResult = data.getParcelableExtra(RecognizerViewActivity.RECOGNIZER_RESULT_KEY);

            if (scanResult != null) {
                viewModel.scanItems(new RecognizerResults(scanResult.getScanResults(), scanResult.getMedia()));
            } else {
                viewModel.scanItems(new RecognizerResults(new Exception(getString(R.string.scan_results_error))));
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        presenter = new MainPresenter();

        final RecyclerView recyclerView = findViewById(R.id.products);

        final ProductsAdapter adapter = new ProductsAdapter();

        LinearLayoutManager manager = new LinearLayoutManager(this);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, manager.getOrientation()));

        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter);

        viewModel.scanItems().observe(this, results -> {
            if (results != null) {
                if (presenter.exception(results)) {
                    Throwable e = results.e();

                    Toast.makeText(MainActivity.this, e != null ? e.toString() :
                            getString(R.string.no_products_found_on_receipt), Toast.LENGTH_LONG).show();

                    return;
                }

                List<Product> products = presenter.products(results);

                if (Utility.isNullOrEmpty(products)) {
                    Toast.makeText(MainActivity.this, R.string.no_products_found_on_receipt, Toast.LENGTH_SHORT).show();

                    return;
                }

                adapter.addAll(products);
            } else {
                Toast.makeText(MainActivity.this, R.string.no_products_found_on_receipt, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sdk_version) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.sdk_version_dialog_title)
                    .setMessage(BlinkReceiptSdk.versionName(this))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();

            return true;
        }

        if (item.getItemId() == R.id.camera) {
            if (EasyPermissions.hasPermissions(this, requestPermissions)) {
                startCameraScanForResult();
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.permissions_rationale),
                        PERMISSIONS_REQUEST_CODE, requestPermissions);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> permissions) {
        startCameraScanForResult();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> permissions) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, permissions)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private void startCameraScanForResult() {
        launcher.launch(RecognizerViewActivity.Companion.createCameraScanIntent(MainActivity.this,
                viewModel.scanOptions(), new ScanUIConfiguration(
                        ScanUIConfigurationKt.getDEFAULT_SCAN_REGION(),
                        true,
                        R.style.BlinkRecognizerStyle,
                        CollectionUtils.newArrayList(
                                ReceiptDataConfigOption.DATE.INSTANCE,
                                ReceiptDataConfigOption.TOTAL.INSTANCE,
                                ReceiptDataConfigOption.MERCHANT.INSTANCE),
                        true,
                        3,
                        3,
                        3
                )));
    }

}
