package com.blinkreceipt.ocr.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.blinkreceipt.ocr.R;
import com.blinkreceipt.ocr.Utility;
import com.blinkreceipt.ocr.adapter.ProductsAdapter;
import com.blinkreceipt.ocr.databinding.ActivityMainBinding;
import com.blinkreceipt.ocr.presenter.MainPresenter;
import com.blinkreceipt.ocr.transfer.RecognizerResults;
import com.microblink.BlinkReceiptSdk;
import com.microblink.Media;
import com.microblink.core.Product;
import com.microblink.core.ScanResults;

import java.util.List;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewGroupCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private ActivityMainBinding binding;

    public static final String DATA_EXTRA = "dataExtra";

    public static final String MEDIA_EXTRA = "mediaExtra";

    public static final String SCAN_OPTIONS_EXTRA = "scanOptionsExtra";

    private static final int PERMISSIONS_REQUEST_CODE = 1000;

    private static final String[] requestPermissions = {
            Manifest.permission.CAMERA
    };

    private MainViewModel viewModel;

    private MainPresenter presenter;

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Intent data = result.getData();

        if (data != null && result.getResultCode() == Activity.RESULT_OK) {
            ScanResults scanResult = data.getParcelableExtra(MainActivity.DATA_EXTRA);

            if (scanResult != null) {
                Media media = data.getParcelableExtra(MainActivity.MEDIA_EXTRA);

                viewModel.scanItems(new RecognizerResults(scanResult,media));
            } else {
                viewModel.scanItems(new RecognizerResults(new Exception(getString(R.string.scan_results_error))));
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(this.getWindow());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() |
                            WindowInsetsCompat.Type.displayCutout()
            );
            // Apply the insets as padding to the view. Here, set all the dimensions
            // as appropriate to your layout. You can also update the view's margin if
            // more appropriate.
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);

            // Return CONSUMED if you don't want the window insets to keep passing down
            // to descendant views.
            return WindowInsetsCompat.CONSUMED;
        });
        ViewGroupCompat.installCompatInsetsDispatch(rootView);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.getWindow().setStatusBarContrastEnforced(true);
        }

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
        launcher.launch(new Intent(this, CameraActivity.class)
                .putExtra(SCAN_OPTIONS_EXTRA, viewModel.scanOptions()));
    }

}
