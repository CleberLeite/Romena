package com.frf.app;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.gson.Gson;
import com.frf.app.informations.UserInformation;
import com.frf.app.utils.CONSTANTS;
import com.frf.app.activitys.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

public class MyAplication extends Application implements LifecycleObserver {

    private MainActivity activity;
    public static MyAplication intance;

    @Override
    public void onCreate() {
        super.onCreate();
        if(intance == null){
            intance = this;
        }

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        Log.i("AsyncOperationTAGFLUXO", "ON_STOP");
        //Log.d("Session2", "ON_STOP " + UserInformation.getSessionId() + " " + UserInformation.getUserId());
        //activity.saveSessionValues(); //Salvando para quando o app for fechado
        activity.endSessionBackground();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        Log.i("AsyncOperationTAGFLUXO", "ON_START");
        // Log.d("Session2", "ON_START: " + UserInformation.getSessionId());
        activity.setSession();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onAppOnDestroy() {
        Log.i("AsyncOperationTAGFLUXO", "ON_DESTROY");

        Hashtable<String, Object> params = new Hashtable<String, Object>();
        params.put("sId", UserInformation.getSessionId());
        params.put("endType", 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        params.put("end", currentDateandTime);
        String endSession = new Gson().toJson(params);
        Log.i("AsyncOperationTAGFLUXO", "seção antes de gravar2 " + endSession);
        activity.prefs.put(CONSTANTS.SHARED_PREFS_KEY_ENDSESSION, endSession);
        // activity.setSession();
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }
}
