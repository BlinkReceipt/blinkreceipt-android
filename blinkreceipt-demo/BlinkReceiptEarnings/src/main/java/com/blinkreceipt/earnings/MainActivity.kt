package com.blinkreceipt.earnings

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.ViewGroupCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.blinkreceipt.earnings.databinding.ActivityMainBinding
import com.microblink.earnings.MissedEarningsClient

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(this.window)

        _binding = ActivityMainBinding.inflate(layoutInflater)
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
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEarnings(view: View) {
        MissedEarningsClient(applicationContext)
                .results("[REPLACE WITH BLINK RECEIPT ID]").addOnSuccessListener {
                    Toast.makeText(applicationContext, "Results $it", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
                }
    }

}