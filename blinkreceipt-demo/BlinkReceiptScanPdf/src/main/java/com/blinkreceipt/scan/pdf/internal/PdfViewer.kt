package com.blinkreceipt.scan.pdf.internal

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.collection.LruCache
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.microblink.core.Events

internal class PdfViewer(
    private val events: Events = Events.NONE
) : DefaultLifecycleObserver {

    private var renderer: PdfRenderer? = null

    private val contents: LruCache<Int, Bitmap> by lazy {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

        val cacheSize = maxMemory / 8

        LruCache(cacheSize)
    }

    fun parse(descriptor: ParcelFileDescriptor) {
        close()

        renderer = PdfRenderer(descriptor)

        val pageCount = renderer?.pageCount ?: -1

        for (pageIndex in 0 until pageCount) {
            val page = renderer?.openPage(pageIndex)

            page?.let {
                val bitmap = Bitmap.createBitmap(
                    page.width,
                    page.height,
                    Bitmap.Config.ARGB_8888,
                )

                page.render(
                    bitmap,
                    null,
                    null,
                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY,
                )

                contents.put(pageIndex, bitmap)

                page.close()
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        close()
    }

    fun size(): Int {
        return contents.size()
    }

    fun get(position: Int): Bitmap? {
        return contents[position]
    }

    fun clear() {
        contents.evictAll()
    }

    private fun close() {
        runCatching {
            clear()
        }.onFailure {
            events.throwable {
                it
            }
        }

        runCatching {
            renderer?.close()
        }.onFailure {
            events.throwable {
                it
            }
        }
    }
}