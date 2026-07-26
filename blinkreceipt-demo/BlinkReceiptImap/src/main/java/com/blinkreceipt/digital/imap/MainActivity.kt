package com.blinkreceipt.digital.imap

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.google.android.gms.tasks.Tasks
import com.microblink.core.InitializeCallback
import com.microblink.core.ScanResults
import com.microblink.digital.*
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

    private var uiState by mutableStateOf(ImapUiState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(this.window)

        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).isAppearanceLightStatusBars = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            @Suppress("DEPRECATION")
            this.window.setStatusBarContrastEnforced(true)
        }

        setContent {
            ImapTheme {
                // Auto-scrape is armed process-wide in BlinkApplication, because close() cancels
                // the periodic schedule and so the client has to outlive this Activity. Its
                // results and its interval therefore live in AutoScrapeController rather than in
                // this Activity's state; fold each new result into the results line.
                val autoScrape = AutoScrapeController.latestResult

                LaunchedEffect(autoScrape) {
                    autoScrape?.let { results(it) }
                }

                ImapScreen(
                    state = uiState,
                    requestedIntervalHours = AutoScrapeController.requestedIntervalHours,
                    effectiveIntervalHours = AutoScrapeController.effectiveIntervalHours,
                    onAction = ::onAction,
                    onIntervalSelected = { AutoScrapeController.select(this, it) },
                    onCredentialsConfirmed = ::onCredentialsConfirmed,
                    onCredentialsDismissed = { credentialsVisible(false) }
                )
            }
        }

        client = ImapClient(
            applicationContext,
            object : InitializeCallback {

                override fun onComplete() {
                    Toast.makeText(
                        applicationContext,
                        "Imap is ready!", Toast.LENGTH_SHORT
                    ).show()

                    uiState = uiState.copy(enabledActions = ImapAction.entries.toSet())
                }

                override fun onException(throwable: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        throwable.toString(), Toast.LENGTH_SHORT
                    ).show()

                    // Nothing on this screen works without an initialized ImapClient, so every
                    // action stays greyed out and the failure is reported in the results line.
                    uiState = uiState.copy(
                        results = throwable.toString(),
                        enabledActions = emptySet()
                    )
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

    private fun onAction(action: ImapAction) {
        when (action) {
            ImapAction.MESSAGES -> onMessagesClick()
            ImapAction.LOGIN -> onLogin()
            ImapAction.LOGOUT -> onLogout()
            ImapAction.CLEAR -> onClear()
            ImapAction.VERIFY -> onVerify()
            ImapAction.DEBUG -> onDebugMessages()
            ImapAction.REMOTE_MESSAGES -> onRemoteMessages()
            ImapAction.MULTIPLE_REMOTE -> onMultipleRemoteMessages()
            ImapAction.MULTIPLE_MESSAGES -> onMultipleMessages()
            ImapAction.SINGLE_LOGOUT -> onSingleLogout()
        }
    }

    private fun results(text: String) {
        uiState = uiState.copy(results = text)
    }

    private fun credentialsVisible(visible: Boolean) {
        uiState = uiState.copy(credentialsVisible = visible)
    }

    private fun onClear() {
        client.clearLastCheckedTime().addOnSuccessListener {
            results("")

            Toast.makeText(
                applicationContext,
                "Cleared last checked time", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun onMessagesClick() {
        tester?.let { credentials ->
            results("Searching for messages...")

            client.accounts().addOnSuccessListener {
                it.account(credentials)?.let { account ->
                    client.messages(account).addOnSuccessListener { scanResults ->
                        results("ScanResults Size: ${scanResults.size}")

                        Toast.makeText(
                            applicationContext,
                            "ScanResults Size: ${scanResults.size}", Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener { failure ->
                        results("User messages failure: $failure")

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
        } ?: accountNotLinkedAlert()
    }

    private fun onDebugMessages() {
        results("Searching for debug messages...")

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

                    results("ScanResults Size: ${it.size}")
                }.addOnFailureListener {
                    Toast.makeText(
                        applicationContext,
                        "User messages failure: $it", Toast.LENGTH_SHORT
                    ).show()

                    results("User messages failure: $it")
                }
        }.addOnFailureListener {
            Log.e(LOG_TAG, "failure in onDebugMessages", it)

            Toast.makeText(
                applicationContext,
                "html exception: $it", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun onSingleLogout() {
        tester?.let { credentials ->
            results("Logging user out of account...")

            client.accounts().addOnSuccessListener {
                it.account(credentials)?.let {
                    client.logout(
                        credentials
                    ).addOnSuccessListener { loggedOut ->
                        Toast.makeText(
                            applicationContext,
                            "User logged out $loggedOut", Toast.LENGTH_SHORT
                        ).show()

                        results("User logged out $loggedOut")

                        tester = null
                    }.addOnFailureListener { failure ->
                        Toast.makeText(
                            applicationContext,
                            "User logout failure: $failure", Toast.LENGTH_SHORT
                        ).show()

                        results("User logout failure: $failure")
                    }
                }
            }
        } ?: accountNotLinkedAlert()
    }

    private fun onLogout() {
        results("Logging user out of all accounts...")

        client.logout().addOnSuccessListener {
            Toast.makeText(
                applicationContext,
                "User logged out $it", Toast.LENGTH_SHORT
            ).show()

            results("User logged out $it")

            tester = null
        }.addOnFailureListener {
            Toast.makeText(
                applicationContext,
                "User logout failure: $it", Toast.LENGTH_SHORT
            ).show()

            results("User logout failure: $it")
        }
    }

    private fun onMultipleMessages() {
        results("Multiple Messages...")

        val messages = mutableMapOf<Credentials.Password, List<ScanResults>>()

        client.messages(object : MessagesCallback {

            override fun onComplete(
                credentials: Credentials.Password,
                result: List<ScanResults>
            ) {
                Log.d(LOG_TAG, "credentials $credentials results $result")

                messages[credentials] = result

                results(
                    "Multiple Messages ${messages.size} ${
                        buildString {
                            messages.forEach { (t, u) ->
                                append("${t.username} : ${u.size} \n")
                            }
                        }
                    }"
                )
            }

            override fun onException(throwable: Throwable) {
                Log.e(LOG_TAG, "failure in onException", throwable)

                results("Multiple Messages $throwable")
            }
        })
    }

    private fun onMultipleRemoteMessages() {
        results("Multiple Remote Messages...")

        val messages = mutableMapOf<Credentials.Password, JobResults>()

        client.remoteMessages(object : JobResultsCallback {

            override fun onComplete(credentials: Credentials.Password, result: JobResults) {
                Log.d(LOG_TAG, "credentials $credentials results $result")

                messages[credentials] = result

                results("Multiple Remote ${messages.size}")
            }

            override fun onException(throwable: Throwable) {
                Log.e(LOG_TAG, "failure in onException", throwable)

                results("Multiple Remote $throwable")
            }
        })
    }

    private fun onRemoteMessages() {
        tester?.let { credentials ->
            results("Remote Messages...")

            client.accounts().addOnSuccessListener {
                it.account(credentials)?.let { account ->
                    client.remoteMessages(account).addOnSuccessListener { remote ->
                        Toast.makeText(
                            applicationContext,
                            "Remote messages $remote", Toast.LENGTH_SHORT
                        ).show()

                        results("Remote messages $remote")
                    }.addOnFailureListener { failure ->
                        Toast.makeText(
                            applicationContext,
                            "Remote messages failure: $failure", Toast.LENGTH_SHORT
                        ).show()

                        results("Remote messages failure: $failure")
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
        } ?: accountNotLinkedAlert()
    }

    private fun onVerify() {
        tester?.let { credentials ->
            results("Verifying account...")

            client.accounts().addOnSuccessListener {
                it.account(credentials)?.let { account ->
                    client.verify(account).addOnSuccessListener { verified ->
                        Toast.makeText(
                            applicationContext,
                            "Verify: $verified", Toast.LENGTH_SHORT
                        ).show()

                        results("Account Verify: $verified")
                    }.addOnFailureListener { failure ->
                        Toast.makeText(
                            applicationContext,
                            "Verify failure: $failure", Toast.LENGTH_SHORT
                        ).show()

                        results("Account Failure: $failure")
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
        } ?: accountNotLinkedAlert()
    }

    private fun onLogin() {
        results("Logging in...")

        credentialsVisible(true)
    }

    /** "Ok" on the credentials dialog: hand off to the SDK's provider setup flow. */
    private fun onCredentialsConfirmed() {
        credentialsVisible(false)

        Toast.makeText(applicationContext, "Logging in...", Toast.LENGTH_SHORT).show()

        val account: Credentials = Credentials.None.Gmail()

        if (!supportFragmentManager.isDestroyed) {
            ProviderSetupFragmentFactory.create(
                account
            ).callback { providerResult: ProviderResults ->
                results("Status ${providerResult.results.name}")

                Toast.makeText(
                    applicationContext,
                    "Status ${providerResult.results.name}",
                    Toast.LENGTH_SHORT
                ).show()

                when (providerResult.results) {
                    ProviderSetupResults.BAD_PASSWORD -> Log.d(LOG_TAG, "BAD_PASSWORD")
                    ProviderSetupResults.BAD_EMAIL -> Log.d(LOG_TAG, "BAD_EMAIL")
                    ProviderSetupResults.CREATED_APP_PASSWORD -> {
                        Log.d(LOG_TAG, "CREATED_APP_PASSWORD")
                        val linked = providerResult.credentials as Credentials.Password

                        tester = linked

                        Toast.makeText(
                            applicationContext,
                            "Linked: ${linked.username}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ProviderSetupResults.NO_CREDENTIALS -> Log.d(LOG_TAG, "NO_CREDENTIALS")
                    ProviderSetupResults.UNKNOWN -> Log.d(LOG_TAG, "UNKNOWN")
                    ProviderSetupResults.NO_APP_PASSWORD -> Log.d(LOG_TAG, "NO_APP_PASSWORD")
                    ProviderSetupResults.LSA_ENABLED -> Log.d(LOG_TAG, "LSA_ENABLED")
                    ProviderSetupResults.DUPLICATE_EMAIL -> Log.d(LOG_TAG, "DUPLICATE_EMAIL")
                    ProviderSetupResults.USER_CANCELLED -> Log.d(LOG_TAG, "USER_CANCELLED")
                    ProviderSetupResults.REDIRECT_TO_BROWSER -> Log.d(LOG_TAG, "REDIRECT_TO_BROWSER")
                    ProviderSetupResults.ADMIN_NEEDED -> Log.d(LOG_TAG, "ADMIN_NEEDED")
                    ProviderSetupResults.RESULT_SAVED -> Log.d(LOG_TAG, "RESULT_SAVED")
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
                        when (val credentials = providerResult.credentials) {
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

    private fun accountNotLinkedAlert() {
        Toast.makeText(applicationContext, "Please login to account", Toast.LENGTH_SHORT).show()
    }
}
