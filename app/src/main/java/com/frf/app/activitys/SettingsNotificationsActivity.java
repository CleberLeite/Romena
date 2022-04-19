package com.frf.app.activitys;

import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_FEEDCOMMENT;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_FEEDLIKE;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_MATCHS;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_NOTICIA;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_QUIZ;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_SHOP;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_STORE;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_VIDEO;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;

import com.frf.app.R;
import com.frf.app.data.NotificationsPermissionsData;
import com.frf.app.informations.UserInformation;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

public class SettingsNotificationsActivity extends MainActivity implements AsyncOperation.IAsyncOpCallback {

    private ViewHolder mHolder;
    private static final int OP_ID_NOTIFICATION = 0;
    private static final int OP_ID_SET_ALL = 1;
    ArrayList<String> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_notifications);
        UTILS.setColorStatusBar(this, R.color.TabBarBackground);

        mHolder = new ViewHolder();
        mHolder.imgBack.setOnClickListener(view -> {
            finish();
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        mHolder.btnEnter.setEnabled(false);
        mHolder.btnEnter.setOnClickListener(v -> finish());
        getNotificationsPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();

        TRACKING.setTrackingClassName(SettingsNotificationsActivity.this, "setnotf_screen");
    }

    void getNotificationsPermissions() {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("idUser", UserInformation.getUserId());
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_NOTIFICATION_PERMISSIONS, OP_ID_NOTIFICATION, this).execute(params);
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
                int status = 0;
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
                            JSONArray itens = obj.getJSONArray("itens");
                            NotificationsPermissionsData[] data = gson.fromJson(obj.getString("itens"), NotificationsPermissionsData[].class);
                            for (int i = 0; i < itens.length(); i++) {
                                CallNotificationsPermissionsData(data[i]);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
            case OP_ID_SET_ALL: {
                int status = 0;
                if (response.has("Status")) {
                    try {
                        status = (int) response.get("Status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (status == 200) {
                    try {
                        Toast.makeText(activity, response.getString("msg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {

    }

    void CallNotificationsPermissionsData(NotificationsPermissionsData data) {
        View view = LayoutInflater.from(activity).inflate(R.layout.include_notifications_permissions, mHolder.contentItens, false);
        ImageView imgNotificary = view.findViewById(R.id.img_notificary);
        TextView titulo = view.findViewById(R.id.title);
        ToggleButton toggleButton = view.findViewById(R.id.toggleButton);

        switch (data.getId()) {
            case PUSH_ID_NOTICIA: imgNotificary.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_news)); break;
            case PUSH_ID_QUIZ: imgNotificary.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_quizes)); break;
            case PUSH_ID_MATCHS: imgNotificary.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_matcher_and_events)); break;
            case PUSH_ID_STORE: imgNotificary.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_coin_blue)); break;
            case PUSH_ID_SHOP: imgNotificary.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_purchases)); break;
            case PUSH_ID_FEEDLIKE: imgNotificary.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_unlike)); break;
            case PUSH_ID_FEEDCOMMENT: imgNotificary.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_comentary)); break;
            case PUSH_ID_VIDEO: imgNotificary.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_play)); break;
            default: imgNotificary.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_notifications)); break;
        }

        titulo.setText(data.getName());
        toggleButton.setChecked(data.getStatus() == 1);

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_SETTINGS_NOTIFICATIONS_CHECK, data.getId()).execute();
            }else {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_SETTINGS_NOTIFICATIONS_UNCHECK, data.getId()).execute();
            }
        });
        toggleButton.setOnClickListener(v -> {
            notifications.add(String.valueOf(data.getId()));
            mHolder.btnEnter.setEnabled(true);
            mHolder.btnEnter.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.PrimaryBackground));
        });

        mHolder.btnEnter.setOnClickListener(v -> {
            String aux = "";
            for (int i = 0; i <= notifications.size() - 1; i++) {
                aux +=  notifications.get(i) + ",";
            }
            setNotificationsPermissionRead(aux.substring(0, aux.length()-1));
        });

        mHolder.contentItens.addView(view);
    }

    void setNotificationsPermissionRead(String id) {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("idType", id);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_NOTIFICATION_PERMISSION_READ, OP_ID_SET_ALL, this).execute(params);
    }

    @Override
    public void onBackPressed() {
        finish();
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    class ViewHolder {
        ImageView imgBack;
        LinearLayout contentItens;
        Button btnEnter;

        public ViewHolder() {
            imgBack = findViewById(R.id.img_back);
            btnEnter = findViewById(R.id.btn_edit_enter);
            contentItens = findViewById(R.id.containerHomeItens);
        }
    }
}