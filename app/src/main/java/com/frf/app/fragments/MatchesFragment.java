package com.frf.app.fragments;

import static com.frf.app.activitys.MainActivity.emptyAsync;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frf.app.R;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.data.CompetitionData;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.CONSTANTS;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.frf.app.vmodels.fragments.FragmentHolderViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class MatchesFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {
    private ViewHolder mHolder;
    private static final int OP_GET_MATCHES = 0;
    int id;

    private FragmentHolderViewModel mfragmentViewModel;

    public MatchesFragment(Integer id, FragmentHolderViewModel mfragmentViewModel) {
        this.id = id;
        this.mfragmentViewModel = mfragmentViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matches, container, false);
        mHolder = new ViewHolder(view);
        getCompetitionMatches(id);
        return view;
    }

    @Override
    public void bind() {
        super.bind();
        ((StartMenuActivity)activity).changeHeader(3);
    }

    @Override
    public void CallHandler(int opId, JSONObject response, boolean success) {
        Message message = new Message();
        Bundle b = new Bundle();
        b.putInt("opId", opId);
        b.putString("response", response.toString());
        b.putBoolean("success", success);
        message.setData(b);
        handlerOp.sendMessage(message);
    }

    private final Handler handlerOp = new Handler(message -> {
        int opId = -1;
        JSONObject response;
        boolean success = false;

        try {
            opId = message.getData().getInt("opId");
            response = new JSONObject(message.getData().getString("response"));
            success = message.getData().getBoolean("success");
        } catch (JSONException e) {
            UTILS.DebugLog("Error", "Error getting handlers params.");
            return false;
        }

        if (success) {
            OnAsyncOperationSuccess(opId, response);
        } else {
            OnAsyncOperationError(opId, response);
        }
        return false;
    });


    @Override
    public void OnAsyncOperationSuccess(int opId, JSONObject response) {
        switch (opId) {
            case OP_GET_MATCHES: {
                int status = 0;
                if (response.has("Status")) {
                    try {
                        status = response.getInt("Status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (response.has("Object")) {
                    if (status == 200) {
                        try {
                            CompetitionData[] gData = new Gson().fromJson(response.getString("Object"), CompetitionData[].class);
                            boolean isEnd = false;
                            for (int i = 0; i <= gData.length - 1; i++) {
                                if (i >= (gData.length - 1)) {
                                    isEnd = true;
                                }
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    Date date = format.parse(gData[i].getDate());
                                    Date now = new Date();
                                    if(now.after(date)){
                                        mHolder.title2.setVisibility(View.VISIBLE);
                                        initAnterioare(gData[i], isEnd);
                                    } else {
                                        mHolder.title1.setVisibility(View.VISIBLE);
                                        initClasamente(gData[i], isEnd);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            break;
        }
    }

    private void initClasamente(CompetitionData gData, boolean end) {
        View view = LayoutInflater.from(activity).inflate(R.layout.include_card_tipo_1, mHolder.containerClasamenteItens, false);
        TextView titulo = view.findViewById(R.id.titulo_tipo_1);
        TextView label1 = view.findViewById(R.id.label1);
        TextView label2 = view.findViewById(R.id.label2);
        TextView horarioInicio = view.findViewById(R.id.horario_inicio);
        TextView horarioFim = view.findViewById(R.id.horario_fim);
        TextView nomeArena = view.findViewById(R.id.nome_arena);
        ImageView img1 = view.findViewById(R.id.img1);
        ImageView img2 = view.findViewById(R.id.img2);
        Button btnBihete = view.findViewById(R.id.btn_bihete);
        View container = view.findViewById(R.id.container);
        View shadowBuy = view.findViewById(R.id.shadonwBtn);
        UTILS.getImageAt(activity, gData.getClubImg1(), img1);
        UTILS.getImageAt(activity, gData.getClubImg2(), img2);

        titulo.setText(gData.getCategory());
        label1.setText(gData.getClub1());
        label2.setText(gData.getClub2());
        nomeArena.setText(gData.getStadium());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM");
        SimpleDateFormat formatHour = new SimpleDateFormat("HH:mm");
        try {
            Date date = format.parse(gData.getDate());
            horarioInicio.setText(formatDate.format(date));
            horarioFim.setText(formatHour.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!CONSTANTS.link_ticketing.equals("")) {
            shadowBuy.setVisibility(View.VISIBLE);
        }

        btnBihete.setOnClickListener(v -> {
            //callUpcoming(gData.id, true);
            try {
                BottomNavigationView bottomNav = activity.findViewById(R.id.bottom_navigation);
                bottomNav.getMenu().getItem(4).setChecked(true);
                mfragmentViewModel.changeFragment(new ShopFragment(0), "ShopFragment", 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        container.setOnClickListener(v -> callUpcoming(gData.id, shadowBuy.getVisibility() == View.VISIBLE));

        if (end) {
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) container.getLayoutParams();
            layoutParams.setMargins((int) UTILS.dpToPx(activity, 20), 0, (int) UTILS.dpToPx(activity, 20), (int) UTILS.dpToPx(activity, 80));
            container.requestLayout();
        }

        mHolder.containerClasamenteItens.addView(view);
    }

    private void initAnterioare(CompetitionData gData, boolean end) {
            View view = LayoutInflater.from(activity).inflate(R.layout.include_item_matches_anterioare, mHolder.containerAnterioareItens, false);

            ImageView clubImg2 = view.findViewById(R.id.img2);
            ImageView clubImg1 = view.findViewById(R.id.img1);
            TextView club1 = view.findViewById(R.id.club1);
            TextView club2 = view.findViewById(R.id.club2);
            TextView goalsClub = view.findViewById(R.id.goalsClub);
            TextView category = view.findViewById(R.id.category);
            View container = view.findViewById(R.id.card2);

            club1.setText(gData.getClub1());
            club2.setText(gData.getClub2());
            category.setText(gData.getCategory());
            goalsClub.setText(gData.getGoalsClub1() + " - " + gData.getGoalsClub2());
            UTILS.getImageAt(activity, gData.clubImg1, clubImg1);
            UTILS.getImageAt(activity, gData.clubImg2, clubImg2);

            container.setOnClickListener(v -> callUpcoming(gData.id, false));

        if (end) {
            int mH, mV;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                mH = 10;
                mV = 80;
            }else {
                mH = 20;
                mV = 90;
            }
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) container.getLayoutParams();
            layoutParams.setMargins((int) UTILS.dpToPx(activity, mH), 0, (int) UTILS.dpToPx(activity, mH), (int) UTILS.dpToPx(activity, mV));
            container.requestLayout();
        }

            mHolder.containerAnterioareItens.addView(view);
    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {

    }

    void getCompetitionMatches(Integer id) {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("idCompetition", id);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_COMPETITION_MATCHES, OP_GET_MATCHES, this).execute(params);
    }

    void callUpcoming(int id, boolean isNextEvent) {
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_COMPETITIONS_OPEN_COMPATITION_DETAILS, id).execute();
        mfragmentViewModel.changeFragment(new EventsUpcomingMatchFragment(id, isNextEvent, false, mfragmentViewModel), "EventsMatchesFragment", isNextEvent ? 5 : 3);
    }

    static class ViewHolder {
        View card2;
        LinearLayout containerAnterioareItens;
        LinearLayout containerClasamenteItens;
        TextView title1;
        TextView title2;

        public ViewHolder(View view) {
            containerAnterioareItens = view.findViewById(R.id.containerAnterioareItens);
            containerClasamenteItens = view.findViewById(R.id.containerClasamenteItens);
            card2 = view.findViewById(R.id.card2);
            title1 = view.findViewById(R.id.title_1);
            title2 = view.findViewById(R.id.title_2);
        }
    }
}