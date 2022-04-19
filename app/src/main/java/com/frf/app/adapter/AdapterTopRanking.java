package com.frf.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.request.RequestOptions;
import com.frf.app.R;
import com.frf.app.data.RankData;
import com.frf.app.utils.UTILS;

import java.util.List;

public class AdapterTopRanking extends RecyclerView.Adapter<AdapterTopRanking.ViewHolder> {

    Context context;
    List<RankData> data;
    Listener listener;
    int margin;

    public AdapterTopRanking(Context context, List<RankData> data, int margin, Listener listener) {
        this.context = context;
        this.data = data;
        this.margin = margin;
        this.listener = listener;
    }

    public void addData (List<RankData> rank){
        if(rank != null)
            data.addAll(rank);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_top_ranking, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position >= (data.size()-1) - margin){
            listener.onLastItem();
        }

        RankData user = data.get(position);

        if(position == 0) {
            Drawable drawablePlace = ContextCompat.getDrawable(context, R.drawable.ic_ranking_second).mutate();
            drawablePlace.setTint(context.getResources().getColor(R.color.ColorItemYellow));
            holder.txtRank.setVisibility(View.GONE);
            holder.ll_image_ranking.setVisibility(View.VISIBLE);
            holder.ll_image_ranking.setImageDrawable(drawablePlace);
        } else if(position == 1) {
            Drawable drawablePlace = ContextCompat.getDrawable(context, R.drawable.ic_ranking_second).mutate();
            drawablePlace.setTint(context.getResources().getColor(R.color.ColorBlueGray400));
            holder.txtRank.setVisibility(View.GONE);
            holder.ll_image_ranking.setVisibility(View.VISIBLE);
            holder.ll_image_ranking.setImageDrawable(drawablePlace);
        } else if(position == 2) {
            Drawable drawablePlace = ContextCompat.getDrawable(context, R.drawable.ic_ranking_second).mutate();
            drawablePlace.setTint(context.getResources().getColor(R.color.ColorItemRank2));
            holder.txtRank.setVisibility(View.GONE);
            holder.ll_image_ranking.setVisibility(View.VISIBLE);
            holder.ll_image_ranking.setImageDrawable(drawablePlace);
        } else {
            holder.txtRank.setVisibility(View.VISIBLE);
            holder.ll_image_ranking.setVisibility(View.GONE);
        }


        Drawable drawableU = context.getResources().getDrawable(R.drawable.ic_placeholderuser);
        drawableU.setTint(context.getResources().getColor(R.color.ThirdBackground));

        UTILS.getImageAt(context, user.getImg(), holder.userImage, new RequestOptions()
                .circleCrop()
                .placeholder(drawableU)
                .error(drawableU));

        holder.userPontos.setText(String.valueOf(user.getCoins()));
        holder.userNome.setText(user.getName());
        holder.subtitle.setText(user.getUserType());
        holder.txtRank.setText(user.getRank()+"");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface Listener {
        void onLastItem();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        ImageView ll_image_ranking;
        TextView userNome;
        TextView userPontos;
        TextView subtitle;
        TextView txtRank;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.adapter_img_ranking);
            ll_image_ranking = itemView.findViewById(R.id.ll_image_ranking);
            userNome = itemView.findViewById(R.id.adapter_title_ranking);
            userPontos = itemView.findViewById(R.id.adapter_total_ranking);
            subtitle = itemView.findViewById(R.id.adapter_subtitle_ranking);
            txtRank = itemView.findViewById(R.id.txtRank);
        }
    }
}