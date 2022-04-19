package com.frf.app.fragments;

import static com.frf.app.activitys.MainActivity.emptyAsync;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.adapter.MatchCompetitionsAdapter;
import com.frf.app.data.MatchCompetitionsData;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.frf.app.vmodels.fragments.FragmentHolderViewModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;


public class EventsFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {
    private ViewHolder mHolder;
    private FragmentHolderViewModel mfragmentViewModel;
    private static final int OP_GET_MATCH_COMPETITIONS = 0;
    boolean isPage = false;
    boolean isCallPage = true;
    MatchCompetitionsAdapter adapter;

    public EventsFragment(FragmentHolderViewModel fragmentHolderViewModel) {
        mfragmentViewModel = fragmentHolderViewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        mHolder = new ViewHolder(view);
        isPage = false;
        getMatchCompetitions(1);

        try {
            ((StartMenuActivity)requireActivity()).resetBottomMenu();
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    private void getMatchCompetitions(int page){
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("page", page);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_MATCH_COMPETITIONS, OP_GET_MATCH_COMPETITIONS, this).execute(params);
    }

    private final Handler handlerOp = new Handler(message -> {
        int opId = -1;
        JSONObject response;
        boolean success = false;

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
            case OP_GET_MATCH_COMPETITIONS: {
                int status = 0;
                boolean success = false;

                if (response.has("Status")) {
                    try {
                        status = (int) response.get("Status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (status == 200) {
                    if (response.has("Object")) {
                        try {
                            Gson gson = new Gson();
                            JSONObject OBJ = response.getJSONObject("Object");

                            int limit = OBJ.getInt("limit");
                            int margin = OBJ.getInt("margin");

                            MatchCompetitionsData[] data = gson.fromJson(OBJ.getString("itens"), MatchCompetitionsData[].class);

                            if (data != null) {
                                if (!isPage) {
                                    List<MatchCompetitionsData> competitionsData = new ArrayList<>(Arrays.asList(data));
                                    adapter = new MatchCompetitionsAdapter(activity, competitionsData, limit, margin, new MatchCompetitionsAdapter.Listener() {
                                        @Override
                                        public void onPressed(MatchCompetitionsData data) {
                                            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_COMPETITIONS_OPEN_COMPATITION, data.getId()).execute();
                                            isPage = false;
                                            isCallPage = true;
                                            mfragmentViewModel.changeFragment(new EventsMatchesFragment(data.getId(), mfragmentViewModel, data.getTitle()), "EventsMatchesFragment", 3);
                                        }

                                        @Override
                                        public void onMargin(int page) {
                                            if (isCallPage) {
                                                getMatchCompetitions(page);
                                            }
                                        }
                                    });
                                    mHolder.recyclerView.setAdapter(adapter);
                                    mHolder.recyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
                                    isPage = true;
                                }else {
                                    for (MatchCompetitionsData data1 : data) {
                                        adapter.addItem(data1);
                                    }
                                }
                            }else {
                                isCallPage = false;
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
        Toast.makeText(activity, R.string.nao_foi_possivel_carregar_a_lista, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bind() {
        super.bind();
        mfragmentViewModel = new ViewModelProvider(requireActivity()).get(FragmentHolderViewModel.class);
        ((StartMenuActivity)getActivity()).changeHeader(1);
        ((StartMenuActivity)getActivity()).setCompetitionsIsOpen(true);
    }

    @Override
    public void unbind() {
        super.unbind();
        ((StartMenuActivity)getActivity()).setCompetitionsIsOpen(false);
    }

    static class ViewHolder {
        RecyclerView recyclerView;
        public ViewHolder(View view) {
            recyclerView = view.findViewById(R.id.events_recycler);
        }
    }
}