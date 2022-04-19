package com.frf.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.frf.app.R;
import com.frf.app.data.TicketsMatchesData;
import com.frf.app.utils.UTILS;
import com.gigamole.library.ShadowLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TicketsMatchesAdapter extends RecyclerView.Adapter<TicketsMatchesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TicketsMatchesData> list;
    private Listener listener;

    public TicketsMatchesAdapter(Context context, ArrayList<TicketsMatchesData> list, Listener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.include_card_tipo_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TicketsMatchesData data = list.get(position);

        try{UTILS.getImageAt(context, ((ArrayList<TicketsMatchesData.Clubs>)data.getClubs()).get(0).getBannerUrl(), holder.imgClub1);}catch (Exception e){e.printStackTrace();}
        try{UTILS.getImageAt(context, ((ArrayList<TicketsMatchesData.Clubs>)data.getClubs()).get(1).getBannerUrl(), holder.imgClub2);}catch (Exception e){e.printStackTrace();}

        holder.category.setText(data.getName());
        holder.startTimer.setText(convertDate(data.getEventDateTime()));
        holder.endTimer.setText(convertHour(data.getEventDateTime()));
        holder.nameArena.setText(data.getVenueDetails());
        holder.txtClub1.setText(((ArrayList<TicketsMatchesData.Clubs>)data.getClubs()).get(0).getName());
        holder.txtClub2.setText(((ArrayList<TicketsMatchesData.Clubs>)data.getClubs()).get(1).getName());

        holder.shadowLayout.setVisibility(View.VISIBLE);

        holder.btnTicket.setOnClickListener(view -> listener.onClick(
                data.getEventID(),
                holder.txtClub1.getText().toString() + " - " + holder.txtClub2.getText().toString(),
                convertCompleteDate(data.getEventDateTime()),
                data.getName()
        ));

        holder.card.setOnClickListener(view -> listener.onClick(
                data.getEventID(),
                holder.txtClub1.getText().toString() + " - " + holder.txtClub2.getText().toString(),
                convertCompleteDate(data.getEventDateTime()),
                data.getName()
        ));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String convertCompleteDate(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date newDate = format.parse(date);

            format = new SimpleDateFormat("ddd, dd MMM, Ora HH:mm");

            return format.format(newDate);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String convertDate(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date newDate = format.parse(date);

            format = new SimpleDateFormat("dd.MM");

            return format.format(newDate);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String convertHour(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date newDate = format.parse(date);

            format = new SimpleDateFormat("HH:mm");

            return format.format(newDate);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public interface Listener {
        void onClick(int eventId, String title, String date, String locale);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View card;
        TextView category, txtClub1, txtClub2, startTimer, endTimer, nameArena;
        ImageView imgClub1, imgClub2;
        ShadowLayout shadowLayout;
        Button btnTicket;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.container);
            category = itemView.findViewById(R.id.titulo_tipo_1);
            imgClub1 = itemView.findViewById(R.id.img1);
            imgClub2 = itemView.findViewById(R.id.img2);
            txtClub1 = itemView.findViewById(R.id.label1);
            txtClub2 = itemView.findViewById(R.id.label2);
            startTimer = itemView.findViewById(R.id.horario_inicio);
            endTimer = itemView.findViewById(R.id.horario_fim);
            nameArena = itemView.findViewById(R.id.nome_arena);
            shadowLayout = itemView.findViewById(R.id.shadonwBtn);
            btnTicket = itemView.findViewById(R.id.btn_bihete);
        }
    }
}
