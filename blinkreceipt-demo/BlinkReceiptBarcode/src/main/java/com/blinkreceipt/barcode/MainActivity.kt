package com.blinkreceipt.barcode

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blinkreceipt.barcode.databinding.ActivityMainBinding
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks

class MainActivity : AppCompatActivity(), PermissionCallbacks {

    private var _binding: ActivityMainBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, permissions: List<String?>) {
        startCamera()
    }

    override fun onPermissionsDenied(requestCode: Int, permissions: List<String?>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, permissions)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    fun onPermissionsCheck(view: View) {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            startCamera()
        } else {
            EasyPermissions.requestPermissions(this, "Check Camera Permissions",
                    REQUEST_CODE, Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        startActivity(Intent(this, CameraActivity::class.java))
    }

    companion object {

        const val REQUEST_CODE = 1_000

    }

}