package com.frf.app.fragments;

import static com.frf.app.activitys.MainActivity.emptyAsync;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.frf.app.R;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.adapter.NoticiaAdapter;
import com.frf.app.data.CategoryData;
import com.frf.app.data.NoticiasData;
import com.frf.app.data.OrderItemData;
import com.frf.app.repository.news.NewsRepository;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.frf.app.views.CustomHorizontalScrollView;
import com.frf.app.vmodels.fragments.FragmentHolderViewModel;
import com.frf.app.vmodels.news.NewsViewModel;
import com.gigamole.library.ShadowLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NewsFragment extends MainFragment {

    private static final int REQUEST_ITEM_NEWS = 1;
    private ViewHolder mHolder;
    private String itemClick = "";
    private String idOrdem = "";
    private int position = -1;
    private int idCat = -1;
    private boolean isAnimate = true;
    private boolean isAnimate2 = false;
    private NewsViewModel newsViewModel;
    private FragmentHolderViewModel mfragmentViewModel;

    private NoticiaAdapter adapter;
    private ArrayList<CategoryData> categoryData;

    boolean trigger = true;
    boolean isInit = false;
    boolean isPage = false;
    boolean toggleCat = true;
    boolean hasBookmarked = false;
    boolean isPush = false;

    int maxChip;
    int idNews = -1;

    public NewsFragment() {
    }

    public NewsFragment(int idNews) {
        this.idNews = idNews;
    }

    public NewsFragment(int idNews, boolean isPush) {
        this.idNews = idNews;
        this.isPush = isPush;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        activity.setTheme(R.style.AppThemeMaterialComponents);
        mHolder = new ViewHolder(view);

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        maxChip = 40 * dm.heightPixels / 100;

        initActions();
        return view;
    }

    @Override
    public void bind() {
        super.bind();
        mfragmentViewModel = new ViewModelProvider(requireActivity()).get(FragmentHolderViewModel.class);
        isPage = false;
        NewsRepository newsRepository = new NewsRepository(activity);
        newsViewModel = new ViewModelProvider(requireActivity()).get(NewsViewModel.class);
        newsViewModel.setmRepository(newsRepository);

        newsViewModel.clearData();

        newsViewModel.getHome().observe(this, jsonObject -> {
            mHolder.msg.setText("");
            hasBookmarked = jsonObject.has("hasBookmarked");

            if(jsonObject.has("itens")) {
                initNews(jsonObject);
            }
            if(jsonObject.has("orderItens")) {
                mHolder.holderDropDown.removeAllViews();
                initArenaOrderItens(jsonObject);
            }
            if(jsonObject.has("categories")) {
                if (categoryData == null) {
                    initArenaCategories(jsonObject);
                }
            }
        });

        newsViewModel.getMessage().observe(this, messageModel -> {
            if (adapter == null) {
                initNoticias(new ArrayList<>(), 0);
                mHolder.msg.setText(messageModel.getMsg());
            }else {
                if (!isPage) {
                    initNoticias(new ArrayList<>(), 0);
                    mHolder.msg.setText(messageModel.getMsg());
                }
            }
        });

        if (!trigger) {
            trigger = true;
            mHolder.holderDropDown.removeAllViews();
        }

        ((StartMenuActivity)getActivity()).changeHeader(1);

        toggleCat = true;
        newsViewModel.getItens("1", idCat+"", "", idOrdem, "");

    }

    @Override
    public void unbind() {
        super.unbind();
        categoryData = null;
    }

    private void initArenaCategories(JSONObject data) {
        try {
            JSONArray categories = data.getJSONArray("categories");

            ArrayList<CategoryData> categoryData = new ArrayList<>();
            for (int i = 0; i < categories.length(); i++) {
                JSONObject item = categories.getJSONObject(i);
                CategoryData gData = new Gson().fromJson(item.toString(), CategoryData.class);
                categoryData.add(gData);
            }
            this.categoryData = categoryData;
            CallChip(categoryData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initArenaOrderItens(JSONObject data) {
        try {
            JSONArray orderItens = data.getJSONArray("orderItens");
            ArrayList<OrderItemData> orderItemData = new ArrayList<>();
            for (int i = 0; i < orderItens.length(); i++) {
                JSONObject item = orderItens.getJSONObject(i);
                OrderItemData gData = new Gson().fromJson(item.toString(), OrderItemData.class);
                orderItemData.add(gData);
            }
            //mHolder.holderDropDown.setVisibility(View.GONE);
            initDropdown(orderItemData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initNews(JSONObject response) {
        try {
            Gson gson = new Gson();
            NoticiasData[] data = gson.fromJson(response.getString("itens"), NoticiasData[].class);
            if (data != null && data.length > 0) {
                List<NoticiasData> news = new ArrayList<>(Arrays.asList(data));
                initNoticias(news, response.getInt("margin"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initNoticias(List<NoticiasData> noticias, int margin) {
        if (!isPage) {
            adapter = new NoticiaAdapter(activity, noticias, margin, new NoticiaAdapter.Listener() {
                @Override
                public void isPressed(NoticiasData data) {
                    new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NEWS_OPEN_NEWS, data.getId()).execute();
                    isPage = false;
                    mfragmentViewModel.changeFragment(new NewsItemFragment(data.getId()), "NewsExpandedFragment", 5);
                }

                @Override
                public void onBottom(int page) {
                    newsViewModel.getItens(page+"", idCat+"", "", idOrdem, "");
                }
            }, false);

            mHolder.recicleNewsAll.setAdapter(adapter);
            mHolder.recicleNewsAll.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
            isPage = true;

            mHolder.pullToRefresh.setColorSchemeColors(Color.parseColor("#0B3575"), Color.parseColor("#0B3575"), Color.parseColor("#0B3575"));
            mHolder.pullToRefresh.setOnRefreshListener(() -> {
                isPage = false;
                if (idCat > -2) {
                    newsViewModel.getItens("1", idCat + "", "", idOrdem, "");
                }else if (idCat == -2) {
                    newsViewModel.getItens("1", "", "1", idOrdem, "");
                }else {
                    newsViewModel.getItens("1", "", "", idOrdem, "1");
                }
                mHolder.pullToRefresh.setRefreshing(false);
            });
        }else {
            for (NoticiasData data : noticias) {
                adapter.addData(data);
            }
        }

        if (idNews != -1 && isPush) {
            isPage = false;
            isPush = false;
            mfragmentViewModel.changeFragment(new NewsItemFragment(idNews, true), "NewsExpandedFragment", 5);
            idNews = -1;
        }
    }

    void initActions() {

        mHolder.shadonwBtn.setOnClickListener(v -> {
            if (mHolder.arrow_drop.getRotation() == 0) {
                mHolder.arrow_drop.setRotation(180);
                mHolder.shadonwBtn.animate()
                        .alpha(1f)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                mHolder.shadonwBtn.setVisibility(View.VISIBLE);
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
                mHolder.shadonwBtn.animate()
                        .alpha(0.0f)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mHolder.shadonwBtn.setVisibility(View.GONE);
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
                mHolder.shadonwBtn.animate()
                        .alpha(1f)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                mHolder.shadonwBtn.setVisibility(View.VISIBLE);
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
                mHolder.shadonwBtn.animate()
                        .alpha(0.0f)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mHolder.shadonwBtn.setVisibility(View.GONE);
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
                        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NEWS_ORDER_NEWS, idOrdem).execute();
                    }
                    newsViewModel.getItens("1", idCat+"", "", idOrdem, "");
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

    void CallChip(ArrayList<CategoryData> data) {

        mHolder.holderChip.removeAllViews();
        mHolder.holderChip2.removeAllViews();

        boolean isAddMonitored = false;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getSelected() == 1) {
                if (data.get(i).getId() >= 0) {
                    isAddMonitored = true;
                    break;
                }
            }

            if (i == data.size() -1) {
                for (int y = 0; y < data.size(); y++) {
                    if (data.get(y).getId() == -2) {
                        data.remove(y);
                        break;
                    }
                }
            }
        }

        for (int y = 0; y < data.size(); y++) {
            if (data.get(y).getId() == -2) {
                isAddMonitored = false;
                break;
            }
        }

        boolean isAddToate = true;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId() == -1) {
                isAddToate = false;
                break;
            }
        }

        boolean isAddPlus = true;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId() == -5) {
                isAddPlus = false;
                break;
            }
        }

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId() == -3) {
                data.remove(i);
                break;
            }
        }

        if (isAddToate) {
            data.add(0, new CategoryData(-1, 1, "Toate"));
        }

        if (isAddMonitored) {
            data.add(1, new CategoryData(-2, 1, "Urmãrite"));
        }

        if (hasBookmarked) {
            data.add(1, new CategoryData(-3, 1, "Marcat"));
        }

        if (isAddPlus) {
            data.add(new CategoryData(-5, -1, "Toate Subiectele + "));
        }

        Comparator<CategoryData> comparadorPorFav = (o1, o2) -> Integer.compare(o2.getSelected(), o1.getSelected());
        try{Collections.sort(data, comparadorPorFav);}catch (Exception e){e.printStackTrace();}

        ArrayList<CategoryData> data1 = new ArrayList<>();

        int count = 0;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId() >= 0 && data.get(i).getSelected() == 0 && idCat != data.get(i).getId()) {
                if (count <= 4) {
                    data1.add(data.get(i));
                    count++;
                }
            }else {
                if (idCat == data.get(i).getId()) {
                    data1.add(0,data.get(i));
                }else {
                    data1.add(data.get(i));
                }
            }
        }

        for (int i=0; i < data1.size(); i++) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.single_chip_layout, mHolder.holderChip, false);
            int paddingDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

            chip.setPadding(15, paddingDp, 15, paddingDp);
            if (i == 0) {
                ChipGroup.LayoutParams lp = new ChipGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMarginStart(UTILS.convertDpToPixels(20));
                chip.setLayoutParams(lp);
            }
            chip.setHeight(66);
            chip.setText(data1.get(i).getTitle());

            if (i != data1.size() - 1) {
                if (idCat == data1.get(i).getId()) {
                    chip.setChipStrokeColorResource((R.color.PrimaryColorHint));
                    chip.setChipStrokeWidth(1f);
                    chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.PrimaryColorHint)));
                    chip.setTextColor((ContextCompat.getColor(activity, R.color.ColorBlueGray800)));
                } else {
                    chip.setChipStrokeColorResource((R.color.PrimaryColorHint));
                    chip.setChipStrokeWidth(2f);
                    chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.PrimaryBackground)));
                    chip.setTextColor((ContextCompat.getColor(activity, R.color.PrimaryText)));
                }
            } else {
                chip.setChipStrokeColorResource(R.color.ColorBlue);
                chip.setChipStrokeWidth(1f);
                chip.setTextColor(Color.parseColor("#FFFFFF"));
                chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.ColorBlue)));
            }

            int finalI = i;

            if (idCat == data1.get(finalI).getId()) {
                if (idCat >= 0) {
                    if (data1.get(finalI).getSelected() == 0) {
                        mHolder.llFollowCate.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.PrimaryText));
                        mHolder.txtFollow.setTextColor(activity.getResources().getColor(R.color.ColorBlue));
                        mHolder.imgFollow.setBackground(activity.getResources().getDrawable(R.drawable.ic_checked_blue));
                        mHolder.llFollowCate.setOnClickListener(v12 -> {
                            newsViewModel.setFollowNewsCategory(idCat);
                            data.get(finalI).setSelected(1);
                            categoryData = data;
                            isAnimate = false;
                            isAnimate2 = false;
                            CallChip(data);
                        });
                    } else {
                        mHolder.llFollowCate.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.ColorBlue));
                        mHolder.txtFollow.setTextColor(activity.getResources().getColor(R.color.PrimaryText));
                        mHolder.imgFollow.setBackground(activity.getResources().getDrawable(R.drawable.ic_mais));
                        mHolder.llFollowCate.setOnClickListener(v1 -> {
                            newsViewModel.setFollowNewsCategory(idCat);
                            data.get(finalI).setSelected(0);
                            categoryData = data;
                            isAnimate = false;
                            isAnimate2 = false;
                            CallChip(data);
                        });
                    }

                    mHolder.llFollowCate.setVisibility(View.VISIBLE);
                }else {
                    mHolder.llFollowCate.setVisibility(View.GONE);
                }
            }

            chip.setOnClickListener(v -> {

                if (data1.get(finalI).getId() > -5) {

                    if (data1.get(finalI).getId() != idCat) {
                        idCat = data1.get(finalI).getId();

                        isPage = false;
                        if (idCat > -2) {
                            newsViewModel.getItens("1", idCat + "", "", idOrdem, "");
                        }else if (idCat == -2) {
                            newsViewModel.getItens("1", "", "1", idOrdem, "");
                        }else {
                            newsViewModel.getItens("1", "", "", idOrdem, "1");
                        }

                        if (idCat >= 0) {
                            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NEWS_CHANGE_CATEGORY, idCat).execute();
                            toggleCat = true;
                            mHolder.llFollowCate.setVisibility(View.VISIBLE);

                            if (data1.get(finalI).getSelected() == 0) {
                                mHolder.llFollowCate.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.ColorBlue));
                                mHolder.txtFollow.setTextColor(activity.getResources().getColor(R.color.PrimaryText));
                                mHolder.imgFollow.setBackground(activity.getResources().getDrawable(R.drawable.ic_mais));
                                mHolder.llFollowCate.setOnClickListener(v12 -> {
                                    new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NEWS_FOLLOW_CATEGORY, idCat).execute();
                                    newsViewModel.setFollowNewsCategory(idCat);
                                    data.get(finalI).setSelected(1);
                                    categoryData = data;
                                    isAnimate = false;
                                    isAnimate2 = false;
                                    CallChip(data);
                                });
                                CallChip(data);
                            } else {
                                mHolder.llFollowCate.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.PrimaryText));
                                mHolder.txtFollow.setTextColor(activity.getResources().getColor(R.color.ColorBlue));
                                mHolder.imgFollow.setBackground(activity.getResources().getDrawable(R.drawable.ic_checked_blue));
                                mHolder.llFollowCate.setOnClickListener(v1 -> {
                                    new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NEWS_UNFOLLOW_CATEGORY, idCat).execute();
                                    newsViewModel.setFollowNewsCategory(idCat);
                                    data.get(finalI).setSelected(0);
                                    categoryData = data;
                                    isAnimate = false;
                                    isAnimate2 = false;
                                    CallChip(data);
                                });
                                CallChip(data);
                            }

                            isAnimate = false;
                            CallChip(data);

                        } else {
                            switch (idCat) {
                                case -1:
                                case -2:
                                case -3:    {
                                    toggleCat = true;
                                    CallChip(data);
                                }
                                break;
                            }
                        }

                    }
                }else {
                    toggleCat = false;
                    data.remove(data.size()-1);
                    data.add(new CategoryData(-5, -1, "Mai Puține Subiecte - "));
                    categoryData = data;
                    isAnimate2 = true;
                    CallChip(data);
                }

            });

            Typeface typeface = ResourcesCompat.getFont(activity, R.font.exo_2_bold);
            chip.setTypeface(typeface);
            mHolder.holderChip.addView(chip);
        }

        try{mHolder.scrollChip.fullScroll(0);}catch (Exception e){e.printStackTrace();}

        for (int i=0; i < data.size(); i++) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.single_chip_layout, mHolder.holderChip2, false);
            int paddingDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            // chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
            chip.setPadding(15, paddingDp, 15, paddingDp);
            chip.setHeight(66);
            chip.setText(data.get(i).getTitle());

            if (i != data.size() - 1) {
                if (idCat == data.get(i).getId()) {
                    chip.setChipStrokeColorResource((R.color.PrimaryColorHint));
                    chip.setChipStrokeWidth(1f);
                    chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.PrimaryColorHint)));
                    chip.setTextColor((ContextCompat.getColor(activity, R.color.ColorBlueGray800)));
                } else {
                    chip.setChipStrokeColorResource((R.color.PrimaryColorHint));
                    chip.setChipStrokeWidth(2f);
                    chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.PrimaryBackground)));
                    chip.setTextColor((ContextCompat.getColor(activity, R.color.PrimaryText)));
                }
            } else {
                chip.setChipStrokeColorResource(R.color.ColorBlue);
                chip.setChipStrokeWidth(1f);
                chip.setTextColor(Color.parseColor("#FFFFFF"));
                chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.ColorBlue)));
            }

            int finalI = i;

            if (idCat == data.get(finalI).getId()) {
                if (idCat >= 0) {
                    if (data.get(finalI).getSelected() == 0) {
                        mHolder.llFollowCate.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.ColorBlue));
                        mHolder.txtFollow.setTextColor(activity.getResources().getColor(R.color.PrimaryText));
                        mHolder.imgFollow.setBackground(activity.getResources().getDrawable(R.drawable.ic_mais));
                        mHolder.llFollowCate.setOnClickListener(v12 -> {
                            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NEWS_FOLLOW_CATEGORY, idCat).execute();
                            newsViewModel.setFollowNewsCategory(idCat);
                            data.get(finalI).setSelected(1);
                            categoryData = data;
                            isAnimate = false;
                            isAnimate2 = false;
                            CallChip(data);
                        });
                    } else {
                        mHolder.llFollowCate.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.PrimaryText));
                        mHolder.txtFollow.setTextColor(activity.getResources().getColor(R.color.ColorBlue));
                        mHolder.imgFollow.setBackground(activity.getResources().getDrawable(R.drawable.ic_checked_blue));
                        mHolder.llFollowCate.setOnClickListener(v1 -> {
                            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NEWS_UNFOLLOW_CATEGORY, idCat).execute();
                            newsViewModel.setFollowNewsCategory(idCat);
                            data.get(finalI).setSelected(0);
                            categoryData = data;
                            isAnimate = false;
                            isAnimate2 = false;
                            CallChip(data);
                        });
                    }

                    mHolder.llFollowCate.setVisibility(View.VISIBLE);
                }else {
                    mHolder.llFollowCate.setVisibility(View.GONE);
                }
            }

            chip.setOnClickListener(v -> {

                if (data.get(finalI).getId() > -5) {
                    if (data.get(finalI).getId() != idCat) {
                        idCat = data.get(finalI).getId();

                        isPage = false;
                        if (idCat > -2) {
                            newsViewModel.getItens("1", idCat + "", "", idOrdem, "");
                        }else if (idCat == -2) {
                            newsViewModel.getItens("1", "", "1", idOrdem, "");
                        }else {
                            newsViewModel.getItens("1", "", "", idOrdem, "1");
                        }

                        if (idCat >= 0) {
                            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NEWS_CHANGE_CATEGORY, idCat).execute();
                            toggleCat = true;
                            mHolder.llFollowCate.setVisibility(View.VISIBLE);
                            if (data.get(finalI).getSelected() == 0) {
                                mHolder.llFollowCate.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.PrimaryText));
                                mHolder.txtFollow.setTextColor(activity.getResources().getColor(R.color.ColorBlue));
                                mHolder.imgFollow.setBackground(activity.getResources().getDrawable(R.drawable.ic_checked_blue));
                                mHolder.llFollowCate.setOnClickListener(v12 -> {
                                    newsViewModel.setFollowNewsCategory(idCat);
                                    data.get(finalI).setSelected(1);
                                    categoryData = data;
                                    isAnimate2 = false;
                                    isAnimate = false;
                                    CallChip(data);
                                });
                            } else {
                                mHolder.llFollowCate.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.ColorBlue));
                                mHolder.txtFollow.setTextColor(activity.getResources().getColor(R.color.PrimaryText));
                                mHolder.imgFollow.setBackground(activity.getResources().getDrawable(R.drawable.ic_mais));
                                mHolder.llFollowCate.setOnClickListener(v1 -> {
                                    newsViewModel.setFollowNewsCategory(idCat);
                                    data.get(finalI).setSelected(0);
                                    categoryData = data;
                                    isAnimate2 = false;
                                    isAnimate = false;
                                    CallChip(data);
                                });
                            }

                            data.remove(data.size() - 1);
                            data.add(new CategoryData(-5, -1, "Toate Subiectele + "));
                            categoryData = data;
                            isAnimate = true;
                            CallChip(data);

                        } else {
                            switch (idCat) {
                                case -1:
                                case -2:
                                case -3:    {
                                    toggleCat = true;
                                    isAnimate = true;
                                    data.remove(data.size() - 1);
                                    data.add(new CategoryData(-5, -1, "Toate Subiectele + "));
                                    CallChip(data);
                                }
                                break;
                            }
                        }

                    }
                }else {
                    toggleCat = true;
                    data.remove(data.size()-1);
                    data.add(new CategoryData(-5, -1, "Toate Subiectele + "));
                    categoryData = data;
                    isAnimate = true;
                    CallChip(data);
                }

            });


            Typeface typeface = ResourcesCompat.getFont(activity, R.font.exo_2_bold);
            chip.setTypeface(typeface);
            mHolder.holderChip2.addView(chip);
        }

        if (toggleCat) {
            mHolder.scrollCat.setVisibility(View.GONE);

            if (isAnimate && mHolder.holderChip.getVisibility() == View.GONE) {
                ValueAnimator anim = ValueAnimator.ofInt(mHolder.holderChip2.getMeasuredHeight(), UTILS.convertDpToPixels(50));
                anim.addUpdateListener(valueAnimator -> {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = mHolder.holderChip.getLayoutParams();
                    layoutParams.height = val;
                    mHolder.holderChip.setLayoutParams(layoutParams);
                });
                anim.setDuration(400);
                anim.start();
            }else {
                isAnimate = false;
            }
            mHolder.holderChip.setVisibility(View.VISIBLE);
            mHolder.holderChip2.setVisibility(View.GONE);
        }else {
            mHolder.holderChip2.setVisibility(View.VISIBLE);

            if (isAnimate2) {
                ViewTreeObserver observer= mHolder.holderChip2.getViewTreeObserver();
                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ValueAnimator anim;
                        if (mHolder.holderChip2.getMeasuredHeight() > maxChip) {
                            anim = ValueAnimator.ofInt(UTILS.convertDpToPixels(50), maxChip);
                        }else {
                            anim = ValueAnimator.ofInt(UTILS.convertDpToPixels(50), mHolder.holderChip2.getMeasuredHeight());
                        }
                        anim.addUpdateListener(valueAnimator -> {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = mHolder.scrollCat.getLayoutParams();
                            layoutParams.height = val;
                            mHolder.scrollCat.setLayoutParams(layoutParams);
                        });
                        anim.setDuration(400);
                        anim.start();
                        observer.removeGlobalOnLayoutListener(this);
                    }
                });
            }else {
                isAnimate2 = false;
            }

            mHolder.holderChip.setVisibility(View.GONE);
            mHolder.scrollCat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ITEM_NEWS) {
             if(data != null){
                int frag = data.getExtras().getInt("frag");
                switch (frag) {
                    case 1: {
                    }
                    break;
                    case 2: {
                        StartMenuActivity startMenuActivity = new StartMenuActivity();
                        startMenuActivity.changeCurrentScreen(StartMenuActivity.Screen.Arena);
                        mfragmentViewModel.changeFragment(new ArenaFragment(), "ArenaFragment", 3);
                    }
                    break;
                    case 3: {
                        mfragmentViewModel.changeFragment(new NewsFragment(), "NewsFragment", 3);
                    }
                    break;
                    case 4: {
                        mfragmentViewModel.changeFragment(new VideosFragment(), "VideosFragment", 3);
                    }
                    break;
                    case 5: {
                        mfragmentViewModel.changeFragment(new ShopFragment(), "ShopFragment", 3);
                    }
                    break;
                }
            }
        }
    }

    static class ViewHolder {
        RecyclerView recicleNewsAll;
        View llNewsCard;
        ChipGroup holderChip;
        ChipGroup holderChip2;
        CustomHorizontalScrollView scrollChip;
        LinearLayout holderDropDown;
        LinearLayout dropDown;
        TextView txt_drop;
        ImageView arrow_drop;
        ShadowLayout shadonwBtn;
        CardView llFollowCate;
        TextView txtFollow;
        ImageView imgFollow;
        ScrollView scrollCat;
        TextView msg;
        SwipeRefreshLayout pullToRefresh;

        public ViewHolder(View view) {
            recicleNewsAll = view.findViewById(R.id.recicleNewsAll);
            holderChip = view.findViewById(R.id.header_holder_chip);
            scrollChip = view.findViewById(R.id.scroll_chip);
            holderChip2 = view.findViewById(R.id.header_holder_chip_2);
            scrollCat = view.findViewById(R.id.scrollCat);
            holderDropDown = view.findViewById(R.id.holder_dropdown);
            dropDown = view.findViewById(R.id.btn_dropdown);
            txt_drop = view.findViewById(R.id.txt_dropdown);
            arrow_drop = view.findViewById(R.id.arrow);
            shadonwBtn = view.findViewById(R.id.shadonwBtn);
            llNewsCard = view.findViewById(R.id.ll_news);
            pullToRefresh = view.findViewById(R.id.pullToRefresh);
            llFollowCate = view.findViewById(R.id.llFollowCate);
            txtFollow = view.findViewById(R.id.txtFollow);
            imgFollow = view.findViewById(R.id.imgFollow);
            msg = view.findViewById(R.id.msg);
        }
    }
}