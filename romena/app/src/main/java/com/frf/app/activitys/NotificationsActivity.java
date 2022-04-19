package com.frf.app.activitys;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.adapter.NotificationAdapter;
import com.frf.app.data.NotificationData;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.CONSTANTS;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class NotificationsActivity extends MainActivity implements AsyncOperation.IAsyncOpCallback {

    private ViewHolder mHolder;
    int page = 1;
    boolean hasData = false;
    boolean seeAll = false;
    boolean isCheck = false;
    NotificationAdapter adapter;

    private static final int OP_ID_NOTIFICATION = 0;
    private static final int OP_ID_SET_ALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notifications);
        UTILS.setColorStatusBar(this, R.color.NotificationBackground);

        mHolder = new ViewHolder();
        initAction();
        getNotification();
    }

    void initAction(){

        mHolder.viewAll.setOnClickListener(view -> {
            if (isCheck) {
                isCheck = false;
                setMarkAll();
            }
        });

        if(hasData) mHolder.viewAll.setVisibility(View.GONE);
        mHolder.imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        });
    }

    @SuppressLint("SetTextI18n")
    void initNotification(List<NotificationData> data, int margin) {

        if (data != null) {

            Comparator<NotificationData> comparadorPorData = (o1, o2) -> {
                Date d1;
                Date d2;
                try {
                    d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(o1.getDateHour());
                    d2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(o2.getDateHour());
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 1;
                }
                return d2.compareTo(d1);
            };

            try{Collections.sort(data, comparadorPorData);}catch (Exception e){e.printStackTrace();}
            try{Collections.sort(data, (o1, o2) -> Integer.compare(o1.getVisualized(), o2.getVisualized()));}catch (Exception e){e.printStackTrace();}

            int notiCount = 0;
            for(int i =0; i < data.size(); i++) {
                if (!data.get(i).isVisualized()) {
                    notiCount++;
                }
            }

            if (notiCount > 0) {
                mHolder.txtNotificary.setText("(" + notiCount + ")");
            }

            if (adapter == null) {
                adapter = new NotificationAdapter(activity, data, margin, new NotificationAdapter.Listener() {
                    @Override
                    public void isPressed(NotificationData data) {
                        SetNotification(data);
                    }

                    @Override
                    public void onBottom() {
                        GetByPagination();
                    }

                    @Override
                    public void isCheck(boolean isCheck) {
                        if (isCheck) {
                            NotificationsActivity.this.isCheck = true;
                            mHolder.imgCheck.getDrawable().setTint(activity.getResources().getColor(R.color.ColorItemYellow));
                            mHolder.txtCheck.setTextColor(activity.getResources().getColor(R.color.ColorItemYellow));
                            mHolder.txtNotificary.setAlpha(1);
                        }
                    }
                });

                mHolder.recyclerView.setAdapter(adapter);
                mHolder.recyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));

            } else {
                if (seeAll) {
                    adapter.setData(data);
                    seeAll = false;
                } else {
                    adapter.addData(data);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    void GetByPagination() {
        if (hasData) {
            page++;
            getNotification();
            hasData = false;
        }
    }

    void SetNotification(NotificationData data){
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NOTIFICATIONS_CENTER_SELECT_ITEM, data.getIdPush()).execute();
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("idPush", data.getIdPush());
        redirectPush(data);
    }

    void setMarkAll() {
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_NOTIFICATION_MARK_ALL, OP_ID_SET_ALL, this, TRACKING.TRACKING_NOTIFICATIONS_CENTER_SELECT_ALL_ITENS).execute();

        int colorFrom = getResources().getColor(R.color.ColorItemYellow);
        int colorTo = getResources().getColor(R.color.PrimaryText);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.addUpdateListener(animator -> {
            mHolder.txtCheck.setTextColor((int) animator.getAnimatedValue());
            mHolder.imgCheck.getDrawable().setTint((int) animator.getAnimatedValue());
        });
        colorAnimation.start();

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);

        mHolder.txtNotificary.setAnimation(fadeOut);
    }

    void redirectPush(NotificationData data) {
        int idPush = data.getIdPush();
        int pushType = data.getType();

        Bundle pushBundle = new Bundle();

        pushBundle.putInt("t", pushType);
        pushBundle.putInt("idPush", idPush);

        pushBundle.putInt("v", data.getId() == 0 ? -1 : data.getId());

        Intent resultIntent;

        resultIntent = new Intent(this, StartMenuActivity.class);
        Bundle _b = new Bundle();
        _b.putBundle("pushBundle", pushBundle);
        _b.putInt(CONSTANTS.LOADING_SCREEN_KEY, CONSTANTS.SCREEN_REDIRECT_PUSH);
        resultIntent.putExtras(_b);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(resultIntent);
    }

    void getNotification() {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("page", page);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_NOTIFICATION_HISTORY, OP_ID_NOTIFICATION, this).execute(params);
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
        switch (opId) {
            case OP_ID_NOTIFICATION: {
                //Noticia
                int status = 0;
                boolean success = false;

                if (response.has("Status")) {
                    try {
                        status = (int) response.get("Status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (status == 200) {
                    if (response.has("Object")) {
                        try {
                            Gson gson = new Gson();
                            JSONObject obj = response.getJSONObject("Object");
                            NotificationData[] data = gson.fromJson(obj.getString("itens"), NotificationData[].class);

                            if (data != null && data.length > 0) {
                                mHolder.recyclerView.setVisibility(View.VISIBLE);
                                mHolder.placeholder.setVisibility(View.GONE);
                                List<NotificationData> notification = new ArrayList<>(Arrays.asList(data));
                                initNotification(notification, response.getInt("margin"));
                                hasData = true;
                            }else {
                                if (adapter != null && adapter.getItemCount() > 0) {
                                    mHolder.recyclerView.setVisibility(View.VISIBLE);
                                    mHolder.placeholder.setVisibility(View.GONE);
                                }else {
                                    mHolder.recyclerView.setVisibility(View.GONE);
                                    mHolder.placeholder.setVisibility(View.VISIBLE);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (adapter != null && adapter.getItemCount() > 0) {
                                mHolder.recyclerView.setVisibility(View.VISIBLE);
                                mHolder.placeholder.setVisibility(View.GONE);
                            }else {
                                mHolder.recyclerView.setVisibility(View.GONE);
                                mHolder.placeholder.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
            break;

            case OP_ID_SET_ALL: {
                int status = 0;
                boolean success = false;
                if (response.has("Status")) {
                    try {
                        status = (int) response.get("Status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (status == 200) {
                    seeAll = true;
                    page = 1;
                    getNotification();
                }
            }
            break;
        }
    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {
        if (opId == OP_ID_NOTIFICATION) {
            if (adapter == null) {
                mHolder.recyclerView.setVisibility(View.GONE);
                mHolder.placeholder.setVisibility(View.VISIBLE);
            }
        }
    }

    class ViewHolder {
        ImageView imgBack, imgCheck;
        RecyclerView recyclerView;
        View viewAll;
        TextView txtNotificary, txtCheck;
        LinearLayout placeholder;

        public ViewHolder() {
            imgBack = findViewById(R.id.img_back);
            placeholder = findViewById(R.id.placeholder);
            recyclerView = findViewById(R.id.notification_recycler);
            viewAll = findViewById(R.id.ll_view_all);
            txtNotificary = findViewById(R.id.txtNotificary);
            txtCheck = findViewById(R.id.txt_check);
            imgCheck = findViewById(R.id.img_check);
        }
    }
}