package com.blinkreceipt.directscan.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blinkreceipt.directscan.R;
import com.blinkreceipt.directscan.ui.recyclerview.ImageAdapter;
import com.microblink.Media;
import com.microblink.RecognizerCallback;
import com.microblink.RecognizerClient;
import com.microblink.RecognizerResult;
import com.microblink.ScanOptions;
import com.microblink.core.Retailer;
import com.microblink.core.ScanResults;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST_CODE = 100;

    private static final String TAG = "MainActivity";

    @NonNull
    private RecyclerView imageRecyclerview;

    @NonNull
    private Button selectImageBtn;

    @NonNull
    private Button scanImageBtn;

    @NonNull
    private Button rotateImagesLeftBtn;

    @NonNull
    private Button rotateImagesRightBtn;

    @NonNull
    private ImageAdapter imageAdapter;

    private Bitmap[] bitmaps;

    private RecognizerClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        initializeRecyclerView();

        initializeClient();

        selectImageBtn.setOnClickListener(v -> {
            Intent intent = createSelectImageIntent();

            startActivityForResult(intent, IMAGE_REQUEST_CODE);
        });

        rotateImagesLeftBtn.setOnClickListener(v -> rotateBitmapsLeft());
        rotateImagesRightBtn.setOnClickListener(v -> rotateBitmapsRight());

        scanImageBtn.setOnClickListener(v -> {
            sendBitmapsForScanning();
        });
    }

    private void sendBitmapsForScanning() {
        ScanOptions options = ScanOptions.newBuilder()
                .retailer(Retailer.UNKNOWN)
                .build();

        client.recognize(options, new RecognizerCallback() {
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
        client = new RecognizerClient(this);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<Uri> imageUris = parseUrisFromIntent(data);

            if (!imageUris.isEmpty()) {
                addImagesToAdapter(imageUris);
            }
        }
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
    private Bitmap loadBitmapFromUri(@NonNull Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    @NonNull
    private List<Uri> parseUrisFromIntent(Intent data) {
        ClipData clipData = data != null ? data.getClipData() : null;
        Uri uri = data != null ? data.getData() : null;
        List<Uri> uris = new ArrayList<>();

        if (clipData != null) {
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);

                Uri itemUri = item != null ? item.getUri() : null;

                if (itemUri != null) {
                    uris.add(itemUri);
                }
            }
        } else if (uri != null) {
            uris.add(uri);
        }

        return uris;
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

    @NonNull
    private Intent createSelectImageIntent() {
        Intent intent = new Intent();

        intent.setType("image/*");

        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);

        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        return Intent.createChooser(intent, getString(R.string.select_image));
    }
}