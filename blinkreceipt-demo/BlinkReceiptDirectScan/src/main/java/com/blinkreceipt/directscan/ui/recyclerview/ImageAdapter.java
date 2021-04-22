package com.blinkreceipt.directscan.ui.recyclerview;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blinkreceipt.directscan.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    private Bitmap[] bitmaps;

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_holder_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        Bitmap bitmap = bitmaps[position];

        holder.bind(bitmap);
    }

    @Override
    public int getItemCount() {
        return bitmaps != null ? bitmaps.length : 0;
    }

    public void addAll(Bitmap[] bitmaps) {
        this.bitmaps = bitmaps;

        notifyDataSetChanged();
    }

    class ImageHolder extends RecyclerView.ViewHolder {

        private final ImageView holderImage;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);

            this.holderImage = itemView.findViewById(R.id.image_view);
        }

        public void bind(@NonNull Bitmap bitmap) {
            this.holderImage.setImageBitmap(bitmap);
        }
    }
}
