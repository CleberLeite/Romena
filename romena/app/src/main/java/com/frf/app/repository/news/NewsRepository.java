package com.frf.app.repository.news;

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

public class NewsRepository extends MainRepository {

    private AsyncResponse asyncResponse;

    private final int OP_GET_NEWS = 0;
    private static final int OP_ID_SET_FOLLOW_VIDEOS_CATEGORY = 2;

    public NewsRepository(Activity context) {
        this.context = context;
    }

    public void getNews(AsyncResponse response, String page, String category, String fav, String order, String bookmarked){
        this.asyncResponse = response;
        getNews(page, category, fav, order, bookmarked);
    }

    private void getNews(String page, String category, String fav, String order, String bookmarked){
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("page", page);
        params.put("category", category);
        params.put("fav", fav);
        params.put("order", order);
        params.put("bookmarked", bookmarked);
        new AsyncOperation(context, AsyncOperation.TASK_ID_GET_ALL_NEWS, OP_GET_NEWS, this).execute(params);
    }

    @Override
    public void onSucces(int opId, int status, JSONObject response) {
        super.onSucces(opId, status, response);
        if (opId == OP_GET_NEWS) {
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
                            if(object.has("orderItens") || object.has("categories")) {
                                asyncResponse.onResponseSucces(object);
                            }
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

        if (opId == OP_GET_NEWS || opId == OP_ID_SET_FOLLOW_VIDEOS_CATEGORY) {
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

    public void setFollowNewsCategory(int id, AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("idCategory", id);
        new AsyncOperation(context, AsyncOperation.TASK_ID_SET_FOLLOW_NEWS_CATEGORY, OP_ID_SET_FOLLOW_VIDEOS_CATEGORY, this).execute(params);
    }


    public interface AsyncResponse {
        void onResponseSucces (JSONObject data);
        void onResponseMessage (MessageModel messageModel);
        void onResponseError (ErrorModel errorModel);
    }
}