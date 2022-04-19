package com.frf.app.fragments;

import static com.frf.app.activitys.MainActivity.emptyAsync;
import static com.frf.app.activitys.MainActivity.prefs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.activitys.ProductActivity;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.adapter.AdapterLojaItens;
import com.frf.app.data.OrderItemData;
import com.frf.app.data.ShopItemData;
import com.frf.app.repository.user.UserRepository;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.frf.app.vmodels.user.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

public class ResaleFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {

    private ViewHolder mHolder;
    private String itemClick = "";
    private int position = -1;
    private int page = 1;

    private static final int OP_GET_PRIZES = 0;
    public static final int REQUEST_ITEM = 1;
    private UserViewModel userViewModel;

    private String idOrdem = "";

    AdapterLojaItens adapter;
    ActivityResultLauncher<Intent> someActivityResultLauncher;

    boolean isInit = false;
    boolean isPage = false;
    boolean trigger = true;

    int v = -1;

    public ResaleFragment() {
    }

    public ResaleFragment(int v) {
        this.v = v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data1 = result.getData();
                        if (data1.getBooleanExtra("us", false)) {
                            userViewModel.getUserProfile();
                            adapter = null;
                            getPrizes(Integer.parseInt(idOrdem), 1);
                        }
                    }
                });

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resale, container, false);
        mHolder = new ViewHolder(view);
        initActions();

        int size = mHolder.bottomNav.getMenu().size();
        for (int i = 0; i < size; i++) {
            mHolder.bottomNav.getMenu().getItem(i).setChecked(false);
        }

        getPrizes(-1, 1);
        return view;
    }

    private void getPrizes(int order, int page){
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("page", page);
        if (order != -1) {
            params.put("order", order);
        }
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_PRIZES, OP_GET_PRIZES, this).execute(params);
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
            case OP_GET_PRIZES: {
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
                            Gson gson = new Gson();
                            JSONObject obj = response.getJSONObject("Object");
                            ShopItemData[] data = gson.fromJson(obj.getString("itens"), ShopItemData[].class);
                            OrderItemData[] orderItemData = gson.fromJson(obj.getString("orderItens"), OrderItemData[].class);

                            mHolder.holderDropDown.removeAllViews();
                            initDropdown(new ArrayList<>(Arrays.asList(orderItemData)));
                            initPrizes(new ArrayList<>(Arrays.asList(data)), obj.getInt("margin"));

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
        UserRepository userRepository = new UserRepository(activity, prefs);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setmRepository(userRepository);
        userViewModel.getUserData().observe(this, ((StartMenuActivity) activity)::initHeader);
    }

    @Override
    public void unbind() {
        super.unbind();
        try{userViewModel.getUserData().removeObservers(this);}catch (Exception e){e.printStackTrace();}
    }

    void initPrizes(ArrayList<ShopItemData> list, int margin){

        Comparator<ShopItemData> comparationRansomed = (o1, o2) -> Integer.compare(o2.getRedeemed(), o1.getRedeemed());
        try{Collections.sort(list, comparationRansomed);}catch (Exception e){e.printStackTrace();}

        if (adapter == null) {
            adapter = new AdapterLojaItens(activity, list, margin, new AdapterLojaItens.Listener() {
                @Override
                public void onProductClick(ShopItemData data) {
                    new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_BADGES_AND_AWARDS_OPEN_AWARD, data.getId()).execute();
                    Intent intent = new Intent(activity, ProductActivity.class);
                    intent.putExtra("id", data.getId());
                    intent.putExtra("isMy", data.isRedeemed());
                    if (data.getIdCupom() != -1) {
                        intent.putExtra("idCupom", data.getIdCupom());
                    }

                    someActivityResultLauncher.launch(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);;
                }

                @Override
                public void onPage(int page) {
                    ResaleFragment.this.page = page;
                    getPrizes(idOrdem.equals("") ? -1 : Integer.parseInt(idOrdem), page);
                }
            });

            mHolder.recyclerView.setAdapter(adapter);
            mHolder.recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        }else {
            for (ShopItemData data : list) {
                adapter.addData(data);
            }
        }

        if (v != -1) {
            Intent intent = new Intent(activity, ProductActivity.class);
            for (ShopItemData item : list) {
                if (item.getId() == v) {
                    intent.putExtra("isMy", item.isRedeemed());
                    if (item.getIdCupom() != -1) {
                        intent.putExtra("idCupom", item.getIdCupom());
                    }
                    break;
                }
            }
            intent.putExtra("id", v);
            intent.putExtra("push", true);
            someActivityResultLauncher.launch(intent);
            v = -1;
        }
    }

    void initActions() {
        mHolder.dropDown.setOnClickListener(v -> {
            if (mHolder.arrow_drop.getRotation() == 0) {
                mHolder.arrow_drop.setRotation(180);
                mHolder.holderDropDown.setVisibility(View.VISIBLE);
            }else {
                mHolder.arrow_drop.setRotation(0);
                mHolder.holderDropDown.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void initDropdown(ArrayList<OrderItemData> itens) {
        TextView firstClick = null;
        int position = 0;

        for (OrderItemData item : itens) {
            View view = LayoutInflater.from(activity).inflate(R.layout.include_item_dropdown_left, mHolder.holderDropDown, false);
            TextView txt_item = view.findViewById(R.id.item);
            LinearLayout divider = view.findViewById(R.id.divider);

            if (position == 0) {
                txt_item.setBackground(activity.getResources().getDrawable(R.drawable.back_drop_left_top));
            } else if (position == itens.size() - 1) {
                txt_item.setBackground(activity.getResources().getDrawable(R.drawable.back_drop_bottom));
                divider.setVisibility(View.GONE);
            }

            txt_item.setText(item.getTitle());

            if (this.position == position) {
                if (itemClick.equals(item.getTitle())) {
                    txt_item.setTag(item);
                    txt_item.getBackground().setTint(activity.getResources().getColor(R.color.PrimaryBackground));
                    txt_item.setTextColor(activity.getResources().getColor(R.color.PrimaryText));
                } else {
                    this.position = -1;
                    itemClick = "";
                    txt_item.setTag(null);
                    txt_item.getBackground().setTint(activity.getResources().getColor(R.color.PrimaryText));
                    txt_item.setTextColor(activity.getResources().getColor(R.color.PrimaryBackground));
                }
            } else {
                txt_item.setTag(null);
                txt_item.getBackground().setTint(activity.getResources().getColor(R.color.PrimaryText));
                txt_item.setTextColor(activity.getResources().getColor(R.color.PrimaryBackground));
            }

            int finalPosition = position;
            txt_item.setOnClickListener(v -> {
                if (itemClick.equals("") || txt_item.getTag() == null) {
                    itemClick = item.getTitle();
                    idOrdem = item.getId()+"";
                }

                if (isInit) {
                    if (!(item.getId() + "").equals("")) {
                        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_BADGES_AND_AWARDS_ORDER_AWARD, item.getId() + "").execute();
                    }

                    if (!idOrdem.equals("") && !idOrdem.equals("-1")) {
                        adapter = null;
                        getPrizes(Integer.parseInt(idOrdem), 1);
                    }

                    mHolder.dropDown.performClick();
                    isPage = false;
                }else {
                    isInit = true;
                }

                if (itemClick.equals("")) {
                    mHolder.txt_drop.setText(itens.get(0).getTitle());
                } else {
                    mHolder.txt_drop.setText(itemClick);
                }

                mHolder.holderDropDown.removeAllViews();
                this.position = finalPosition;
                initDropdown(itens);
            });

            if (position == 0 && idOrdem.equals("")) {
                firstClick = txt_item;
            }else {
                if (String.valueOf(item.getId()).equals(idOrdem)) {
                    firstClick = txt_item;
                }
            }

            position++;

            mHolder.holderDropDown.addView(view);
        }

        if (trigger){
            trigger = false;
            isInit = false;
            firstClick.performClick();
        }
    }

    class ViewHolder {
        LinearLayout holderDropDown;
        LinearLayout dropDown;
        TextView txt_drop;
        ImageView arrow_drop;
        RecyclerView recyclerView;
        BottomNavigationView bottomNav;

        public ViewHolder(View view) {
            recyclerView = view.findViewById(R.id.recycler_loja);
            holderDropDown = view.findViewById(R.id.holder_dropdown);
            dropDown = view.findViewById(R.id.btn_dropdown);
            bottomNav = activity.findViewById(R.id.bottom_navigation);
            txt_drop = view.findViewById(R.id.txt_dropdown);
            arrow_drop = view.findViewById(R.id.arrow);
        }
    }
}