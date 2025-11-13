package com.blinkreceipt.ocr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.blinkreceipt.ocr.ui.MainRoute
import com.blinkreceipt.ocr.ui.theme.BlinkReceiptDemoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlinkReceiptDemoTheme {
                MainRoute()
            }
        }
    }
}
