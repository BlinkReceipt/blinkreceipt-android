package com.blinkreceipt.linking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.blinkreceipt.linking.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.microblink.core.Timberland;
import com.microblink.linking.Account;
import com.microblink.linking.AccountLinkingClient;
import com.microblink.linking.AccountLinkingException;
import com.microblink.linking.PasswordCredentials;
import com.microblink.linking.RetailerIds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.Unit;

public class MainActivity extends AppCompatActivity {

    private static final Account ACCOUNT = new Account(
            RetailerIds.TARGET,
            new PasswordCredentials(
                    "",
                    ""
            )
    );

    private static final List<Account> ACCOUNTS = Arrays.asList(
            ACCOUNT,
            new Account(
                    RetailerIds.WALMART,
                    new PasswordCredentials(
                            "",
                            ""
                    )
            ),
            new Account(
                    RetailerIds.HOME_DEPOT,
                    new PasswordCredentials(
                            "",
                            ""
                    )
            )
    );

    private ActivityMainBinding binding;

    private AccountLinkingClient client;

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
            Timberland.d("verification " + verification);

            Toast.makeText(getApplicationContext(),
                    "verification " + verification, Toast.LENGTH_LONG).show();

            return Unit.INSTANCE;
        }, e -> {
            if (e instanceof AccountLinkingException) {
                AccountLinkingException exception = (AccountLinkingException) e;

                if (exception.view() != null) {
                    binding.webContainer.removeAllViews();

                    binding.webContainer.addView(exception.view());
                }
            }

            Toast.makeText(getApplicationContext(), "verification exception" + e, Toast.LENGTH_LONG).show();

            return Unit.INSTANCE;
        }, webView -> {
            if (webView != null) {
                binding.webContainer.removeAllViews();

                binding.webContainer.addView(webView);
            }

            Timberland.d("preview debug only available in development mode.");

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

    public void onAllOrders(View view) {
        binding.webContainer.removeAllViews();

        List<Task<?>> tasks = new ArrayList<>();

        for (Account account : ACCOUNTS) {
            //noinspection deprecation
            tasks.add(Tasks.call(ExecutorSupplier.getInstance().io(),
                    () -> Tasks.await(client.link(account))));
        }

        //noinspection deprecation
        tasks.add(Tasks.call(ExecutorSupplier.getInstance().io(),
                () -> Tasks.await(client.accounts()))
                .addOnSuccessListener(this, accounts -> Toast.makeText(getApplicationContext(),
                        "accounts " + accounts.toString(), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(this, e ->
                        Toast.makeText(getApplicationContext(), "exception " + e, Toast.LENGTH_SHORT).show()));

        Tasks.whenAllComplete(tasks)
                .addOnSuccessListener(this, completed -> client.orders((retailerId, scanResults, remaining, uuid) -> {
                    Toast.makeText(
                            getApplicationContext(),
                            "retailer id " + retailerId + " remaining "
                                    + remaining + " uuid " + uuid,
                            Toast.LENGTH_SHORT
                    ).show();

                    return Unit.INSTANCE;
                }, (retailerId, e) -> {
                    if (e.view() != null) {
                        binding.webContainer.removeAllViews();

                        binding.webContainer.addView(e.view());
                    }

                    Toast.makeText(getApplicationContext(), "orders exception" + e, Toast.LENGTH_LONG).show();

                    return Unit.INSTANCE;
                }));
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