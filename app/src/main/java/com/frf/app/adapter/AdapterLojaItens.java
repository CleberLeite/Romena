package com.frf.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.frf.app.R;
import com.frf.app.data.ShopItemData;
import com.frf.app.utils.UTILS;

import java.util.List;

public class AdapterLojaItens extends RecyclerView.Adapter<AdapterLojaItens.ViewHolder> {

    Context context;
    List<ShopItemData> data;
    Listener listener;
    int margin = 0;
    int page = 1;

    public AdapterLojaItens(Context context, List<ShopItemData> data, int margin, Listener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
        this.margin = margin;
    }

    public void addData(ShopItemData shopItemData) {
        data.add(shopItemData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.adapter_item_loja, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position == (data.size() - margin)) {
            page++;
            listener.onPage(page);
        }

        ShopItemData data = this.data.get(position);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            UTILS.getImageAt(context, data.getImg(), holder.image, new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(UTILS.convertDpToPixels(10))));
        } else {
            UTILS.getImageAt(context, data.getImg(), holder.image);
        }

        holder.txtDescription.setText(data.getTitle());
        holder.cardLoja.setOnClickListener(view -> listener.onProductClick(data));

        if (data.getValue() != -1 && !data.isRedeemed()) {
            holder.shapeCoins.setVisibility(View.VISIBLE);
            holder.txtCoin.setText(data.getValue() + "");
        }

        if (data.getTypeValue() != null && !data.getTypeValue().equals("")) {
            holder.iconCoin.setVisibility(View.GONE);
            holder.txtCoin.setText(data.getTypeValue());
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public interface Listener {
        void onProductClick(ShopItemData data);
        void onPage(int page);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardDiscont;
        View cardLoja;
        View shapeCoins;
        TextView txtDescription;
        ImageView image;
        ImageView iconCoin;
        TextView txtCoin;
        FrameLayout llFrame;

        //layout 2
        LinearLayout content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardDiscont = itemView.findViewById(R.id.cardDiscont);
            llFrame = itemView.findViewById(R.id.llFrame);
            image = itemView.findViewById(R.id.image);
            iconCoin = itemView.findViewById(R.id.icon_coin);
            txtDescription = itemView.findViewById(R.id.txt_description);
            cardLoja = itemView.findViewById(R.id.cardLoja);
            shapeCoins = itemView.findViewById(R.id.shape_coins);
            txtCoin = itemView.findViewById(R.id.txt_coin);

            content = itemView.findViewById(R.id.contact);
        }
    }
}