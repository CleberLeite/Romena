package com.frf.app.adapter;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.data.BadgeData;
import com.frf.app.utils.UTILS;


import java.util.List;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.ViewHolder> {

    private Context context;
    private List<BadgeData> data;
    protected Listener listener;

    public RewardsAdapter(Context context, List<BadgeData> images, Listener listener) {
        this.context = context;
        this.data = images;
        this.listener = listener;
    }

    public void addBadges (List<BadgeData> data){
        boolean trigger = true;
        for(BadgeData d : data) {

            for (int i=0; i < this.data.size(); i++) {
                if (this.data.get(i).getId() == d.getId()) {
                    trigger = false;
                    break;
                }
            }

            if (trigger) {
                this.data.add(d);
            }else {
                trigger = true;
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.include_rewards_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       // int image = images.get(position);
        if(position >= data.size() -1){
            listener.OnEndOfList();
        }
        final BadgeData badge = data.get(position);

        UTILS.getImageAt(context, badge.getImg(), holder.imageView);


        ColorMatrix satured = new ColorMatrix();
        satured.setSaturation(0f);

        ColorMatrix color = new ColorMatrix();
        color.setSaturation(1f);

        if(badge.getOwned() == 1){
            holder.imageView.setColorFilter(new ColorMatrixColorFilter(color));
        }else{
            holder.imageView.setColorFilter(new ColorMatrixColorFilter(satured));
        }


        holder.imageView.setOnClickListener(view -> listener.OnClick(badge));
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View view){
            super(view);
            imageView = view.findViewById(R.id.image);
        }
    }

    public interface Listener {
        void OnClick (BadgeData data);
        void OnEndOfList ();
    }
}
