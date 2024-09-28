package com.blinkreceipt.digital.outlook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.microblink.core.InitializeCallback
import com.microblink.digital.OutlookClient

class MainActivity : AppCompatActivity() {

    private lateinit var client: OutlookClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

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
