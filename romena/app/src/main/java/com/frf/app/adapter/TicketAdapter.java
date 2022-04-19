package com.frf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.data.TicketData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TicketData> list;
    private Listener listener;

    public TicketAdapter(Context context, ArrayList<TicketData> list, Listener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.include_card_match, parent, false));
        }else {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.include_card_match_2, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        TicketData data = list.get(position);

        holder.seat.setText("");
        holder.match.setText("");
        holder.infos.setText("");
        holder.value.setText("");

        if (data.getTicketType() == 1) {
            holder.card.setOnClickListener(v -> listener.callSeat(data.getId()));
        }else {
            holder.card.setOnClickListener(v -> listener.callGeneral(data.getId()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getTicketType();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface Listener {
        void callSeat(int id);
        void callGeneral(int id);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView seat, match, infos, value;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            seat = itemView.findViewById(R.id.seat);
            match = itemView.findViewById(R.id.match);
            infos  = itemView.findViewById(R.id.infos);
            value = itemView.findViewById(R.id.value);
        }
    }
}
