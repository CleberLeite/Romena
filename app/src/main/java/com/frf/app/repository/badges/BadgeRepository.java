package com.frf.app.repository.badges;

import android.app.Activity;

import com.frf.app.data.BadgeData;
import com.google.gson.Gson;
import com.frf.app.repository.MainRepository;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.SecurePreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class BadgeRepository extends MainRepository {

    private AsyncResponse asyncResponse;
    private int page = 1;

    private final int OP_GET_BADGES = 0;

    public BadgeRepository() {}

    public BadgeRepository(Activity context) {
        this.context = context;
    }

    public BadgeRepository(Activity context, SecurePreferences preferences) {
        this.context = context;
        this.securePreferences = preferences;
    }

    public void getList(int page, AsyncResponse response){
        this.asyncResponse = response;
        this.page = page;
        getList();
    }

    private void getList (){
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("page", page);
        new AsyncOperation(context, AsyncOperation.TASK_ID_GET_BADGE, OP_GET_BADGES, this).execute(params);
    }

    @Override
    public void onSucces(int opId, int status, JSONObject response) {
        super.onSucces(opId, status, response);
        switch (opId){
            case OP_GET_BADGES:{
                if(response.has("Object")){
                    if(status == 200){
                        try {
                            Gson gson = new Gson();
                            JSONObject object = response.getJSONObject("Object");
                            BadgeData[] data = gson.fromJson(object.getString("itens"), BadgeData[].class);

                            if(data != null && data.length > 0){
                                List<BadgeData> badge = new ArrayList<>(Arrays.asList(data));
                                asyncResponse.onResponseSucces(badge);
                            }else{
                                asyncResponse.onResponseSucces(null);
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

    @Override
    public void onError(int opId, int status, JSONObject response) {
        super.onError(opId, status, response);
    }

    public interface AsyncResponse {
        void onResponseSucces (List<BadgeData> data);
        void onResponseMessage (String message);
        void onResponseError (String message);
    }
}
