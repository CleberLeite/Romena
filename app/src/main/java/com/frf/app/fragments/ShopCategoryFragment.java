package com.frf.app.fragments;

import static com.frf.app.activitys.MainActivity.emptyAsync;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.adapter.ShopItensAdapter;
import com.frf.app.data.OrderItemData;
import com.frf.app.data.ShopProductData;
import com.frf.app.repository.shop.ShopRepository;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.vmodels.fragments.FragmentHolderViewModel;
import com.frf.app.vmodels.shop.ShopViewModel;
import com.gigamole.library.ShadowLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class ShopCategoryFragment extends MainFragment {

    private ViewHolder mHolder;
    private String itemClick = "";
    private String txtCat = "";
    private String idOrdem = "";
    private int position = -1;
    private int cat = 0;
    boolean isInit = false;
    boolean trigger = false;
    private ArrayList<OrderItemData> arrayTeste;
    private FragmentHolderViewModel mfragmentViewModel;
    private ShopViewModel shopViewModel;
    private ShopItensAdapter adapter;

    public ShopCategoryFragment() {
    }

    public ShopCategoryFragment(int id, String txtCat) {
        cat = id;
        this.txtCat = txtCat;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_category, container, false);
        mHolder = new ViewHolder(view);
        initActions();

        arrayTeste = new ArrayList<>();

        JSONArray orderItens = null;
        try {
            orderItens = new JSONArray("[{\"id\":9,\"title\":\"Cele mai noi\"},{\"id\":7,\"title\":\"Cele mai populare\"}]");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        arrayTeste.clear();
        mHolder.holderDropDown.removeAllViews();
        for (int i = 0; i < orderItens.length(); i++) {
            JSONObject item = null;
            try {
                item = orderItens.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OrderItemData gData = new Gson().fromJson(item.toString(), OrderItemData.class);
            arrayTeste.add(gData);
        }

        initDropdown(arrayTeste);
        return view;
    }

    @Override
    public void bind() {
        super.bind();

        mfragmentViewModel = new ViewModelProvider(requireActivity()).get(FragmentHolderViewModel.class);

        ShopRepository shopRepository = new ShopRepository(activity);
        shopViewModel = new ViewModelProvider(this).get(ShopViewModel.class);
        shopViewModel.setmRepository(shopRepository);
        shopViewModel.getShop().observe(requireActivity(), this::initProducts);

        shopViewModel.getMessage().observe(requireActivity(), messageModel -> mHolder.animationLoad.setVisibility(View.GONE));
        shopViewModel.getError().observe(requireActivity(), errorModel -> {
            mHolder.animationLoad.setVisibility(View.GONE);
            mHolder.msgSearch.setText(errorModel.getMsg() != null ? errorModel.getMsg() : "");
            mHolder.msgSearch.setVisibility(View.VISIBLE);
        });

        if (!trigger) {
            trigger = true;
            mHolder.holderDropDown.removeAllViews();
        }

        shopViewModel.getItens(1, cat);

        mHolder.btnFilter.setOnClickListener(view -> {
            BottomSheetFilterFragment fragment = new BottomSheetFilterFragment(activity, new BottomSheetFilterFragment.Listener() {
                @Override
                public void onFilter(String minValue, String maxValue, String size) {

                }
            });
            fragment.show(getChildFragmentManager(), fragment.getTag());
        });


    }

    @Override
    public void unbind() {
        super.unbind();
        try{shopViewModel.getShop().removeObservers(this);}catch (Exception e){e.printStackTrace();}
        try{shopViewModel.getError().removeObservers(this);}catch (Exception e){e.printStackTrace();}
        try{shopViewModel.getMessage().removeObservers(this);}catch (Exception e){e.printStackTrace();}
        trigger = false;
    }

    void initActions() {

        mHolder.animationLoad.setVisibility(View.VISIBLE);

        mHolder.titleCat.setText(txtCat);

        mHolder.contentDrop.setOnClickListener(v -> {
            if (mHolder.arrow_drop.getRotation() == 0) {
                mHolder.arrow_drop.setRotation(180);
                mHolder.contentDrop.animate()
                        .alpha(1f)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                mHolder.contentDrop.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
            } else {
                mHolder.arrow_drop.setRotation(0);
                mHolder.contentDrop.animate()
                        .alpha(0.0f)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mHolder.contentDrop.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
            }
        });

        mHolder.dropDown.setOnClickListener(v -> {
            if (mHolder.arrow_drop.getRotation() == 0) {
                mHolder.arrow_drop.setRotation(180);
                mHolder.contentDrop.animate()
                        .alpha(1f)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                mHolder.contentDrop.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
            } else {
                mHolder.arrow_drop.setRotation(0);
                mHolder.contentDrop.animate()
                        .alpha(0.0f)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mHolder.contentDrop.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
            }
        });

        final Handler[] handlerSearch = {new Handler()};

        mHolder.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mHolder.animationLoad.getVisibility() == View.GONE) {
                    mHolder.recyclerView.setAdapter(null);
                    if (!charSequence.toString().isEmpty()) {
                        handlerSearch[0].removeCallbacksAndMessages(null);
                        handlerSearch[0].postDelayed(() -> shopViewModel.search(charSequence.toString()), 2000);
                    }else {
                        handlerSearch[0].removeCallbacksAndMessages(null);
                        shopViewModel.getItens(1, cat);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    void initProducts(ShopProductData[] data) {
        mHolder.animationLoad.setVisibility(View.GONE);
        mHolder.msgSearch.setVisibility(View.GONE);

        if (data != null && data.length > 0) {
            ShopItensAdapter adapter = new ShopItensAdapter(activity, new ArrayList<>(Arrays.asList(data)), id -> mfragmentViewModel.changeFragment(new CartFragment(), "CartFragment", 1));
            mHolder.recyclerView.setAdapter(adapter);
            mHolder.recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        }
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
                    if (!(item.getId()+"").equals("")) {
                        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_ARENA_ORDER_FEED, item.getId() + "").execute();
                    }

                    mHolder.dropDown.performClick();

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

    static class ViewHolder {
        View btnFilter;
        RecyclerView recyclerView;
        LinearLayout holderDropDown;
        EditText search;
        ShadowLayout contentDrop;
        LinearLayout dropDown;
        ConstraintLayout animationLoad;
        TextView txt_drop, titleCat, msgSearch;
        ImageView arrow_drop;

        public ViewHolder(View view) {
            btnFilter = view.findViewById(R.id.ll_filter);
            recyclerView = view.findViewById(R.id.recycler_loja);
            holderDropDown = view.findViewById(R.id.holder_dropdown);
            search = view.findViewById(R.id.edit_search);
            animationLoad = view.findViewById(R.id.animation_load);
            dropDown = view.findViewById(R.id.btn_dropdown);
            contentDrop = view.findViewById(R.id.shadonwBtn);
            txt_drop = view.findViewById(R.id.txt_dropdown);
            titleCat = view.findViewById(R.id.title_cat);
            arrow_drop = view.findViewById(R.id.arrow);
            msgSearch = view.findViewById(R.id.msg_search);
        }
    }
}