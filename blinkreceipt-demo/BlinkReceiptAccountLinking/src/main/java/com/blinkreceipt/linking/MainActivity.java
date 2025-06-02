package com.blinkreceipt.linking;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

        client = new AccountLinkingClient(this);

        client.dayCutoff(2_000);
    }

    public void onLink(View view) {
        binding.webContainer.removeAllViews();

        client.link(ACCOUNT)
                .addOnSuccessListener(this, success -> Toast.makeText(getApplicationContext(),
                        "Account linked " + success, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(this, e -> Toast.makeText(getApplicationContext(),
                        "Account linked " + e, Toast.LENGTH_SHORT).show());
    }

    public void onVerifyAccount(View view) {
        binding.webContainer.removeAllViews();

        client.verify(ACCOUNT.retailerId(), (verification, s) -> {
            Log.d(TAG, "verification " + verification);

            Toast.makeText(getApplicationContext(),
                    "verification " + verification, Toast.LENGTH_LONG).show();

            return Unit.INSTANCE;
        }, e -> {
            if (e != null) {
                if (e.view() != null) {
                    binding.webContainer.removeAllViews();

                    binding.webContainer.addView(e.view());
                }
            }

            Toast.makeText(getApplicationContext(), "verification exception" + e, Toast.LENGTH_LONG).show();

            return Unit.INSTANCE;
        }, webView -> {
            if (webView != null) {
                binding.webContainer.removeAllViews();

                binding.webContainer.addView(webView);
            }

            Log.d(TAG, "preview debug only available in development mode.");

            return Unit.INSTANCE;
        });
    }

    public void onResetHistory(View view) {
        client.resetHistory()
                .addOnSuccessListener(this, success -> Toast.makeText(
                        getApplicationContext(),
                        "reset history: " + success, Toast.LENGTH_SHORT
                ).show())
                .addOnFailureListener(this, e -> Toast.makeText(
                        getApplicationContext(),
                        "reset history exception: " + e, Toast.LENGTH_LONG
                ).show());
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

        client.orders(ACCOUNT.retailerId(), (retailerId, scanResults, remaining, uuid) -> {
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
                }, webView -> {
                    if (webView != null) {
                        binding.webContainer.removeAllViews();

                        binding.webContainer.addView(webView);
                    }

                    return Unit.INSTANCE;
                }
        );
    }

    public void onUnlinkAccounts(View view) {
        client.unlink()
                .addOnSuccessListener(this, success -> Toast.makeText(getApplicationContext(),
                        "Unlink Accounts " + success, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(this, e -> Toast.makeText(getApplicationContext(),
                        "Unlink Accounts " + e, Toast.LENGTH_SHORT).show());
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

}