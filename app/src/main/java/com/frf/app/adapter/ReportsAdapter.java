package com.frf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.data.ReportsData;

import java.util.ArrayList;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ReportsData> reports;
    private Listener listener;

    public ReportsAdapter(Context context, ArrayList<ReportsData> reports, Listener listener) {
        this.context = context;
        this.reports = reports;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_reports, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReportsData data = reports.get(position);

        holder.title.setText(data.getTitle());
        holder.item.setOnClickListener(view -> listener.onClick(data));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public interface Listener {
        void onClick(ReportsData data);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout item;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            title = itemView.findViewById(R.id.title);
        }
    }
}
