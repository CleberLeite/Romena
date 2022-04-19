package com.frf.app.repository.tickets;

import android.app.Activity;
import android.util.Log;
import com.frf.app.R;
import com.frf.app.data.CompetitionsData;
import com.frf.app.data.ShopProductData;
import com.frf.app.data.TicketsMatchesData;
import com.frf.app.data.TicketsOffersData;
import com.frf.app.data.TicketsSectorsData;
import com.frf.app.models.ErrorModel;
import com.frf.app.models.MessageModel;
import com.frf.app.repository.MainRepository;
import com.frf.app.utils.AsyncOperation;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Hashtable;

public class TicketsRepository extends MainRepository {

    private AsyncResponse asyncResponse;

    private static final int OP_GET_COMPETITIONS = 0;
    private static final int OP_GET_MATCHES = 1;
    private static final int OP_GET_SECTORS = 2;
    private static final int OP_GET_OFFERS = 3;
    private static final int OP_GET_SECTORS_SVG = 4;

    public TicketsRepository(Activity context) {
        this.context = context;
    }

    public void getCompetitions(AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
        new AsyncOperation(context, AsyncOperation.TASK_ID_GET_COMPETITIONS_TICKETS, OP_GET_COMPETITIONS, this).execute();
    }

    public void getMatches(int id, AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        new AsyncOperation(context, AsyncOperation.TASK_ID_GET_MATCHES_TICKETS, OP_GET_MATCHES, this).execute(params);
    }

    public void getSectors(int id, AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        new AsyncOperation(context, AsyncOperation.TASK_ID_GET_SECTORS_TICKETS, OP_GET_SECTORS, this).execute(params);
    }

    public void getSectorsSvg(int id, AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        new AsyncOperation(context, AsyncOperation.TASK_ID_GET_SECTORS_SVG_TICKETS, OP_GET_SECTORS_SVG, this).execute(params);
    }

    public void getOffers(int id, AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        new AsyncOperation(context, AsyncOperation.TASK_ID_GET_OFFERS_TICKETS, OP_GET_OFFERS, this).execute(params);
    }

    @Override
    public void onSucces(int opId, int status, JSONObject response) {
        super.onSucces(opId, status, response);

        switch (opId) {
            case OP_GET_COMPETITIONS: {
                try {
                    CompetitionsData[] data = new Gson().fromJson(response.getString("items"), CompetitionsData[].class);
                    if (data != null && data.length > 0) {
                        asyncResponse.onResponseSucces(data);
                    }else {
                        asyncResponse.onResponseSucces((CompetitionsData[]) null);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    asyncResponse.onResponseSucces((CompetitionsData[]) null);
                }
            }
            break;

            case OP_GET_MATCHES: {
                try {
                    TicketsMatchesData[] data = new Gson().fromJson(response.getString("items"), TicketsMatchesData[].class);
                    if (data != null && data.length > 0) {
                        asyncResponse.onResponseSucces(data);
                    }else {
                        asyncResponse.onResponseSucces((TicketsMatchesData[]) null);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    asyncResponse.onResponseSucces((TicketsMatchesData[]) null);
                }
            }
            break;

            case OP_GET_SECTORS: {
                try {
                    TicketsSectorsData data = new Gson().fromJson(response.getString("object"), TicketsSectorsData.class);
                    if (data != null && data.getAvailableSectors().size() > 0) {
                        asyncResponse.onResponseSucces(data);
                    }else {
                        asyncResponse.onResponseSucces((TicketsSectorsData) null);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    asyncResponse.onResponseSucces((TicketsSectorsData) null);
                }
            }
            break;

            case OP_GET_OFFERS: {
                try {
                    TicketsOffersData data = new Gson().fromJson(response.getString("object"), TicketsOffersData.class);
                    if (data != null && data.getOffers().size() > 0) {
                        asyncResponse.onResponseSucces(data);
                    }else {
                        asyncResponse.onResponseSucces((TicketsOffersData) null);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    asyncResponse.onResponseSucces((TicketsOffersData) null);
                }
            }
            break;

            case OP_GET_SECTORS_SVG: {
                try {
                    asyncResponse.onResponseSucces(response.getString("object"));
                }catch (Exception e){
                    e.printStackTrace();
                    asyncResponse.onResponseSucces((TicketsOffersData) null);
                }
            }
            break;
        }
    }

    @Override
    public void onError(int opId, int status, JSONObject response) {
        super.onError(opId, status, response);

        switch (opId) {
            case OP_GET_MATCHES:
            case OP_GET_OFFERS:
            case OP_GET_SECTORS:
            case OP_GET_SECTORS_SVG:
            case OP_GET_COMPETITIONS: {
                ErrorModel errorModel = new ErrorModel();
                errorModel.setError(1);
                if (response.has("msg")) {
                    try {
                        errorModel.setMsg(response.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if (response.has("message")) {
                    try {
                        errorModel.setMsg(response.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    errorModel.setMsg(context.getResources().getString(R.string.error_in_server));
                }
                asyncResponse.onResponseError(errorModel);
            }
            break;
        }
    }

    public interface AsyncResponse {
        void onResponseSucces(CompetitionsData[] data);
        void onResponseSucces(TicketsMatchesData[] data);
        void onResponseSucces(TicketsSectorsData data);
        void onResponseSucces(TicketsOffersData data);
        void onResponseSucces(String data);

        void onResponseMessage(MessageModel messageModel);
        void onResponseError(ErrorModel errorModel);
    }

}
