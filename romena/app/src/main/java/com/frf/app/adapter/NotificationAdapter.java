package com.frf.app.adapter;

import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_FEEDCOMMENT;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_FEEDLIKE;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_MATCHS;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_NOTICIA;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_QUIZ;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_SHOP;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_STORE;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_VIDEO;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.data.NotificationData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    List<NotificationData> data;
    Listener callback;
    boolean trigger = false;
    int margin;

    public NotificationAdapter(Context context, List<NotificationData> data, int margin, Listener callback) {
        this.context = context;
        this.data = data;
        this.callback = callback;
        this.margin = margin;
    }

    public List<NotificationData> getData() {
        return data;
    }

    public void setData(List<NotificationData> data) {
        this.data.removeAll(data);
        this.data = data;
        notifyDataSetChanged();
    }

    public void addData (List<NotificationData> data){
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_notification,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        NotificationData notification = data.get(position);

        if(position == (data.size() -1) - margin){
            callback.onBottom();
        }

        try {
            String dateString = "";

            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.ROOT);
            DateFormat inputFormatComparation = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);

            Date date = inputFormat.parse(notification.getDateHour());

            Date cDate1 = inputFormatComparation.parse(notification.getDateHour());
            Date cDate2 = inputFormatComparation.parse(inputFormat.format(Calendar.getInstance().getTime()));

            if (cDate1 != null) {
                if (cDate1.equals(cDate2)) {
                    DateFormat outputFormat = new SimpleDateFormat("hh:mm", Locale.ROOT);
                    if (date != null) {
                        dateString = outputFormat.format(date);
                    }
                }else {
                    DateFormat outputFormat = new SimpleDateFormat("dd.MM", Locale.ROOT);
                    if (date != null) {
                        dateString = outputFormat.format(date);
                    }
                }
            }

            holder.time.setText(dateString);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        holder.tittle.setText(notification.getTitle());
        holder.msg.setText(notification.getMsg());

        holder.notification.setBackgroundColor(context.getResources().getColor(notification.isVisualized() ? R.color.NotificationBackground : R.color.PrimaryBackground));
        holder.divider.setBackgroundColor(context.getResources().getColor(notification.isVisualized() ? R.color.PrimaryBackground : R.color.NotificationBackground));

        Drawable drawable = null;

        switch (notification.getType()){
            case PUSH_ID_NOTICIA : drawable = ContextCompat.getDrawable(context, R.drawable.ic_news).mutate(); break;
            case PUSH_ID_QUIZ : drawable = ContextCompat.getDrawable(context, R.drawable.ic_quizes).mutate(); break;
            case PUSH_ID_STORE : drawable = ContextCompat.getDrawable(context, R.drawable.ic_coin_blue).mutate(); break;
            case PUSH_ID_MATCHS : drawable = ContextCompat.getDrawable(context, R.drawable.ic_matcher_and_events).mutate(); break;
            case PUSH_ID_SHOP : drawable = ContextCompat.getDrawable(context, R.drawable.ic_purchases).mutate(); break;
            case PUSH_ID_FEEDCOMMENT : drawable = ContextCompat.getDrawable(context, R.drawable.ic_comentary).mutate(); break;
            case PUSH_ID_FEEDLIKE : drawable = ContextCompat.getDrawable(context, R.drawable.ic_unlike).mutate(); break;
            case PUSH_ID_VIDEO : drawable = ContextCompat.getDrawable(context, R.drawable.ic_videos).mutate(); break;
            default: drawable = ContextCompat.getDrawable(context, R.drawable.ic_notifications).mutate(); break;
        }

        if (!notification.isVisualized()) {
            drawable.setTint(context.getResources().getColor(R.color.MaintenanceBackground));
            holder.icon.setImageDrawable(drawable);
            trigger = true;
        }else {
            drawable.setTint(context.getResources().getColor(R.color.BackgroundTintText));
            holder.icon.setImageDrawable(drawable);
        }

        holder.notification.setOnClickListener(v -> callback.isPressed(notification));

        if (position == data.size() -1) {
            callback.isCheck(trigger);
            trigger = false;
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface Listener {
        void isPressed(NotificationData data);
        void onBottom ();
        void isCheck(boolean isCheck);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View notification;
        ImageView icon;
        TextView time;
        TextView tittle;
        TextView msg;
        View divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notification = itemView.findViewById(R.id.notification);
            icon = itemView.findViewById(R.id.img_notification_icon);
            time = itemView.findViewById(R.id.txt_notification_time);
            tittle = itemView.findViewById(R.id.txt_notification_title);
            msg = itemView.findViewById(R.id.msg);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}
