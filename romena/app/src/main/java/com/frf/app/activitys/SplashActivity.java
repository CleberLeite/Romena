package com.frf.app.activitys;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import com.frf.app.R;
import com.frf.app.informations.UserInformation;
import com.frf.app.repository.user.UserRepository;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.CONSTANTS;
import com.frf.app.utils.NetworkChangeReceiver;
import com.frf.app.utils.RomenaNotificationManager;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.frf.app.vmodels.user.UserViewModel;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.FirebaseApp;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Objects;

public class SplashActivity extends MainActivity implements AsyncOperation.IAsyncOpCallback {

    boolean getVersionOk = false;
    private String gcm_token = "";

    private static final int OP_GET_GCM_TOKEN = 0;
    private static final int OP_CHECK_VERSION = 1;

    private UserViewModel userViewModel;

    private ViewHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        UTILS.setColorStatusBar(this, R.color.PrimaryBackground);

        if (getIntent().getBooleanExtra("close", false)) {
            this.finishAffinity();
        }

        FirebaseApp.initializeApp(activity);
        mHolder = new ViewHolder();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("messagemToken", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();

                    UserInformation.getUserData().setApns(token);
                    RomenaNotificationManager.gcm = token;
                    Proceed();
                    Log.e("messagemToken", token);
                });

        UTILS.context = this;
        initActions();
        serverOK();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void bindViewModel() {
        super.bindViewModel();

        UserRepository repository = new UserRepository(activity, prefs);
        userViewModel = ViewModelProviders.of(SplashActivity.this).get(UserViewModel.class);
        userViewModel.setmRepository(repository);
        userViewModel.getUserData().observe(this, user -> {

            if (user != null) {
                initUser();
            }else {
                initNoUser();
            }
        });

        userViewModel.getMessage().observe(this, messageModel -> {

            if (messageModel.getId() == 1) {
                        Bundle b = getIntent().getExtras();
                        boolean hasBundle = b != null;
                        UTILS.setUserLoggedIn(true);
                        if(hasBundle){
                            initMenuByPush(b);
                        }else{
                            goMainMenu();
                        }
                    }else {
                        mHolder.contentRetry.setVisibility(View.VISIBLE);
                        mHolder.msg.setText(messageModel.getMsg()+"");
                    }
                }
        );

        userViewModel.getError().observe(this, error -> {
            // getVersionOk
            if (error.getError() == 1) {
                initNoUser();
            } else if (error.getError() == 2) {
                Bundle b = getIntent().getExtras();
                boolean hasBundle = b != null;

                UTILS.setUserLoggedIn(true);
                if(hasBundle){
                    initMenuByPush(b);
                }else{
                    goMainMenu();
                }
            }else {
                mHolder.contentRetry.setVisibility(View.VISIBLE);
                mHolder.msg.setText(error.getMsg()+"");
            }
        });
    }

    @Override
    public void unBindViewModel() {
        super.unBindViewModel();
        try{userViewModel.getUserData().removeObservers(this);}catch (Exception e){e.printStackTrace();}
        try{userViewModel.getError().removeObservers(this);}catch (Exception e){e.printStackTrace();}
    }

    @Override
    protected void onResume() {
        super.onResume();
        getVersionOk = false;
    }

    private void initActions() {
        mHolder.retry.setOnClickListener(v -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_SPLASH_RETRY).execute();
            mHolder.contentRetry.setVisibility(View.GONE);
            serverOK();
        });
    }

    private void initNoUser (){
        UTILS.setUserLoggedIn(false);
        goLoginMenu();
    }

    private void goLoginMenu (){
        finish();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (getIntent().getBooleanExtra("resetSSO", false)) {
            intent.putExtra("resetSSO", true);
        }
        startActivity(intent);
        activity.overridePendingTransition(R.anim.no_anim, R.anim.no_anim);
    }

    private void initUser() {

        boolean hasBundle = false;
        Bundle b = getIntent().getExtras();
        if(b != null && b.containsKey("t")){
            hasBundle = true;
        }

        UTILS.setUserLoggedIn(true);
        if (hasBundle) {
            initMenuByPush(b);
        } else {
            goMainMenu();
        }

    }

    private void goMainMenu (){
        Intent intent = new Intent(activity, StartMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    void initMenuByPush (Bundle b){
        int idPush = b.getInt("idPush", 0);
        int pushType = b.getInt("t", 0);

        if (idPush == 0) {
            idPush = Integer.parseInt(b.getString("idPush", "0"));
        }

        if (pushType == 0) {
            pushType = Integer.parseInt(b.getString("t", "0"));
        }

        Bundle pushBundle = new Bundle();

        pushBundle.putInt("t", pushType);
        pushBundle.putInt("idPush", idPush);

        int id = -1;

        try {id = b.getInt("v", -1) == -1 ? Integer.parseInt(b.getString("v", "-1")) : b.getInt("v", -1);}catch (Exception e){e.printStackTrace();}
        pushBundle.putInt("v", id == 0 ? -1 : id);

        Intent resultIntent;

        //Se esta logado
        if(UTILS.isUserLoggedIn()) {
            resultIntent = new Intent(this, StartMenuActivity.class);
            Bundle _b = new Bundle();
            _b.putBundle("pushBundle", pushBundle);
            _b.putInt(CONSTANTS.LOADING_SCREEN_KEY, CONSTANTS.SCREEN_REDIRECT_PUSH);
            resultIntent.putExtras(_b);

            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(resultIntent);
            finish();
        }
    }

    void CallUpdateSuggestion(String message){
        finish();
        Intent intent = new Intent(SplashActivity.this, MaintenanceLockScreenActivity.class);
        intent.putExtra("msg", message);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    void CallUpdateSuggestion(String message, int status){
        finish();
        Intent intent = new Intent(SplashActivity.this, MaintenanceLockScreenActivity.class);
        intent.putExtra("msg", message);
        intent.putExtra("status", status);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    void CallUpdateSuggestion(String message, String url, int status) {
        finish();
        Intent intent = new Intent(SplashActivity.this, MaintenanceLockScreenActivity.class);
        intent.putExtra("msg", message);
        intent.putExtra("url", url);
        intent.putExtra("status", status);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void CallNoConnectionWarning(){
        mHolder.contentRetry.setVisibility(View.VISIBLE);
        mHolder.msg.setText(getResources().getString(R.string.sem_conexao));
    }

    private void serverOK (){
        if(UTILS.isNetworkAvailable(this)) {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_VERSION, OP_CHECK_VERSION, this).execute();
        }else{
            NetworkChangeReceiver.currentActivity = this;
            CallNoConnectionWarning();
        }
    }

    private void getUser(){
        ConfigurePushNotification();
        try{userViewModel.getUser(true);}catch (Exception e){e.printStackTrace();}
    }

    public void ConfigurePushNotification(){
        if(UTILS.checkGooglePlayServiceAvailability(getApplicationContext())){
            FirebaseInstallations.getInstance().getToken(true).addOnSuccessListener(instanceIdResult -> {

            }).addOnFailureListener(e -> Proceed());
        }
        else{
            AskForGooglePlayServicesUpdate();
        }
    }

    void AskForGooglePlayServicesUpdate(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, R.style.AlertDialogCustom);

        alertDialogBuilder.setTitle("");
        alertDialogBuilder
                .setMessage("error")
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.sim), (dialog, id) -> GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(SplashActivity.this))
                .setNegativeButton(getResources().getString(R.string.nao), (dialog, id) -> {
                    dialog.cancel();
                    Proceed();
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        try{alertDialog.show();}catch (Exception e){e.printStackTrace();}
    }

    void Proceed (){
        UserInformation.getUserData().setApns(RomenaNotificationManager.gcm);
        prefs.put(CONSTANTS.SHARED_PREFS_KEY_APNS, RomenaNotificationManager.gcm);
    }

    private final Handler handlerOp = new Handler(message -> {
        int opId = -1;
        JSONObject response;
        boolean success = false;

        try{
            opId = message.getData().getInt("opId");
            response = new JSONObject(Objects.requireNonNull(message.getData().getString("response")));
            success = message.getData().getBoolean("success");
        }
        catch (JSONException e){
            UTILS.DebugLog("Error", "Error getting handlers params.");
            return false;
        }

        if(success) {
            OnAsyncOperationSuccess(opId, response);
        }
        else{
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
        switch (opId){
            case OP_GET_GCM_TOKEN:{

            }
            case OP_CHECK_VERSION:{
                Log.e("getVersion", "OP_CHECK_VERSION: " + response);
                boolean success = false;
                String msg = "";
                String url = "";
                int status = 0;
                try{

                    if( (response.has("Message")) && (response.get("Message") != JSONObject.NULL) ){
                        msg = response.getString("Message");
                    }
                    if( (response.has("url")) && (response.get("url") != JSONObject.NULL) ){
                        url = response.getString("url");
                    }
                    if( (response.has("Status")) && (response.get("Status") != JSONObject.NULL) ){
                        status = response.getInt("Status");
                    }

                    if( (response.has("api")) && (response.get("api") != JSONObject.NULL) ) {
                        String version = response.getString("api");
                        if(!CONSTANTS.serverURL.contains(version)) {
                            CONSTANTS.serverVersion = version + "/";
                            CONSTANTS.serverURL = CONSTANTS.serverURL.concat(CONSTANTS.serverVersion);
                        }
                    }

                    if(response.has("link_ecommerce") && response.get("link_ecommerce") != JSONObject.NULL) {
                        CONSTANTS.link_ecommerce = response.getString("link_ecommerce");
                    }

                    if(response.has("link_ticketing") && response.get("link_ticketing") != JSONObject.NULL) {
                        CONSTANTS.link_ticketing = response.getString("link_ticketing");
                    }

                    success = ( (status == HttpURLConnection.HTTP_OK) ||
                            (status == CONSTANTS.ERROR_CODE_UPDATE_SUGGESTED) ||
                            (status == CONSTANTS.ERROR_CODE_UPDATE_REQUIRED) ||
                            (status == CONSTANTS.ERROR_CODE_MAINTENANCE));

                    if((response.has("sId")) && (response.getInt("sId") > 0)){
                        UserInformation.setSessionId((int) response.get("sId"));
                    }

                } catch (JSONException e) {
                    UTILS.DebugLog("Error", e);
                }

                if(success){

                    Intent intent = getIntent();
                    if (intent.hasExtra("trigger")) {
                        status = 200;
                        getIntent().removeExtra("trigger");
                    }

                    if(status == CONSTANTS.ERROR_CODE_UPDATE_REQUIRED || status == CONSTANTS.ERROR_CODE_UPDATE_SUGGESTED) {
                        CallUpdateSuggestion(msg, url, status);
                    }else if(status == CONSTANTS.ERROR_CODE_MAINTENANCE){
                        CallUpdateSuggestion(msg, status);
                    } else {
                        getVersionOk = true;
                        getUser();
                    }
                } else{
                    CallUpdateSuggestion(msg);
                }
            }
            break;


        }
    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {

    }

    class ViewHolder {
        LinearLayout contentRetry;
        TextView msg;
        Button retry;

        public ViewHolder() {
            contentRetry = findViewById(R.id.content_retry);
            msg = findViewById(R.id.msg);
            retry = findViewById(R.id.retry);
        }
    }
}