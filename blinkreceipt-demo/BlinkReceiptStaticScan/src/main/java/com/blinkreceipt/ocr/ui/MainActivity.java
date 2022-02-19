package com.blinkreceipt.ocr.ui;

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
import com.microblink.camera.ui.CameraCharacteristics;
import com.microblink.camera.ui.CameraRecognizerContract;
import com.microblink.camera.ui.CameraRecognizerOptions;
import com.microblink.camera.ui.CameraRecognizerResults;
import com.microblink.camera.ui.ScanCharacteristics;
import com.microblink.camera.ui.TooltipCharacteristics;
import com.microblink.core.Product;

import java.util.List;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    private MainPresenter presenter;

    private final ActivityResultLauncher<CameraRecognizerOptions> launcher = registerForActivityResult(new CameraRecognizerContract(), result -> {
        if (result instanceof CameraRecognizerResults.Success) {
            CameraRecognizerResults.Success results = ((CameraRecognizerResults.Success) result);

            viewModel.scanItems(new RecognizerResults(results.scanResults(), results.media()));
        } else if (result instanceof CameraRecognizerResults.Exception) {
            viewModel.scanItems(new RecognizerResults(((CameraRecognizerResults.Exception) result).exception()));
        } else {
            Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
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
            launcher.launch(new CameraRecognizerOptions.Builder()
                    .options(viewModel.scanOptions())
                    .characteristics(new CameraCharacteristics.Builder()
                            .cameraPermission(true)
                            .scanCharacteristics( new ScanCharacteristics.Builder()
                                    .total(true)
                                    .merchant(true)
                                    .date(true)
                                    .build())
                            .tooltipCharacteristics(
                                    new TooltipCharacteristics.Builder()
                                            .displayTooltips(true)
                                            .build())
                            .build())
                    .build());

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
