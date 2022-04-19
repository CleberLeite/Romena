package com.frf.app.fragments;

import static com.frf.app.activitys.MainActivity.emptyAsync;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.adapter.RewardsAdapter;
import com.frf.app.data.BadgeData;
import com.frf.app.repository.badges.BadgeRepository;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.frf.app.vmodels.badges.BadgeViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.List;

public class TrophiesFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {
    private ViewHolder mHolder;
    RewardsAdapter adapter;
    private BadgeViewModel badgeViewModel;
    private int page = 1;
    private boolean lastData = false;
    private static final int OP_ID_BADGE_DETAIL = 0;

    public TrophiesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trophies, container, false);
        mHolder = new ViewHolder(view);
        return view;
    }

    private final Handler handlerOp = new Handler(message -> {
        int opId = -1;
        JSONObject response;
        boolean success = false;

        try {
            opId = message.getData().getInt("opId");
            response = new JSONObject(message.getData().getString("response"));
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
            case OP_ID_BADGE_DETAIL: {
                //Noticia
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
                            JSONObject obj = response.getJSONObject("Object");
                              showDialog(obj.getString("name"),
                                      obj.getString("description"),
                                      obj.getString("img"),
                                      obj.getString("prize"),
                                      obj.getInt("ocurrenceActual"),
                                      obj.getInt("occurrenceNeeded"));
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

        BadgeRepository badgeRepository = new BadgeRepository(activity);
        badgeViewModel = new ViewModelProvider(requireActivity()).get(BadgeViewModel.class);
        badgeViewModel.setmRepository(badgeRepository);
        badgeViewModel.getBadges().observe(this, badges -> {
            if(badges != null){
                initBadges(badges);
            }else {
                lastData = true;
            }
        });

        adapter = null;
        page = 1;
        badgeViewModel.getBadges(1);

    }

    @Override
    public void unbind() {
        super.unbind();
        try{badgeViewModel.getBadges().removeObservers(this);}catch (Exception e){e.printStackTrace();}
        page = 1;
        lastData = false;
    }

    void initBadges (List<BadgeData> data){
        if(adapter == null){
            lastData = false;
            page = 1;
            adapter = new RewardsAdapter(activity, data, new RewardsAdapter.Listener() {
                @Override
                public void OnClick(BadgeData data) {
                    new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_BADGES_AND_AWARDS_BADGE_DETAILS, data.getId()).execute();
                    getBadgeDetails(data.getId());
                }

                @Override
                public void OnEndOfList() {
                    if(!lastData){
                        page++;
                        badgeViewModel.getBadges(page);
                    }
                }
            });
            mHolder.recyclerView.setAdapter(adapter);
            mHolder.recyclerView.setLayoutManager(new GridLayoutManager(activity, 4));
        }else{
            if(data != null){
                adapter.addBadges(data);
            }
        }
    }
    void getBadgeDetails(int id){
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("badgeId", id);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_BADGE_DETAIL, OP_ID_BADGE_DETAIL, this).execute(params);
    }

    @SuppressLint("SetTextI18n")
    public void showDialog(String name, String description, String img, String prize, int ocurrenceActual, int occurrenceNeeded) {
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        dialog.setCancelable(true);

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_informative_awards, null);
        dialog.setContentView(view);

        TextView descricao = view.findViewById(R.id.descricao);
        TextView progress = view.findViewById(R.id.progress);
        TextView nome = view.findViewById(R.id.nome);
        TextView mprize = view.findViewById(R.id.prize);
        descricao.setText(description);
        nome.setText(name);
        mprize.setText(prize);

        progress.setText(ocurrenceActual + "/" + occurrenceNeeded);

        UTILS.getImageAt(activity, img, view.findViewById(R.id.imageBadge));
        try{dialog.show();}catch (Exception e){e.printStackTrace();}

        view.findViewById(R.id.menu_itm_dialog_ranking).setOnClickListener(view2 -> {
            Animation animation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    try{dialog.dismiss();}catch (Exception e){e.printStackTrace();}
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            view.startAnimation(animation);
        });

        view.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in));
    }

    static class ViewHolder {
        RecyclerView recyclerView;
        public ViewHolder(View view) {
            recyclerView = view.findViewById(R.id.recycler_rewards);
        }
    }
}