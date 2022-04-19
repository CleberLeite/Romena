package com.frf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.data.NoticiasData;
import com.frf.app.utils.UTILS;

import java.util.List;

public class NoticiaAdapter extends RecyclerView.Adapter<NoticiaAdapter.ViewHolder> {

    Context context;
    List<NoticiasData> data;
    Listener callback;
    boolean isGreat = true;
    private int page = 1;
    private int margin;

    public NoticiaAdapter(Context context, List<NoticiasData> data, int margin, Listener callback, boolean isGreat) {
        this.context = context;
        this.data = data;
        this.callback = callback;
        this.isGreat = isGreat;
        this.margin = margin;
    }

    public List<NoticiasData> getData() {
        return data;
    }

    public void addData(NoticiasData data) {
        this.data.add(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (position == (data.size() - margin)) {
            page++;
            callback.onBottom(page);
        }

        NoticiasData item = data.get(position);
         UTILS.getImageAt(context, item.getImg(), holder.imgNew);

         if (item.getTitle() != null && !item.getTitle().equals("")) {
             holder.titulo.setText(item.getTitle());
         }else {
             holder.titulo.setVisibility(View.GONE);
         }
        if (item.getDesc() != null && !item.getDesc().equals("")) {
            holder.desc.setText(item.getDesc());
        }else {
            holder.desc.setVisibility(View.GONE);
        }
        holder.txt_cat.setText(item.getCat());
        holder.txtDate.setText(UTILS.PrettyTime(context, item.getDate()));

        holder.cardNews.setOnClickListener(v -> callback.isPressed(item));

        if(isGreat){
            holder.txt_cat.setVisibility(View.GONE);
            holder.txtDate.setVisibility(View.GONE);
            holder.titulo.setVisibility(View.GONE);

            //cardNews
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Math.round(UTILS.pxFromDp(context, 216)), Math.round(UTILS.pxFromDp(context, 96)));
            holder.imgNew.setLayoutParams(params);
            holder.cardNews.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface Listener {
        void isPressed(NoticiasData data);
        void onBottom(int page);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View notification;
        ImageView imgNew;
        TextView titulo;
        TextView txt_cat;
        TextView desc;
        TextView txtDate;
        View cardNews;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notification = itemView.findViewById(R.id.notification);
            txt_cat = itemView.findViewById(R.id.txt_cat);
            imgNew = itemView.findViewById(R.id.img_card1);
            desc = itemView.findViewById(R.id.desc_tipo_0);
            titulo = itemView.findViewById(R.id.titulo_tipo_0);
            txtDate = itemView.findViewById(R.id.time_tipo_0);
            cardNews = itemView.findViewById(R.id.card_news);
        }
    }
}
