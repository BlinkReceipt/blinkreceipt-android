package com.blinkreceipt.earnings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.blinkreceipt.earnings.databinding.ActivityMainBinding
import com.microblink.earnings.MissedEarningsClient

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

    fun onEarnings(view: View) {
        MissedEarningsClient(applicationContext)
                .results("[REPLACE WITH BLINK RECEIPT ID]").addOnSuccessListener {
                    Toast.makeText(applicationContext, "Results $it", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
                }
    }

}