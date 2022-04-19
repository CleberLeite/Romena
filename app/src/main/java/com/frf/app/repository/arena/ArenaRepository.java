package com.frf.app.repository.arena;

import android.app.Activity;
import android.widget.Toast;

import com.frf.app.R;
import com.frf.app.models.ErrorModel;
import com.frf.app.models.MessageModel;
import com.frf.app.repository.MainRepository;
import com.frf.app.utils.AsyncOperation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

public class ArenaRepository extends MainRepository {

    private AsyncResponse asyncResponse;

    private final int OP_GET_ARENA = 0;
    private final int OP_SET_ARENA_POST_LIKE = 0;
    private static final int OP_ID_GET_CATEGORIE_ALL = 1;
    private final int OP_UPLOAD_FOTO = 2;

    public ArenaRepository(Activity context) {
        this.context = context;
    }

    public void getArena(AsyncResponse response, String idUser, String page, String order, String category) {
        this.asyncResponse = response;
        getArena(idUser, page, order, category);
    }

    private void getArena(String idUser, String page, String order, String category) {
        Hashtable<String, Object> params = new Hashtable<>();

        params.put("idUser", idUser);
        params.put("order", order);
        params.put("page", page);
        params.put("category", category);

        new AsyncOperation(context, AsyncOperation.TASK_ID_GET_ARENA, OP_GET_ARENA, this).execute(params);
    }

    public void setArenaPostLike(int id) {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        new AsyncOperation(context, AsyncOperation.TASK_ID_SET_ARENA_POST_LIKE, OP_SET_ARENA_POST_LIKE, this).execute(params);
    }

    public void getCategories(AsyncResponse response) {
        this.asyncResponse = response;
        getCategories();
    }
    private void getCategories() {
        new AsyncOperation(context, AsyncOperation.TASK_ID_GET_ARENA_CATEGORIES, OP_ID_GET_CATEGORIE_ALL, this).execute();
    }

    @Override
    public void onSucces(int opId, int status, JSONObject response) {
        super.onSucces(opId, status, response);
        if (opId == OP_GET_ARENA) {
            if (response.has("Object")) {
                if (status == 200) {
                    try {
                        JSONObject object = response.getJSONObject("Object");
                        JSONArray itens = null;

                        try {
                            itens = object.getJSONArray("itens");
                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (itens != null && itens.length() > 0) {
                            object.put("margin", response.getInt("margin"));
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
                } else {
                    ErrorModel errorModel = new ErrorModel();
                    errorModel.setError(1);
                    errorModel.setMsg(context.getResources().getString(R.string.an_error_has_occurred));
                    asyncResponse.onResponseError(errorModel);
                }
            } else {
                ErrorModel errorModel = new ErrorModel();
                errorModel.setError(1);
                errorModel.setMsg(context.getResources().getString(R.string.an_error_has_occurred));
                asyncResponse.onResponseError(errorModel);
            }
        }

        if(opId == OP_UPLOAD_FOTO){
            Toast.makeText(context, ""+response.toString(), Toast.LENGTH_SHORT).show();
          //  if(response.has("img")){
  //              try {
//                    asyncResponse.onResponseSucces(changeUserModelValues(data));
    //            } catch (JSONException e) {
      //              e.printStackTrace();
        //        }

        }

        if(opId == OP_SET_ARENA_POST_LIKE){
                //listener.onResponseStatus(response.getInt("status"));
        }

        if (opId == OP_ID_GET_CATEGORIE_ALL) {
            if (status == 200) {
                try {
                    JSONObject object = response.getJSONObject("Object");
                    JSONArray itens = object.getJSONArray("itens");
                    if (itens.length() > 0) {
                        asyncResponse.onResponseSucces(object);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onError(int opId, int status, JSONObject response) {
        super.onError(opId, status, response);

        if (opId == OP_GET_ARENA) {
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
            } else {
                ErrorModel errorModel = new ErrorModel();
                errorModel.setError(1);
                errorModel.setMsg(context.getResources().getString(R.string.an_error_has_occurred));
                asyncResponse.onResponseError(errorModel);
            }
        }
    }

    public interface AsyncResponse {
        void onResponseSucces(JSONObject data);

        void onResponseMessage(MessageModel messageModel);

        void onResponseError(ErrorModel errorModel);
    }

}