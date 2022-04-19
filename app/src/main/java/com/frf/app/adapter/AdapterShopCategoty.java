package com.frf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.frf.app.R;
import com.frf.app.data.CategoriesData;

import java.util.ArrayList;

public class AdapterShopCategoty extends RecyclerView.Adapter<AdapterShopCategoty.ViewHolder> {

    Context context;
    ArrayList<CategoriesData> data;
    Listener listener;

    public AdapterShopCategoty(Context context, ArrayList<CategoriesData> data, Listener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_shop_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        CategoriesData categoria = data.get(position);

        int drawable = 0;

        switch (categoria.getCategoryID()) {
            case 130: {
                drawable = R.drawable.store_category_barbati;
            }
            break;
            case 140: {
                drawable = R.drawable.store_category_femei;
            }
            break;
            case 132: {
                drawable = R.drawable.store_category_juniori;
            }
            break;
            case 125: {
                drawable = R.drawable.store_category_accesorii;
            }
            break;
            case 127: {
                drawable = R.drawable.store_category_echipa_nationala;
            }
            break;
            case 137: {
                drawable = R.drawable.store_category_arbitri;
            }
            break;
            case 30: {
                //drawable = R.drawable.ic_about;
            }
            break;
            case 31: {
                drawable = R.drawable.store_category_personalizare;
            }
            break;
        }

        if (drawable != 0) {
            Glide.with(context)
                    .load(drawable)
                    .into(holder.imgShopCategory);
        }

        holder.txtShopCategory.setText(categoria.getInfo().getCategory());

        holder.cardShop.setOnClickListener(view -> listener.onClick(categoria.getCategoryID(), categoria.getInfo().getCategory()));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public interface Listener {
        void onClick(Integer id, String cat);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View cardShop;
        ImageView imgShopCategory;
        TextView txtShopCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardShop = itemView.findViewById(R.id.cardShopCategory);
            imgShopCategory = itemView.findViewById(R.id.img_shop_category);
            txtShopCategory = itemView.findViewById(R.id.txt_shop_category);
        }
    }
}