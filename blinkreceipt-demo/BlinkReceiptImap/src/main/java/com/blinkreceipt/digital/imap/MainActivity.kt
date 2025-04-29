package com.blinkreceipt.digital.imap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.blinkreceipt.digital.imap.databinding.ActivityMainBinding
import com.blinkreceipt.digital.imap.databinding.CredentialsViewBinding
import com.google.android.gms.tasks.Tasks
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.microblink.core.InitializeCallback
import com.microblink.core.ScanResults
import com.microblink.digital.Credentials
import com.microblink.digital.ImapClient
import com.microblink.digital.JobResults
import com.microblink.digital.JobResultsCallback
import com.microblink.digital.MessagesCallback
import com.microblink.digital.Provider
import com.microblink.digital.ProviderFragment
import com.microblink.digital.ProviderResults
import com.microblink.digital.ProviderSetupFragmentFactory
import com.microblink.digital.ProviderSetupResults
import com.microblink.digital.internal.account

class MainActivity : AppCompatActivity() {

    internal companion object Imap {

        const val DAYS_CUT_OFF = 15

        const val COUNTRY_CODE = "US"

        const val TAG = "ProviderSetupDialogFragment"

        var tester: Credentials.Password? = null

        const val LOG_TAG = "ImapMainActivity"
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

        requestNotificationPermission()
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
        tester?.let { credentials ->
            binding.results.text = "Searching for messages..."

            client.accounts().addOnSuccessListener {
                it.account(credentials)?.let { account ->
                    client.messages(account).addOnSuccessListener { results ->
                        binding.results.text = "ScanResults Size: ${results.size}"

                        Toast.makeText(
                            applicationContext,
                            "ScanResults Size: ${results.size}", Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener { failure ->
                        binding.results.text = "User messages failure: $failure"

                        Toast.makeText(
                            applicationContext,
                            "User messages failure: $failure", Toast.LENGTH_SHORT
                        ).show()
                    }
                } ?: Toast.makeText(
                    applicationContext,
                    "unable to " +
                            "find tester account ${credentials.username}",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }?: accountNotLinkedAlert()
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onDebugMessages(view: View) {
        binding.results.text = "Searching for debug messages..."

        @Suppress("DEPRECATION")
        Tasks.call(ExecutorSupplier.getInstance().io()) {
            IOUtils.tryReadStream(applicationContext.assets.open("peapod.html")) ?: ""
        }.addOnSuccessListener { html ->
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
            Log.e(LOG_TAG, "failure in onDebugMessages",  it )

            Toast.makeText(
                applicationContext,
                "html exception: $it", Toast.LENGTH_SHORT
            ).show()
        }
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onSingleLogout(view: View) {
        tester?.let { credentials ->
            binding.results.text = "Logging user out of account..."

            client.accounts().addOnSuccessListener {
                it.account(credentials)?.let {
                    client.logout(
                        credentials
                    ).addOnSuccessListener { results ->
                        Toast.makeText(
                            applicationContext,
                            "User logged out $results", Toast.LENGTH_SHORT
                        ).show()

                        binding.results.text = "User logged out $results"

                        tester = null
                    }.addOnFailureListener { failure ->
                        Toast.makeText(
                            applicationContext,
                            "User logout failure: $failure", Toast.LENGTH_SHORT
                        ).show()

                        binding.results.text = "User logout failure: $failure"
                    }
                }
            }
        }?: accountNotLinkedAlert()
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

            tester = null
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

        val messages = mutableMapOf<Credentials.Password, List<ScanResults>>()

        client.messages(object : MessagesCallback {

            @SuppressLint("SetTextI18n")
            override fun onComplete(
                credentials: Credentials.Password,
                result: List<ScanResults>
            ) {
                Log.d(LOG_TAG, "credentials $credentials results $result")

                messages[credentials] = result

                binding.results.text = "Multiple Messages ${messages.size} ${
                    buildString {
                        messages.forEach { (t, u) ->
                            append("${t.username} : ${u.size} \n")
                        }
                    }
                }"
            }

            @SuppressLint("SetTextI18n")
            override fun onException(throwable: Throwable) {
                Log.e(LOG_TAG, "failure in onException", throwable )

                binding.results.text = "Multiple Messages $throwable"
            }
        })
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onMultipleRemoteMessages(view: View) {
        binding.results.text = "Multiple Remote Messages..."

        val messages = mutableMapOf<Credentials.Password, JobResults>()

        client.remoteMessages(object : JobResultsCallback {

            @SuppressLint("SetTextI18n")
            override fun onComplete(credentials: Credentials.Password, result: JobResults) {
                Log.d(LOG_TAG, "credentials $credentials results $result" )

                messages[credentials] = result

                binding.results.text = "Multiple Remote ${messages.size}"
            }

            @SuppressLint("SetTextI18n")
            override fun onException(throwable: Throwable) {
                Log.e(LOG_TAG, "failure in onException", throwable )

                binding.results.text = "Multiple Remote $throwable"
            }
        })
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onRemoteMessages(view: View) {
        tester?.let { credentials ->
            binding.results.text = "Remote Messages..."

            client.accounts().addOnSuccessListener {
                it.account(credentials)?.let { account ->
                    client.remoteMessages(account).addOnSuccessListener { results ->
                        Toast.makeText(
                            applicationContext,
                            "Remote messages $results", Toast.LENGTH_SHORT
                        ).show()

                        binding.results.text = "Remote messages $results"
                    }.addOnFailureListener { failure ->
                        Toast.makeText(
                            applicationContext,
                            "Remote messages failure: $failure", Toast.LENGTH_SHORT
                        ).show()

                        binding.results.text = "Remote messages failure: $failure"
                    }
                } ?: Toast.makeText(
                    applicationContext,
                    "unable to " +
                            "find tester account ${credentials.username}",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }?: accountNotLinkedAlert()
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onVerify(view: View) {
        tester?.let { credentials ->
            binding.results.text = "Verifying account..."

            client.accounts().addOnSuccessListener {
                it.account(credentials)?.let { account ->
                    client.verify(account).addOnSuccessListener { results ->
                        Toast.makeText(
                            applicationContext,
                            "Verify: $results", Toast.LENGTH_SHORT
                        ).show()

                        binding.results.text = "Account Verify: $results"
                    }.addOnFailureListener { failure ->
                        Toast.makeText(
                            applicationContext,
                            "Verify failure: $failure", Toast.LENGTH_SHORT
                        ).show()

                        binding.results.text = "Account Failure: $failure"
                    }
                } ?: Toast.makeText(
                    applicationContext,
                    "unable to " +
                            "find tester account ${credentials.username}",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }?: accountNotLinkedAlert()
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

                val account: Credentials = Credentials.None.Gmail()

                if (!supportFragmentManager.isDestroyed) {
                    ProviderSetupFragmentFactory.create(
                        account
                    ).callback { providerResult: ProviderResults ->
                        this.binding.results.text = "Status ${providerResult.results.name}"

                        Toast.makeText(
                            applicationContext,
                            "Status ${providerResult.results.name}",
                            Toast.LENGTH_SHORT
                        ).show()

                        when (providerResult.results) {
                            ProviderSetupResults.BAD_PASSWORD -> Log.d(LOG_TAG, "BAD_PASSWORD" )
                            ProviderSetupResults.BAD_EMAIL -> Log.d(LOG_TAG, "BAD_EMAIL" )
                            ProviderSetupResults.CREATED_APP_PASSWORD -> {
                                Log.d(LOG_TAG, "CREATED_APP_PASSWORD" )
                                val linked = providerResult.credentials as Credentials.Password

                                tester = linked

                                Toast.makeText(
                                    applicationContext,
                                    "Linked: ${linked.username}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            ProviderSetupResults.NO_CREDENTIALS -> Log.d(LOG_TAG, "NO_CREDENTIALS" )
                            ProviderSetupResults.UNKNOWN -> Log.d(LOG_TAG, "UNKNOWN" )
                            ProviderSetupResults.NO_APP_PASSWORD -> Log.d(LOG_TAG, "NO_APP_PASSWORD" )
                            ProviderSetupResults.LSA_ENABLED -> Log.d(LOG_TAG, "LSA_ENABLED" )
                            ProviderSetupResults.DUPLICATE_EMAIL -> Log.d(LOG_TAG, "DUPLICATE_EMAIL" )
                            ProviderSetupResults.USER_CANCELLED -> Log.d(LOG_TAG, "USER_CANCELLED" )
                            ProviderSetupResults.REDIRECT_TO_BROWSER -> Log.d(LOG_TAG, "REDIRECT_TO_BROWSER" )
                            ProviderSetupResults.ADMIN_NEEDED -> Log.d(LOG_TAG, "ADMIN_NEEDED" )
                            ProviderSetupResults.RESULT_SAVED -> Log.d(LOG_TAG, "RESULT_SAVED" )
                        }

                        if (!supportFragmentManager.isDestroyed) {
                            (supportFragmentManager.findFragmentByTag(TAG)
                                    as? ProviderFragment)?.let { dialog ->
                                if (dialog.isAdded) {
                                    dialog.dismiss()
                                }
                            }
                        }

                        // If the status is not CREATED_APP_PASSWORD, then display "Not Linked"
                        // message with credentials
                        if (providerResult.results != ProviderSetupResults.CREATED_APP_PASSWORD) {
                            val message: String =
                                when (val credentials = providerResult.credentials){
                                    is Credentials.Password -> {
                                        "Not Linked: ${credentials.username}"
                                    }
                                    is Credentials.None -> {
                                        "Not Linked: No Credentials"
                                    }
                                }

                            Toast.makeText(
                                applicationContext,
                                message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.show(supportFragmentManager, TAG)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun accountNotLinkedAlert(){
        Toast.makeText(applicationContext, "Please login to account", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 42) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onNotificationPermissionGranted()
            } else {
                onNotificationPermissionDenied()
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    42
                )
            } else {
                onNotificationPermissionGranted()
            }
        } else {
            onNotificationPermissionGranted()
        }
    }

    private fun onNotificationPermissionGranted() {
        Toast.makeText(
            applicationContext,
            "Background Refresh enabled", Toast.LENGTH_LONG
        ).show()
    }

    private fun onNotificationPermissionDenied() {
        Toast.makeText(
            applicationContext,
            "Warning: Notification permission needed for Background Refresh", Toast.LENGTH_LONG
        ).show()
    }

}