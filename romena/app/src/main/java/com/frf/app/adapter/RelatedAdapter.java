package com.frf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.data.RelatedData;
import com.frf.app.utils.UTILS;


import java.util.List;

public class RelatedAdapter extends RecyclerView.Adapter<RelatedAdapter.ViewHolder> {

    Context context;
    List<RelatedData> data;
    Listener callback;

    public RelatedAdapter(Context context, List<RelatedData> data, Listener callback) {
        this.context = context;
        this.data = data;
        this.callback = callback;
    }

    public List<RelatedData> getData() {
        return data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.include_related, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (position == data.size() - 11) {
            callback.onBottom();
        }

        RelatedData item = data.get(position);
        holder.title.setText(item.getTitle());
        UTILS.getImageAt(context, item.getImg(), holder.imgNew);
        holder.card_news.setOnClickListener(v -> callback.isPressed(item));

        if (position == 0) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.card_news.getLayoutParams();
            params.width = UTILS.convertDpToPixels(216); params.height = UTILS.convertDpToPixels(170); params.leftMargin = UTILS.convertDpToPixels(20);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface Listener {
        void isPressed(RelatedData data);
        void onBottom();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View card_news;
        ImageView imgNew;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card_news = itemView.findViewById(R.id.card_news);
            imgNew = itemView.findViewById(R.id.img_card1);
            title = itemView.findViewById(R.id.titulo_tipo_0);

        }
    }
}
