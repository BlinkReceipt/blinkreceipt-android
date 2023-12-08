package com.blinkreceipt.scan.pdf

import android.content.ContentResolver
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.LinearLayoutManager
import com.blinkreceipt.scan.pdf.databinding.ActivityMainBinding
import com.blinkreceipt.scan.pdf.internal.PdfViewer
import com.blinkreceipt.scan.pdf.internal.PdfViewerAdapter
import com.microblink.PdfClient
import com.microblink.ScanOptions
import com.microblink.internal.READ_MODE

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null

    @VisibleForTesting
    internal val binding
        get() = _binding!!

    private lateinit var client: PdfClient

    private var document: Uri? = null

    private val pdfViewer: PdfViewer by lazy {
        PdfViewer()
    }

    private lateinit var pdfAdapter: PdfViewerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        client = PdfClient(applicationContext).also {
            val options: ScanOptions = ScanOptions.newBuilder().build()

            it.countryCode = options.countryCode()

            it.filterSensitiveData = options.filterSensitiveData()

            it.returnSubProducts = options.returnSubProducts()

            it.returnVoidedProducts = options.returnVoidedProducts()

            lifecycle.addObserver(it)
        }

        pdfAdapter = PdfViewerAdapter(this, pdfViewer)

        lifecycle.addObserver(pdfViewer)

        val launcher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            document = uri

            uri?.let {
                contentResolver.toFileDescriptor(uri)?.let { contents ->
                    runCatching {
                        pdfViewer.parse(contents).also {
                            pdfAdapter.notifyItemRangeInserted(0, pdfViewer.size())
                        }

                        contents.close()
                    }.onFailure {
                        Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.selectPdf.setOnClickListener {
            pdfAdapter.clear()

            launcher.launch(arrayOf(MIME_TYPE))
        }

        binding.scanPdf.setOnClickListener {
            document?.let { uri ->
                Toast.makeText(applicationContext, "Scanning PDF", Toast.LENGTH_SHORT).show()

                runCatching {
                    client.recognize(uri)
                        .addOnSuccessListener(this) {
                            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG)
                                .show()
                        }.addOnFailureListener {
                            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                }.onFailure {
                    Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                }
            } ?: Toast.makeText(
                applicationContext,
                getString(R.string.please_select_a_pdf), Toast.LENGTH_SHORT,
            ).show()
        }

        with(binding.pdfViewer) {
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = pdfAdapter
        }
    }

    private fun ContentResolver.toFileDescriptor(uri: Uri): ParcelFileDescriptor? =
        openFileDescriptor(uri, READ_MODE)


    internal companion object {

        const val MIME_TYPE = "application/pdf"
    }
}