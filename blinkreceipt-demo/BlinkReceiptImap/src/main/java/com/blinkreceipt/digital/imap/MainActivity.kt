package com.blinkreceipt.digital.imap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.blinkreceipt.digital.imap.databinding.ActivityMainBinding
import com.blinkreceipt.digital.imap.databinding.CredentialsViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.microblink.core.InitializeCallback
import com.microblink.core.internal.Timberland
import com.microblink.digital.*

class MainActivity : AppCompatActivity() {

    companion object {

        const val TAG = "ProviderSetupDialogFragment"
    }

    private lateinit var client: ImapClient

    private var _binding: ActivityMainBinding? = null

    private val binding get() = _binding!!

    private val options by lazy {
        ProviderSetupOptions.newBuilder(Provider.YAHOO)
                .debug(false)
                .clearCache(false)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        client = ImapClient(applicationContext, options.provider(), object : InitializeCallback {

            override fun onComplete() {
                Toast.makeText(applicationContext,
                        "Imap is ready!", Toast.LENGTH_SHORT).show()

                binding.clear.isEnabled = true

                binding.login.isEnabled = true

                binding.logout.isEnabled = true

                binding.messages.isEnabled = true

                binding.verify.isEnabled = true
            }

            override fun onException(throwable: Throwable) {
                Toast.makeText(applicationContext,
                        throwable.toString(), Toast.LENGTH_SHORT).show()

                binding.clear.isEnabled = true

                binding.login.isEnabled = true

                binding.logout.isEnabled = true

                binding.messages.isEnabled = true

                binding.verify.isEnabled = true
            }

        }).apply {
            dayCutoff(15)
            countryCode("US")
        }
    }

    override fun onStop() {
        super.onStop()

        client.destroy()
    }

    fun onClear(view: View) {
        client.clearLastCheckedTime().addOnSuccessListener {
            Toast.makeText(applicationContext,
                    "Cleared last checked time", Toast.LENGTH_SHORT).show()
        }
    }

    fun onMessagesClick(view: View) {
        Toast.makeText(applicationContext, "Searching for messages...", Toast.LENGTH_SHORT).show()

        client.messages().addOnSuccessListener {
            Toast.makeText(applicationContext,
                    "ScanResults Size: ${it?.size.toString()}", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(applicationContext,
                    "User messages failure: ${it.toString()}", Toast.LENGTH_SHORT).show()
        }
    }

    fun onLogout(view: View) {
        client.logout().addOnSuccessListener {
            Toast.makeText(applicationContext,
                    "User logged out $it", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(applicationContext,
                    "User logout failure: $it", Toast.LENGTH_SHORT).show()
        }
    }

    fun onVerify(view: View) {
        Toast.makeText(applicationContext, "Verifying account...", Toast.LENGTH_SHORT).show()

        client.verify().addOnSuccessListener {
            Toast.makeText(applicationContext,
                    "verify: ${it.toString()}", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(applicationContext,
                    "verify failure: ${it.toString()}", Toast.LENGTH_SHORT).show()
        }
    }

    fun onLogin(view: View) {
        val binding = CredentialsViewBinding.inflate(layoutInflater)

        MaterialAlertDialogBuilder(this)
                .setTitle("Credentials")
                .setView(binding.root)
                .setPositiveButton("Ok") { it, _ ->
                    it.dismiss()

                    Toast.makeText(applicationContext, "Logging in...", Toast.LENGTH_SHORT).show()

                    client.credentials(PasswordCredentials(binding.email.text.toString(),
                            binding.password.text.toString())).addOnSuccessListener {
                        if (!supportFragmentManager.isDestroyed) {
                            ProviderSetupDialogFragment.newInstance(options)
                                    .callback {
                                        Toast.makeText(applicationContext, "Status ${it.name}", Toast.LENGTH_SHORT).show()

                                        when (it) {
                                            ProviderSetupResults.BAD_PASSWORD -> Timberland.e("BAD_PASSWORD")
                                            ProviderSetupResults.BAD_EMAIL -> Timberland.e("BAD_EMAIL")
                                            ProviderSetupResults.CREATED_APP_PASSWORD -> Timberland.d("CREATED_APP_PASSWORD")
                                            ProviderSetupResults.NO_CREDENTIALS -> Timberland.e("NO_CREDENTIALS")
                                            ProviderSetupResults.UNKNOWN -> Timberland.e("UNKNOWN")
                                        }

                                        if (!supportFragmentManager.isDestroyed) {
                                            val dialog = supportFragmentManager.findFragmentByTag(
                                                    TAG) as ProviderSetupDialogFragment

                                            if (dialog.isAdded) {
                                                dialog.dismiss()
                                            }
                                        }
                                    }.show(supportFragmentManager, TAG)
                        }
                    }.addOnFailureListener {
                        Toast.makeText(applicationContext,
                                "User login failure: ${it.toString()}", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
    }
}
