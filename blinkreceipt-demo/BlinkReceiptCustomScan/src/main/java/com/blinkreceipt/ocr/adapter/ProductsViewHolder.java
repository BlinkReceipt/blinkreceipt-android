package com.blinkreceipt.ocr.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.blinkreceipt.ocr.R;
import com.microblink.core.Product;
import com.microblink.core.internal.TypeValueUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

final class ProductsViewHolder extends RecyclerView.ViewHolder {

    private TextView name;

    private TextView total;

    ProductsViewHolder(@NonNull View itemView) {
        super( itemView );

        name = itemView.findViewById( R.id.name );

        total = itemView.findViewById( R.id.total );
    }

    void bind(@NonNull Product product) {
        Context context = itemView.getContext();

        name.setText( TypeValueUtils.value( product.description() ) );

        total.setText( context.getString( R.string.total_price, product.totalPrice() ) );
    }

}
