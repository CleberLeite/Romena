package com.frf.app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.data.GroupData;
import com.frf.app.utils.UTILS;

import java.util.List;

public class TabIeItensAdapter extends RecyclerView.Adapter<TabIeItensAdapter.ViewHolder> {

    Context context;
    List<GroupData> data;
    Listener listener;

    public TabIeItensAdapter(Context context, List<GroupData> data, Listener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_table, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupData item = data.get(position);

        holder.columnName.setText(item.columns.name);
        holder.columnMatches.setText(item.columns.matches);
        holder.columnPoints.setText(item.columns.points);
        holder.columnWins.setText(item.columns.wins);
        holder.columnDefeats.setText(item.columns.defeats);
        holder.columnDraws.setText(item.columns.draws);

        View viewTitle = LayoutInflater.from(context).inflate(R.layout.include_item_table, holder.contentItens, false);

        ImageView imgValuesImgC = viewTitle.findViewById(R.id.img_values_img);
        TextView imgValuesNameC = viewTitle.findViewById(R.id.img_values_name);
        TextView txtValuesMatchesC = viewTitle.findViewById(R.id.txt_values_matches);
        TextView txtValuesWinsC = viewTitle.findViewById(R.id.txt_values_wins);
        TextView txtValuesPointsC = viewTitle.findViewById(R.id.txt_values_points);
        TextView txtValuesDefeatsC = viewTitle.findViewById(R.id.txt_values_defeats);
        TextView txtValuesDrawsC = viewTitle.findViewById(R.id.txt_values_draws);

        imgValuesImgC.setVisibility(View.GONE);
        imgValuesNameC.setText(item.columns.getName());
        txtValuesDefeatsC.setText(item.columns.getDraws()+"");
        txtValuesDrawsC.setText(item.columns.getDefeats() + "");
        txtValuesWinsC.setText(item.columns.getWins() + "");
        txtValuesPointsC.setText(item.columns.getPoints() + "");
        txtValuesMatchesC.setText(item.columns.getMatches() + "");

        imgValuesNameC.setTextColor(context.getResources().getColor(R.color.PrimaryColorText));
        imgValuesNameC.setTypeface(null, Typeface.BOLD);
//        imgValuesNameC.setFont
        txtValuesDrawsC.setTextColor(context.getResources().getColor(R.color.ColorBlue));
        txtValuesDefeatsC.setTextColor(context.getResources().getColor(R.color.ColorBlue));
        txtValuesWinsC.setTextColor(context.getResources().getColor(R.color.ColorBlue));
        txtValuesPointsC.setTextColor(context.getResources().getColor(R.color.ColorBlue));
        txtValuesMatchesC.setTextColor(context.getResources().getColor(R.color.ColorBlue));

        holder.contentItens.addView(viewTitle);

        for(int i = 0; i <= item.values.size() -1; i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.include_item_table, holder.contentItens, false);
            ImageView imgValuesImg = view.findViewById(R.id.img_values_img);
            TextView imgValuesName = view.findViewById(R.id.img_values_name);
            TextView txtValuesMatches = view.findViewById(R.id.txt_values_matches);
            TextView txtValuesWins = view.findViewById(R.id.txt_values_wins);
            TextView txtValuesPoints = view.findViewById(R.id.txt_values_points);
            TextView txtValuesDefeats = view.findViewById(R.id.txt_values_defeats);
            TextView txtValuesDraws = view.findViewById(R.id.txt_values_draws);
            UTILS.getImageAt(context, item.values.get(i).img, imgValuesImg);
            imgValuesName.setText(item.values.get(i).getName());
            txtValuesDefeats.setText(item.values.get(i).getDraws()+"");
            txtValuesDraws.setText(item.values.get(i).getDefeats() + "");
            txtValuesWins.setText(item.values.get(i).getWins() + "");
            txtValuesPoints.setText(item.values.get(i).getPoints() + "");
            txtValuesMatches.setText(item.values.get(i).getMatches() + "");
            holder.contentItens.addView(view);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public interface Listener {
        void onProductClick(Integer id);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView columnName;
        TextView columnMatches;
        TextView columnPoints;
        TextView columnWins;
        TextView columnDefeats;
        TextView columnDraws;

        TableLayout contentItens;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            columnName = itemView.findViewById(R.id.column_name);
            columnMatches = itemView.findViewById(R.id.column_matches);
            columnPoints = itemView.findViewById(R.id.column_points);
            columnWins = itemView.findViewById(R.id.column_wins);
            columnDefeats = itemView.findViewById(R.id.column_defeats);
            columnDraws = itemView.findViewById(R.id.column_draws);
            contentItens = itemView.findViewById(R.id.containerHomeItens);
        }
    }
}