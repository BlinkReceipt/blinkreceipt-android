package com.actualplatform.android.activation.development

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.actualplatform.android.activation.development.ui.ActivationActivity
import com.microblink.ScanOptions
import com.microblink.camera.ui.CameraCharacteristics

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EngageHomeScreen()
        }
    }
}

@Composable
private fun EngageHomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val activity = LocalActivity.current
        val context = LocalContext.current
        Button(
            onClick = {
                activity?.let {
                    val intent = Intent(context, ActivationActivity::class.java).apply {
                        putExtra(
                            ActivationActivity.Companion.OPTIONS_KEY,
                            ScanOptions.Builder()
                                .detectDuplicates(true)
                                .build(),
                        )
                        putExtra(
                            ActivationActivity.Companion.CAMERA_CHARACTERISTICS,
                            CameraCharacteristics.Builder()
                                .cameraPermission(true)
                                .build(),
                        )

                    }
                    it.startActivity(intent)
                }
            }
        ) {
            Text(text = stringResource(R.string.activation_title))
        }
    }
}
