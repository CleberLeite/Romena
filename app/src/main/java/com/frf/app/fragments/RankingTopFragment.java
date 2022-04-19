package com.frf.app.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.request.RequestOptions;
import com.frf.app.R;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.adapter.AdapterTopRanking;
import com.frf.app.data.RankData;
import com.frf.app.models.user.UserModel;
import com.frf.app.repository.user.UserRepository;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.UTILS;
import com.frf.app.vmodels.user.UserViewModel;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;


public class RankingTopFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {
    private ViewHolderTop mHolder;
    private static final int OP_GET_RANK = 1;
    int page = 1;
    AdapterTopRanking adapterRankItem;
    UserViewModel userViewModel;

    public RankingTopFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_ranking, container, false);
        mHolder = new ViewHolderTop(view);

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
            case OP_GET_RANK:{
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
                                RankData[] data = gson.fromJson(obj.getString("itens"), RankData[].class);

                                if(data != null && data.length > 0){
                                    List<RankData> rank = new ArrayList<>(Arrays.asList(data));
                                    initRank(rank, obj.getInt("margin"));
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

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {

    }
    @Override
    public void bind() {
        super.bind();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        UserRepository userRepository = new UserRepository(activity, ((StartMenuActivity)requireActivity()).getPref());
        userViewModel.setmRepository(userRepository);
        userViewModel.getUserData().observe(this, this::initHeader);
        userViewModel.getUser(false);

        adapterRankItem = null;
        page = 1;
        getRanking(page);
    }

    void getRanking(Integer page){

        Hashtable<String, Object> params = new Hashtable<>();

        if(page != null)
            params.put("page", page);

        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_RANK, OP_GET_RANK, this).execute(params);
    }

    void initRank (List<RankData> rank, int margin){

        if(rank != null){
            if(adapterRankItem == null){
                adapterRankItem = new AdapterTopRanking(activity, rank, margin, () -> {
                    page++;
                    getRanking(page);
                });

                mHolder.recyclerShopCategory.setAdapter(adapterRankItem);
                mHolder.recyclerShopCategory.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
            }else{
                adapterRankItem.addData(rank);
            }
        }
    }

    void initHeader(UserModel user) {
        UTILS.getImageAt(activity, user.getImg(), mHolder.imgUser, new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_placeholderuser)
                .error(R.drawable.ic_placeholderuser));

        mHolder.txtNomeUser.setText(user.getNickname());
        mHolder.typeFan.setText(user.getUserType());
        mHolder.txtCoinsRank.setText(user.getCoins() + "");
        mHolder.txtRanking.setText(user.getRank() + "");
    }

    class ViewHolderTop {
        RecyclerView recyclerShopCategory;
        TextView typeFan;
        TextView txtNomeUser;
        TextView txtCoinsRank;
        TextView txtRanking;
        ImageView imgUser;

        public ViewHolderTop(View view) {
            typeFan = activity.findViewById(R.id.typeFan);
            txtRanking = activity.findViewById(R.id.txtRanking);
            txtCoinsRank = activity.findViewById(R.id.txtCoinsRank);
            imgUser = activity.findViewById(R.id.imgUser);
            txtNomeUser = activity.findViewById(R.id.txt_nome_user);
            recyclerShopCategory = view.findViewById(R.id.recycler_top_ranking);
        }
    }
}