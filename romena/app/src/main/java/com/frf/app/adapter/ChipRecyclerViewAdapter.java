package com.frf.app.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.data.CategoryData;

import java.util.List;

public class ChipRecyclerViewAdapter extends RecyclerView.Adapter<ChipRecyclerViewAdapter.ViewHolder> {

    private List<CategoryData> mImages;
    private LayoutInflater mInflater;
    private Listener listener;
    private Activity context;
    int selected = 0;

    public ChipRecyclerViewAdapter(Activity context, int selected, List<CategoryData> data, Listener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.mImages = data;
        this.context = context;
        this.listener = listener;
        this.selected = selected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_news, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CategoryData data = mImages.get(position);
        holder.ItemNews.setText(data.getTitle());

        if (position == selected) {
            holder.spacerStart.setVisibility(View.VISIBLE);
            holder.ItemNews.setBackground(context.getDrawable(R.drawable.item_radio_flat_blue));
            holder.ItemNews.setTextColor(Color.parseColor("#1E293B"));
        } else {
            holder.ItemNews.setBackground(context.getDrawable(R.drawable.item_radio_flat_border_blue));
            holder.ItemNews.setTextColor(Color.parseColor("#FFFFFF"));
            holder.spacerStart.setVisibility(View.GONE);
        }

 /*       if (position == mImages.size()-1) {
            holder.ItemNews.setBackground(context.getDrawable(R.drawable.item_radio_flat_border_blue_black));
            holder.ItemNews.setTextColor(Color.parseColor("#FFFFFF"));
            holder.spacerEnd.setVisibility(View.VISIBLE);
        }*/

        holder.ItemNews.setOnClickListener(view -> {
              listener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public interface Listener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button ItemNews;
        Space spacerStart;
        Space spacerEnd;
        ViewHolder(@NonNull View view) {
            super(view);
            ItemNews = itemView.findViewById(R.id.btn_item_news);
            spacerStart = itemView.findViewById(R.id.spacer_start);
            spacerEnd = itemView.findViewById(R.id.spacer_end);
        }
    }
}