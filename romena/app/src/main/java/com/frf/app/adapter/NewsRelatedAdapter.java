package com.frf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.data.RelatedData;

import java.util.List;

public class NewsRelatedAdapter  extends RecyclerView.Adapter<NewsRelatedAdapter.ViewHolder> {

    Context context;
    List<RelatedData> data;
    Listener callback;
    public NewsRelatedAdapter(Context context, List<RelatedData> data, Listener callback) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface Listener {
        void isPressed(RelatedData data);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
