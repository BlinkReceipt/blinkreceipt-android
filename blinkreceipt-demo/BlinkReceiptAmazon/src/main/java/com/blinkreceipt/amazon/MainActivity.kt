package com.blinkreceipt.amazon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.blinkreceipt.amazon.databinding.ActivityMainBinding
import com.microblink.core.ScanResults
import com.microblink.core.Timberland
import com.microblink.linking.AmazonCallback
import com.microblink.linking.AmazonCredentials
import com.microblink.linking.AmazonException
import com.microblink.linking.AmazonManager

class MainActivity : AppCompatActivity() {

    private val scanResultOrders = mutableSetOf<ScanResults>()

    private var _callback: AmazonCallback? = object : AmazonCallback {

        override fun onComplete(scanResults: ScanResults, ordersRemaining: Int) {
            scanResultOrders.add(scanResults)

            if (ordersRemaining == 0) {
                binding.results.text = "Orders parsed from amazon ${scanResultOrders.size}"
            }
        }

        override fun onAccountVerified() {
            AmazonManager.getInstance(applicationContext)
                    .hasCredentials()
                    .addOnCompleteListener {
                        binding.results.text = "Account Verified ${it.result}"
                    }
        }

        override fun onException(e: AmazonException) {
            onException(e, "No Message!!!")
        }

        override fun onException(e: AmazonException, message: String?) {
            scanResultOrders.clear()

            binding.results.text = "exception: $e, $message"

            when (e) {
                AmazonException.VERIFICATION_NEEDED,
                AmazonException.INVALID_EMAIL,
                AmazonException.INVALID_PASSWORD ->
                    binding.webViewContainer.addView(AmazonManager.getInstance(applicationContext).webView())
                else ->
                    Timberland.e(e.toString())
            }
        }
    }

    private lateinit var binding: ActivityMainBinding

    private val callback get() = _callback!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val view = binding.root

        setContentView(view)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.amazon_menu, menu)

        return true
    }

    override fun onDestroy() {
        super.onDestroy()

        _callback = null

        AmazonManager.getInstance(applicationContext).destroy()
    }

    fun clearCredentials(menu: MenuItem) {
        scanResultOrders.clear()

        binding.results.text = "Cleared Credentials!"

        AmazonManager.getInstance(applicationContext).apply {
            initialize().addOnSuccessListener {
                clearCredentials()
                        .addOnSuccessListener {
                            Toast.makeText(
                                    applicationContext,
                                    "cleared credentials $it", Toast.LENGTH_SHORT
                            ).show()
                        }.addOnFailureListener {
                            Toast.makeText(
                                    applicationContext,
                                    "cleared credentials failed $it", Toast.LENGTH_SHORT
                            ).show()
                        }
            }.addOnFailureListener {
                Toast.makeText(
                        applicationContext,
                        "exception $it", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun orders(menu: MenuItem) {
        if (TextUtils.isEmpty(binding.accountEmail.text)) {
            Toast.makeText(
                    applicationContext,
                    "Email is required!", Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (TextUtils.isEmpty(binding.accountPassword.text)) {
            Toast.makeText(
                    applicationContext,
                    "Password is required!", Toast.LENGTH_SHORT
            ).show()

            return
        }

        scanResultOrders.clear()

        binding.results.text = "Loading Orders!"

        AmazonManager.getInstance(applicationContext).apply {
            dayCutoff(binding.accountDaysCutOff.text?.toString()?.toInt() ?: dayCutoff())

            initialize().addOnSuccessListener {
                storeCredentials(
                        AmazonCredentials(
                                binding.accountEmail.text.toString(),
                                binding.accountPassword.text.toString()
                        )
                ).addOnSuccessListener {
                    orders(callback)
                }.addOnFailureListener {
                    Toast.makeText(
                            applicationContext,
                            it.toString(), Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                Toast.makeText(
                        applicationContext,
                        "exception $it", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun verifyAccount(menu: MenuItem) {
        if (TextUtils.isEmpty(binding.accountEmail.text)) {
            Toast.makeText(
                    applicationContext,
                    "Email is required!", Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (TextUtils.isEmpty(binding.accountPassword.text)) {
            Toast.makeText(
                    applicationContext,
                    "Password is required!", Toast.LENGTH_SHORT
            ).show()

            return
        }

        scanResultOrders.clear()

        binding.results.text = "Verifying Account!"

        AmazonManager.getInstance(applicationContext).apply {
            dayCutoff(binding.accountDaysCutOff.text?.toString()?.toInt() ?: dayCutoff())

            initialize().addOnSuccessListener {
                storeCredentials(
                        AmazonCredentials(
                                binding.accountEmail.text.toString(),
                                binding.accountPassword.text.toString()
                        )
                ).addOnSuccessListener {
                    verifyAccount(callback)
                }.addOnFailureListener {
                    Toast.makeText(
                            applicationContext,
                            it.toString(), Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                Toast.makeText(
                        applicationContext,
                        "exception $it", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun clearOrders(menu: MenuItem) {
        scanResultOrders.clear()

        binding.results.text = "Cleared Orders!"

        AmazonManager.getInstance(applicationContext).apply {
            initialize().addOnSuccessListener {
                clearOrders()
                        .addOnSuccessListener {
                            Toast.makeText(
                                    applicationContext,
                                    "cleared orders $it", Toast.LENGTH_SHORT
                            ).show()
                        }.addOnFailureListener {
                            Toast.makeText(
                                    applicationContext,
                                    "cleared ordered failed $it", Toast.LENGTH_SHORT
                            ).show()
                        }
            }.addOnFailureListener {
                Toast.makeText(
                        applicationContext,
                        "exception $it", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}