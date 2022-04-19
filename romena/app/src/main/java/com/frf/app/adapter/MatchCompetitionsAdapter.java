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
import com.frf.app.data.MatchCompetitionsData;
import com.frf.app.utils.UTILS;

import java.util.List;

public class MatchCompetitionsAdapter extends RecyclerView.Adapter<MatchCompetitionsAdapter.ViewHolder> {

    Context context;
    List<MatchCompetitionsData> data;
    Listener callback;
  
    int limit;
    int margin;
    int page = 1;

    public MatchCompetitionsAdapter(Context context, List<MatchCompetitionsData> data, int limit, int margin, Listener listener) {
        this.context = context;
        this.data = data;
        this.callback = listener;
        this.limit = limit;
        this.margin = margin;
    }

    public List<MatchCompetitionsData> getData() {
        return data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_match_competitions, parent, false);
        return new ViewHolder(view);
    }

    public void addItem(MatchCompetitionsData data) {
        this.data.add(data);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (position == data.size() - margin) {
            page++;
            callback.onMargin(page);
        }

        MatchCompetitionsData item = data.get(position);

        holder.cardEvent.setOnClickListener(v -> callback.onPressed(item));
        holder.txtTitle.setText(item.getTitle());

        UTILS.getImageAt(context, item.getImg(), holder.imgCompetitions);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface Listener {
        void onPressed(MatchCompetitionsData data);
        void onMargin(int page);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        ImageView imgCompetitions;
        View cardEvent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title);
            imgCompetitions = itemView.findViewById(R.id.img_competitions);
            cardEvent = itemView.findViewById(R.id.card_event);
        }
    }
}
