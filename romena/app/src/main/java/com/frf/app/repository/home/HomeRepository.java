package com.frf.app.repository.home;

import android.app.Activity;

import com.frf.app.R;
import com.frf.app.models.ErrorModel;
import com.frf.app.models.MessageModel;
import com.frf.app.repository.MainRepository;
import com.frf.app.utils.AsyncOperation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

public class HomeRepository extends MainRepository {

    private AsyncResponse asyncResponse;

    private final int OP_GET_HOME = 0;

    public HomeRepository(Activity context) {
        this.context = context;
    }

    public void getHome(AsyncResponse response){
        this.asyncResponse = response;
        getHome();
    }

    private void getHome (){
        new AsyncOperation(context, AsyncOperation.TASK_ID_GET_HOME, OP_GET_HOME, this).execute();
    }

    public void postLike(String id) {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        new AsyncOperation(context, AsyncOperation.TASK_ID_SET_ARENA_POST_LIKE, -1, this).execute(params);
    }

    @Override
    public void onSucces(int opId, int status, JSONObject response) {
        super.onSucces(opId, status, response);
        if (opId == OP_GET_HOME) {
            if (response.has("Object")) {
                if (status == 200) {
                    try {
                        JSONObject object = response.getJSONObject("Object");
                        JSONArray itens = object.getJSONArray("itens");

                        if (itens.length() > 0) {
                            asyncResponse.onResponseSucces(object);
                        } else {
                            MessageModel messageModel = new MessageModel();
                            messageModel.setId(1);
                            messageModel.setMsg(context.getResources().getString(R.string.there_is_nothing_here_at_the_moment));
                            asyncResponse.onResponseMessage(messageModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ErrorModel errorModel = new ErrorModel();
                        errorModel.setError(1);
                        errorModel.setMsg(context.getResources().getString(R.string.an_error_has_occurred));
                        asyncResponse.onResponseError(errorModel);
                    }
                }else {
                    ErrorModel errorModel = new ErrorModel();
                    errorModel.setError(1);
                    errorModel.setMsg(context.getResources().getString(R.string.an_error_has_occurred));
                    asyncResponse.onResponseError(errorModel);
                }
            }else {
                ErrorModel errorModel = new ErrorModel();
                errorModel.setError(1);
                errorModel.setMsg(context.getResources().getString(R.string.an_error_has_occurred));
                asyncResponse.onResponseError(errorModel);
            }
        }
    }

    @Override
    public void onError(int opId, int status, JSONObject response) {
        super.onError(opId, status, response);

        if (opId == OP_GET_HOME) {
            if (response.has("msg")) {
                ErrorModel errorModel = new ErrorModel();
                errorModel.setError(1);
                try {
                    errorModel.setMsg(response.getString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorModel.setMsg(context.getResources().getString(R.string.an_error_has_occurred));
                }
                asyncResponse.onResponseError(errorModel);
            }else {
                ErrorModel errorModel = new ErrorModel();
                errorModel.setError(1);
                errorModel.setMsg(context.getResources().getString(R.string.an_error_has_occurred));
                asyncResponse.onResponseError(errorModel);
            }
        }
    }

    public interface AsyncResponse {
        void onResponseSucces (JSONObject data);
        void onResponseMessage (MessageModel messageModel);
        void onResponseError (ErrorModel errorModel);
    }
}