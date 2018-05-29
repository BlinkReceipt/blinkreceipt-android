package com.blinkreceipt.ocr.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blinkreceipt.ocr.R;
import com.microblink.Product;

import java.util.ArrayList;
import java.util.List;

public final class ProductsAdapter extends RecyclerView.Adapter<ProductsViewHolder> {

    private final List<Product> products = new ArrayList<>();

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.products_list_item, parent,false );

        return new ProductsViewHolder( view );
    }

    @Override
    public void onBindViewHolder( @NonNull ProductsViewHolder holder, int position ) {
        holder.bind( products.get( position ) );
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void addAll( @NonNull List<Product> items ) {
        products.clear();

        products.addAll( items );

        notifyDataSetChanged();
    }

    @Override
    public String toString() {
        return "ProductsAdapter{" +
                "products=" + products +
                '}';
    }

}
