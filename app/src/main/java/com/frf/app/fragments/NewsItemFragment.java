package com.frf.app.fragments;

import static com.frf.app.activitys.MainActivity.emptyAsync;
import static com.frf.app.fragments.BottomSheetMoreFragment.NEWS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.frf.app.BuildConfig;
import com.frf.app.R;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.adapter.CommentAdapter;
import com.frf.app.adapter.RelatedAdapter;
import com.frf.app.data.CommentData;
import com.frf.app.data.NewsData;
import com.frf.app.data.RelatedData;
import com.frf.app.data.RepliesData;
import com.frf.app.informations.UserInformation;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.URLImageParser;
import com.frf.app.utils.UTILS;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;


public class NewsItemFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {

    private int id;
    private ViewHolder mHolder;

    private static final int OP_GET_NEWS = 0;
    private static final int OP_GET_NEWS_COMENT = 1;
    private static final int OP_SET_NEWS_FAVORITE = 2;
    private static final int OP_SET_NEWS_COMMENT = 3;
    private static final int OP_DELET_COMMENT = 4;
    private static final int OP_GET_NEWS_COMMENT_REPLIES = 5;

    CommentAdapter adapter;
    ArrayList<CommentData> dataComment = new ArrayList<>();
    private LinearLayout contentItens;
    private boolean refreshList = true;
    private int idComment = -1;
    private boolean isPush = false;
    private boolean more = false;

    private enum Screen {
        up,
        down,
    }

    Screen screenStates = Screen.up;

    public NewsItemFragment(int id) {
        this.id = id;
    }

    public NewsItemFragment(int id, boolean isPush) {
        this.id = id;
        this.isPush = isPush;
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Window window = activity.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        View view = inflater.inflate(R.layout.fragment_item_news, container, false);

        mHolder = new ViewHolder(view);

        //Adapt layout
        showSoftKeyboard(mHolder.comment);
        hideSoftKeyboard(mHolder.comment);

        getNews(id);
        mHolder.btnSubmit.setOnClickListener(v -> setComment(id, idComment));

        mHolder.imgExpand.setOnClickListener(view2 -> {
            if (screenStates == Screen.up) {
                mHolder.imgExpand.setImageDrawable(activity.getDrawable(R.drawable.ic_expand_down));

                ConstraintLayout.LayoutParams lp2 = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
                lp2.setMargins(0,0,0, UTILS.convertDpToPixels(100));
                mHolder.llNewsCard.setLayoutParams(lp2);

                UTILS.translate(mHolder.llNewsCard, UTILS.getToolBarHeight(activity) * 1.4f);
                screenStates = Screen.down;

            } else {
                mHolder.imgExpand.setImageDrawable(activity.getDrawable(R.drawable.ic_expande_up));

                ConstraintLayout.LayoutParams lp2 = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
                lp2.setMargins(0,0,0, UTILS.convertDpToPixels(250));
                mHolder.llNewsCard.setLayoutParams(lp2);

                UTILS.translate(mHolder.llNewsCard, UTILS.getToolBarHeight(activity) * 3.6f);
                screenStates = Screen.up;
            }
        });

        mHolder.contentInputText.setOnClickListener(v -> {
            mHolder.contentInputText.setVisibility(View.GONE);
            hideSoftKeyboard(mHolder.comment);
        });

        final boolean[] isKeyboardShowing = {false};
        mHolder.contentInputText.getViewTreeObserver().addOnGlobalLayoutListener(
                () -> {
                    Rect r = new Rect();
                    mHolder.contentInputText.getWindowVisibleDisplayFrame(r);
                    int screenHeight = mHolder.contentInputText.getRootView().getHeight();

                    // r.bottom is the position above soft keypad or device button.
                    // if keypad is shown, the r.bottom is smaller than that before.
                    int keypadHeight = screenHeight - r.bottom;

                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        // keyboard is opened
                        if (!isKeyboardShowing[0]) {
                            isKeyboardShowing[0] = true;
                        }
                    }
                    else {
                        // keyboard is closed
                        if (isKeyboardShowing[0]) {
                            isKeyboardShowing[0] = false;
                            mHolder.contentInputText.setVisibility(View.GONE);
                        }
                    }
                });

        mHolder.back.setOnClickListener(view2 -> ((StartMenuActivity)activity).onBackPressed());

        initComment();

        return view;
    }

    @Override
    public void bind() {
        super.bind();
        ((StartMenuActivity)getActivity()).changeHeader(5);
    }

    void getNews(Integer id) {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_NEWS, OP_GET_NEWS, this).execute(params);
        getNewsComent(id, more);
    }

    void getNewsComent(Integer id, boolean more) {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        if (more) {
            params.put("all", true);
        }
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_NOTIFICATION_NEWS, OP_GET_NEWS_COMENT, this).execute(params);
    }

    void setComment(int id, int idComment) {
        mHolder.load.setVisibility(View.VISIBLE);
        hideSoftKeyboard(mHolder.comment);
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("idPost", id);
        if (idComment != -1) {
            params.put("idReplyComment", idComment);
        }
        params.put("text", mHolder.comment.getText().toString());
        mHolder.comment.setText("");
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SET_NOTIFICATION_NEWS, OP_SET_NEWS_COMMENT, this).execute(params);
    }

    private void initComment() {
        adapter = new CommentAdapter(activity, dataComment, getChildFragmentManager(), NEWS, new CommentAdapter.Listener() {
            @Override
            public void isPressed(int id) {
                alertDeletComment(id, null, null);
            }

            @Override
            public void isComment(int id) {
                idComment = id;
                showSoftKeyboard(mHolder.contentInputText);
            }

            @Override
            public void getPostReplies(int id, LinearLayout contentItens) {
                NewsItemFragment.this.contentItens = contentItens;
                GetPostReplies(id);
            }

            @Override
            public void onCallBack(String id) {
                getNewsComent(NewsItemFragment.this.id, more);
            }
        });

        mHolder.llComentary.setOnClickListener(v -> {
            showSoftKeyboard(mHolder.contentInputText);
            idComment = -1;
        });

        mHolder.recycleComments.setAdapter(adapter);
        mHolder.recycleComments.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
    }

    private void alertDeletComment(int id, LinearLayout CI, View view) {
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NEWS_DELETE_COMMENT, id).execute();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AlertDialogCustom);
        alertDialog.setMessage(getResources().getString(R.string.sunteti_sigur_ca_doriti_sa_stergeti_acest_comentariu))
                .setPositiveButton(R.string.sim, (dialog, which) -> {
                    new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NEWS_DELETE_COMMENT_CONFIRM, id).execute();
                    if (CI != null && view != null) {
                        refreshList = false;
                        CI.removeView(view);
                    }
                    setCommentDelet(id);
                })
                .setNegativeButton(R.string.nao, (dialog, which) -> {
                    new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NEWS_DELETE_COMMENT_CANCEL, id).execute();
                    try{dialog.dismiss();}catch (Exception e){e.printStackTrace();}
                });
        AlertDialog alert = alertDialog.create();

        alert.setOnShowListener(arg0 -> {
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.PrimaryColorText));
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorRed600));
        });
        alert.show();
    }

    public void showSoftKeyboard(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        }

        DrawerLayout dl = getActivity().findViewById(R.id.drawerLayout);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,0,0, UTILS.convertDpToPixels(0));
        dl.setLayoutParams(lp);
        mHolder.comment.requestFocus();
        view.setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mHolder.comment, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setCommentDelet(int id) {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_DELETE_NOTIFICATION_NEWS, OP_DELET_COMMENT, this).execute(params);
    }

    private void GetPostReplies(int idComment) {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("idPost", id);
        params.put("idComment", idComment);
        new AsyncOperation(activity, AsyncOperation.WS_METHOD_GET_NEWS_COMMENT_REPLIES, OP_GET_NEWS_COMMENT_REPLIES, this).execute(params);
    }

    public void share(Bitmap bitmap) {
        // save bitmap to cache directory
        try {
            File cachePath = new File(activity.getCacheDir(), "imageview");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        File imagePath = new File(activity.getCacheDir(), "imageview");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, activity.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.setType("image/png");
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.send_to)));
        }
    }

    private void initNews(NewsData data) {

        if (data.getShare() != null) {
            mHolder.share.setVisibility(View.VISIBLE);
        }

        mHolder.share.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String tittle = data.getTitle();
            String body = data.getShare();
            intent.putExtra(Intent.EXTRA_SUBJECT, tittle);
            intent.putExtra(Intent.EXTRA_TEXT,  body + " \n" + tittle);
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.imparte_cu)));
        });

        initRelated(data.getRelated());
        String htmlData = data.getText();
        String showData = htmlData.replace("\n", "");

        URLImageParser p = new URLImageParser(mHolder.txtNewsDescription, activity);
        Spanned htmlAsSpanned = Html.fromHtml(showData,p,null);


        mHolder.txtNewsDescription.setText(htmlAsSpanned);
        mHolder.txtNewsDescription.setTextColor(activity.getResources().getColor(R.color.ColorBlueGray700));

        UTILS.getImageAt(activity, data.getImg(), mHolder.imgNewsBack);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dataa = data.getDateHour().replaceAll("-", "/");
        try {
            Date date = formatter.parse(dataa);
            mHolder.txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(date).replaceAll("-", "."));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mHolder.txtNoticeCateg.setText(data.getNoticeCateg());
        mHolder.txtTitle.setText(data.getTitle());
        mHolder.favorite.setOnClickListener(v -> {
            if(data.getFav() == 1) {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NEWS_FAVORITE_NEWS, data.getId()).execute();
            }else {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NEWS_DISFAVOR_NEWS, data.getId()).execute();
            }

            setNewsFavorite(data.getId());
        });

        mHolder.favorite.setBackground(data.getFav() == 1 ? activity.getResources().getDrawable(R.drawable.ic_baseline_bookmark) : activity.getResources().getDrawable(R.drawable.ic_baseline_bookmark_border));

    }

    private void addReplies(ArrayList<RepliesData> repliesData) {
        if (contentItens != null) {
            contentItens.removeAllViews();
            for (RepliesData data : repliesData) {
                View view = LayoutInflater.from(activity).inflate(R.layout.include_comentary_replies, contentItens, false);
                ImageView avatar = view.findViewById(R.id.avatar);
                TextView txtUserName = view.findViewById(R.id.txtUserName);
                TextView typeFan = view.findViewById(R.id.typeFan);
                TextView text = view.findViewById(R.id.text);
                ImageView ReplyBntDelete = view.findViewById(R.id.ReplyBntDelete);
                TextView hours = view.findViewById(R.id.hours);

                @SuppressLint("UseCompatLoadingForDrawables")
                Drawable drawablePlace = getResources().getDrawable(R.drawable.ic_placeholderuser);
                drawablePlace.setTint(getResources().getColor(R.color.ThirdBackground));

                ReplyBntDelete.setVisibility(UserInformation.getUserId().equals(data.getIdUser()) ? View.VISIBLE : View.GONE);
                LinearLayout CI = contentItens;
                ReplyBntDelete.setOnClickListener(v -> alertDeletComment(data.getId(), CI, view));

                PrettyTime prettyTime = new PrettyTime(getResources().getConfiguration().locale);

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat dateFormatData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Date date = null;
                try {
                    date = dateFormatData.parse(data.getCreated_at());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                hours.setText(prettyTime.format(date));
                typeFan.setText(data.getUserType());
                text.setText(data.getText());
                txtUserName.setText(data.getUserName());
                UTILS.getImageAt(activity, data.getUserImg(), avatar, new RequestOptions().circleCrop().placeholder(drawablePlace));

                contentItens.addView(view);
            }
        }
    }

    private void initRelated(ArrayList<RelatedData> related) {

        RelatedAdapter adapter = new RelatedAdapter(activity, related, new RelatedAdapter.Listener() {
            @Override
            public void isPressed(RelatedData data) {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NEWS_OPEN_RELATED_NEWS, data.getId()).execute();
                id = data.getId();
                getNews(data.getId());
            }

            @Override
            public void onBottom() {

            }
        });

        mHolder.recicleNewsAll.setAdapter(adapter);
        mHolder.recicleNewsAll.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
    }

    void setNewsFavorite(int id) {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("idUser", UserInformation.getUserId());
        params.put("newsId", id);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SET_NEWS_FAVORITE, OP_SET_NEWS_FAVORITE, this).execute(params);
    }

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

    @SuppressLint("SetTextI18n")
    @Override
    public void OnAsyncOperationSuccess(int opId, JSONObject response) {
        switch (opId) {
            case OP_GET_NEWS: {
                int status = 0;
                if (response.has("Status")) {
                    try {
                        status = response.getInt("Status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (response.has("Object")) {
                    if (status == 200) {
                        try {
                            NewsData gData = new Gson().fromJson(response.getString("Object"), NewsData.class);
                            initNews(gData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    if (isPush) {
                        Toast.makeText(activity, getResources().getString(R.string.se_pare_ca_aceasta_veste_nu_mai_exista), Toast.LENGTH_LONG).show();
                        ((StartMenuActivity)activity).onBackPressed();
                    }else {
                        Toast.makeText(activity, getResources().getString(R.string.nu_se_pot_incarca_stirile), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case OP_SET_NEWS_FAVORITE: {
                if (response.has("msg")) {
                    try {
                        Toast.makeText(activity, response.getString("msg"), Toast.LENGTH_SHORT).show();
                        getNews(id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case OP_GET_NEWS_COMENT: {
                try {
                    if (!response.getString("Object").equals("[]")) {
                        JSONObject object = response.getJSONObject("Object");
                        CommentData[] gData = new Gson().fromJson(object.getString("itens"), CommentData[].class);
                        dataComment.clear();
                        dataComment.addAll(new ArrayList<>(Arrays.asList(gData)));

                        mHolder.contentMore.setOnClickListener(view -> {
                            more = true;
                            getNewsComent(id, more);
                            mHolder.contentMore.setVisibility(View.GONE);
                        });

                        if (object.has("commentsCount")) {
                            if ((object.getInt("commentsCount") - dataComment.size()) > 0) {
                                mHolder.txtMore.setText(getResources().getString(R.string.vezi_toate_comentariile) + "(" + (object.getInt("commentsCount") - dataComment.size()) + ") ");
                            } else {
                                mHolder.contentMore.setVisibility(View.GONE);
                            }
                        }

                        initComment();
                        mHolder.txtComentarii.setVisibility(View.VISIBLE);
                    }else {
                        mHolder.contentMore.setVisibility(View.GONE);
                        dataComment.clear();
                        initComment();
                        mHolder.txtComentarii.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHolder.contentMore.setVisibility(View.GONE);
                }
            }
            break;

            case OP_DELET_COMMENT: {
                try {
                    String msg = response.getString("msg");
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                    if (refreshList) {
                        getNewsComent(id, more);
                    }else {
                        refreshList = true;
                    }
                    mHolder.load.setVisibility(View.GONE);
                } catch (JSONException e) {
                    refreshList = true;
                }
            }
            break;
            case OP_SET_NEWS_COMMENT: {
                getNewsComent(id, more);
                mHolder.contentInputText.setVisibility(View.GONE);
                mHolder.load.setVisibility(View.GONE);
            }
            break;

            case OP_GET_NEWS_COMMENT_REPLIES: {
                int status = 0;
                if (response.has("Status")) {
                    try {
                        status = response.getInt("Status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (response.has("Object")) {
                    if (status == 200) {
                        try {
                            JSONObject object = response.getJSONObject("Object");
                            JSONArray itens = object.getJSONArray("itens");

                            RepliesData[] gData = new Gson().fromJson(itens.toString(), RepliesData[].class);
                            addReplies(new ArrayList<>(Arrays.asList(gData)));
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
        switch (opId) {
            case OP_SET_NEWS_FAVORITE: {
                if (response.has("msg")) {
                    try {
                        Toast.makeText(activity, response.getString("msg"), Toast.LENGTH_SHORT).show();
                        getNews(id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case OP_SET_NEWS_COMMENT: {
                getNews(id);
                mHolder.contentInputText.setVisibility(View.GONE);
                mHolder.load.setVisibility(View.GONE);
            }
            break;
            case OP_GET_NEWS: {
                if (isPush) {
                    Toast.makeText(activity, getResources().getString(R.string.se_pare_ca_aceasta_veste_nu_mai_exista), Toast.LENGTH_LONG).show();
                    ((StartMenuActivity)activity).onBackPressed();
                }else {
                    if (response.has("msg")) {
                        try {
                            Toast.makeText(activity, response.getString("msg"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, getResources().getString(R.string.nu_se_pot_incarca_stirile), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, getResources().getString(R.string.nu_se_pot_incarca_stirile), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        }
        DrawerLayout dl = getActivity().findViewById(R.id.drawerLayout);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,0,0, UTILS.convertDpToPixels(0));
        dl.setLayoutParams(lp);
    }

    static class ViewHolder {
        LinearLayout llNewsCard;
        ImageView imgExpand;
        ImageView favorite;
        ImageView imgNewsBack;
        TextView txtNewsDescription;
        TextView load;
        View header;
        View back;
        TextView txtNoticeCateg;
        TextView txtTitle;
        TextView txtDate;
        TextView txtMore;
        LinearLayout share;
        LinearLayout contentMore;
        RecyclerView recycleComments;
        RecyclerView recicleNewsAll;
        ConstraintLayout contentInputText;
        EditText comment;
        Button btnSubmit;
        NestedScrollView scroll;
        LinearLayout contentItens;
        View txtComentarii;
        View llComentary;
        ConstraintLayout rootView;

        public ViewHolder(View view) {
            txtComentarii = view.findViewById(R.id.txtComentarii);
            scroll = view.findViewById(R.id.scroll);
            btnSubmit = view.findViewById(R.id.btnSubmit);
            load = view.findViewById(R.id.load);
            comment = view.findViewById(R.id.comment);
            txtMore = view.findViewById(R.id.text_more);
            contentMore = view.findViewById(R.id.content_more);
            contentInputText = view.findViewById(R.id.content_input_text);
            txtDate = view.findViewById(R.id.txtDate);
            recicleNewsAll = view.findViewById(R.id.recicleNewsAll);
            txtTitle = view.findViewById(R.id.txt_title);
            share = view.findViewById(R.id.share);
            favorite = view.findViewById(R.id.favorite);
            txtNoticeCateg = view.findViewById(R.id.txt_noticeCateg);
            imgNewsBack = view.findViewById(R.id.imgNewsBack);
            llNewsCard = view.findViewById(R.id.ll_news);
            txtNewsDescription = view.findViewById(R.id.txt_news_description);
            imgExpand = view.findViewById(R.id.img_news_expand);
            header = view.findViewById(R.id.include_header);
            back = view.findViewById(R.id.back);
            recycleComments = view.findViewById(R.id.recycleComments);
            contentItens = view.findViewById(R.id.containerHomeItens);
            llComentary = view.findViewById(R.id.llComentary);
            rootView = view.findViewById(R.id.root_view);
        }
    }
}