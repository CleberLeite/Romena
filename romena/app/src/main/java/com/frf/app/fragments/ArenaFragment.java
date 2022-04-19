package com.frf.app.fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import com.frf.app.R;
import com.frf.app.activitys.ArenaCreatePostActivity;
import com.frf.app.activitys.MainActivity;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.adapter.ArenaItensAdapter;
import com.frf.app.adapter.SlideAdapter;
import com.frf.app.data.CategoryData;
import com.frf.app.data.GalleryArenaData;
import com.frf.app.data.OrderItemData;
import com.frf.app.repository.arena.ArenaRepository;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.frf.app.views.CustomHorizontalScrollView;
import com.frf.app.vmodels.arena.ArenaViewModel;
import com.frf.app.vmodels.fragments.FragmentHolderViewModel;
import com.frf.app.vmodels.user.UserViewModel;
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
import java.util.Objects;

public class ArenaFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {

    private ViewHolder mHolder;
    private FragmentHolderViewModel mfragmentViewModel;
    private static final int OP_ID_GET_CATEGORIE_ALL = 0;

    private String itemClick = "";
    private String categoria = "";
    private String categorias = "";
    private String idOrdem = "";
    private int position = -1;
    private int idCat = 0;
    private ArenaViewModel arenaViewModel;
    private UserViewModel userViewModel;
    private ArrayList<CategoryData> categoryData;
    private ArenaItensAdapter adapterRankItem;

    boolean isPage = false;
    boolean trigger = false;
    boolean isInit = false;

    private boolean isAnimate = true;

    ArrayList<OrderItemData> orderItemData = new ArrayList<>();

    public ArenaFragment() {
    }

    public ArenaFragment(int id) {
        isPage = false;
        mfragmentViewModel.changeFragment(new ArenaViewPostFragment((id)), "ArenaViewPostFragment", 3);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arena, container, false);
        activity.setTheme(R.style.AppThemeMaterialComponents);
        mHolder = new ViewHolder(view);
        initActions();

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

            case OP_ID_GET_CATEGORIE_ALL: {
                if (response.has("Object")) {
                    try {
                        JSONObject object = response.getJSONObject("Object");
                        JSONArray itens = object.getJSONArray("itens");

                        if (itens.length() > 0) {
                            categorias = response.toString();
                            ArrayList<CategoryData> categoryData = new ArrayList<>();
                            for (int i = 0; i < itens.length(); i++) {
                                JSONObject item = itens.getJSONObject(i);
                                CategoryData gData = new Gson().fromJson(item.toString(), CategoryData.class);
                                categoryData.add(gData);
                            }

                            CategoryData toate = new CategoryData(0, 1, "Toate");
                            categoryData.add(toate);

                            try {
                                CallChip(categoryData);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            this.categoryData = categoryData;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {

    }

    @Override
    public void bind() {
        super.bind();
        ArenaRepository newsRepository = new ArenaRepository(activity);
        arenaViewModel = new ViewModelProvider(requireActivity()).get(ArenaViewModel.class);
        arenaViewModel.setmRepository(newsRepository);

/*        UserRepository userRepository = new UserRepository(activity, prefs);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setmRepository(userRepository);
        userViewModel.getUserData().observe(this, ((StartMenuActivity) activity)::initHeader);*/

        arenaViewModel.clearData();

/*        if (((StartMenuActivity) getActivity()).isUpdateProfile()) {
            ((StartMenuActivity) getActivity()).setIsUpdateProfile(false);
            userViewModel.getUserProfile();
        }*/

        arenaViewModel.getHome().observe(this, this::initArena);
        isPage = false;

        arenaViewModel.getMessage().observe(this, messageModel -> {
            if (adapterRankItem == null) {
                GalleryArenaData[] d = {};
                initGallery(d, 0);
                mHolder.msg.setText(messageModel.getMsg());
            } else {
                if (!isPage) {
                    GalleryArenaData[] d = {};
                    initGallery(d, 0);
                    mHolder.msg.setText(messageModel.getMsg());
                }
            }
        });

        if (!trigger) {
            trigger = true;
            mHolder.holderDropDown.removeAllViews();
        }

        ((StartMenuActivity) getActivity()).changeHeader(1);

        arenaViewModel.getItens("", "1", idOrdem, idCat == -1 ? "1" : idCat + "");
        getCategories();

        mfragmentViewModel = new ViewModelProvider(requireActivity()).get(FragmentHolderViewModel.class);

    }

    @Override
    public void unbind() {
        super.unbind();
        categoryData = null;
        try {
            arenaViewModel.getHome().removeObservers(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {arenaViewModel.getMessage().removeObservers(this);} catch (Exception e) {e.printStackTrace();}
/*        try {userViewModel.getUserData().removeObservers(this);} catch (Exception e) {e.printStackTrace();}*/
    }

    private void getCategories() {
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_ARENA_CATEGORIES, OP_ID_GET_CATEGORIE_ALL, this).execute();
    }

    private void initArenaOrderItens(JSONObject data) {
        try {
            JSONArray orderItens = data.getJSONArray("orderItens");
            orderItemData.clear();
            mHolder.holderDropDown.removeAllViews();
            for (int i = 0; i < orderItens.length(); i++) {
                JSONObject item = orderItens.getJSONObject(i);
                OrderItemData gData = new Gson().fromJson(item.toString(), OrderItemData.class);
                orderItemData.add(gData);
            }

            initDropdown(orderItemData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initArena(JSONObject response) {
        if (response != null) {
            mHolder.msg.setText("");
            try {
                if (!response.getBoolean("isCategory")) {
                    initArenaOrderItens(response);
                    GalleryArenaData[] gData = new Gson().fromJson(response.getString("itens"), GalleryArenaData[].class);
                    initGallery(gData, response.getInt("margin"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void initGallery(GalleryArenaData[] data, int margin) {

        ArrayList<GalleryArenaData> list = new ArrayList<>(Arrays.asList(data));

        if (!categoria.equals("") && !categoria.equals("Toate")) {
            for (int i = 0; i < data.length; i++) {
                try {
                    if (!list.get(i).getCat().equals(categoria)) {

                        list.remove(i);
                        i--;

                    }
                } catch (Exception ignored) {
                }
            }
        }

        if (!isPage) {

            adapterRankItem = new ArenaItensAdapter(activity, list, margin, true, new ArenaItensAdapter.Listener() {
                @Override
                public void onArenaClick(Integer id) {
                    new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_ARENA_OPEN_POST, id).execute();
                    isPage = false;
                    mfragmentViewModel.changeFragment(new ArenaViewPostFragment((id)), "ArenaViewPostFragment", 3);
                }

                @Override
                public void onMargin(int page) {
                    arenaViewModel.getItens("", page + "", idOrdem, idCat == -1 ? "" : idCat + "");
                }

                @Override
                public void onLikedClick(Integer id) {
                    arenaViewModel.setLike(id);
                }

                @Override
                public void onGalleryClick(ArrayList<String> imagens) {
                    initSlides(imagens);
                }
            });

            mHolder.recyclerArena.setLayoutManager(new LinearLayoutManager(activity));
            mHolder.recyclerArena.setAdapter(adapterRankItem);
            isPage = true;

            mHolder.pullToRefresh.setColorSchemeColors(Color.parseColor("#0B3575"), Color.parseColor("#0B3575"), Color.parseColor("#0B3575"));
            mHolder.pullToRefresh.setOnRefreshListener(() -> {
                isPage = false;
                arenaViewModel.getItens("", "1", idOrdem, idCat == -1 ? "" : idCat + "");
                mHolder.pullToRefresh.setRefreshing(false);
            });
        } else {
            for (GalleryArenaData galleryArenaData : data) {
                adapterRankItem.addItem(galleryArenaData);
            }
        }
    }

    void initSlides(ArrayList<String> imgs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.DialogFullScreen);
        builder.setCancelable(true);
        View view = LayoutInflater.from(activity).inflate(R.layout.include_slides, null, false);

        AlertDialog dialog = builder.create();

        ViewPager viewPager = view.findViewById(R.id.pager_next_img);
        ImageView close = view.findViewById(R.id.img_close);

        close.setOnClickListener(v -> dialog.dismiss());

        SlideAdapter slideAdapter = new SlideAdapter(activity, imgs);
        viewPager.setAdapter(slideAdapter);

        dialog.setView(view);

        dialog.show();
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

        mHolder.nPost.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_ARENA_NEW_POST).execute();
            itemClick = "";
            isPage = false;
            idCat = -1;
            isInit = false;
            idOrdem = "";
            trigger = false;
            Intent intent = new Intent(activity, ArenaCreatePostActivity.class);
            intent.putExtra("categorias", categorias);
            intent.putExtra("catSlected", categoria);
            categoria = "";
            startActivity(intent);
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
                    idOrdem = item.getId() + "";
                }

                if (isInit) {
                    if (!(item.getId() + "").equals("")) {
                        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_ARENA_ORDER_FEED, item.getId() + "").execute();
                    }
                    arenaViewModel.getItens("", "1", idOrdem, idCat == -1 ? "" : idCat + "");
                    mHolder.dropDown.performClick();
                    isPage = false;
                } else {
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
            } else {
                if (String.valueOf(item.getId()).equals(idOrdem)) {
                    firstClick = txt_item;
                }
            }

            position++;

            mHolder.holderDropDown.addView(view);
        }

        if (trigger) {
            trigger = false;
            isInit = false;
            firstClick.performClick();
        }
    }

    void CallChip(ArrayList<CategoryData> data) {
        this.categoryData = data;
        mHolder.holderChip.removeAllViews();

        Comparator<CategoryData> comparadorPorFav = (o1, o2) -> Integer.compare(o2.getSelected(), o1.getSelected());
        try {
            Collections.sort(data, comparadorPorFav);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < data.size(); i++) {
            if (idCat == data.get(i).getId()) {
                CategoryData cat = data.get(i);
                data.remove(i);
                data.add(0, cat);
                break;
            } else {
                if (idCat == -1) {
                    if (data.get(i).getTitle().equals("Toate")) {
                        idCat = data.get(i).getId();
                        CategoryData cat = data.get(i);
                        data.remove(i);
                        data.add(0, cat);
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < data.size(); i++) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.single_chip_layout, mHolder.holderChip, false);
            int paddingDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

            chip.setPadding(15, paddingDp, 15, paddingDp);
            if (i == 0) {
                ChipGroup.LayoutParams lp = new ChipGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMarginStart(UTILS.convertDpToPixels(20));
                chip.setLayoutParams(lp);
            }
            chip.setHeight(66);
            chip.setText(data.get(i).getTitle());

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

            int finalI = i;

            chip.setOnClickListener(v -> {
                if (data.get(finalI).getId() != idCat) {
                    idCat = data.get(finalI).getId();

                    isPage = false;
                    categoria = data.get(finalI).getTitle();
                    new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_ARENA_CHANGE_CATEGORY, idCat).execute();
                    arenaViewModel.getItens("", "1", idOrdem, idCat + "");

                    try {
                        CallChip(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            });

            Typeface typeface = ResourcesCompat.getFont(activity, R.font.exo_2_bold);
            chip.setTypeface(typeface);
            mHolder.holderChip.addView(chip);
        }

        mHolder.scrollChip.fullScroll(0);

        if (isAnimate) {
            ValueAnimator anim = ValueAnimator.ofInt(0, UTILS.convertDpToPixels(50));
            anim.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParamsy = mHolder.holderChip.getLayoutParams();
                layoutParamsy.height = val;
                mHolder.holderChip.setLayoutParams(layoutParamsy);
            });
            anim.setDuration(400);
            anim.start();
        }

        isAnimate = false;

        mHolder.holderChip.setVisibility(View.VISIBLE);
    }


    static class ViewHolder {
        LinearLayout llVideos;
        CustomHorizontalScrollView scrollChip;
        ChipGroup holderChip;
        Button nPost;
        LinearLayout holderDropDown;
        LinearLayout dropDown;
        TextView txt_drop;
        ImageView arrow_drop;
        RecyclerView recyclerArena;
        ShadowLayout shadonwBtn;
        TextView msg;
        SwipeRefreshLayout pullToRefresh;

        public ViewHolder(View view) {
            llVideos = view.findViewById(R.id.ll_videos);
            recyclerArena = view.findViewById(R.id.recycler_arena);
            scrollChip = view.findViewById(R.id.scroll_chip);
            holderChip = view.findViewById(R.id.header_holder_chip);
            holderDropDown = view.findViewById(R.id.holder_dropdown);
            dropDown = view.findViewById(R.id.btn_dropdown);
            txt_drop = view.findViewById(R.id.txt_dropdown);
            shadonwBtn = view.findViewById(R.id.shadonwBtn);
            nPost = view.findViewById(R.id.new_post);
            msg = view.findViewById(R.id.msg);
            arrow_drop = view.findViewById(R.id.arrow);
            pullToRefresh = view.findViewById(R.id.pullToRefresh);
        }
    }
}