package com.frf.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.data.GameTimeLineData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterTimeLineStatus extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<GameTimeLineData> timeline;
    private int isLeft;
    private String gols1, gols2;

    public AdapterTimeLineStatus(Context context, ArrayList<GameTimeLineData> timeline, int id, String gols1, String gols2) {
        this.context = context;
        this.timeline = timeline;
        isLeft = id;
        this.gols1 = gols1;
        this.gols2 = gols2;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.include_timeline_game, null, false));
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        ArrayList<GameTimeLineData> lines = new ArrayList<>();
        ViewHolder mHolder = (ViewHolder) holder;


        for (GameTimeLineData d : timeline) {
            if(position == 1) {
                if (d.getPeriod() == 2) {
                    lines.add(d);
                }
            }else {
                if (d.getPeriod() == 1) {
                    lines.add(d);
                }
            }
        }

        if (lines.size() > 0) {
            GameTimeLineData data1 = lines.get(0);
            mHolder.numStatus.setText(data1.getPeriod() == 1 ? "REPRIZA I" : "REPRIZA II");
            mHolder.placar.setText(data1.getPeriod() == 1 ? gols1 : gols2);

            for (GameTimeLineData data : lines) {
                View view;
                if (isLeft == data.getClubId()) {
                    view = LayoutInflater.from(context).inflate(R.layout.include_status_game_left, mHolder.containerStatus, false);
                } else {
                    view = LayoutInflater.from(context).inflate(R.layout.include_status_game_right, mHolder.containerStatus, false);
                }

                LinearLayout container = view.findViewById(R.id.container);
                TextView time = view.findViewById(R.id.time);
                ImageView icon = view.findViewById(R.id.icon);
                TextView name = view.findViewById(R.id.name);

                int colorSpan = context.getResources().getColor(R.color.NewsTitleText);

                switch (data.getType()) {
                    case 1: {
                        container.setBackgroundColor(context.getResources().getColor(R.color.ColorBlue));
                        icon.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_timeline_goals));
                        name.setTextColor(Color.WHITE);
                        time.setTextColor(Color.WHITE);
                        colorSpan = Color.WHITE;
                    }
                    break;
                    case 2: {
                        icon.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_timeline_redcard));
                    }
                    break;
                    case 3: {
                        icon.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_timeline_yellowcard));
                    }
                    break;
                    case 4: {
                        icon.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_timeline_substituition));
                    }
                    break;
                    case 5: {
                        icon.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_timeline_penalty));
                    }
                    break;
                    case 6: {
                        icon.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_timeline_secondyellow));
                    }
                    break;
                }

                String span;
                if (!data.getObs().equals("")) {
                    span = data.getName() + " ~ " + data.getObs();
                    SpannableStringBuilder spannable = new SpannableStringBuilder(span);
                    spannable.setSpan(
                            new ForegroundColorSpan(colorSpan),
                            span.indexOf("~"), // start
                            span.length(), // end
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    );
                    spannable.setSpan(
                            new RelativeSizeSpan(1f),
                            span.indexOf("~"), // start
                            span.length(), // end
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    );

                    Typeface font2 = ResourcesCompat.getFont(context, R.font.exo2_regular);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        spannable.setSpan(
                                new TypefaceSpan(font2),
                                span.indexOf("~"), // start
                                span.length(), // end
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        );
                    }
                    name.setText(spannable.replace(span.indexOf("~"), span.indexOf("~")+1, ""));
                }else {
                    span = data.getName();
                    name.setText(span);
                }

                time.setText(data.getMinute() + "’");

                mHolder.containerStatus.addView(view);
            }

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mHolder.container.setLayoutParams(lp);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView numStatus, placar;
        LinearLayout containerStatus, container;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            numStatus = itemView.findViewById(R.id.numStatus);
            placar = itemView.findViewById(R.id.placar);
            containerStatus = itemView.findViewById(R.id.container_status);
        }
    }
}
