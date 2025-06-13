package com.blinkreceipt.linking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blinkreceipt.linking.databinding.ActivityMainBinding;
import com.microblink.linking.Account;
import com.microblink.linking.AccountLinkingClient;
import com.microblink.linking.Credentials;
import com.microblink.linking.RetailerIds;

import kotlin.Unit;

@SuppressLint("UnsafeOptInUsageWarning")
public class MainActivity extends AppCompatActivity {

    private static final Account ACCOUNT = new Account(
            RetailerIds.AMAZON,
            Credentials.None.INSTANCE
    );

    private ActivityMainBinding binding;

    private AccountLinkingClient client;

    private final String TAG = "AccountLinking";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        bindViews();

        client = new AccountLinkingClient(this);

        client.dayCutoff(2_000);

        requestNotificationPermission();
    }

    private void bindViews() {
        binding.accounts.setOnClickListener(this::onAccounts);
        binding.link.setOnClickListener(this::onLink);
        binding.unlink.setOnClickListener(this::onUnlinkAccount);
        binding.resetRetailerHistory.setOnClickListener(this::onResetRetailerHistory);
        binding.orders.setOnClickListener(this::onOrders);
    }

    public void onLink(View view) {
        binding.webContainer.removeAllViews();

        client.link(
                ACCOUNT, account -> {
                    Toast.makeText(
                            getApplicationContext(),
                            "Account linked " + account, Toast.LENGTH_SHORT
                    ).show();

                    return Unit.INSTANCE;
                }, e -> {
                    if (e != null) {
                        if (e.view() != null) {
                            binding.webContainer.removeAllViews();

                            binding.webContainer.addView(e.view());
                        }
                    }

                    Toast.makeText(
                            getApplicationContext(),
                            "linking exception" + e,
                            Toast.LENGTH_LONG
                    ).show();

                    return Unit.INSTANCE;
                }
        );
    }

    public void onResetRetailerHistory(View view) {
        client.resetHistory(ACCOUNT.retailerId())
                .addOnSuccessListener(this, success -> Toast.makeText(
                        getApplicationContext(),
                        "reset history: " + success, Toast.LENGTH_SHORT
                ).show()).addOnFailureListener(this, e -> Toast.makeText(
                        getApplicationContext(),
                        "reset history: " + e, Toast.LENGTH_SHORT
                ).show());
    }

    public void onOrders(View view) {
        binding.webContainer.removeAllViews();

        client.orders(
                ACCOUNT.retailerId(),
                (retailerId, scanResults, remaining, uuid) -> {
                    Toast.makeText(
                            getApplicationContext(),
                            "retailer id " + retailerId + " remaining "
                                    + remaining + " uuid " + uuid,
                            Toast.LENGTH_SHORT
                    ).show();

                    return Unit.INSTANCE;
                }, (integer, e) -> {
                    if (e.view() != null) {
                        binding.webContainer.removeAllViews();

                        binding.webContainer.addView(e.view());
                    }

                    Toast.makeText(getApplicationContext(),
                            "orders exception" + e, Toast.LENGTH_LONG).show();

                    return Unit.INSTANCE;
                }
        );
    }

    public void onUnlinkAccount(View view) {
        client.unlink(ACCOUNT)
                .addOnSuccessListener(this, success -> Toast.makeText(getApplicationContext(),
                        "Account Unlink " + success, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(this, e -> Toast.makeText(getApplicationContext(),
                        "Account Unlink " + e, Toast.LENGTH_SHORT).show());
    }

    public void onAccounts(View view) {
        client.accounts().addOnSuccessListener(this, accounts -> Toast.makeText(getApplicationContext(),
                        "Account linked " + accounts.toString(), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(this, e -> Toast.makeText(getApplicationContext(),
                        "Account linked " + e, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        client.close();

        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 42) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onNotificationPermissionGranted();
            } else {
                onNotificationPermissionDenied();
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{ Manifest.permission.POST_NOTIFICATIONS },
                        42
                );
            } else {
                onNotificationPermissionGranted();
            }
        } else {
            onNotificationPermissionGranted();
        }
    }

    private void onNotificationPermissionGranted() {
        Toast.makeText(
                getApplicationContext(),
                "Background Refresh enabled", Toast.LENGTH_LONG
        ).show();
    }

    private void onNotificationPermissionDenied() {
        Toast.makeText(
                getApplicationContext(),
                "Warning: Notification permission needed for Background Refresh", Toast.LENGTH_LONG
        ).show();
    }

}
