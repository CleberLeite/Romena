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
import com.frf.app.data.CompetitionsData;
import com.frf.app.utils.UTILS;
import java.util.ArrayList;
import java.util.Locale;

public class CompetitionsAdapter extends RecyclerView.Adapter<CompetitionsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CompetitionsData> competitions;
    private Listener listener;

    public CompetitionsAdapter(Context context, ArrayList<CompetitionsData> competitions, Listener listener) {
        this.context = context;
        this.competitions = competitions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_match_competitions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CompetitionsData data = competitions.get(position);

        UTILS.getImageAt(context, data.getImageUrl(), holder.imgCompetitions);

        if (Locale.getDefault().getLanguage().equals("en")) {
            holder.txtTitle.setText(data.getTitleEn());
        }else {
            holder.txtTitle.setText(data.getTitle());
        }

        holder.cardEvent.setOnClickListener(view -> listener.onClick(data.getId(), holder.txtTitle.getText().toString()));
    }

    @Override
    public int getItemCount() {
        return competitions.size();
    }

    public interface Listener {
        void onClick(int id, String title);
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
