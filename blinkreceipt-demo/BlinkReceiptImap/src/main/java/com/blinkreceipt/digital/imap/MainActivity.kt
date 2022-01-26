package com.blinkreceipt.digital.imap

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.blinkreceipt.digital.imap.databinding.ActivityMainBinding
import com.blinkreceipt.digital.imap.databinding.CredentialsViewBinding
import com.google.android.gms.tasks.Tasks
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.microblink.core.InitializeCallback
import com.microblink.core.ScanResults
import com.microblink.core.Timberland
import com.microblink.core.internal.ExecutorSupplier
import com.microblink.core.internal.IOUtils
import com.microblink.digital.*
import com.microblink.digital.internal.account

class MainActivity : AppCompatActivity() {

    internal companion object Imap {

        const val DAYS_CUT_OFF = 15

        const val COUNTRY_CODE = "US"

        const val TAG = "ProviderSetupDialogFragment"

        val tester = PasswordCredentials.newBuilder(
            Provider.GMAIL,
            "email address",
            "app password stored"
        ).build()
    }

    private lateinit var client: ImapClient

    private var _binding: ActivityMainBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        client = ImapClient(
            applicationContext,
            object : InitializeCallback {

                override fun onComplete() {
                    Toast.makeText(
                        applicationContext,
                        "Imap is ready!", Toast.LENGTH_SHORT
                    ).show()

                    binding.clear.isEnabled = true

                    binding.login.isEnabled = true

                    binding.logout.isEnabled = true

                    binding.messages.isEnabled = true

                    binding.verify.isEnabled = true

                    binding.debug.isEnabled = true

                    binding.remoteMessages.isEnabled = true

                    binding.multipleMessages.isEnabled = true

                    binding.multipleRemote.isEnabled = true

                    binding.logoutSingle.isEnabled = true
                }

                override fun onException(throwable: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        throwable.toString(), Toast.LENGTH_SHORT
                    ).show()

                    binding.results.text = throwable.toString()

                    binding.clear.isEnabled = true

                    binding.login.isEnabled = true

                    binding.logout.isEnabled = true

                    binding.messages.isEnabled = true

                    binding.verify.isEnabled = true

                    binding.debug.isEnabled = true
                }
            }
        ).apply {
            dayCutoff(DAYS_CUT_OFF)
            countryCode(COUNTRY_CODE)
            // sendersToSearch( listOf( Merchant( "Apple.com", "no_reply@email.apple.com")))
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        client.close()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClear(view: View) {
        client.clearLastCheckedTime().addOnSuccessListener {
            binding.results.text = null

            Toast.makeText(
                applicationContext,
                "Cleared last checked time", Toast.LENGTH_SHORT
            ).show()
        }
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onMessagesClick(view: View) {
        binding.results.text = "Searching for messages..."

        client.accounts().addOnSuccessListener {
            it.account(tester)?.let { account ->
                client.messages(account).addOnSuccessListener { results ->
                    binding.results.text = "ScanResults Size: ${results.size}"

                    Toast.makeText(
                        applicationContext,
                        "ScanResults Size: ${results.size}", Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener {
                    binding.results.text = "User messages failure: $it"

                    Toast.makeText(
                        applicationContext,
                        "User messages failure: $it", Toast.LENGTH_SHORT
                    ).show()
                }
            } ?: Toast.makeText(
                applicationContext,
                "unable to " +
                        "find tester account ${tester.username()}",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnFailureListener {
            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onDebugMessages(view: View) {
        binding.results.text = "Searching for debug messages..."

        @Suppress("DEPRECATION")
        Tasks.call(ExecutorSupplier.getInstance().io(), {
            IOUtils.tryReadStream(applicationContext.assets.open("peapod.html")) ?: ""
        }).addOnSuccessListener { html ->
            client.messages(Provider.GMAIL, "yourfriends@peapod.com", html)
                .addOnSuccessListener {
                    Toast.makeText(
                        applicationContext,
                        "ScanResults Size: ${it.size}", Toast.LENGTH_SHORT
                    ).show()

                    binding.results.text = "ScanResults Size: ${it.size}"
                }.addOnFailureListener {
                    Toast.makeText(
                        applicationContext,
                        "User messages failure: $it", Toast.LENGTH_SHORT
                    ).show()

                    binding.results.text = "User messages failure: $it"
                }
        }.addOnFailureListener {
            Timberland.e(it)

            Toast.makeText(
                applicationContext,
                "html exception: $it", Toast.LENGTH_SHORT
            ).show()
        }
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onSingleLogout(view: View) {
        binding.results.text = "Logging user out of account..."

        client.accounts().addOnSuccessListener {
            it.account(tester)?.let {
                client.logout(
                    tester
                ).addOnSuccessListener {
                    Toast.makeText(
                        applicationContext,
                        "User logged out $it", Toast.LENGTH_SHORT
                    ).show()

                    binding.results.text = "User logged out $it"
                }.addOnFailureListener {
                    Toast.makeText(
                        applicationContext,
                        "User logout failure: $it", Toast.LENGTH_SHORT
                    ).show()

                    binding.results.text = "User logout failure: $it"
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onLogout(view: View) {
        binding.results.text = "Logging user out of all accounts..."

        client.logout().addOnSuccessListener {
            Toast.makeText(
                applicationContext,
                "User logged out $it", Toast.LENGTH_SHORT
            ).show()

            binding.results.text = "User logged out $it"
        }.addOnFailureListener {
            Toast.makeText(
                applicationContext,
                "User logout failure: $it", Toast.LENGTH_SHORT
            ).show()

            binding.results.text = "User logout failure: $it"
        }
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onMultipleMessages(view: View) {
        binding.results.text = "Multiple Messages..."

        val messages = mutableMapOf<PasswordCredentials, List<ScanResults>>()

        client.messages(object : MessagesCallback {

            @SuppressLint("SetTextI18n")
            override fun onComplete(
                credential: PasswordCredentials,
                result: List<ScanResults>
            ) {
                Timberland.d("credentials $credential results $result")

                messages[credential] = result

                binding.results.text = "Multiple Messages ${messages.size} ${
                    buildString {
                        messages.forEach { (t, u) ->
                            append("${t.username()} : ${u.size} \n")
                        }
                    }
                }"
            }

            @SuppressLint("SetTextI18n")
            override fun onException(throwable: Throwable) {
                Timberland.e(throwable)

                binding.results.text = "Multiple Messages $throwable"
            }
        })
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onMultipleRemoteMessages(view: View) {
        binding.results.text = "Multiple Remote Messages..."

        val messages = mutableMapOf<PasswordCredentials, JobResults>()

        client.remoteMessages(object : JobResultsCallback {

            @SuppressLint("SetTextI18n")
            override fun onComplete(credential: PasswordCredentials, result: JobResults) {
                Timberland.d("credentials $credential results $result")

                messages[credential] = result

                binding.results.text = "Multiple Remote ${messages.size}"
            }

            @SuppressLint("SetTextI18n")
            override fun onException(throwable: Throwable) {
                Timberland.e(throwable)

                binding.results.text = "Multiple Remote $throwable"
            }
        })
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onRemoteMessages(view: View) {
        binding.results.text = "Remote Messages..."

        client.accounts().addOnSuccessListener {
            it.account(tester)?.let { account ->
                client.remoteMessages(account).addOnSuccessListener {
                    Toast.makeText(
                        applicationContext,
                        "Remote messages $it", Toast.LENGTH_SHORT
                    ).show()

                    binding.results.text = "Remote messages $it"
                }.addOnFailureListener {
                    Toast.makeText(
                        applicationContext,
                        "Remote messages failure: $it", Toast.LENGTH_SHORT
                    ).show()

                    binding.results.text = "Remote messages failure: $it"
                }
            } ?: Toast.makeText(
                applicationContext,
                "unable to " +
                        "find tester account ${tester.username()}",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnFailureListener {
            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onVerify(view: View) {
        binding.results.text = "Verifying account..."

        client.accounts().addOnSuccessListener {
            it.account(tester)?.let { account ->
                client.verify(account).addOnSuccessListener {
                    Toast.makeText(
                        applicationContext,
                        "Verify: $it", Toast.LENGTH_SHORT
                    ).show()

                    binding.results.text = "Account Verify: $it"
                }.addOnFailureListener {
                    Toast.makeText(
                        applicationContext,
                        "Verify failure: $it", Toast.LENGTH_SHORT
                    ).show()

                    binding.results.text = "Account Failure: $it"
                }
            } ?: Toast.makeText(
                applicationContext,
                "unable to " +
                        "find tester account ${tester.username()}",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnFailureListener {
            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onLogin(view: View) {
        binding.results.text = "Logging in..."

        val binding = CredentialsViewBinding.inflate(layoutInflater)

        MaterialAlertDialogBuilder(this)
            .setTitle("Credentials")
            .setView(binding.root)
            .setPositiveButton(
                "Ok"
            ) { it, _ ->
                it.dismiss()

                Toast.makeText(applicationContext, "Logging in...", Toast.LENGTH_SHORT).show()

                val account = PasswordCredentials.newBuilder(
                    binding.provider.text.toString().toProvider(),
                    binding.email.text.toString(),
                    binding.password.text.toString()
                ).build()

                if (!supportFragmentManager.isDestroyed) {
                    ProviderSetupDialogFragment.newInstance(
                        ProviderSetupOptions.newBuilder(
                            account
                        ).build()
                    ).callback {
                        this.binding.results.text = "Status ${it.name}"

                        Toast.makeText(
                            applicationContext,
                            "Status ${it.name}",
                            Toast.LENGTH_SHORT
                        ).show()

                        when (it) {
                            ProviderSetupResults.BAD_PASSWORD -> Timberland.e("BAD_PASSWORD")
                            ProviderSetupResults.BAD_EMAIL -> Timberland.e("BAD_EMAIL")
                            ProviderSetupResults.CREATED_APP_PASSWORD -> Timberland.d("CREATED_APP_PASSWORD")
                            ProviderSetupResults.NO_CREDENTIALS -> Timberland.e("NO_CREDENTIALS")
                            ProviderSetupResults.UNKNOWN -> Timberland.e("UNKNOWN")
                            ProviderSetupResults.NO_APP_PASSWORD -> Timberland.e("NO_APP_PASSWORD")
                            ProviderSetupResults.LSA_ENABLED -> Timberland.e("LSA_ENABLED")
                        }

                        if (!supportFragmentManager.isDestroyed) {
                            val dialog = supportFragmentManager.findFragmentByTag(
                                TAG
                            ) as ProviderSetupDialogFragment

                            if (dialog.isAdded) {
                                dialog.dismiss()
                            }
                        }
                    }.show(supportFragmentManager, TAG)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

}
