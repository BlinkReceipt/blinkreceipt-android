package com.blinkreceipt.ocr.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.blinkreceipt.ocr.R;
import com.blinkreceipt.ocr.TypeValueUtils;
import com.microblink.core.Product;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

final class ProductsViewHolder extends RecyclerView.ViewHolder {

    private final TextView name;

    private final TextView total;

    ProductsViewHolder(@NonNull View itemView) {
        super( itemView );

        name = itemView.findViewById( R.id.name );

        total = itemView.findViewById( R.id.total );
    }

    void bind(@NonNull Product product) {
        Context context = itemView.getContext();

        name.setText( TypeValueUtils.value( product.description() ) );

        total.setText( context.getString( R.string.total_price, TypeValueUtils.value(product.totalPrice()) ) );
    }

}
