package com.blinkreceipt.digital.imap

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        var accountSelected: Credentials.Password? = null

        private const val GMAIL: String = "Gmail"

        private const val GMAIL_LEGACY: String = "Gmail Legacy"

        private const val YAHOO: String = "Yahoo"

        private const val AOL: String = "Aol"

        private val providerSpinnerItems: List<String> = listOf(GMAIL, GMAIL_LEGACY, YAHOO, AOL)
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

                    binding.selectAccount.isEnabled = true
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

        accountSelected?.let { account ->
            client.accounts().addOnSuccessListener {
                it.account(account)?.let { account ->
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
                            "find tester account ${account.username}",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }?: noAccountSelectedAlert()

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

        accountSelected?.let { account ->
            client.accounts().addOnSuccessListener {
                it.account(account)?.let {
                    client.logout(
                        account
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
        }?: noAccountSelectedAlert()

        setDefaultAccountIfExists()
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

        setDefaultAccountIfExists()
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
                Timberland.d("credentials $credentials results $result")

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
                Timberland.e(throwable)

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
                Timberland.d("credentials $credentials results $result")

                messages[credentials] = result

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

        accountSelected?.let { account ->
            client.accounts().addOnSuccessListener {
                it.account(account)?.let { account ->
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
                            "find tester account ${account.username}",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }?: noAccountSelectedAlert()
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onVerify(view: View) {
        binding.results.text = "Verifying account..."

        accountSelected?.let { account ->
            client.accounts().addOnSuccessListener {
                it.account(account)?.let { account ->
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
                            "find tester account ${account.username}",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }?: noAccountSelectedAlert()
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun onLogin(view: View) {
        binding.results.text = "Logging in..."

        val binding = CredentialsViewBinding.inflate(layoutInflater)

        var selectedPosition = 0

        binding.provider.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, providerSpinnerItems)

        binding.provider.setSelection(selectedPosition)

        binding.provider.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPosition = position

                when (position){
                    0 -> {
                        binding.email.visibility = View.GONE
                        binding.password.visibility = View.GONE
                        binding.appPasswordMode.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.email.visibility = View.VISIBLE
                        binding.password.visibility = View.VISIBLE
                        binding.appPasswordMode.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Credentials")
            .setView(binding.root)
            .setPositiveButton(
                "Ok"
            ) { it, _ ->
                it.dismiss()

                if (selectedPosition != 0){
                    if (binding.email.text.isNullOrEmpty() || binding.password.text.isNullOrEmpty()) {
                        Toast.makeText(applicationContext, "Username and password can't be blank", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                }

                Toast.makeText(applicationContext, "Logging in...", Toast.LENGTH_SHORT).show()

                if (!supportFragmentManager.isDestroyed) {

                    val credentials: Credentials = when (selectedPosition){
                        0 -> Credentials.None.Gmail(if (binding.appPasswordMode.isChecked) AppPassword.MANUAL else AppPassword.AUTOMATIC)
                        1 -> Credentials.Password.Gmail(binding.email.text.toString(), binding.password.text.toString())
                        2 -> Credentials.Password.Yahoo(binding.email.text.toString(), binding.password.text.toString())
                        3 -> Credentials.Password.Aol(binding.email.text.toString(), binding.password.text.toString())
                        else -> throw IllegalArgumentException("selected provider is not valid")
                    }

                    ProviderSetupFragmentFactory.create(credentials).callback {
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
                            ProviderSetupResults.LSA_ENABLED -> Timberland.e("LSA_ENABLED")
                            ProviderSetupResults.NO_APP_PASSWORD -> Timberland.e("NO_APP_PASSWORD")
                            ProviderSetupResults.UNKNOWN -> Timberland.e("UNKNOWN")
                            ProviderSetupResults.DUPLICATE_EMAIL -> Timberland.e("DUPLICATE_EMAIL")
                            ProviderSetupResults.USER_CANCELLED -> Timberland.e("USER_CANCELLED")
                            ProviderSetupResults.REDIRECT_TO_BROWSER -> Timberland.e("REDIRECT_TO_BROWSER")
                            ProviderSetupResults.ADMIN_NEEDED -> Timberland.e("ADMIN_NEEDED")
                            ProviderSetupResults.RESULT_SAVED -> Timberland.e("RESULT_SAVED")
                        }

                        if (!supportFragmentManager.isDestroyed) {
                            val dialog = supportFragmentManager.findFragmentByTag(
                                TAG
                            ) as ProviderFragment

                            if (dialog.isAdded) {
                                dialog.dismiss()
                            }
                        }

                        setDefaultAccountIfExists()
                    }.show(supportFragmentManager, TAG)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSelectAccount(view: View) {
        val dialog = AlertDialog.Builder(this).setTitle("Select account")
        client.accounts().addOnSuccessListener { account ->
            dialog.setItems(account.map { it.username }.toTypedArray()) { _, index ->
                setDefaultAccountIfExists(account[index])
            }
            dialog.show()
        }
    }

    private fun setDefaultAccountIfExists(account: Credentials.Password) {
        accountSelected = account
        binding.selectedAccount.setText("Selected account: ${accountSelected!!.username}")
    }

    private fun setDefaultAccountIfExists() {
        client.accounts().addOnSuccessListener {
            if (it.any()) {
                accountSelected = it[0]
                binding.selectedAccount.setText("Selected account: ${accountSelected!!.username}")
            } else {
                accountSelected = null
                binding.selectedAccount.setText("No account selected")
            }
        }
    }

    private fun noAccountSelectedAlert(){
        Toast.makeText(
            applicationContext,
            "No account selected!",
            Toast.LENGTH_SHORT
        ).show()
    }

}
