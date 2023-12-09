package com.blinkreceipt.scan.pdf.internal

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blinkreceipt.scan.pdf.R

internal class PdfViewerAdapter(
    private val context: Context,
    private val viewer: PdfViewer
) : RecyclerView.Adapter<ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(context).inflate(R.layout.image_view_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return viewer.size()
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageView.setImageBitmap(viewer.get(position))
    }

    fun clear() {
        notifyItemRangeRemoved(0, viewer.size())

        viewer.clear()
    }

}