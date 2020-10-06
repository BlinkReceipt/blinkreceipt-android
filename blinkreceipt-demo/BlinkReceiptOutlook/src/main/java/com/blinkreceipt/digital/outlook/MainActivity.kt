package com.blinkreceipt.digital.outlook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.microblink.core.InitializeCallback
import com.microblink.core.Timberland
import com.microblink.digital.OutlookClient

class MainActivity : AppCompatActivity() {

    private lateinit var client: OutlookClient

    init {
        Timberland.enable( true )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        client = OutlookClient(applicationContext, R.raw.auth_config_single_account, object : InitializeCallback {

            override fun onComplete() {
                Toast.makeText(this@MainActivity,
                        "Microsoft is ready!", Toast.LENGTH_SHORT).show()
            }

            override fun onException(throwable: Throwable) {
                Toast.makeText(this@MainActivity,
                        throwable.toString(), Toast.LENGTH_SHORT).show()
            }

        }).apply {
            dayCutoff( 15 )
        }
    }

    fun onClear(view: View) {
        client.clearLastCheckedTime().addOnSuccessListener {
            Toast.makeText(this@MainActivity,
                    "Cleared Results", Toast.LENGTH_LONG).show()
        }
    }

    fun onMessagesClick(view: View) {
        client.messages().addOnSuccessListener { data ->
            Toast.makeText(this@MainActivity,
                    "ScanResults Size: ${data?.size.toString()}", Toast.LENGTH_LONG).show()

            data?.forEach {
                Timberland.d(it.toString())
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this@MainActivity,
                    "User login failure: ${e.toString()}", Toast.LENGTH_LONG).show()
        }
    }

    fun onLogout(view: View) {
        client.logout()
                .addOnSuccessListener {
                    Toast.makeText(this@MainActivity,
                            "User logged out!", Toast.LENGTH_LONG).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(this@MainActivity,
                            "User Logout failure: ${e.toString()}", Toast.LENGTH_LONG).show()
                }
    }

    fun onLogin(view: View) {
        client.login(this).addOnSuccessListener { data ->
            Toast.makeText(this@MainActivity,
                    "User logged in! ${data?.toString()}", Toast.LENGTH_LONG).show()
        }.addOnFailureListener { e ->
            Toast.makeText(this@MainActivity,
                    "User login failure: ${e.toString()}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        client.destroy()
    }
}
