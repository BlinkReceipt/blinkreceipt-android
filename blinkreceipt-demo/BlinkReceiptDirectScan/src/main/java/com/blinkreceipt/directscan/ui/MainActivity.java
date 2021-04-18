package com.blinkreceipt.directscan.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blinkreceipt.directscan.R;
import com.blinkreceipt.directscan.ui.recyclerview.ImageAdapter;

public class MainActivity extends AppCompatActivity {

    @NonNull
    private RecyclerView imageRecyclerview;

    @NonNull
    private TextView orientationLabel;

    @NonNull
    private Button selectImageBtn;

    @NonNull
    private Button scanImageBtn;

    @NonNull
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageRecyclerview = findViewById(R.id.image_rv);
        orientationLabel = findViewById(R.id.orientation_label);
        selectImageBtn = findViewById(R.id.select_image);
        scanImageBtn = findViewById(R.id.scan_image);

        selectImageBtn.setOnClickListener(v -> {
            // TODO Implement click listener for fetching images
        });

        scanImageBtn.setOnClickListener(v -> {
            // TODO Send images to direct api
        });
    }
}