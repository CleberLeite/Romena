package com.frf.app.fragments;

import static com.frf.app.activitys.MainActivity.emptyAsync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.adapter.AdapterTimeLineStatus;
import com.frf.app.adapter.TabIeItensAdapter;
import com.frf.app.data.CompetitionMatchData;
import com.frf.app.data.GameTimeLineData;
import com.frf.app.data.GroupData;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.frf.app.vmodels.fragments.FragmentHolderViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;

public class EventsUpcomingMatchFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {

    private ViewHolder mHolder;

    private int id;
    private boolean isNextEvent, isPush;

    private static final int OP_ID_MATCH_DETAILS = 0;

    private FragmentHolderViewModel mfragmentViewModel;

    public EventsUpcomingMatchFragment(int id, boolean isNextEvent, boolean isPush, FragmentHolderViewModel mfragmentViewModel) {
        this.id = id;
        this.isNextEvent = isNextEvent;
        this.isPush = isPush;
        this.mfragmentViewModel = mfragmentViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_upcoming_match, container, false);

        mHolder = new ViewHolder(view);
        mHolder.back.setOnClickListener(view2 -> ((StartMenuActivity)activity).onBackPressed());

        getCompetitionMatch();

        return view;
    }

    @Override
    public void bind() {
        super.bind();
        ((StartMenuActivity)getActivity()).changeHeader(5);
    }

    private void setNotificationMath() {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SET_NOTIFICATION_MATCH, -1, this).execute(params);
    }

    private void getCompetitionMatch() {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_COMPETITION_MATCH, OP_ID_MATCH_DETAILS, this).execute(params);
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
    public void CallHandler(int opId, JSONObject response, boolean success) {
        Message message = new Message();
        Bundle b = new Bundle();
        b.putInt("opId", opId);
        b.putString("response", response.toString());
        b.putBoolean("success", success);
        message.setData(b);
        handlerOp.sendMessage(message);
    }

    @Override
    public void OnAsyncOperationSuccess(int opId, JSONObject response) {
        if (opId == OP_ID_MATCH_DETAILS) {
            if (response.has("Object")) {
                try {
                    CompetitionMatchData gData = new Gson().fromJson(response.getString("Object"), CompetitionMatchData.class);
                    if (response.getJSONObject("Object").has("group")) {
                        GroupData data = new Gson().fromJson(response.getJSONObject("Object").getString("group"), GroupData.class);
                        gData.setGroupData(data);
                    }
                    if (response.has("timeline")) {
                        GameTimeLineData[] data = new Gson().fromJson(response.getJSONObject("Object").getString("timeline"), GameTimeLineData[].class);
                        gData.setTimeline(data);
                    }
                    initCompetition(gData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                if (isPush) {
                    Toast.makeText(activity, getResources().getString(R.string.nu_se_pot_incarca_detaliile_meciului), Toast.LENGTH_LONG).show();
                    ((StartMenuActivity)activity).onBackPressed();
                }else {
                    Toast.makeText(activity, getResources().getString(R.string.nu_se_pot_incarca_detaliile_meciului), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void initCompetition(CompetitionMatchData data) {
        mHolder.title.setText(data.getChampionship());
        UTILS.getImageAt(activity, data.getClubImg1(), mHolder.imgTime1);
        UTILS.getImageAt(activity, data.getClubImg2(), mHolder.imgTime2);
        mHolder.txtTime1.setText(data.getClub1());
        mHolder.txtTime2.setText(data.getClub2());

        if (isNextEvent) {
            if (data.getNotify() > 0) {
                mHolder.notfy.setChecked(true);
            }else {
                mHolder.notfy.setChecked(false);
            }
            mHolder.llNotification.setVisibility(View.VISIBLE);
            mHolder.btnCat.setVisibility(View.VISIBLE);
            mHolder.btnBuy.setVisibility(View.VISIBLE);
            mHolder.placar.setVisibility(View.GONE);

            mHolder.btnBuy.setOnClickListener(view -> {
                try {
                    BottomNavigationView bottomNav = activity.findViewById(R.id.bottom_navigation);
                    bottomNav.getMenu().getItem(4).setChecked(true);
                    mfragmentViewModel.changeFragment(new ShopFragment(0), "ShopFragment", 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            mHolder.notfy.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_MATCH_CHECK_NOTIFICATION, id).execute();
                }else {
                    new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_MATCH_UNCHECK_NOTIFICATION, id).execute();
                }
                setNotificationMath();
            });

        }else {
            mHolder.llNotification.setVisibility(View.INVISIBLE);
            mHolder.btnCat.setVisibility(View.GONE);
            mHolder.btnBuy.setVisibility(View.GONE);
            mHolder.placar.setVisibility(View.VISIBLE);

            mHolder.placar.setText(data.getGoalsClub1() + " - " + data.getGoalsClub2());
        }

        Date date1 = null;
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date1 = sdf.parse(data.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mHolder.data.setText("" + android.text.format.DateFormat.format("dd.MM", date1));
        mHolder.hora.setText("" + android.text.format.DateFormat.format("hh:mm", date1));

        mHolder.arena.setText(data.getStadium());

        if (data.getTimeline() != null) {
            initGameStatus(data);
        }

        if (data.getGroupData() != null) {
            initTab(data);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initGameStatus(CompetitionMatchData data) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_class_list, mHolder.llList, false);
        TextView titulo = view.findViewById(R.id.title);
        RecyclerView list = view.findViewById(R.id.itens);
        titulo.setText("Rezumat");

        int T1_P1 = 0;
        int T2_P1 = 0;
        int T1_P2 = 0;
        int T2_P2 = 0;
        String txtT1, txtT2;

        for (GameTimeLineData d : data.getTimeline()) {
            if (d.getClubId() == data.getIdClub1()) {
                if (d.getType() == 1) {
                    if (d.getPeriod() == 1) {
                        T1_P1++;
                    }
                }
            }else {
                if (d.getType() == 1) {
                    if (d.getPeriod() == 1) {
                        T2_P1++;
                    }
                }
            }
        }

        T1_P2 = T1_P2 + T1_P1;
        T2_P2 = T2_P2 + T2_P1;

        for (GameTimeLineData d : data.getTimeline()) {
            if (d.getClubId() == data.getIdClub1()) {
                if (d.getType() == 1) {
                    if (d.getPeriod() == 2) {
                        T1_P2++;
                    }
                }
            }else {
                if (d.getType() == 1) {
                    if (d.getPeriod() == 2) {
                        T2_P2++;
                    }
                }
            }
        }

        txtT1 = T1_P1 + " - " + T2_P1;
        txtT2 = T1_P2 + " - " + T2_P2;

        AdapterTimeLineStatus adapterTimeLineStatus = new AdapterTimeLineStatus(activity, new ArrayList<>(Arrays.asList(data.getTimeline())), data.getIdClub1(), txtT1, txtT2);

        list.setLayoutManager(new LinearLayoutManager(activity));
        list.setAdapter(adapterTimeLineStatus);

        mHolder.llList.addView(view);
    }

    @SuppressLint("SetTextI18n")
    private void initTab(CompetitionMatchData data) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_class_list, mHolder.llList, false);
        TextView titulo = view.findViewById(R.id.title);
        RecyclerView list = view.findViewById(R.id.itens);
        titulo.setText("Clasamente");
        TabIeItensAdapter adapter = new TabIeItensAdapter(activity, Collections.singletonList(data.getGroupData()), id -> {

        });

        list.setLayoutManager(new LinearLayoutManager(activity));
        list.setAdapter(adapter);

        mHolder.llList.addView(view);
    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {
        if (opId == OP_ID_MATCH_DETAILS) {
            if (isPush) {
                Toast.makeText(activity, getResources().getString(R.string.nu_se_pot_incarca_detaliile_meciului), Toast.LENGTH_LONG).show();
                ((StartMenuActivity)activity).onBackPressed();
            }else {
                Toast.makeText(activity, getResources().getString(R.string.nu_se_pot_incarca_detaliile_meciului), Toast.LENGTH_SHORT).show();
            }
        }
    }

    static class ViewHolder {
        ImageButton back;
        ImageView imgTime1;
        ImageView imgTime2;
        TextView title;
        TextView txtTime1;
        TextView txtTime2;
        TextView data;
        TextView hora;
        TextView arena;
        TextView placar;
        ToggleButton notfy;
        Button btnBuy;
        LinearLayout llNotification, llList;
        Button btnCat;

        public ViewHolder(View view) {
            back = view.findViewById(R.id.img_back);
            btnBuy = view.findViewById(R.id.btn_buy);
            title = view.findViewById(R.id.title);
            imgTime1 = view.findViewById(R.id.imgtime1);
            imgTime2 = view.findViewById(R.id.imgTime2);
            txtTime1 = view.findViewById(R.id.txtTime1);
            txtTime2 = view.findViewById(R.id.txtTime2);
            notfy = view.findViewById(R.id.toggleButton1);
            placar = view.findViewById(R.id.placar);
            data = view.findViewById(R.id.data);
            hora = view.findViewById(R.id.hora);
            arena = view.findViewById(R.id.arena);
            llNotification = view.findViewById(R.id.ll_notification);
            llList = view.findViewById(R.id.ll_list);
            btnCat = view.findViewById(R.id.btn_cat);
        }
    }
}