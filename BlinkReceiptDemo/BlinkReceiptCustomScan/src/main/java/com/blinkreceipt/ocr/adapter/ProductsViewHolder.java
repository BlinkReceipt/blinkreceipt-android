package com.blinkreceipt.ocr.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.blinkreceipt.ocr.R;
import com.microblink.Product;
import com.microblink.TypeValueUtils;

public final class ProductsViewHolder extends RecyclerView.ViewHolder {

    private TextView name;

    private TextView total;

    ProductsViewHolder(@NonNull View itemView) {
        super( itemView );

        name = itemView.findViewById( R.id.name );

        total = itemView.findViewById( R.id.total );
    }

    public void bind( @NonNull Product product ) {
        Context context = itemView.getContext();

        name.setText( TypeValueUtils.value( product.description() ) );

        total.setText( context.getString( R.string.total_price, product.totalPrice() ) );
    }

}
