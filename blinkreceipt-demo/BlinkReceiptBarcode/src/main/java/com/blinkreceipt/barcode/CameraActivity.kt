package com.blinkreceipt.barcode

import android.content.res.Configuration
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blinkreceipt.barcode.databinding.ActivityCameraBinding
import com.microblink.barcode.DefaultDecoderFactory
import com.microblink.barcode.MetadataCallbacks
import com.microblink.barcode.RecognizerBundle
import com.microblink.barcode.RecognizerClient
import com.microblink.core.Timberland
import com.microblink.hardware.orientation.Orientation
import com.microblink.view.BaseCameraView
import com.microblink.view.CameraAspectMode
import com.microblink.view.CameraEventsListener

class CameraActivity : AppCompatActivity(), CameraEventsListener {

    private var _binding: ActivityCameraBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityCameraBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val client = RecognizerClient(this)

        val callbacks = MetadataCallbacks()

        callbacks.failedDetectionCallback {
            Toast.makeText(applicationContext, "Unable to find barcode!", Toast.LENGTH_LONG).show()
        }

        callbacks.recognizerCallback {
            client.lookup(it.text()).addOnSuccessListener { product ->
                Toast.makeText(applicationContext, "Name ${product?.name()}", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                binding.recognizer.resumeScanning(true)

                Timberland.e(it)
            }
        }

        binding.recognizer.also {
            it.metadataCallbacks(callbacks)

            it.scanResultListener {

            }

            it.setOrientationAllowedListener {
                true
            }

            it.cameraEventsListener = this@CameraActivity

            it.decoder(DefaultDecoderFactory())

            it.recognizerBundle(RecognizerBundle())

            it.initialOrientation = Orientation.ORIENTATION_PORTRAIT

            it.aspectMode = CameraAspectMode.ASPECT_FILL

            it.initialOrientation = Orientation.ORIENTATION_PORTRAIT
        }

        binding.recognizer.create()
    }

    override fun onStart() {
        super.onStart()

        binding.recognizer.start()
    }

    override fun onResume() {
        super.onResume()

        binding.recognizer.resume()
    }

    override fun onPause() {
        super.onPause()

        binding.recognizer.pause()
    }

    override fun onStop() {
        super.onStop()

        binding.recognizer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.recognizer.destroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        binding.recognizer.changeConfiguration(newConfig)
    }

    override fun onAutofocusFailed() {
    }

    override fun onAutofocusStarted(focusAreas: Array<out Rect>?) {
    }

    override fun onAutofocusStopped(focusAreas: Array<out Rect>?) {
    }

    override fun onCameraPreviewStarted() {
        if (binding.recognizer.cameraViewState != BaseCameraView.CameraViewState.RESUMED) {
            Timberland.w("Camera preview started callback received after view was paused")

            return
        }

        binding.recognizer.setMeteringAreas(arrayOf(
                RectF(.05f, .10f, .95f, .90f)
        ), true)
    }

    override fun onCameraPreviewStopped() {
    }

    override fun onError(throwable: Throwable) {
    }

    override fun onCameraPermissionDenied() {
    }

    fun onResumeScan(view: View) {
        binding.recognizer.resumeScanning(true)
    }

}