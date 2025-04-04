package com.blinkreceipt.directscan.ui;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blinkreceipt.directscan.R;
import com.blinkreceipt.directscan.ui.recyclerview.ImageAdapter;
import com.microblink.ImageClient;
import com.microblink.Media;
import com.microblink.RecognizerCallback;
import com.microblink.RecognizerResult;
import com.microblink.ScanOptions;
import com.microblink.core.ScanResults;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView imageRecyclerview;

    private Button selectImageBtn;

    private Button scanImageBtn;

    private Button rotateImagesLeftBtn;

    private Button rotateImagesRightBtn;

    private ImageAdapter imageAdapter;

    private Bitmap[] bitmaps;

    private ImageClient client;

    private final String TAG = "DirectScanMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        initializeRecyclerView();

        initializeClient();

        ActivityResultLauncher<String> imageRequest = registerForActivityResult(
                new ActivityResultContracts.GetMultipleContents(),
                (imageUris) -> {
                    if (!imageUris.isEmpty()) {
                        addImagesToAdapter(imageUris);
                    }
                }
        );
        selectImageBtn.setOnClickListener(v -> {
            imageRequest.launch("image/*");
        });

        rotateImagesLeftBtn.setOnClickListener(v -> rotateBitmapsLeft());
        rotateImagesRightBtn.setOnClickListener(v -> rotateBitmapsRight());

        scanImageBtn.setOnClickListener(v -> {
            sendBitmapsForScanning();
        });
    }

    private void sendBitmapsForScanning() {
        client.recognize(ScanOptions.newBuilder()
                .build(), new RecognizerCallback() {
            @Override
            public void onRecognizerDone(@NonNull ScanResults scanResults, @NonNull Media media) {
                Toast.makeText(MainActivity.this, "Results: " + scanResults.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRecognizerException(@NonNull Throwable throwable) {
                Toast.makeText(MainActivity.this, "Exception: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRecognizerResultsChanged(@NonNull RecognizerResult recognizerResult) {

            }
        }, bitmaps);
    }

    private void initializeClient() {
        client = new ImageClient(this);
    }

    private void initializeRecyclerView() {
        imageAdapter = new ImageAdapter();
        imageRecyclerview.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        imageRecyclerview.setAdapter(imageAdapter);
    }

    private void initializeViews() {
        imageRecyclerview = findViewById(R.id.image_rv);
        selectImageBtn = findViewById(R.id.select_image);
        scanImageBtn = findViewById(R.id.scan_image);
        rotateImagesLeftBtn = findViewById(R.id.rotate_image_left);
        rotateImagesRightBtn = findViewById(R.id.rotate_image_right);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        imageRecyclerview.setAdapter(null);
        imageRecyclerview.setLayoutManager(null);

        selectImageBtn.setOnClickListener(null);
        rotateImagesLeftBtn.setOnClickListener(null);
        rotateImagesRightBtn.setOnClickListener(null);
        scanImageBtn.setOnClickListener(null);
    }

    private void addImagesToAdapter(@NonNull List<Uri> uris) {
        Bitmap[] bitmaps = new Bitmap[uris.size()];

        for (int i = 0; i < uris.size(); i++) {
            Uri uri = uris.get(i);

            if (uri != null) {
                Bitmap bmp = loadBitmapFromUri(uri);

                if (bmp != null) {
                    bitmaps[i] = bmp;
                }
            }
        }

        this.bitmaps = bitmaps;
        imageAdapter.addAll(bitmaps);
    }

    @Nullable
    @SuppressWarnings("deprecation")
    private Bitmap loadBitmapFromUri(@NonNull Uri uri) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
                return ImageDecoder.decodeBitmap(source);
            } else {
                return MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            }
        } catch (Exception e) {
            Log.e(TAG, "failure in loadBitmapFromUri", e);
        }

        return null;
    }

    private boolean areBitmapsLoaded() {
        return bitmaps != null && bitmaps.length > 0;
    }

    private void rotateBitmapsRight() {
        if (!areBitmapsLoaded()) {
            Toast.makeText(this, "Please load images before rotating", Toast.LENGTH_SHORT).show();

            return;
        }

        Bitmap[] rotatedBitmaps = new Bitmap[bitmaps.length];
        for (int i = 0; i < bitmaps.length; i++) {
            rotatedBitmaps[i] = rotateBitmap(bitmaps[i], 90);
        }

        this.bitmaps = rotatedBitmaps;
        imageAdapter.addAll(bitmaps);
    }

    private void rotateBitmapsLeft() {
        if (!areBitmapsLoaded()) {
            Toast.makeText(this, "Please load images before rotating", Toast.LENGTH_SHORT).show();

            return;
        }

        Bitmap[] rotatedBitmaps = new Bitmap[bitmaps.length];
        for (int i = 0; i < bitmaps.length; i++) {
            rotatedBitmaps[i] = rotateBitmap(bitmaps[i], -90);
        }

        this.bitmaps = rotatedBitmaps;
        imageAdapter.addAll(bitmaps);
    }

    @NonNull
    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix rotationMatrix = new Matrix();

        rotationMatrix.postRotate(degrees);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), rotationMatrix, true);
    }

}
