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
import com.frf.app.data.VideosData;
import com.frf.app.utils.UTILS;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    Context context;
    List<VideosData> data;
    Listener callback;
    int margin;
    private int page = 1;

    public VideosAdapter(Context context, List<VideosData> data, int margin, Listener callback) {
        this.context = context;
        this.data = data;
        this.callback = callback;
        this.margin = margin;
    }

    public void addData(VideosData data) {
        this.data.add(data);
        notifyDataSetChanged();
    }

    public List<VideosData> getData() {
        return data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_videos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (position == (data.size() - margin)) {
            page++;
            callback.onBottom(page);
        }

        VideosData item = data.get(position);
        // UTILS.getImageAt(context, item.getImg(), holder.imgNew);
        UTILS.getImageAt(context, item.getImg(), holder.imgNew);
        holder.title.setText(item.getCat());
        holder.desc.setText(item.getTitle());
        holder.txtDuration.setText(item.getDuration());

        holder.txtDate.setText(UTILS.PrettyTime(context, item.getDate()));


        holder.cardVideos.setOnClickListener(v -> {
            callback.isPressed(item);
        });
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface Listener {
        void isPressed(VideosData data);
        void onBottom(int page);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View notification;
        ImageView imgNew;
        TextView title;
        TextView desc;
        TextView txtDuration;
        TextView txtDate;
        View cardVideos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notification = itemView.findViewById(R.id.notification);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            imgNew = itemView.findViewById(R.id.img_videos);
            desc = itemView.findViewById(R.id.txt_desc_videos);
            title = itemView.findViewById(R.id.txt_title_videos);
            txtDate = itemView.findViewById(R.id.txtDate);
            cardVideos = itemView.findViewById(R.id.card_videos);
        }
    }
}
