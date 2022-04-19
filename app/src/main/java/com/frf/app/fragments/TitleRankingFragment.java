package com.frf.app.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.adapter.AdapterTitleRankingAdapter;
import com.frf.app.data.RankingTitleData;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.UTILS;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TitleRankingFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {

    private ViewHolder mHolder;
    private static final int OP_GET_RANK_TITLE                     = 1;
    AdapterTitleRankingAdapter adapterRankItem;

    public TitleRankingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_title_ranking, container, false);
        mHolder = new ViewHolder(view);
        getRanking();
        return view;
    }

    private final Handler handlerOp = new Handler(message -> {
        int opId = -1;
        JSONObject response;
        boolean success = false;

        try{
            opId = message.getData().getInt("opId");
            response = new JSONObject(message.getData().getString("response"));
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
        switch (opId) {
            case OP_GET_RANK_TITLE:{
                //pegar quiz
                int status = 0;
                //pegar categoria de quiz
                if(response.has("Status")) {
                    try {
                        status = response.getInt("Status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //pegar categoria de quiz
                if(response.has("Object")){
                    if(status == 200){
                        try {
                            Gson gson = new Gson();
                            JSONObject obj = response.getJSONObject("Object");

                            if(obj.has("itens")){
                                RankingTitleData[] data = gson.fromJson(obj.getString("itens"), RankingTitleData[].class);

                                if(data != null && data.length > 0){
                                    List<RankingTitleData> rank = new ArrayList<>(Arrays.asList(data));
                                    initRank(rank);
                                }
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

    private void initRank(List<RankingTitleData> rank) {
        if(rank != null){
            adapterRankItem = new AdapterTitleRankingAdapter(activity, rank);
            mHolder.recyclerShopCategory.setAdapter(adapterRankItem);
            mHolder.recyclerShopCategory.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
        }
    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {

    }

    @Override
    public void bind() {
        super.bind();
    }
    void getRanking(){
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_RANK_TITLE, OP_GET_RANK_TITLE, this).execute();
    }

    class ViewHolder {
        RecyclerView recyclerShopCategory;
        TextView typeFan;
        public ViewHolder(View view) {
                recyclerShopCategory = view.findViewById(R.id.recycler_title_ranking);
            typeFan = activity.findViewById(R.id.typeFan);
        }
    }
}