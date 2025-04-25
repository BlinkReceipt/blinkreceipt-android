package com.blinkreceipt.linking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewGroupCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.annotation.SuppressLint;
import android.os.Build;
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
        WindowCompat.enableEdgeToEdge(this.getWindow());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        bindViews();

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

        client = new AccountLinkingClient(this);

        client.dayCutoff(2_000);
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

        client.link(ACCOUNT, (verification) -> {
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
        });
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

}
