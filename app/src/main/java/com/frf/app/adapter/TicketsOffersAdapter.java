package com.frf.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.frf.app.R;
import com.frf.app.data.TicketsOffersData;

public class TicketsOffersAdapter extends RecyclerView.Adapter<TicketsOffersAdapter.ViewHolder> {

    private Context context;
    private TicketsOffersData offersData;
    private Listener listener;

    public TicketsOffersAdapter(Context context, TicketsOffersData offersData, Listener listener) {
        this.context = context;
        this.offersData = offersData;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_sectors_tickets, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TicketsOffersData.Offers offer = offersData.getOffers().get(position);

        try{holder.colorTag.setBackgroundColor(Color.parseColor("#AAAAAA"));}catch (Exception e){e.printStackTrace();}
        try{holder.sector.setText(offer.getCategoryName());}catch (Exception e){e.printStackTrace();}
        try{holder.value.setText(offer.getSeatPriceFormatted());}catch (Exception e){e.printStackTrace();}
        try{holder.numberTickets.setText(offer.getSeatCountAvailable() + " " + context.getResources().getString(R.string.available_tickets));}catch (Exception e){e.printStackTrace();}

        holder.card.setOnClickListener(view -> {
            if (holder.btnSelector.getVisibility() == View.VISIBLE) {
                holder.border.setVisibility(View.GONE);
                holder.btnSelector.setVisibility(View.GONE);
            } else {
                holder.border.setVisibility(View.VISIBLE);
                holder.btnSelector.setVisibility(View.VISIBLE);
            }
            listener.onClick(offer, holder.btnSelector.getVisibility() == View.VISIBLE);
        });

        holder.btnSelector.setOnClickListener(view -> listener.onSelectSector(offer));

    }

    @Override
    public int getItemCount() {
        return offersData.getOffers().size();
    }

    public interface Listener {
        void onClick(TicketsOffersData.Offers data, boolean isChecked);
        void onSelectSector(TicketsOffersData.Offers data);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        ConstraintLayout colorTag;
        TextView sector, value, numberTickets;
        Button btnSelector;
        ImageView border;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            colorTag = itemView.findViewById(R.id.colorTag);
            sector = itemView.findViewById(R.id.sector);
            value = itemView.findViewById(R.id.value);
            numberTickets = itemView.findViewById(R.id.available_ticket_numbers);
            btnSelector = itemView.findViewById(R.id.btn_selector);
            border = itemView.findViewById(R.id.border);
        }
    }
}
