package com.frf.app.activitys;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.frf.app.MyAplication;
import com.frf.app.R;
import com.frf.app.data.EndSessionData;
import com.frf.app.informations.UserInformation;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.CONSTANTS;
import com.frf.app.utils.SecurePreferences;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    protected AppCompatActivity activity;
    public static MainActivity instance;
    public static SecurePreferences prefs = null;

    private static final int OP_ID_SING_OUT = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = new SecurePreferences(getApplicationContext(), CONSTANTS.SHARED_PREFS_KEY_FILE, CONSTANTS.SHARED_PREFS_SECRET_KEY, true);
        activity = this;

        if (instance == null) {
            instance = this;
           // registerBroadCasts();
            MyAplication.intance.setActivity(instance);
        }
    }

    public void setSession() {
        Gson gson = new Gson();
        EndSessionData data = gson.fromJson(prefs.getString(CONSTANTS.SHARED_PREFS_KEY_ENDSESSION, ""), EndSessionData.class);

        if(data != null) {
            Hashtable<String, Object> params = new Hashtable<String, Object>();
            params.put("sId", data.getsId()+"");
            params.put("endType", 2);
            params.put("time", data.getTime()+"");

            new AsyncOperation(instance, AsyncOperation.TASK_ID_SET_END_SESSION, OP_ID_SING_OUT, mainCallback).execute(params);

        }
    }

    public void endSessionBackground() {
        Hashtable<String, Object> params = new Hashtable<String, Object>();
        params.put("sId", UserInformation.getSessionId());
        params.put("endType", 2);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        params.put("end", currentDateandTime);
        params.put("time", currentDateandTime);
        String endSession = new Gson().toJson(params);

        Log.i("AsyncOperationTAGFLUXO", "seção antes de gravar " + endSession);
        prefs.put(CONSTANTS.SHARED_PREFS_KEY_ENDSESSION, endSession);
        // new AsyncOperation(instance, AsyncOperation.TASK_ID_SET_END_SESSION, OP_ID_SING_OUT, mainCallback).execute(params);
    }

    public static AsyncOperation.IAsyncOpCallback emptyAsync = new AsyncOperation.IAsyncOpCallback() {
        @Override
        public void CallHandler(int opId, JSONObject response, boolean success) {

        }

        @Override
        public void OnAsyncOperationSuccess(int opId, JSONObject response) {

        }

        @Override
        public void OnAsyncOperationError(int opId, JSONObject response) {

        }
    };

    private AsyncOperation.IAsyncOpCallback mainCallback = new AsyncOperation.IAsyncOpCallback() {
        @Override
        public void CallHandler(int opId, JSONObject response, boolean success) {
            if (success) {
                this.OnAsyncOperationSuccess(opId, response);
            } else {
                this.OnAsyncOperationError(opId, response);
            }
        }

        @Override
        public void OnAsyncOperationSuccess(int opId, JSONObject response) {
            switch (opId) {

                case OP_ID_SING_OUT: {
                    int status = 0;
                    if (response.has("Status")) {
                        try {
                            status = response.getInt("Status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (status == 200) {
                        Log.d("Async", "Session: " + UserInformation.getSessionId());
                    }
                }
                break;
            }
        }

        @Override
        public void OnAsyncOperationError(int opId, JSONObject response) {
            switch (opId) {
                case OP_ID_SING_OUT: {

                    int status = 0;
                    if (response.has("Status")) {
                        try {
                            status = response.getInt("Status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (status == 200) {
                        // UserInformation.setSessionId(0);
                    }
                }
                break;
            }
        }
    };

    public void closeApp() {
        this.finishAffinity();
    }

    @Override
    protected void onResume() {
        bindViewModel();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unBindViewModel();
    }

    public void bindViewModel (){

    }

    public void unBindViewModel (){

    }
    public void bindBroadcast (){

    }

    public void unBindBroadcast (){

    }



}