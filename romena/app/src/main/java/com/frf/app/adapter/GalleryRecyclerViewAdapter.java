package com.frf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.utils.UTILS;

import java.util.List;

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<String> images;
    protected Listene listene;
    private int p = -1;

    public GalleryRecyclerViewAdapter(Context context, List<String> images, Listene listene) {
        this.context = context;
        this.images = images;
        this.listene = listene;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.galery_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String image = images.get(position);

        //  Glide.with(context).load(image).into(holder.imageView);

        UTILS.getImageAt(context, image, holder.imageView);


        holder.imageView.setOnClickListener(view -> {
            holder.container.setVisibility(View.GONE);
            listene.onPhotoClick(image, () -> {
                holder.container.setVisibility(View.VISIBLE);
            });
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CardView container;
        public ViewHolder(@NonNull View view){
            super(view);
            imageView = view.findViewById(R.id.image);
            container = view.findViewById(R.id.container);
        }
    }

    public interface OnCallBackGalleryList {
        void addItem();
    }

    public interface Listene {
        void onPhotoClick(String path, OnCallBackGalleryList listener);
    }
}
