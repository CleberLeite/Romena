package com.frf.app.activitys;

import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_BADGE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.frf.app.R;
import com.frf.app.data.BadgeData;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.UTILS;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class WinBadgeActivity extends MainActivity implements AsyncOperation.IAsyncOpCallback {

    private static final int OP_GET_BADGES = 0;
    List<BadgeData> badges = new ArrayList<>();
    int listItemValue = 0;

    ViewHolder mHolder;

    Animation animPop, fadeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_badge);

        mHolder = new ViewHolder();
        initValues();
        initFirtAnimation();

        if (getIntent() != null) {
            if (getIntent().hasExtra(PUSH_ID_BADGE + "")) {
                //new AsyncOperation(activity, AsyncOperation.TASK_ID_CHECK_VERSION, 999, emptyAsync, TRACKING_EXECUTAR_PUSH_SILENCIOSO_DE_GETVERSION, getIntent().getIntExtra("idSilent", 0)).execute();
            }
        }

        Intent intent = getIntent();
        if (intent.hasExtra("badges")) {
            Gson gson = new Gson();
            BadgeData[] data = gson.fromJson(intent.getStringExtra("badges"), BadgeData[].class);
            if (data != null && data.length > 0) {
                badges = new ArrayList<>(Arrays.asList(data));
                showBadges();
            }
        }else{
            //getNewBadges();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Toast.makeText(activity, getResources().getString(R.string.receive_your_badges), Toast.LENGTH_SHORT).show();
    }

    void initValues() {
        animPop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pop);
        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
    }

    void initFirtAnimation() {

    }

/*    void getNewBadges (){
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_BADGES, OP_GET_BADGES, this).execute();
    }*/

    void showBadges() {
        if (badges.size() > 0) {
            final BadgeData badge = badges.get(listItemValue);

            UTILS.getImageAt(activity, badge.getImg(), mHolder.badgeImage);

            mHolder.badgeImage.startAnimation(animPop);
            mHolder.badgeTitle.startAnimation(fadeIn);
            mHolder.badgeDescription.startAnimation(fadeIn);


            try {
                mHolder.badgeTitle.setText(badge.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mHolder.badgeDescription.setText(badge.getDescricao());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mHolder.progress.setText(badge.getOcurrenceActual() + "/" + badge.getOccurrenceNeeded() + " Răspunsuri Corecte");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mHolder.prize.setText(badge.getPrize() + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (listItemValue < badges.size() - 1) {
                mHolder.nextBadge.setText(getResources().getString(R.string.next));
                mHolder.nextBadge.setOnClickListener(v -> {
                    //new AsyncOperation(activity,AsyncOperation.TASK_ID_SAVE_TRACKING,999, emptyAsync, TRACKING_PROXIMA_CONQUISTA, badge.getId()).execute();
                    listItemValue++;
                    showBadges();
                });

            } else {
                mHolder.nextBadge.setText(getResources().getString(R.string.exit));
                mHolder.nextBadge.setOnClickListener(v -> {
                    //new AsyncOperation(activity,AsyncOperation.TASK_ID_SAVE_TRACKING,999, emptyAsync, TRACKING_NOVA_CONQUISTA_FECHAR, badge.getId()).execute();
                    finish();
                });
            }
        }
    }

    //region Async
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
            response = new JSONObject(Objects.requireNonNull(message.getData().getString("response")));
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
        if (opId == OP_GET_BADGES) {//pegar quiz
            int status = 0;
            //pegar categoria de quiz
            if (response.has("Status")) {
                try {
                    status = response.getInt("Status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //pegar categoria de quiz
            if (response.has("Object")) {
                if (status == 200) {
                    try {
                        Gson gson = new Gson();
                        JSONObject obj = response.getJSONObject("Object");
                        BadgeData[] data = gson.fromJson(obj.getString("itens"), BadgeData[].class);
                        if (data != null && data.length > 0) {
                            badges = new ArrayList<>(Arrays.asList(data));
                            showBadges();
                        } else {
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {

    }
    //endregion

    class ViewHolder {
        ImageView badgeImage;
        TextView badgeTitle;
        TextView badgeDescription;
        TextView progress;
        TextView prize;
        Button nextBadge;

        public ViewHolder() {
            badgeImage = findViewById(R.id.imageBadge);
            badgeTitle = findViewById(R.id.nome);
            badgeDescription = findViewById(R.id.descricao);
            progress = findViewById(R.id.progress);
            prize = findViewById(R.id.prize);

            nextBadge = findViewById(R.id.btn_next);
        }
    }
}
