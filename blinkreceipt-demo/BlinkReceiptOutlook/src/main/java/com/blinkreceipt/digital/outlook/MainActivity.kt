package com.blinkreceipt.digital.outlook

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.ViewGroupCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.blinkreceipt.digital.outlook.databinding.ActivityMainBinding
import com.microblink.core.InitializeCallback
import com.microblink.digital.OutlookClient

class MainActivity : AppCompatActivity() {

    private lateinit var client: OutlookClient
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(this.window)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val rootView: View = binding.getRoot()
        setContentView(rootView)

        ViewCompat.setOnApplyWindowInsetsListener(
            rootView
        ) { v: View, windowInsets: WindowInsetsCompat? ->
            val insets = windowInsets!!.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout()
            )
            // Apply the insets as padding to the view. Here, set all the dimensions
            // as appropriate to your layout. You can also update the view's margin if
            // more appropriate.
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
        ViewGroupCompat.installCompatInsetsDispatch(rootView)

        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).isAppearanceLightStatusBars = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            @Suppress("DEPRECATION")
            this.window.setStatusBarContrastEnforced(true)
        }

        client = OutlookClient(applicationContext,
            R.raw.auth_config_single_account, object : InitializeCallback {

            override fun onComplete() {
                Toast.makeText(this@MainActivity,
                        "Microsoft is ready!", Toast.LENGTH_SHORT).show()
            }

            override fun onException(throwable: Throwable) {
                Toast.makeText(this@MainActivity,
                        throwable.toString(), Toast.LENGTH_SHORT).show()
            }

        }).apply {
            dayCutoff(15)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onRemoteMessages(view: View) {
        client.remoteMessages().addOnSuccessListener {
            Toast.makeText(
                applicationContext,
                "Remote messages $it", Toast.LENGTH_SHORT
            ).show()
        }.addOnFailureListener {
            Toast.makeText(
                applicationContext,
                "Remote messages failure: $it", Toast.LENGTH_SHORT
            ).show()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClear(view: View) {
        client.clearLastCheckedTime().addOnSuccessListener {
            Toast.makeText(this@MainActivity,
                    "Cleared Results", Toast.LENGTH_LONG).show()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onMessagesClick(view: View) {
        client.messages().addOnSuccessListener { data ->
            Toast.makeText(this@MainActivity,
                    "ScanResults Size: ${data?.size.toString()}", Toast.LENGTH_LONG).show()

            data?.forEach {
                Log.d(TAG,  it.toString() )
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this@MainActivity,
                    "User login failure: $e", Toast.LENGTH_LONG).show()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onLogout(view: View) {
        client.logout()
                .addOnSuccessListener {
                    Toast.makeText(this@MainActivity,
                            "User logged out!", Toast.LENGTH_LONG).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(this@MainActivity,
                            "User Logout failure: $e", Toast.LENGTH_LONG).show()
                }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onLogin(view: View) {
        client.login(this).addOnSuccessListener { data ->
            Toast.makeText(this@MainActivity,
                    "User logged in! ${data?.toString()}", Toast.LENGTH_LONG).show()
        }.addOnFailureListener { e ->
            Toast.makeText(this@MainActivity,
                    "User login failure: $e", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        client.close()
    }

    private companion object {
        const val TAG = "OutlookMainActivity"
    }
}
