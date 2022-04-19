package com.frf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.utils.UTILS;

import java.util.List;

public class ImageSelectedAdapter extends RecyclerView.Adapter<ImageSelectedAdapter.ViewHolder> {

    private Context context;
    private List<String> images;
    private GalleryRecyclerViewAdapter.OnCallBackGalleryList listener;

    public ImageSelectedAdapter(Context context, List<String> images, GalleryRecyclerViewAdapter.OnCallBackGalleryList listener) {
        this.context = context;
        this.images = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.galery_item_selected, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String image = images.get(position);

        //  Glide.with(context).load(image).into(holder.imageView);

        UTILS.getImageAt(context, image, holder.imageView);


        holder.imageView.setOnClickListener(view -> {
            images.remove(position);
            listener.addItem();
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View view){
            super(view);
            imageView = view.findViewById(R.id.image);
        }
    }

}
