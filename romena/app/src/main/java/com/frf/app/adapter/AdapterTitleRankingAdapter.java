package com.frf.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.frf.app.R;
import com.frf.app.data.RankingTitleData;
import com.frf.app.informations.UserInformation;
import com.frf.app.utils.UTILS;
import com.frf.app.views.RoundedShadowedLayout;

import java.util.List;

public class AdapterTitleRankingAdapter extends RecyclerView.Adapter<AdapterTitleRankingAdapter.ViewHolder> {

    Context context;
    List<RankingTitleData> data;
    boolean img;

    public AdapterTitleRankingAdapter(Context context, List<RankingTitleData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_title_ranking, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RankingTitleData index = data.get(position);

        if(img){
            holder.iconLock.setVisibility(View.VISIBLE);
            Drawable drawablePlace = context.getResources().getDrawable(R.drawable.ic_coin_anber_blue);

            UTILS.getImageAt(context, "", holder.icon_titulo, new RequestOptions()
                    .circleCrop()
                    .placeholder(drawablePlace)
                    .error(drawablePlace));
            holder.txt_total.setTextColor(context.getResources().getColor(R.color.TabBarUnselected));
            holder.viewCard.setBackgroundResource(R.drawable.card_view_curved_bg_lock);
        } else {
            holder.iconLock.setVisibility(View.GONE);
        }

        if (index.getTitle().equals(UserInformation.getUserData().getUserType())) {
            img = true;
            holder.txt_title.setTextColor(Color.parseColor("#ffffff"));
            holder.txt_total.setTextColor(context.getResources().getColor(R.color.PrimaryAnchor));
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                RoundedShadowedLayout rsl = (RoundedShadowedLayout) holder.viewCard;
                rsl.setBackgroundColor(context.getResources().getColor(R.color.ColorBlue));
            }else {
                holder.viewCard.setBackgroundResource(R.drawable.card_view_curved_bg);
            }

        }
        holder.txt_total.setText(String.valueOf(index.getValueCoins()));
        holder.txt_title.setText(index.getTitle());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconLock;
        TextView txt_total;
        TextView txt_title;
        View viewCard;
        ImageView icon_titulo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconLock = itemView.findViewById(R.id.icon_lock);
            txt_total = itemView.findViewById(R.id.txt_total);
            viewCard = itemView.findViewById(R.id.viewCard);
            txt_title = itemView.findViewById(R.id.txt_title);
            icon_titulo = itemView.findViewById(R.id.icon_titulo);
        }
    }
}
