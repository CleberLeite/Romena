package com.frf.app.fragments;

import static com.frf.app.activitys.MainActivity.emptyAsync;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.frf.app.R;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.activitys.TermsAndPrivacyPolicyActivity;
import com.frf.app.data.Answers;
import com.frf.app.data.QuizData;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

public class QuizFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {

    private ViewHolder mHolder;
    private static final int OP_ID_QUIZ = 0;
    private static final int OP_ID_QUIZ_ANSWER = 1;

    private int points = 0;

    public QuizFragment(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        mHolder = new ViewHolder(view);

        initActions();
        getQuizzes();

        try {
            ((StartMenuActivity)requireActivity()).resetBottomMenu();
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    void initActions() {
        mHolder.reg.setOnClickListener(view -> {
            Intent intent = new Intent(activity, TermsAndPrivacyPolicyActivity.class);
            intent.putExtra("link", getString(R.string.link_terms_quiz));
            intent.putExtra("title", getString(R.string.regulament));
            activity.startActivity(intent);
        });
    }

    void getQuizzes() {
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_QUIZ, OP_ID_QUIZ, this).execute();
    }

    void setQuizAnswer(int idQuestion, int idAnswer) {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("idQuestion", idQuestion);
        params.put("idAnswer", idAnswer);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SET_QUIZ_ANSWER, OP_ID_QUIZ_ANSWER, this).execute(params);
    }

    void initQuestions(ArrayList<QuizData> normal, ArrayList<QuizData> anterior) {
        for (QuizData data : normal) {
            addQuiz(data);
        }

        for (QuizData data : anterior) {
            addAnterior(data);
        }
    }

    void addQuiz(QuizData data) {
        View view = LayoutInflater.from(activity).inflate(R.layout.include_quiz_item, mHolder.quiz, false);

        TextView titleQuiz = view.findViewById(R.id.titleQuiz);
        CardView cardQuiz1 = view.findViewById(R.id.cardQuiz1);
        LinearLayout opcCard2 = view.findViewById(R.id.opcCard2);
        LinearLayout listAlternatives = view.findViewById(R.id.list_alternatives);
        ImageView imgCoin = view.findViewById(R.id.img_coin);
        TextView txtMoedas = view.findViewById(R.id.txt_moedas);

        if (data.getAnswers().get(0).getImg() != null) {

            txtMoedas.setText(data.getReward().equals("") ? data.getReward() : "+" + data.getReward());
            titleQuiz.setText(data.getQuestion());
            if (data.getReward().equals("0")) {
                imgCoin.setVisibility(View.INVISIBLE);
                txtMoedas.setVisibility(View.INVISIBLE);
            }

            cardQuiz1.setOnClickListener(view2 -> {
                if (listAlternatives.getVisibility() != View.VISIBLE) {
                    new AsyncOperation((Activity) activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_QUIZ_EXPAND_QUIZ, data.getId()).execute();
                    TransitionManager.beginDelayedTransition(listAlternatives, new AutoTransition());
                    listAlternatives.setVisibility(View.VISIBLE);

                    final int[] count = {1};
                    final LinearLayout[] linearLayout = {new LinearLayout(activity)};
                    linearLayout[0].setOrientation(LinearLayout.HORIZONTAL);

                    if (listAlternatives.getTag() == null) {
                        for (int i = 0; i < data.getAnswers().size(); i++) {
                            Answers answers = data.getAnswers().get(i);
                            View elementView = LayoutInflater.from(activity).inflate(R.layout.include_alternatives_img, listAlternatives, false);

                            CardView container = elementView.findViewById(R.id.container);
                            View check = elementView.findViewById(R.id.check);
                            ImageView img = elementView.findViewById(R.id.img);
                            ImageView iconCheck = elementView.findViewById(R.id.icon_check);
                            View mask = elementView.findViewById(R.id.mask_select);

                            UTILS.getImageAt(activity, answers.getImg(), img);

                            int finalI1 = i;
                            container.setOnClickListener(v -> {
                                points = Integer.parseInt(data.getReward());
                                setQuizAnswer(Integer.parseInt(data.getId()), answers.getId());

                                if (answers.getCorrect() == 1) {
                                    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_checked).mutate();
                                    iconCheck.setImageDrawable(drawable);
                                } else {
                                    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_close_red).mutate();
                                    iconCheck.setImageDrawable(drawable);
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

                                for (int y = 0; y < data.getAnswers().size(); y++) {

                                    Answers answersy = data.getAnswers().get(y);
                                    View elementViewy = LayoutInflater.from(activity).inflate(R.layout.include_alternatives_img, listAlternatives, false);

                                    View checky = elementViewy.findViewById(R.id.check);
                                    ImageView imgy = elementViewy.findViewById(R.id.img);
                                    ImageView iconChecky = elementViewy.findViewById(R.id.icon_check);
                                    View masky = elementViewy.findViewById(R.id.mask_select);

                                    UTILS.getImageAt(activity, answersy.getImg(), imgy);

                                    if (y == finalI) {
                                        if (answersy.getCorrect() == 1) {
                                            trigger = false;
                                            Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_checked).mutate();
                                            iconChecky.setImageDrawable(drawable);
                                        } else {
                                            Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_close_red).mutate();
                                            iconChecky.setImageDrawable(drawable);
                                        }
                                        data.setIdAnswered(answersy.getId());
                                        masky.setVisibility(View.VISIBLE);
                                        checky.setVisibility(View.VISIBLE);
                                    } else {
                                        if (trigger) {
                                            if (answersy.getCorrect() == 1) {
                                                checky.setVisibility(View.VISIBLE);
                                                Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_checked).mutate();
                                                iconChecky.setImageDrawable(drawable);
                                            }
                                        }
                                    }

                                    if (count[0] <= 2) {
                                        linearLayout[0].addView(elementViewy);
                                        if (y == data.getAnswers().size() - 1 && count[0] == 1) {
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
                                if (i == data.getAnswers().size() - 1 && count[0] == 1) {
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
                    cardQuiz1.setClickable(false);
                    if (listAlternatives.getTag() == null) {
                        TransitionManager.beginDelayedTransition(listAlternatives, new AutoTransition());
                        listAlternatives.setVisibility(View.GONE);
                        listAlternatives.removeAllViews();
                        cardQuiz1.setClickable(true);
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
                                        mHolder.quiz.removeView(view);
                                        addAnterior(data);
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
        }else {
            txtMoedas.setText(data.getReward().equals("") ? data.getReward() : "+" + data.getReward());
            titleQuiz.setText(data.getQuestion());

            if (data.getReward().equals("0")) {
                imgCoin.setVisibility(View.INVISIBLE);
                txtMoedas.setVisibility(View.INVISIBLE);
            }

            cardQuiz1.setOnClickListener(view2 -> {

                if (opcCard2.getVisibility() != View.VISIBLE) {
                    new AsyncOperation((Activity) activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_QUIZ_EXPAND_QUIZ, data.getId()).execute();
                    TransitionManager.beginDelayedTransition(opcCard2, new AutoTransition());
                    opcCard2.setVisibility(View.VISIBLE);

                    LinearLayout linearLayout = new LinearLayout(activity);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                    for(int i =0; i < data.getAnswers().size(); i++) {
                        Answers answers = data.getAnswers().get(i);
                        View elementView = LayoutInflater.from(activity).inflate(R.layout.include_button_quiz, opcCard2, false);

                        Button ans = elementView.findViewById(R.id.btn_opc);
                        ans.setText(answers.getTitle());

                        int finalI = i;
                        final boolean[] trigger = {true};

                        ans.setOnClickListener(v -> {
                            points = Integer.parseInt(data.getReward());
                            setQuizAnswer(Integer.parseInt(data.getId()), answers.getId());

                            opcCard2.removeAllViews();
                            opcCard2.setTag(true);

                            for(int y =0; y < data.getAnswers().size(); y++){

                                Answers answersy = data.getAnswers().get(y);
                                View elementViewy = LayoutInflater.from(activity).inflate(R.layout.include_button_quiz, opcCard2, false);

                                Button ansy = elementViewy.findViewById(R.id.btn_opc);
                                ansy.setText(answersy.getTitle());

                                if (y == finalI) {
                                    if (answersy.getCorrect() == 1) {
                                        trigger[0] = false;
                                        btnSelectedCorrect(ansy);
                                    } else {
                                        btnSelectedIncorrect(ansy);
                                    }
                                    ansy.setText("           "+answersy.getTitle());
                                    data.setIdAnswered(answersy.getId());
                                }else {
                                    if (trigger[0]) {
                                        if (answersy.getCorrect() == 1) {
                                            ansy.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checked, 0);
                                            ansy.setText("           "+answersy.getTitle());
                                        }
                                    }
                                }

                               opcCard2.addView(elementViewy);
                            }
                        });

                        opcCard2.addView(elementView);
                    }
                } else {
                    cardQuiz1.setClickable(false);
                    if (opcCard2.getTag() == null) {
                        TransitionManager.beginDelayedTransition(opcCard2, new AutoTransition());
                        opcCard2.setVisibility(View.GONE);
                        opcCard2.removeAllViews();
                        cardQuiz1.setClickable(true);
                    }else {
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
                                        mHolder.quiz.removeView(view);
                                        addAnterior(data);
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
                            public void onTransitionCancel(Transition transition) {}

                            @Override
                            public void onTransitionPause(Transition transition) {}

                            @Override
                            public void onTransitionResume(Transition transition) {}
                        });
                        TransitionManager.beginDelayedTransition(opcCard2, autoTransition);
                        opcCard2.setVisibility(View.GONE);
                    }
                }
            });
        }

        mHolder.quiz.addView(view);
    }

    void addAnterior(QuizData data) {
        View view = LayoutInflater.from(activity).inflate(R.layout.include_quiz_item, mHolder.anterior, false);

        TextView titleQuiz = view.findViewById(R.id.titleQuiz);
        CardView cardQuiz1 = view.findViewById(R.id.cardQuiz1);
        LinearLayout opcCard2 = view.findViewById(R.id.opcCard2);
        LinearLayout opcCard1 = view.findViewById(R.id.opcCard1);
        LinearLayout listAlternatives = view.findViewById(R.id.list_alternatives);
        ImageView imgCoin = view.findViewById(R.id.img_coin);
        TextView txtMoedas = view.findViewById(R.id.txt_moedas);
        TextView txtTime = view.findViewById(R.id.cardTime);

        if (data.getAnswers().get(0).getImg() != null) {
            cardQuiz1.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.ColorWhitte));
            titleQuiz.setTextColor(ContextCompat.getColorStateList(activity, R.color.ColorBlueGray800));
            txtTime.setTextColor(ContextCompat.getColorStateList(activity, R.color.ColorBlueGray500));
            txtMoedas.setTextColor(ContextCompat.getColorStateList(activity, R.color.PrimaryColorText));
            imgCoin.setImageDrawable(activity.getDrawable(R.drawable.ic_coin_blue));
            txtMoedas.setText(data.getReward().equals("0") ? data.getReward() : "+" + data.getReward());
            titleQuiz.setText(data.getQuestion());

            if (data.getReward().equals("0")) {
                imgCoin.setVisibility(View.INVISIBLE);
                txtMoedas.setVisibility(View.INVISIBLE);
            }

            cardQuiz1.setOnClickListener(view2 -> {
                if (listAlternatives.getVisibility() != View.VISIBLE){
                    new AsyncOperation((Activity) activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_QUIZ_EXPAND_QUIZ, data.getId()).execute();
                    TransitionManager.beginDelayedTransition(listAlternatives, new AutoTransition());
                    listAlternatives.setVisibility(View.VISIBLE);

                    final int[] count = {1};
                    final LinearLayout[] linearLayout = {new LinearLayout(activity)};
                    linearLayout[0].setOrientation(LinearLayout.HORIZONTAL);

                    int i = 0;

                    for(Answers answers : data.getAnswers()){
                        View elementView = LayoutInflater.from(activity).inflate(R.layout.include_alternatives_img, listAlternatives, false);

                        View check = elementView.findViewById(R.id.check);
                        ImageView img = elementView.findViewById(R.id.img);
                        ImageView iconCheck = elementView.findViewById(R.id.icon_check);
                        View mask = elementView.findViewById(R.id.mask_select);

                        UTILS.getImageAt(activity, answers.getImg(), img);

                        if (answers.getCorrect() == 1) {
                            check.setVisibility(View.VISIBLE);
                            Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_checked).mutate();
                            iconCheck.setImageDrawable(drawable);

                            if (data.getIdAnswered() == answers.getId()) {
                                mask.setVisibility(View.VISIBLE);
                            }
                        }else {
                            if (data.getIdAnswered() == answers.getId()) {
                                check.setVisibility(View.VISIBLE);
                                if (answers.getCorrect() == 0) {
                                    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_close_red).mutate();
                                    iconCheck.setImageDrawable(drawable);
                                    mask.setVisibility(View.VISIBLE);
                                }else {
                                    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_checked).mutate();
                                    iconCheck.setImageDrawable(drawable);
                                    mask.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        if (count[0] <= 2) {
                            linearLayout[0].addView(elementView);
                            if (i == data.getAnswers().size() -1 && count[0] == 1) {
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
        }else {
            cardQuiz1.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.ColorWhitte));
            titleQuiz.setTextColor(ContextCompat.getColorStateList(activity, R.color.ColorBlueGray800));
            txtTime.setTextColor(ContextCompat.getColorStateList(activity, R.color.ColorBlueGray500));
            txtMoedas.setTextColor(ContextCompat.getColorStateList(activity, R.color.PrimaryColorText));
            imgCoin.setImageDrawable(activity.getDrawable(R.drawable.ic_coin_blue));
            txtMoedas.setText(data.getReward().equals("") ? data.getReward() : "+" + data.getReward());
            titleQuiz.setText(data.getQuestion());

            if (data.getReward().equals("0")) {
                imgCoin.setVisibility(View.INVISIBLE);
                txtMoedas.setVisibility(View.INVISIBLE);
            }

            cardQuiz1.setOnClickListener(view2 -> {
                if (opcCard2.getVisibility() != View.VISIBLE) {
                    new AsyncOperation((Activity) activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_QUIZ_EXPAND_QUIZ, data.getId()).execute();
                    TransitionManager.beginDelayedTransition(opcCard1, new AutoTransition());
                    opcCard2.setVisibility(View.VISIBLE);
                    for (Answers answers : data.getAnswers()) {
                        View elementView = LayoutInflater.from(activity).inflate(R.layout.include_button_quiz, opcCard2, false);

                        Button ans = elementView.findViewById(R.id.btn_opc);
                        ans.setText(answers.getTitle());

                        if (answers.getCorrect() == 1) {
                            ans.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checked, 0);
                            if (data.getIdAnswered() == answers.getId()) {
                                btnSelectedCorrect(ans);
                            }
                            ans.setText("           " + answers.getTitle());
                        } else {
                            if (data.getIdAnswered() == answers.getId()) {
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

        mHolder.anterior.addView(view);
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
            case OP_ID_QUIZ: {
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
                            QuizData[] data = gson.fromJson(obj.getString("itens"), QuizData[].class);

                            ArrayList<QuizData> anterior = new ArrayList<>();
                            ArrayList<QuizData> normal = new ArrayList<>();
                            if (data != null && data.length > 0) {

                                for (int i = 0; i <= data.length - 1; i++) {
                                    if (data[i].getIdAnswered() != 0) {
                                        anterior.add(data[i]);
                                    } else {
                                        normal.add(data[i]);
                                    }
                                }

                                initQuestions(normal, anterior);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            break;
            case OP_ID_QUIZ_ANSWER: {
                if (response.has("us")) {
                    TextView coins = activity.findViewById(R.id.txt_moedas);
                    int coinsValue = Integer.parseInt(coins.getText().toString());
                    coinsValue = coinsValue + points;
                    coins.setText(coinsValue+"");
                    points = 0;
                    ((StartMenuActivity)activity).setIsUpdateProfile(true);
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

    @Override
    public void bind() {
        super.bind();
        ((StartMenuActivity)getActivity()).setQuizIsOpen(true);
    }

    @Override
    public void unbind() {
        super.unbind();
        ((StartMenuActivity)getActivity()).setQuizIsOpen(false);
    }

    public void btnSelected(Button btn) {
        btn.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.ColorBlue));
        btn.setTextColor(ContextCompat.getColorStateList(activity, R.color.ColorWhitte));
        btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checked, 0);
    }

    static class ViewHolder {
        LinearLayout quiz;
        LinearLayout anterior;
        TextView reg;

        public ViewHolder(View view) {
            quiz = view.findViewById(R.id.quiz);
            anterior = view.findViewById(R.id.anterior);
            reg = view.findViewById(R.id.reg);
        }
    }
}