package com.frf.app.repository;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.SecurePreferences;
import com.frf.app.utils.UTILS;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;


public class MainRepository implements AsyncOperation.IAsyncOpCallback {

    protected Activity context;

    //Local Data
    protected SecurePreferences securePreferences;
    public static final String SHARED_KEY_USER                          = "prts:user:11FsERsC$*sdaQ!2";
    public static final String SHARED_KEY_USER_TOKEN                    = "prts:token:11FsERC$*sdaQ!2";

    @Override
    public  void CallHandler(int opId, JSONObject response, boolean success) {
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
        boolean success;

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
    public void OnAsyncOperationSuccess(int opId, JSONObject response) {
        int status = 0;
        boolean sucess = false;

        if(response.has("Status") || response.has("status")){
            try {
                if(response.has("Status")) {
                    status = response.getInt("Status");
                }else if(response.has("status")) {
                    status = response.getInt("status");
                }

                if(status == 200 || status == 404){
                    sucess = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(sucess){
            onSucces(opId, status, response);
        }else{
            onError(opId, status, response);
        }

    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {
        onError(opId, 0, response);
    }

    public void onSucces (int opId, int status, JSONObject response){
    }

    public void onError (int opId, int status, JSONObject response){
    }
}
