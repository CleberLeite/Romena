package com.frf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.frf.app.R;
import com.frf.app.data.BadgeData;
import com.frf.app.utils.UTILS;
import java.util.ArrayList;

public class AdapterWinBadge extends RecyclerView.Adapter<AdapterWinBadge.ViewHolder>{

    private final ArrayList<BadgeData> data;
    private final Context context;
    private Animation animPop, fadeIn;
    private Listener listener;

    public AdapterWinBadge(Context context, ArrayList<BadgeData> data, Listener listener) {
        this.data = data;
        this.context = context;
        animPop = AnimationUtils.loadAnimation(context, R.anim.pop);
        fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_win_badges, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        BadgeData badge = data.get(position);

        UTILS.getImageAt(context, badge.getImg(), holder.badgeImage);

        holder.badgeImage.startAnimation(animPop);
        holder.badgeTitle.startAnimation(fadeIn);
        holder.badgeDescription.startAnimation(fadeIn);

        if (data.size() == 1) {
            holder.close.setVisibility(View.VISIBLE);
            holder.close.setOnClickListener(view12 -> listener.close());
        }

        try {
            holder.badgeTitle.setText(badge.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            holder.badgeDescription.setText(badge.getDescricao());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            holder.progress.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            holder.prize.setText(badge.getPrize() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface Listener {
        void close();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView badgeImage;
        ImageView close;
        TextView badgeTitle;
        TextView badgeDescription;
        TextView progress;
        TextView prize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            badgeImage = itemView.findViewById(R.id.imageBadge);
            close = itemView.findViewById(R.id.close);
            badgeTitle = itemView.findViewById(R.id.nome);
            badgeDescription = itemView.findViewById(R.id.descricao);
            progress = itemView.findViewById(R.id.progress);
            prize = itemView.findViewById(R.id.prize);
        }
    }
}
