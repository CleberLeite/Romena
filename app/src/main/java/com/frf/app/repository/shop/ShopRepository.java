package com.frf.app.repository.shop;

import android.app.Activity;
import android.util.Log;

import com.frf.app.R;
import com.frf.app.data.ShopProductData;
import com.frf.app.models.ErrorModel;
import com.frf.app.repository.MainRepository;
import com.google.gson.Gson;
import com.frf.app.models.MessageModel;
import com.frf.app.utils.AsyncOperation;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

public class ShopRepository extends MainRepository {

    private AsyncResponse asyncResponse;

    private static final int OP_GET_ITENS = 0;
    private static final int OP_GET_SEARCH = 1;

    public ShopRepository(Activity context) {
        this.context = context;
    }

    public void getItens(int page, int category, AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("page", page);
        params.put("categoryid", category);
        new AsyncOperation(context, AsyncOperation.TASK_ID_GET_SHOP_ITENS, OP_GET_ITENS, this).execute(params);
    }

    public void initSearch(String keyword, AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("keyword", keyword);
        new AsyncOperation(context, AsyncOperation.TASK_ID_GET_SEARCH_SHOP, OP_GET_SEARCH, this).execute(params);
    }

    public static void longInfo(String str) {
        if(str.length() > 4000) {
            Log.e("TAG", str.substring(0, 4000));
            longInfo(str.substring(4000));
        } else
            Log.e("TAG", str);
    }

    @Override
    public void onSucces(int opId, int status, JSONObject response) {
        super.onSucces(opId, status, response);

        switch (opId) {
            case OP_GET_ITENS: {
                if (response.has("data")) {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        if (data.has("data")) {

                            ShopProductData[] itens = new Gson().fromJson(data.getString("data"), ShopProductData[].class);

                            if (itens != null) {
                                asyncResponse.onResponseSucces(itens);
                            }else {
                                ErrorModel errorModel = new ErrorModel();
                                errorModel.setError(1);
                                errorModel.setMsg(context.getResources().getString(R.string.an_error_has_occurred));
                                asyncResponse.onResponseError(errorModel);
                            }

                        }else {
                            ErrorModel errorModel = new ErrorModel();
                            errorModel.setError(2);
                            errorModel.setMsg(context.getResources().getString(R.string.nu_au_fost_gasite_articolele));
                            asyncResponse.onResponseError(errorModel);
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
            }
            break;
            case OP_GET_SEARCH: {
                if (response.has("data")) {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        if (data.has("data")) {

                            ShopProductData[] itens = new Gson().fromJson(data.getString("data"), ShopProductData[].class);

                            if (itens != null && itens.length > 0) {
                                asyncResponse.onResponseSucces(itens);
                            }else {
                                ErrorModel errorModel = new ErrorModel();
                                errorModel.setError(0);
                                errorModel.setMsg(context.getResources().getString(R.string.no_items_found));
                                asyncResponse.onResponseError(errorModel);
                            }

                        }else {
                            ErrorModel errorModel = new ErrorModel();
                            errorModel.setError(2);
                            errorModel.setMsg(context.getResources().getString(R.string.nu_au_fost_gasite_articolele));
                            asyncResponse.onResponseError(errorModel);
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
            }
            break;
        }
    }

    @Override
    public void onError(int opId, int status, JSONObject response) {
        super.onError(opId, status, response);

        switch (opId) {
            case OP_GET_ITENS: {
                ErrorModel errorModel = new ErrorModel();
                errorModel.setError(1);
                errorModel.setMsg(context.getResources().getString(R.string.an_error_has_occurred));
                asyncResponse.onResponseError(errorModel);
            }
            break;
        }
    }

    public interface AsyncResponse {
        void onResponseSucces(ShopProductData[] data);
        void onResponseMessage(MessageModel messageModel);
        void onResponseError(ErrorModel errorModel);
    }
}
