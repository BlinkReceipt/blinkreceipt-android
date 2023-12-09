package com.blinkreceipt.scan.pdf.internal

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.blinkreceipt.scan.pdf.R


internal class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imageView: ImageView

    init {
        imageView = itemView.findViewById<ImageView>(R.id.image_view)
    }
}

