package com.frf.app.fragments;

import static com.frf.app.activitys.MainActivity.emptyAsync;
import static com.frf.app.activitys.MainActivity.prefs;
import static com.frf.app.fragments.BottomSheetMoreFragment.ARENA_POSTS;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.transition.Fade;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.request.RequestOptions;
import com.frf.app.R;
import com.frf.app.activitys.ArenaViewPhotoActivity;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.activitys.TermsAndPrivacyPolicyActivity;
import com.frf.app.activitys.VideoActivity;
import com.frf.app.adapter.SlideAdapter;
import com.frf.app.data.Answers;
import com.frf.app.data.BannerData;
import com.frf.app.data.GalleryData;
import com.frf.app.data.GameData;
import com.frf.app.data.NoticiaData;
import com.frf.app.data.NotificationData;
import com.frf.app.data.QuizData;
import com.frf.app.data.VideosHomeData;
import com.frf.app.repository.home.HomeRepository;
import com.frf.app.repository.user.UserRepository;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.CONSTANTS;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.frf.app.vmodels.fragments.FragmentHolderViewModel;
import com.frf.app.vmodels.home.HomeViewModel;
import com.frf.app.vmodels.user.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class HomeFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {

    private ViewHolder mHolder;
    private HomeViewModel homeViewModel;
    private UserViewModel userViewModel;
    private FragmentHolderViewModel mfragmentViewModel;
    private static final int OP_ID_QUIZ_ANSWER = 0;
    private static final int REQUEST_ITEM_NEWS = 1;

    private int points = 0;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mHolder = new ViewHolder(view);

        return view;
    }


    @Override
    public void bind() {
        super.bind();

        mfragmentViewModel = new ViewModelProvider(requireActivity()).get(FragmentHolderViewModel.class);

        ((StartMenuActivity) getActivity()).changeHeader(1);

        UserRepository userRepository = new UserRepository(activity, prefs);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setmRepository(userRepository);
        userViewModel.getUserData().observe(this, ((StartMenuActivity) activity)::initHeader);

        if (((StartMenuActivity) getActivity()).isUpdateProfile()) {
            ((StartMenuActivity) getActivity()).setIsUpdateProfile(false);
            userViewModel.getUserProfile();
        } else {
            userViewModel.getUser(false);
        }

        HomeRepository homeRepository = new HomeRepository(activity);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        homeViewModel.setmRepository(homeRepository);
        homeViewModel.getHome().observe(this, this::initHome);

        homeViewModel.getItens();
    }

    @Override
    public void unbind() {
        super.unbind();
        try {
            homeViewModel.getHome().removeObservers(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initHome(JSONObject data) {
        mHolder.contentItens.removeAllViews();
        try {
            JSONArray itens = data.getJSONArray("itens");

            boolean isEnd = false;
            for (int i = 0; i <= itens.length() - 1; i++) {

                if (i >= (itens.length() - 1)) {
                    isEnd = true;
                }
                JSONObject item = itens.getJSONObject(i);
                switch (item.getInt("t")) {
                    case 1: {
                        NoticiaData noticiaData = new Gson().fromJson(item.getString("data"), NoticiaData.class);
                        includeNew(noticiaData, isEnd);
                    }
                    break;
                    case 2: {
                        GameData gData = new Gson().fromJson(item.getString("data"), GameData.class);
                        includeGame(gData, isEnd);
                    }
                    break;
                    case 3: {
                        GalleryData gData = new Gson().fromJson(item.getString("data"), GalleryData.class);
                        initGallery(gData, isEnd);
                    }
                    break;
                    case 4: {
                        VideosHomeData gData = new Gson().fromJson(item.getString("data"), VideosHomeData.class);
                        initVideo(gData, isEnd);
                    }
                    break;
                    case 5: {
                        QuizData gData = new Gson().fromJson(item.getString("data"), QuizData.class);
                        initQuiz(gData, isEnd);
                    }
                    break;
                    case 6: {
                        BannerData bData = new Gson().fromJson(item.getString("data"), BannerData.class);
                        initBanner(bData, isEnd);
                    }
                    break;
                    case 7: {
                        GalleryData gData = new Gson().fromJson(item.getString("data"), GalleryData.class);
                        initPostSponsored(gData, isEnd);
                    }
                    break;
                }
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            Toast.makeText(activity, activity.getResources().getString(R.string.an_error_has_occurred), Toast.LENGTH_SHORT).show();
        }

        mHolder.pullToRefresh.setColorSchemeColors(Color.parseColor("#0B3575"), Color.parseColor("#0B3575"), Color.parseColor("#0B3575"));
        mHolder.pullToRefresh.setOnRefreshListener(() -> {
            homeViewModel.getItens();
            mHolder.pullToRefresh.setRefreshing(false);
        });
    }

    private void initQuiz(QuizData gData, boolean end) {
        View view = LayoutInflater.from(activity).inflate(R.layout.include_quiz_item, mHolder.contentItens, false);

        TextView titleQuiz = view.findViewById(R.id.titleQuiz);
        TextView header = view.findViewById(R.id.title);
        CardView cardQuiz1 = view.findViewById(R.id.cardQuiz1);
        View content = view.findViewById(R.id.container);
        LinearLayout opcCard2 = view.findViewById(R.id.opcCard2);
        LinearLayout opcCard1 = view.findViewById(R.id.opcCard1);
        LinearLayout listAlternatives = view.findViewById(R.id.list_alternatives);
        LinearLayout contentCoins = view.findViewById(R.id.ll_coins);
        ImageView imgCoin = view.findViewById(R.id.img_coin);
        TextView txtMoedas = view.findViewById(R.id.txt_moedas);
        TextView txtTime = view.findViewById(R.id.cardTime);
        TextView reg = view.findViewById(R.id.reg);

        reg.setVisibility(View.VISIBLE);
        reg.setOnClickListener(view1 -> {
            Intent intent = new Intent(activity, TermsAndPrivacyPolicyActivity.class);
            intent.putExtra("link", getString(R.string.link_terms_quiz));
            intent.putExtra("title", getString(R.string.regulament));
            activity.startActivity(intent);
        });

        if (gData.getAnswers().get(0).getImg() != null) {
            if (gData.getIdAnswered() == 0) {

                if (gData.getReward() != null && !gData.getReward().equals("")) {
                    txtMoedas.setText(gData.getReward().equals("") ? gData.getReward() : "+" + gData.getReward());
                } else {
                    contentCoins.setVisibility(View.GONE);
                }
                titleQuiz.setText(gData.getQuestion());
                header.setText(gData.getTitle());

                if (gData.getReward().equals("0")) {
                    imgCoin.setVisibility(View.INVISIBLE);
                    txtMoedas.setVisibility(View.INVISIBLE);
                }

                cardQuiz1.setOnClickListener(vieww -> {
                    if (listAlternatives.getVisibility() != View.VISIBLE) {
                        TransitionManager.beginDelayedTransition(listAlternatives, new AutoTransition());
                        listAlternatives.setVisibility(View.VISIBLE);

                        final int[] count = {1};
                        final LinearLayout[] linearLayout = {new LinearLayout(activity)};
                        linearLayout[0].setOrientation(LinearLayout.HORIZONTAL);

                        if (listAlternatives.getTag() == null) {
                            for (int i = 0; i < gData.getAnswers().size(); i++) {
                                Answers answers = gData.getAnswers().get(i);
                                View elementView = LayoutInflater.from(activity).inflate(R.layout.include_alternatives_img, listAlternatives, false);

                                CardView container = elementView.findViewById(R.id.container);
                                View check = elementView.findViewById(R.id.check);
                                ImageView img = elementView.findViewById(R.id.img);
                                ImageView iconCheck = elementView.findViewById(R.id.icon_check);
                                View mask = elementView.findViewById(R.id.mask_select);

                                UTILS.getImageAt(activity, answers.getImg(), img);

                                int finalI1 = i;
                                container.setOnClickListener(v -> {//int idQuestion, int idAnswer
                                    points = Integer.parseInt(gData.getReward());
                                    setQuizAnswer(Integer.parseInt(gData.getId()), (answers.getId()));
                                    if (answers.getCorrect() == 1) {
                                        iconCheck.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_checked));
                                    } else {
                                        iconCheck.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_close_red));
                                    }
                                    mask.setVisibility(View.VISIBLE);
                                    check.setVisibility(View.VISIBLE);

                                    int finalI = finalI1;
                                    boolean trigger = true;

                                    listAlternatives.removeAllViews();
                                    listAlternatives.setTag(true);

                                    count[0] = 1;
                                    linearLayout[0] = new LinearLayout(activity);
                                    linearLayout[0].setOrientation(LinearLayout.HORIZONTAL);

                                    for (int y = 0; y < gData.getAnswers().size(); y++) {

                                        Answers answersy = gData.getAnswers().get(y);
                                        View elementViewy = LayoutInflater.from(activity).inflate(R.layout.include_alternatives_img, listAlternatives, false);

                                        View checky = elementViewy.findViewById(R.id.check);
                                        ImageView imgy = elementViewy.findViewById(R.id.img);
                                        ImageView iconChecky = elementViewy.findViewById(R.id.icon_check);
                                        View masky = elementViewy.findViewById(R.id.mask_select);

                                        UTILS.getImageAt(activity, answersy.getImg(), imgy);

                                        if (y == finalI) {
                                            if (answersy.getCorrect() == 1) {
                                                trigger = false;
                                                iconChecky.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_checked));
                                            } else {
                                                iconChecky.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_close_red));
                                            }
                                            gData.setIdAnswered(answersy.getId());
                                            masky.setVisibility(View.VISIBLE);
                                            checky.setVisibility(View.VISIBLE);
                                        } else {
                                            if (trigger) {
                                                if (answersy.getCorrect() == 1) {
                                                    checky.setVisibility(View.VISIBLE);
                                                    iconChecky.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_checked));
                                                }
                                            }
                                        }

                                        if (count[0] <= 2) {
                                            linearLayout[0].addView(elementViewy);
                                            if (y == gData.getAnswers().size() - 1 && count[0] == 1) {
                                                View vy = LayoutInflater.from(activity).inflate(R.layout.include_alternatives_img, listAlternatives, false);
                                                vy.setVisibility(View.INVISIBLE);
                                                linearLayout[0].addView(vy);
                                                count[0]++;
                                            }
                                            if (count[0] == 2) {
                                                listAlternatives.addView(linearLayout[0]);
                                                linearLayout[0] = new LinearLayout(activity);
                                                linearLayout[0].setOrientation(LinearLayout.HORIZONTAL);
                                                count[0] = 0;
                                            }
                                            count[0]++;
                                        }
                                    }
                                });

                                if (count[0] <= 2) {
                                    linearLayout[0].addView(elementView);
                                    if (i == gData.getAnswers().size() - 1 && count[0] == 1) {
                                        View vy = LayoutInflater.from(activity).inflate(R.layout.include_alternatives_img, listAlternatives, false);
                                        vy.setVisibility(View.INVISIBLE);
                                        linearLayout[0].addView(vy);
                                        count[0]++;
                                    }
                                    if (count[0] == 2) {
                                        listAlternatives.addView(linearLayout[0]);
                                        linearLayout[0] = new LinearLayout(activity);
                                        linearLayout[0].setOrientation(LinearLayout.HORIZONTAL);
                                        count[0] = 0;
                                    }
                                    count[0]++;
                                }
                            }
                        }
                    } else {
                        if (listAlternatives.getTag() == null) {
                            TransitionManager.beginDelayedTransition(listAlternatives, new AutoTransition());
                            listAlternatives.setVisibility(View.GONE);
                            listAlternatives.removeAllViews();
                        } else {
                            AutoTransition autoTransition = new AutoTransition();
                            autoTransition.addListener(new Transition.TransitionListener() {
                                @Override
                                public void onTransitionStart(Transition transition) {

                                }

                                @Override
                                public void onTransitionEnd(Transition transition) {
                                    AutoTransition autoTransition2 = new AutoTransition();
                                    autoTransition2.addListener(new Transition.TransitionListener() {
                                        @Override
                                        public void onTransitionStart(@NonNull @NotNull Transition transition) {

                                        }

                                        @Override
                                        public void onTransitionEnd(@NonNull @NotNull Transition transition) {
                                            cardQuiz1.setClickable(false);
                                        }

                                        @Override
                                        public void onTransitionCancel(@NonNull @NotNull Transition transition) {

                                        }

                                        @Override
                                        public void onTransitionPause(@NonNull @NotNull Transition transition) {

                                        }

                                        @Override
                                        public void onTransitionResume(@NonNull @NotNull Transition transition) {

                                        }
                                    });
                                    TransitionManager.beginDelayedTransition(cardQuiz1, autoTransition2);
                                    cardQuiz1.setVisibility(View.GONE);
                                }

                                @Override
                                public void onTransitionCancel(Transition transition) {
                                }

                                @Override
                                public void onTransitionPause(Transition transition) {
                                }

                                @Override
                                public void onTransitionResume(Transition transition) {
                                }
                            });
                            TransitionManager.beginDelayedTransition(listAlternatives, autoTransition);
                            listAlternatives.setVisibility(View.GONE);
                        }
                    }
                });

            } else {

                cardQuiz1.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.ColorWhitte));
                titleQuiz.setTextColor(ContextCompat.getColorStateList(activity, R.color.ColorBlueGray800));
                header.setTextColor(ContextCompat.getColorStateList(activity, R.color.PrimaryColorText));
                reg.setTextColor(ContextCompat.getColor(activity, R.color.PrimaryColorText));
                txtTime.setTextColor(ContextCompat.getColorStateList(activity, R.color.ColorBlueGray500));
                txtMoedas.setTextColor(ContextCompat.getColorStateList(activity, R.color.PrimaryColorText));
                imgCoin.setImageDrawable(activity.getDrawable(R.drawable.ic_coin_blue));
                txtMoedas.setText(gData.getReward());
                titleQuiz.setText(gData.getQuestion());
                header.setText(gData.getTitle());

                if (gData.getReward().equals("0")) {
                    imgCoin.setVisibility(View.INVISIBLE);
                    txtMoedas.setVisibility(View.INVISIBLE);
                }

                cardQuiz1.setOnClickListener(vieww -> {
                    if (listAlternatives.getVisibility() != View.VISIBLE) {
                        TransitionManager.beginDelayedTransition(listAlternatives, new AutoTransition());
                        listAlternatives.setVisibility(View.VISIBLE);

                        final int[] count = {1};
                        final LinearLayout[] linearLayout = {new LinearLayout(activity)};
                        linearLayout[0].setOrientation(LinearLayout.HORIZONTAL);

                        int i = 0;

                        for (Answers answers : gData.getAnswers()) {
                            View elementView = LayoutInflater.from(activity).inflate(R.layout.include_alternatives_img, listAlternatives, false);

                            View check = elementView.findViewById(R.id.check);
                            ImageView img = elementView.findViewById(R.id.img);
                            ImageView iconCheck = elementView.findViewById(R.id.icon_check);
                            View mask = elementView.findViewById(R.id.mask_select);

                            UTILS.getImageAt(activity, answers.getImg(), img);

                            if (answers.getCorrect() == 1) {
                                check.setVisibility(View.VISIBLE);
                                iconCheck.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_checked));

                                if (gData.getIdAnswered() == answers.getId()) {
                                    mask.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (gData.getIdAnswered() == answers.getId()) {
                                    check.setVisibility(View.VISIBLE);
                                    if (answers.getCorrect() == 0) {
                                        iconCheck.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_close_red));
                                        mask.setVisibility(View.VISIBLE);
                                    } else {
                                        iconCheck.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_checked));
                                        mask.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            if (count[0] <= 2) {
                                linearLayout[0].addView(elementView);
                                if (i == gData.getAnswers().size() - 1 && count[0] == 1) {
                                    View vy = LayoutInflater.from(activity).inflate(R.layout.include_alternatives_img, listAlternatives, false);
                                    vy.setVisibility(View.INVISIBLE);
                                    linearLayout[0].addView(vy);
                                    count[0]++;
                                }
                                if (count[0] == 2) {
                                    listAlternatives.addView(linearLayout[0]);
                                    linearLayout[0] = new LinearLayout(activity);
                                    linearLayout[0].setOrientation(LinearLayout.HORIZONTAL);
                                    count[0] = 0;
                                }
                                count[0]++;
                            }

                            i++;
                        }
                    } else {
                        TransitionManager.beginDelayedTransition(listAlternatives, new AutoTransition());
                        listAlternatives.setVisibility(View.GONE);
                        listAlternatives.removeAllViews();
                    }
                });
            }
        } else {
            if (gData.getIdAnswered() == 0) {
                if (gData.getReward() != null && !gData.getReward().equals("")) {
                    txtMoedas.setText(gData.getReward());
                } else {
                    contentCoins.setVisibility(View.GONE);
                }
                titleQuiz.setText(gData.getQuestion());
                header.setText(gData.getTitle());

                cardQuiz1.setOnClickListener(vieww -> {

                    if (opcCard2.getVisibility() != View.VISIBLE) {
                        TransitionManager.beginDelayedTransition(opcCard2, new AutoTransition());
                        opcCard2.setVisibility(View.VISIBLE);

                        for (int i = 0; i < gData.getAnswers().size(); i++) {
                            Answers answers = gData.getAnswers().get(i);
                            View elementView = LayoutInflater.from(activity).inflate(R.layout.include_button_quiz, opcCard2, false);

                            Button ans = elementView.findViewById(R.id.btn_opc);
                            ans.setText(answers.getTitle());

                            int finalI = i;
                            ans.setOnClickListener(v -> {
                                points = Integer.parseInt(gData.getReward());
                                setQuizAnswer(Integer.parseInt(gData.getId()), (answers.getId()));

                                opcCard2.removeAllViews();
                                opcCard2.setTag(true);
                                for (int y = 0; y < gData.getAnswers().size(); y++) {

                                    Answers answersy = gData.getAnswers().get(y);
                                    View elementViewy = LayoutInflater.from(activity).inflate(R.layout.include_button_quiz, opcCard2, false);

                                    Button ansy = elementViewy.findViewById(R.id.btn_opc);
                                    ansy.setText(answersy.getTitle());

                                    if (y == finalI) {
                                        if (answersy.getCorrect() == 1) {
                                            btnSelectedCorrect(ansy);
                                        } else {
                                            btnSelectedIncorrect(ansy);
                                        }
                                    }

                                    opcCard2.addView(elementViewy);
                                }
                            });

                            opcCard2.addView(elementView);
                        }
                    } else {
                        if (opcCard2.getTag() == null) {
                            TransitionManager.beginDelayedTransition(opcCard2, new AutoTransition());
                            opcCard2.setVisibility(View.GONE);
                            opcCard2.removeAllViews();
                        } else {
                            AutoTransition autoTransition = new AutoTransition();
                            autoTransition.addListener(new Transition.TransitionListener() {
                                @Override
                                public void onTransitionStart(Transition transition) {

                                }

                                @Override
                                public void onTransitionEnd(Transition transition) {
                                    AutoTransition autoTransition2 = new AutoTransition();
                                    autoTransition2.addListener(new Transition.TransitionListener() {
                                        @Override
                                        public void onTransitionStart(@NonNull @NotNull Transition transition) {

                                        }

                                        @Override
                                        public void onTransitionEnd(@NonNull @NotNull Transition transition) {

                                        }

                                        @Override
                                        public void onTransitionCancel(@NonNull @NotNull Transition transition) {

                                        }

                                        @Override
                                        public void onTransitionPause(@NonNull @NotNull Transition transition) {

                                        }

                                        @Override
                                        public void onTransitionResume(@NonNull @NotNull Transition transition) {

                                        }
                                    });
                                    TransitionManager.beginDelayedTransition(cardQuiz1, autoTransition2);
                                    cardQuiz1.setVisibility(View.GONE);
                                }

                                @Override
                                public void onTransitionCancel(Transition transition) {
                                }

                                @Override
                                public void onTransitionPause(Transition transition) {
                                }

                                @Override
                                public void onTransitionResume(Transition transition) {
                                }
                            });
                            TransitionManager.beginDelayedTransition(opcCard2, autoTransition);
                            opcCard2.setVisibility(View.GONE);
                        }
                    }
                });

            } else {

                cardQuiz1.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.ColorWhitte));
                titleQuiz.setTextColor(ContextCompat.getColorStateList(activity, R.color.ColorBlueGray800));
                header.setTextColor(ContextCompat.getColorStateList(activity, R.color.PrimaryColorText));
                txtTime.setTextColor(ContextCompat.getColorStateList(activity, R.color.ColorBlueGray500));
                reg.setTextColor(ContextCompat.getColor(activity, R.color.PrimaryColorText));
                txtMoedas.setTextColor(ContextCompat.getColorStateList(activity, R.color.PrimaryColorText));
                imgCoin.setImageDrawable(activity.getDrawable(R.drawable.ic_coin_blue));
                txtMoedas.setText(gData.getReward());
                titleQuiz.setText(gData.getQuestion());
                header.setText(gData.getTitle());

                if (gData.getReward().equals("0")) {
                    imgCoin.setVisibility(View.INVISIBLE);
                    txtMoedas.setVisibility(View.INVISIBLE);
                }

                cardQuiz1.setOnClickListener(vieww -> {
                    if (opcCard2.getVisibility() != View.VISIBLE) {
                        TransitionManager.beginDelayedTransition(opcCard1, new AutoTransition());
                        opcCard2.setVisibility(View.VISIBLE);

                        for (Answers answers : gData.getAnswers()) {
                            View elementView = LayoutInflater.from(activity).inflate(R.layout.include_button_quiz, opcCard2, false);

                            Button ans = elementView.findViewById(R.id.btn_opc);
                            ans.setText(answers.getTitle());

                            if (answers.getCorrect() == 1) {
                                ans.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checked, 0);
                                if (gData.getIdAnswered() == answers.getId()) {
                                    btnSelectedCorrect(ans);
                                }
                                ans.setText("           " + answers.getTitle());
                            } else {
                                if (gData.getIdAnswered() == answers.getId()) {
                                    if (answers.getCorrect() == 0) {
                                        btnSelectedIncorrect(ans);
                                    } else {
                                        btnSelectedCorrect(ans);
                                    }
                                    ans.setText("           " + answers.getTitle());
                                }
                            }

                            opcCard2.addView(elementView);
                        }
                        opcCard2.setTag(true);
                    } else {
                        TransitionManager.beginDelayedTransition(opcCard2, new AutoTransition());
                        opcCard2.setVisibility(View.GONE);
                        opcCard2.removeAllViews();
                    }
                });
            }
        }

        if (end) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) content.getLayoutParams();
                layoutParams.setMargins((int) UTILS.dpToPx(activity, 13), 0, (int) UTILS.dpToPx(activity, 13), (int) UTILS.dpToPx(activity, 80));
                content.requestLayout();
            } else {
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) cardQuiz1.getLayoutParams();
                layoutParams.setMargins((int) UTILS.dpToPx(activity, 20), 0, (int) UTILS.dpToPx(activity, 20), (int) UTILS.dpToPx(activity, 90));
                cardQuiz1.requestLayout();
            }
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) content.getLayoutParams();
                layoutParams.setMargins((int) UTILS.dpToPx(activity, 13), 0, (int) UTILS.dpToPx(activity, 13), (int) UTILS.dpToPx(activity, 7));
                content.requestLayout();
            } else {
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) cardQuiz1.getLayoutParams();
                layoutParams.setMargins((int) UTILS.dpToPx(activity, 20), 0, (int) UTILS.dpToPx(activity, 20), (int) UTILS.dpToPx(activity, 20));
                cardQuiz1.requestLayout();
            }
        }

        mHolder.contentItens.addView(view);
    }

    public void btnSelectedCorrect(Button btn) {
        btn.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.ColorBlue));
        btn.setTextColor(ContextCompat.getColorStateList(activity, R.color.ColorWhitte));
        btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checked, 0);
        //btn.setPadding(50, 0, 10, 0);
    }

    public void btnSelectedIncorrect(Button btn) {
        btn.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.ColorBlue));
        btn.setTextColor(ContextCompat.getColorStateList(activity, R.color.ColorWhitte));
        btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_red, 0);
        //  btn.setPadding(50, 0, 10, 0);
    }

    private void initVideo(VideosHomeData item, boolean end) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.adapter_videos, mHolder.contentItens, false);

        ImageView imgNew;
        TextView title;
        TextView desc;
        TextView txtDuration;
        TextView txtDate;
        View card;
        txtDuration = itemView.findViewById(R.id.txtDuration);
        imgNew = itemView.findViewById(R.id.img_videos);
        desc = itemView.findViewById(R.id.txt_desc_videos);
        title = itemView.findViewById(R.id.txt_title_videos);
        txtDate = itemView.findViewById(R.id.txtDate);
        card = itemView.findViewById(R.id.card_videos);

        UTILS.getImageAt(activity, item.getImg(), imgNew);
        title.setText(item.getCat());
        desc.setText(item.getTitle());
        txtDuration.setText(item.getDuration());

        if (item.getDate() != null) {
            txtDate.setText(UTILS.PrettyTime(activity, item.getDate()));
        } else {
            txtDate.setText("");
        }

        card.setOnClickListener(v -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_HOME_OPEN_VIDEO, item.getId()).execute();
            initFullScreenVideo(item.getUrl());
        });

        if (end) {
            int mH, mV;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                mH = 13;
                mV = 90;
            } else {
                mH = 20;
                mV = 80;
            }

            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) card.getLayoutParams();
            layoutParams.setMargins((int) UTILS.dpToPx(activity, mH), 0, (int) UTILS.dpToPx(activity, mH), (int) UTILS.dpToPx(activity, mV));
            card.requestLayout();
        } else {
            int mH, mV;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                mH = 13;
                mV = 10;
            } else {
                mH = 20;
                mV = 20;
            }

            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) card.getLayoutParams();
            layoutParams.setMargins((int) UTILS.dpToPx(activity, mH), 0, (int) UTILS.dpToPx(activity, mH), (int) UTILS.dpToPx(activity, mV));
            card.requestLayout();
        }

        mHolder.contentItens.addView(itemView);
    }

    void initFullScreenVideo(String video) {
        Intent intent = new Intent(activity, VideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("link", video);
        intent.putExtra("extra", bundle);
        startActivity(intent);
    }

    void initBanner(final BannerData data, boolean end) {
        if (data != null) {
            View view = LayoutInflater.from(activity).inflate(R.layout.include_banner_home, mHolder.contentItens, false);

            CardView card = view.findViewById(R.id.container);
            FrameLayout imgBanerLoja2 = view.findViewById(R.id.img_banner_loja2);
            ImageView imgBanerLoja = view.findViewById(R.id.img_banner_loja);
            ImageView bannerBackground = view.findViewById(R.id.img_banner_background);
            RelativeLayout bannerLoja = view.findViewById(R.id.view_banner_loja);
            TextView txtBanerLoja = view.findViewById(R.id.txt_banner_loja);

            imgBanerLoja2.removeAllViews();

            DisplayMetrics dimension = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dimension);
            int weight = dimension.widthPixels;//mHolder.bannerNews.getWidth();
            int height = (weight / 100) * 30;

            if (data.getMode() == 1) {
                UTILS.getImageAt(activity, data.getImages().get(0), imgBanerLoja);
                imgBanerLoja.setVisibility(View.VISIBLE);
                imgBanerLoja.getLayoutParams().height = height;
                imgBanerLoja.requestLayout();

            } else if (data.getMode() == 2) {
                List<ImageView> imageViews = new ArrayList<>();

                if (data.getImages().size() == 2) {
                    data.getImages().add(data.getImages().get(0));
                    data.getImages().add(data.getImages().get(1));
                } else if (data.getImages().size() == 1) {
                    data.getImages().add(data.getImages().get(0));
                    data.getImages().add(data.getImages().get(0));
                }

                for (String s : data.getImages()) {

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT);


                    ImageView image = new ImageView(activity);
                    image.setLayoutParams(params);

                    UTILS.getImageAt(activity, s, image);

                    imageViews.add(image);
                    image.setVisibility(View.GONE);
                    imgBanerLoja2.addView(image);
                }

                imgBanerLoja.setVisibility(View.GONE);
                animateBanner(imageViews);
            } else {
                UTILS.getImageAt(activity, data.getImages().get(0), imgBanerLoja);
                imgBanerLoja.setVisibility(View.VISIBLE);
                imgBanerLoja.getLayoutParams().height = height;
                imgBanerLoja.requestLayout();
            }

            bannerLoja.getLayoutParams().height = height;
            bannerLoja.requestLayout();

            txtBanerLoja.setText(data.getTittle());
            bannerLoja.setVisibility(View.VISIBLE);

            if (!data.getColor().isEmpty()) {
                String color = data.getColor().replace("#", "");
                int parcedColor = Color.parseColor("#" + color);
                bannerBackground.setBackgroundColor(parcedColor);
            }

            if (!data.getBg().isEmpty()) {
                UTILS.getImageAt(activity, data.getBg(), bannerBackground);
            }

            bannerLoja.setOnClickListener(v -> {
                if (data.getT() >= 0) {
                    redirectPush(new NotificationData(data.getV(), data.getId(), data.getT()));
                } else {
                    if (data.getEmbed() != null && data.getEmbed().equals("1")) {
                        Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                        httpIntent.setData(Uri.parse(data.getLink()));
                        startActivity(httpIntent);
                    } else {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getLink()));
                        startActivity(browserIntent);
                    }
                }
            });

            if (end) {
                int mH, mV;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    mH = 13;
                    mV = 90;
                } else {
                    mH = 20;
                    mV = 80;
                }
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                layoutParams.setMargins((int) UTILS.dpToPx(activity, mH), 0, (int) UTILS.dpToPx(activity, mH), (int) UTILS.dpToPx(activity, mV));
                card.requestLayout();
            } else {
                int mH, mV;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    mH = 13;
                    mV = 5;
                } else {
                    mH = 20;
                    mV = 20;
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    ViewGroup.MarginLayoutParams layoutParams =
                            (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                    layoutParams.setMargins((int) UTILS.dpToPx(activity, mH), 0, (int) UTILS.dpToPx(activity, mH), (int) UTILS.dpToPx(activity, mV));
                    card.requestLayout();
                } else {
                    ViewGroup.MarginLayoutParams layoutParams =
                            (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                    layoutParams.setMargins((int) UTILS.dpToPx(activity, mH), 0, (int) UTILS.dpToPx(activity, mH), (int) UTILS.dpToPx(activity, mV));
                    card.requestLayout();
                }
            }

            mHolder.contentItens.addView(view);
        }
    }

    void redirectPush(NotificationData data) {
        int idPush = data.getIdPush();
        int pushType = data.getType();

        Bundle pushBundle = new Bundle();

        pushBundle.putInt("t", pushType);
        pushBundle.putInt("idPush", idPush);

        pushBundle.putInt("v", data.getId() == 0 ? -1 : data.getId());

        Intent resultIntent;

        resultIntent = new Intent(activity, StartMenuActivity.class);
        Bundle _b = new Bundle();
        _b.putBundle("pushBundle", pushBundle);
        _b.putInt(CONSTANTS.LOADING_SCREEN_KEY, CONSTANTS.SCREEN_REDIRECT_PUSH);
        resultIntent.putExtras(_b);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(resultIntent);
    }

    String getAnim(int animState) {
        switch (animState) {
            case 0:
            case 2:
                return "translationX";
            case 1:
            case 3:
                return "translationY";
        }
        return "translationX";
    }

    float getValueAnim(int animState) {
        switch (animState) {
            case 0:
                return -2f;
            case 1:
                return -0.5f;
            case 2:
                return 2f;
            case 3:
                return 0.5f;
        }
        return 1;
    }

    void animateBanner(List<ImageView> images) {
        final int[] oldCurrent = {1};
        final int[] newCurrent = {1};
        final int[] anim = {1};
        newCurrent[0] = oldCurrent[0] + 1;
        Handler handler = new Handler();
        // Define the code block to be executed
        final Runnable[] runnable = new Runnable[1];
        runnable[0] = new Runnable() {
            @Override
            public void run() {
                Log.d("Animation", "old:" + oldCurrent[0] + ": new" + newCurrent[0]);
                // Insert custom code here
                try {
                    animateAction(images.get(oldCurrent[0]), images.get(newCurrent[0]), getAnim(anim[0]), getValueAnim(anim[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                    oldCurrent[0] = 0;
                    newCurrent[0] = 0;
                    anim[0] = 0;
                    animateAction(images.get(oldCurrent[0]), images.get(newCurrent[0]), getAnim(anim[0]), getValueAnim(anim[0]));
                }


                if (anim[0] >= 3) {
                    anim[0] = 0;
                } else {
                    anim[0]++;
                }

                if (oldCurrent[0] >= images.size() - 1) {
                    oldCurrent[0] = 0;
                } else {
                    oldCurrent[0]++;
                }

                if (newCurrent[0] >= images.size() - 1) {
                    newCurrent[0] = 0;
                } else {
                    newCurrent[0]++;
                }

                // Repeat every 2 seconds
                handler.postDelayed(runnable[0], 3500);
            }
        };
        handler.post(runnable[0]);
    }

    void animateAction(ImageView old, ImageView newlest, String type, float value) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(old, type, 0, 800 * value);
        animation.setDuration(2000);
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                new Handler().postDelayed(() -> old.animate()
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation1) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation1) {
                                old.setVisibility(View.GONE);
                                old.setAlpha(1f);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation1) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation1) {

                            }
                        }), 1200);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animation.start();

        newlest.setVisibility(View.VISIBLE);
        ObjectAnimator animation2 = ObjectAnimator.ofFloat(newlest, type, -800 * value, 0);
        animation2.setDuration(2000);
        animation2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                newlest.setVisibility(View.VISIBLE);
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
        animation2.start();
    }

    void includeNew(NoticiaData data, boolean end) {
        View view = LayoutInflater.from(activity).inflate(R.layout.include_card_tipo_0, mHolder.contentItens, false);

        View card = view.findViewById(R.id.card_news);
        ImageView img = view.findViewById(R.id.img_card1);
        TextView titulo = view.findViewById(R.id.titulo_tipo_0);
        TextView dataPost = view.findViewById(R.id.time_tipo_0);
        TextView desc = view.findViewById(R.id.desc_tipo_0);

        UTILS.getImageAt(activity, data.getImg(), img);

        titulo.setText(data.getTitle());
        dataPost.setText(data.getDateHour());

        dataPost.setText(UTILS.PrettyTime(activity, data.getDateHour()));

        //dataPost.setText(UTILS.PrettyTime(data.getDateHour()));

        desc.setText(data.getSubTitle());

        card.setOnClickListener(v -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_HOME_OPEN_NEWS, data.getId()).execute();
            mfragmentViewModel.changeFragment(new NewsItemFragment(data.getId()), "NewsExpandedFragment", 5);
        });

        if (end) {
            int mH, mV;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                mH = 13;
                mV = 80;
            } else {
                mH = 20;
                mV = 90;
            }
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) card.getLayoutParams();
            layoutParams.setMargins((int) UTILS.dpToPx(activity, mH), 0, (int) UTILS.dpToPx(activity, mH), (int) UTILS.dpToPx(activity, mV));
            card.requestLayout();
        } else {
            int mH, mV;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                mH = 13;
                mV = 5;
            } else {
                mH = 20;
                mV = 20;
            }

            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) card.getLayoutParams();
            layoutParams.setMargins((int) UTILS.dpToPx(activity, mH), 0, (int) UTILS.dpToPx(activity, mH), (int) UTILS.dpToPx(activity, mV));
            card.requestLayout();
        }

        mHolder.contentItens.addView(view);
    }

    void includeGame(GameData data, boolean end) {
        View view = LayoutInflater.from(activity).inflate(R.layout.include_card_tipo_1, mHolder.contentItens, false);

        View card = view.findViewById(R.id.container);
        TextView titulo = view.findViewById(R.id.titulo_tipo_1);
        TextView label1 = view.findViewById(R.id.label1);
        TextView label2 = view.findViewById(R.id.label2);
        TextView horarioInicio = view.findViewById(R.id.horario_inicio);
        TextView horarioFim = view.findViewById(R.id.horario_fim);
        TextView nomeArena = view.findViewById(R.id.nome_arena);
        ImageView img1 = view.findViewById(R.id.img1);
        ImageView img2 = view.findViewById(R.id.img2);
        Button btnBihete = view.findViewById(R.id.btn_bihete);
        View shadowLayout = view.findViewById(R.id.shadonwBtn);

        UTILS.getImageAt(activity, data.getClubImg1(), img1);
        UTILS.getImageAt(activity, data.getClubImg2(), img2);

        titulo.setText(data.getTitle());
        label1.setText(data.getClub1());
        label2.setText(data.getClub2());
        nomeArena.setText(data.getSubtit());
        btnBihete.setVisibility(View.VISIBLE);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM");
        SimpleDateFormat formatHour = new SimpleDateFormat("HH:mm");
        Date date = null;

        try {
            date = format.parse(data.getDate());
            horarioInicio.setText(formatDate.format(date));
            horarioFim.setText(formatHour.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (data.getLink().equals("")) {
            shadowLayout.setVisibility(View.INVISIBLE);
        } else {
            shadowLayout.setVisibility(View.VISIBLE);
        }


        btnBihete.setOnClickListener(v -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_HOME_OPEN_MATCH, data.getId()).execute();
            TRACKING.setTrackingButtonName(activity, "ticket_button", false, 0);
            try {
                BottomNavigationView bottomNav = activity.findViewById(R.id.bottom_navigation);
                bottomNav.getMenu().getItem(4).setChecked(true);
                mfragmentViewModel.changeFragment(new ShopFragment(0, data.getLink()), "ShopFragment", 1);
            } catch (Exception e) {
                e.printStackTrace();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(data.getLink()));
                activity.startActivity(i);
            }
        });

        card.setOnClickListener(v -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_HOME_OPEN_MATCH, data.getId()).execute();
            mfragmentViewModel.changeFragment(new EventsUpcomingMatchFragment(data.getId(), btnBihete.getVisibility() == View.VISIBLE, false, mfragmentViewModel), "EventsUpcomingMatchFragment", btnBihete.getVisibility() == View.VISIBLE ? 5 : 3);
        });

        if (end) {
            int mH, mV;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                mH = 13;
                mV = 90;
            } else {
                mH = 20;
                mV = 80;
            }

            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) card.getLayoutParams();
            layoutParams.setMargins((int) UTILS.dpToPx(activity, mH), 0, (int) UTILS.dpToPx(activity, mH), (int) UTILS.dpToPx(activity, mV));
            card.requestLayout();
        } else {
            int mH, mV;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                mH = 13;
                mV = 9;
            } else {
                mH = 20;
                mV = 20;
            }

            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) card.getLayoutParams();
            layoutParams.setMargins((int) UTILS.dpToPx(activity, mH), 0, (int) UTILS.dpToPx(activity, mH), (int) UTILS.dpToPx(activity, mV));
            card.requestLayout();

        }

        mHolder.contentItens.addView(view);

    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    void initGallery(GalleryData data, boolean end) {
        View view = LayoutInflater.from(activity).inflate(R.layout.include_card_tipo_2, mHolder.contentItens, false);

        View card = view.findViewById(R.id.container);
        TextView horario = view.findViewById(R.id.horario);
        TextView nome = view.findViewById(R.id.nome);
        TextView nivel = view.findViewById(R.id.nivel);
        TextView countImgs = view.findViewById(R.id.count_imgs);
        TextView desc = view.findViewById(R.id.desc);
        ImageView avatar = view.findViewById(R.id.avatar);
        CardView imgSecundariaCard = view.findViewById(R.id.card_img_secundaria);
        CardView imgTernariaCard = view.findViewById(R.id.card_img_ternario);
        CardView imgPrimariaCard = view.findViewById(R.id.img_principal);
        ImageView imgFoco = view.findViewById(R.id.img_foco);
        ImageView imgSecundaria = view.findViewById(R.id.img_secondary);
        ImageView imgTernaria = view.findViewById(R.id.img_ternaria);
        ImageView like = view.findViewById(R.id.ic_like);
        TextView likes = view.findViewById(R.id.num_likes);
        TextView comentarios = view.findViewById(R.id.num_comentarios);
        TextView cat = view.findViewById(R.id.cat);
        LinearLayout contentImagens = view.findViewById(R.id.llImages);
        LinearLayout llImgsRight = view.findViewById(R.id.ll_img_right);
        ImageView more = view.findViewById(R.id.more);

        card.setOnClickListener(v -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_HOME_OPEN_ARENA, data.getId()).execute();
            mfragmentViewModel.changeFragment(new ArenaViewPostFragment((data.getId())), "ArenaViewPostFragment", 3);
        });

        if(String.valueOf(data.getIdUser()).equals(UTILS.id)){
            more.setVisibility(View.GONE);
        }else{
            more.setVisibility(View.VISIBLE);
        }


        more.setOnClickListener(view1-> {
            BottomSheetMoreFragment fragment = new BottomSheetMoreFragment(ARENA_POSTS, data.getId(), data.getIdUser(), () -> homeViewModel.getItens());
            fragment.show(getChildFragmentManager(),fragment.getTag());
        });


        Drawable drawablePlace = getResources().getDrawable(R.drawable.ic_placeholderuser);
        drawablePlace.setTint(getResources().getColor(R.color.ThirdBackground));

        UTILS.getImageAt(activity, data.getAvatar(), avatar, new RequestOptions()
                .circleCrop()
                .placeholder(drawablePlace)
                .error(drawablePlace));

        desc.setText(data.getText());
        nome.setText(data.getName());
        nivel.setText(data.getUserType());
        comentarios.setText(data.getComments() + "");
        likes.setText(data.getNumLikes() + "");

        like.setImageDrawable(data.isLike() ? getResources().getDrawable(R.drawable.ic_like) : getResources().getDrawable(R.drawable.ic_unlike));
        like.setOnClickListener(v -> {
            homeViewModel.postLike(data.getId() + "");
            if (data.isLike()) {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_HOME_UNLIKE_ARENA, data.getId()).execute();
                like.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlike));
                data.setNumLikes((data.getNumLikes() - 1));
                data.setLike(0);
                likes.setText(data.getNumLikes() + "");
            } else {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_HOME_LIKE_ARENA, data.getId()).execute();
                like.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                data.setNumLikes((data.getNumLikes() + 1));
                data.setLike(1);
                likes.setText(data.getNumLikes() + "");
            }
        });

        int sizeGallery = data.getImgs().size();

        if (sizeGallery >= 3) {
            imgPrimariaCard.setVisibility(View.VISIBLE);
            imgSecundariaCard.setVisibility(View.VISIBLE);
            imgTernariaCard.setVisibility(View.VISIBLE);
            UTILS.getImageAt(activity, data.getImgs().get(0), imgFoco);
            UTILS.getImageAt(activity, data.getImgs().get(1), imgSecundaria);
            UTILS.getImageAt(activity, data.getImgs().get(2), imgTernaria);
        } else if (sizeGallery == 2) {
            imgPrimariaCard.setVisibility(View.VISIBLE);
            imgSecundariaCard.setVisibility(View.VISIBLE);
            UTILS.getImageAt(activity, data.getImgs().get(0), imgFoco);
            UTILS.getImageAt(activity, data.getImgs().get(1), imgSecundaria);
            imgTernariaCard.setVisibility(View.GONE);
        } else if (sizeGallery == 1) {
            imgPrimariaCard.setVisibility(View.VISIBLE);
            UTILS.getImageAt(activity, data.getImgs().get(0), imgFoco);
            llImgsRight.setVisibility(View.GONE);
            imgSecundariaCard.setVisibility(View.GONE);
            imgTernariaCard.setVisibility(View.GONE);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Math.round(UTILS.pxFromDp(activity, 150)), Math.round(UTILS.pxFromDp(activity, 205)));

            params.height = Math.round(UTILS.pxFromDp(activity, 220));
            params.width = Math.round(UTILS.pxFromDp(activity, 405));

            params.setMargins(0, 0, 0, 0);
            imgFoco.setLayoutParams(params);
            imgFoco.requestLayout();
        } else {
            imgPrimariaCard.setVisibility(View.GONE);
            imgSecundariaCard.setVisibility(View.GONE);
            imgTernariaCard.setVisibility(View.GONE);
        }

        if (sizeGallery > 3) {
            countImgs.setText("+" + (sizeGallery - 3));
        }

        cat.setText(data.getCat());

        horario.setText(UTILS.PrettyTime(activity, data.getDate()));


        contentImagens.setOnClickListener(view12 -> initSlides(data.getImgs()));
        llImgsRight.setOnClickListener(view1 -> initSlides(data.getImgs()));


        if (end) {
            int mH, mV;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                mH = 13;
                mV = 80;
            } else {
                mH = 20;
                mV = 90;
            }
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) card.getLayoutParams();
            layoutParams.setMargins((int) UTILS.dpToPx(activity, mH), 0, (int) UTILS.dpToPx(activity, mH), (int) UTILS.dpToPx(activity, mV));
            card.requestLayout();
        } else {
            int mH, mV;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                mH = 13;
                mV = 5;
            } else {
                mH = 20;
                mV = 20;
            }

            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) card.getLayoutParams();
            layoutParams.setMargins((int) UTILS.dpToPx(activity, mH), 0, (int) UTILS.dpToPx(activity, mH), (int) UTILS.dpToPx(activity, mV));
            card.requestLayout();
        }

        mHolder.contentItens.addView(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    void initPostSponsored(GalleryData data, boolean end) {
        View view = LayoutInflater.from(activity).inflate(R.layout.include_card_tipo_2, mHolder.contentItens, false);

        View card = view.findViewById(R.id.container);
        TextView horario = view.findViewById(R.id.horario);
        TextView nome = view.findViewById(R.id.nome);
        TextView nivel = view.findViewById(R.id.nivel);
        TextView countImgs = view.findViewById(R.id.count_imgs);
        TextView desc = view.findViewById(R.id.desc);
        ImageView avatar = view.findViewById(R.id.avatar);
        CardView imgSecundariaCard = view.findViewById(R.id.card_img_secundaria);
        CardView imgTernariaCard = view.findViewById(R.id.card_img_ternario);
        CardView imgPrimariaCard = view.findViewById(R.id.img_principal);
        ImageView imgFoco = view.findViewById(R.id.img_foco);
        ImageView imgSecundaria = view.findViewById(R.id.img_secondary);
        ImageView imgTernaria = view.findViewById(R.id.img_ternaria);
        ImageView like = view.findViewById(R.id.ic_like);
        ImageView imgComentarios = view.findViewById(R.id.ic_comentarios);
        TextView likes = view.findViewById(R.id.num_likes);
        TextView comentarios = view.findViewById(R.id.num_comentarios);
        TextView cat = view.findViewById(R.id.cat);
        LinearLayout contentImagens = view.findViewById(R.id.llImages);
        LinearLayout llImgsRight = view.findViewById(R.id.ll_img_right);
        LinearLayout sponsored = view.findViewById(R.id.sponsored);
        TextView sponsorName = view.findViewById(R.id.name_sponsor);
        ImageView more = view.findViewById(R.id.more);

        more.setVisibility(View.GONE);

/*        card.setOnClickListener(v -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_HOME_OPEN_ARENA, data.getId()).execute();
            mfragmentViewModel.changeFragment(new ArenaViewPostFragment((data.getId())), "ArenaViewPostFragment", 3);
        });*/

        sponsored.setVisibility(View.VISIBLE);
        nivel.setVisibility(View.GONE);

        Drawable drawablePlace = getResources().getDrawable(R.drawable.ic_placeholderuser);
        drawablePlace.setTint(getResources().getColor(R.color.ThirdBackground));

        UTILS.getImageAt(activity, data.getAvatar(), avatar, new RequestOptions()
                .circleCrop()
                .placeholder(drawablePlace)
                .error(drawablePlace));

        sponsorName.setText(data.getUserType());
        desc.setText(data.getText());
        nome.setText(data.getName());
        //comentarios.setText(data.getComments() + "");
        imgComentarios.setVisibility(View.INVISIBLE);
        likes.setText(data.getNumLikes() + "");

        like.setImageDrawable(data.isLike() ? getResources().getDrawable(R.drawable.ic_like) : getResources().getDrawable(R.drawable.ic_unlike));
        like.setOnClickListener(v -> {
            homeViewModel.postLike(data.getId() + "");
            if (data.isLike()) {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_HOME_UNLIKE_ARENA, data.getId()).execute();
                like.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlike));
                data.setNumLikes((data.getNumLikes() - 1));
                data.setLike(0);
                likes.setText(data.getNumLikes() + "");
            } else {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_HOME_LIKE_ARENA, data.getId()).execute();
                like.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                data.setNumLikes((data.getNumLikes() + 1));
                data.setLike(1);
                likes.setText(data.getNumLikes() + "");
            }
        });

        int sizeGallery = data.getImages().size();

        if (sizeGallery >= 3) {
            imgPrimariaCard.setVisibility(View.VISIBLE);
            imgSecundariaCard.setVisibility(View.VISIBLE);
            imgTernariaCard.setVisibility(View.VISIBLE);
            UTILS.getImageAt(activity, data.getImages().get(0), imgFoco);
            UTILS.getImageAt(activity, data.getImages().get(1), imgSecundaria);
            UTILS.getImageAt(activity, data.getImages().get(2), imgTernaria);
        } else if (sizeGallery == 2) {
            imgPrimariaCard.setVisibility(View.VISIBLE);
            imgSecundariaCard.setVisibility(View.VISIBLE);
            UTILS.getImageAt(activity, data.getImages().get(0), imgFoco);
            UTILS.getImageAt(activity, data.getImages().get(1), imgSecundaria);
            imgTernariaCard.setVisibility(View.GONE);
        } else if (sizeGallery == 1) {
            imgPrimariaCard.setVisibility(View.VISIBLE);
            UTILS.getImageAt(activity, data.getImages().get(0), imgFoco);
            llImgsRight.setVisibility(View.GONE);
            imgSecundariaCard.setVisibility(View.GONE);
            imgTernariaCard.setVisibility(View.GONE);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Math.round(UTILS.pxFromDp(activity, 150)), Math.round(UTILS.pxFromDp(activity, 205)));

            params.height = Math.round(UTILS.pxFromDp(activity, 220));
            params.width = Math.round(UTILS.pxFromDp(activity, 405));

            params.setMargins(0, 0, 0, 0);
            imgFoco.setLayoutParams(params);
            imgFoco.requestLayout();
        } else {
            imgPrimariaCard.setVisibility(View.GONE);
            imgSecundariaCard.setVisibility(View.GONE);
            imgTernariaCard.setVisibility(View.GONE);
        }

        if (sizeGallery > 3) {
            countImgs.setText("+" + (sizeGallery - 3));
        }

        if (data.getCat() != null && !data.getCat().equals("")) {
            cat.setText(data.getCat());
        } else {
            cat.setVisibility(View.GONE);
        }

        horario.setText(UTILS.PrettyTime(activity, data.getDate()));


        contentImagens.setOnClickListener(view12 -> initSlides(data.getImages()));
        llImgsRight.setOnClickListener(view1 -> initSlides(data.getImages()));


        if (end) {
            int mH, mV;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                mH = 13;
                mV = 80;
            } else {
                mH = 20;
                mV = 90;
            }
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) card.getLayoutParams();
            layoutParams.setMargins((int) UTILS.dpToPx(activity, mH), 0, (int) UTILS.dpToPx(activity, mH), (int) UTILS.dpToPx(activity, mV));
            card.requestLayout();
        } else {
            int mH, mV;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                mH = 13;
                mV = 5;
            } else {
                mH = 20;
                mV = 20;
            }

            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) card.getLayoutParams();
            layoutParams.setMargins((int) UTILS.dpToPx(activity, mH), 0, (int) UTILS.dpToPx(activity, mH), (int) UTILS.dpToPx(activity, mV));
            card.requestLayout();
        }

        mHolder.contentItens.addView(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ITEM_NEWS) {
            if (data != null) {
                int frag = data.getExtras().getInt("frag");
                switch (frag) {
                    case 1: {
                        mfragmentViewModel.changeFragment(new HomeFragment(), "HomeFragment", 3);
                    }
                    break;
                    case 2: {
/*                        StartMenuActivity startMenuActivity = new StartMenuActivity();
                        startMenuActivity.changeCurrentScreen(StartMenuActivity.Screen.Arena);*/
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

    void initSlides(ArrayList<String> imgs) {
        Intent intent = new Intent(activity, ArenaViewPhotoActivity.class);
        intent.putExtra("imgs", imgs);
        startActivity(intent);
    }

    void setQuizAnswer(int idQuestion, int idAnswer) {
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_HOME_ANSWER_QUIZ, idQuestion).execute();
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("idQuestion", idQuestion);
        params.put("idAnswer", idAnswer);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SET_QUIZ_ANSWER, OP_ID_QUIZ_ANSWER, this).execute(params);
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

    @SuppressLint("SetTextI18n")
    @Override
    public void OnAsyncOperationSuccess(int opId, JSONObject response) {
        switch (opId) {
            case OP_ID_QUIZ_ANSWER: {
                if (response.has("us")) {
                    TextView coins = getActivity().findViewById(R.id.txt_moedas);
                    int coinsValue = Integer.parseInt(coins.getText().toString());
                    coinsValue = coinsValue + points;
                    coins.setText(coinsValue + "");
                    points = 0;
                    userViewModel.getUserProfile();
                }
            }
            break;
        }
    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {
        switch (opId) {
            case OP_ID_QUIZ_ANSWER: {
                if (response.has("msg")) {
                    try {
                        Toast.makeText(activity, response.getString("msg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    class ViewHolder {
        View cardNews;
        ImageView img_news;
        ImageView imgFotoUser;
        TextView desc;
        TextView txtNomeUser;
        LinearLayout contentItens;
        Button button;
        SwipeRefreshLayout pullToRefresh;

        public ViewHolder(View view) {
            button = view.findViewById(R.id.button);
            cardNews = view.findViewById(R.id.card_news);
            desc = view.findViewById(R.id.desc_tipo_0);
            img_news = view.findViewById(R.id.img_card1);
            pullToRefresh = view.findViewById(R.id.pullToRefresh);
            contentItens = view.findViewById(R.id.containerHomeItens);
            imgFotoUser = activity.findViewById(R.id.imgFotoUser);
            txtNomeUser = activity.findViewById(R.id.txtNomeUser);
        }
    }
}