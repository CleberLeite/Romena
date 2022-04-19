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
import com.frf.app.adapter.TabIeItensAdapter;
import com.frf.app.data.GroupData;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.UTILS;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Objects;

public class ClassificationFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {

    private static final int OP_POST = 0;
    private ViewHolder mHolder;
    private int id;
    public ClassificationFragment(int id) {
        this.id = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classification, container, false);
        mHolder = new ViewHolder(view);
        getCompetitionTables(id);
        return view;
    }


    private final Handler handlerOp = new Handler(message -> {
        int opId = -1;
        JSONObject response;
        boolean success = false;

        try {
            opId = message.getData().getInt("opId");
            response = new JSONObject(Objects.requireNonNull(message.getData().getString("response")));
            success = message.getData().getBoolean("success");
        } catch (JSONException e) {
            UTILS.DebugLog("Error", "Error getting handlers params.");
            return false;
        }

        if (success) {
            OnAsyncOperationSuccess(opId, response);
        } else {
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
            case OP_POST:{
                int status = 0;
                if(response.has("Status")) {
                    try {
                        status = response.getInt("Status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(response.has("Object")){
                    if(status == 200){
                        try {
                            Gson gson = new Gson();
                            JSONObject obj = response.getJSONObject("Object");
                            if(obj.has("group")){
                                mHolder.title.setVisibility(View.VISIBLE);
                                GroupData[] data = gson.fromJson(obj.getString("group"), GroupData[].class);
                                TabIeItensAdapter adapter = new TabIeItensAdapter(activity, Arrays.asList(data), id -> {

                                });
                                mHolder.competitionTables.setAdapter(adapter);
                                mHolder.competitionTables.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
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
    public void OnAsyncOperationError(int opId, JSONObject response) {

    }

    private void getCompetitionTables(int id) {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("idCompetition", id);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_COMPETITION_TABLES, OP_POST, this).execute(params);
    }

    static class ViewHolder {
        RecyclerView competitionTables;
        TextView title;
        public ViewHolder(View view) {
            competitionTables = view.findViewById(R.id.competitionTables);
            title = view.findViewById(R.id.title);
        }
    }
}