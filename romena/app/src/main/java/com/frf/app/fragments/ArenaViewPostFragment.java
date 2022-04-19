package com.frf.app.fragments;

import static com.frf.app.activitys.MainActivity.emptyAsync;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.request.RequestOptions;
import com.frf.app.R;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.adapter.CommentAdapter;
import com.frf.app.adapter.SlideAdapter;
import com.frf.app.data.ArenaViewData;
import com.frf.app.data.CommentData;
import com.frf.app.data.RepliesData;
import com.frf.app.informations.UserInformation;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Objects;

public class ArenaViewPostFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {
    private ViewHolder mHolder;
    private int id;
    private int idComment = -1;
    private final int OP_POST = 0;
    private final int OP_DELET_COMMENT = 1;
    private final int OP_POST_COMMENT = 2;
    private final int OP_DELET_POST = 3;
    private final int OP_POST_REPLIES = 4;
    CommentAdapter adapter;
    ArrayList<CommentData> dataComment = new ArrayList<>();
    private LinearLayout contentItens;
    private boolean refreshList = true;
    private boolean isPush = false;
    private boolean more = false;

    public ArenaViewPostFragment(int id) {
        this.id = id;
    }
    public ArenaViewPostFragment(int id, boolean ispush) {
        this.id = id;
        this.isPush = ispush;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arena_view_post, container, false);
        mHolder = new ViewHolder(view, activity);
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
            case OP_POST: {
                if (response.has("Object")) {
                    try {
                        JSONObject object = response.getJSONObject("Object");

                        ArenaViewData gData = new Gson().fromJson(object.toString(), ArenaViewData.class);

                        initViewArena(gData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (isPush) {
                            Toast.makeText(activity, getResources().getString(R.string.se_pare_ca_acest_post_nu_mai_exista), Toast.LENGTH_LONG).show();
                            ((StartMenuActivity)getActivity()).onBackPressed();
                        }else {
                            Toast.makeText(activity, getResources().getString(R.string.nu_sa_putut_incarca_aceasta_postare), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    if (isPush) {
                    Toast.makeText(activity, getResources().getString(R.string.se_pare_ca_acest_post_nu_mai_exista), Toast.LENGTH_LONG).show();
                    ((StartMenuActivity)getActivity()).onBackPressed();
                }else {
                    Toast.makeText(activity, getResources().getString(R.string.nu_sa_putut_incarca_aceasta_postare), Toast.LENGTH_SHORT).show();
                }
                }
            }
            break;

            case OP_DELET_COMMENT: {
                try {
                    String msg = response.getString("msg");
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                    if (refreshList) {
                        getPost(id, more);
                    }else {
                        refreshList = true;
                    }
                    mHolder.load.setVisibility(View.GONE);
                } catch (JSONException e) {
                    refreshList = true;
                }
            }
            break;
            case OP_POST_COMMENT: {
                getPost(id, more);
                mHolder.contentInputText.setVisibility(View.GONE);
                mHolder.load.setVisibility(View.GONE);
            }
            break;
            case OP_DELET_POST: {
                ((StartMenuActivity)activity).onBackPressed();
            }
            break;

            case OP_POST_REPLIES: {
                try {
                    JSONObject object = response.getJSONObject("Object");
                    JSONArray itens = object.getJSONArray("itens");

                    RepliesData[] gData = new Gson().fromJson(itens.toString(), RepliesData[].class);
                    addReplies(new ArrayList<>(Arrays.asList(gData)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;
        }
    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {
        switch (opId) {
            case OP_POST: {
                if (isPush) {
                    Toast.makeText(activity, getResources().getString(R.string.se_pare_ca_acest_post_nu_mai_exista), Toast.LENGTH_LONG).show();
                    ((StartMenuActivity)getActivity()).onBackPressed();
                }else {
                    Toast.makeText(activity, getResources().getString(R.string.nu_sa_putut_incarca_aceasta_postare), Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case OP_POST_COMMENT: {
                getPost(id,more);
                mHolder.contentInputText.setVisibility(View.GONE);
                mHolder.load.setVisibility(View.GONE);
            }
            break;
        }
    }

    private void initActions() {
        mHolder.contentInputText.setOnClickListener(v -> {
            mHolder.contentInputText.setVisibility(View.GONE);
            hideSoftKeyboard(mHolder.comment);
        });

        final boolean[] isKeyboardShowing = {false};
        mHolder.container.getViewTreeObserver().addOnGlobalLayoutListener(
                () -> {
                    Rect r = new Rect();
                    mHolder.container.getWindowVisibleDisplayFrame(r);
                    int screenHeight = mHolder.container.getRootView().getHeight();

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
    }

    @SuppressLint("SetTextI18n")
    private void initViewArena(ArenaViewData gData) {
        mHolder.txtText.setText(gData.getText());
        mHolder.txtCat.setText(gData.getCat());
        mHolder.txtUtTitle.setText(gData.getUserType());
        mHolder.txtUserName.setText(gData.getUserName());
        mHolder.txtLikesCount.setText(gData.getLikesCount() + "");
        mHolder.txtCommentsCount.setText(gData.getCommentsCount() + "");

        if (gData.isSponsored()) {
            mHolder.sponsored.setVisibility(View.VISIBLE);
            mHolder.txtUtTitle.setVisibility(View.GONE);
        }else {
            mHolder.sponsored.setVisibility(View.GONE);
            mHolder.txtUtTitle.setVisibility(View.VISIBLE);
        }

        Drawable drawablePlace = activity.getResources().getDrawable(R.drawable.ic_placeholderuser);
        drawablePlace.setTint(activity.getResources().getColor(R.color.ThirdBackground));

        UTILS.getImageAt(activity, gData.getUserImg(), mHolder.imgUserImage, new RequestOptions().circleCrop().placeholder(R.drawable.ic_avatar_placeholder));

        int sizeGallery = gData.getImages().size();

        if (sizeGallery >= 3) {
            mHolder.imgFoco.setVisibility(View.VISIBLE);
            mHolder.imgSecundariaCard.setVisibility(View.VISIBLE);
            mHolder.imgTernariaCard.setVisibility(View.VISIBLE);
            UTILS.getImageAt(activity, gData.getImages().get(0), mHolder.imgFoco);
            UTILS.getImageAt(activity, gData.getImages().get(1), mHolder.imgSecundaria);
            UTILS.getImageAt(activity, gData.getImages().get(2), mHolder.imgTernaria);
        } else if (sizeGallery == 2) {
            mHolder.imgFoco.setVisibility(View.VISIBLE);
            mHolder.imgSecundariaCard.setVisibility(View.VISIBLE);
            UTILS.getImageAt(activity, gData.getImages().get(0), mHolder.imgFoco);
            UTILS.getImageAt(activity, gData.getImages().get(1), mHolder.imgSecundaria);
            mHolder.imgTernariaCard.setVisibility(View.GONE);
        } else if (sizeGallery == 1) {
            mHolder.imgFoco.setVisibility(View.VISIBLE);
            UTILS.getImageAt(activity, gData.getImages().get(0), mHolder.imgFoco);
            mHolder.llImgsRight.setVisibility(View.GONE);
            mHolder.imgSecundariaCard.setVisibility(View.GONE);
            mHolder.imgTernariaCard.setVisibility(View.GONE);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Math.round(UTILS.pxFromDp(activity, 150)), Math.round(UTILS.pxFromDp(activity, 205)));

            params.height = Math.round(UTILS.pxFromDp(activity, 220));
            params.width = Math.round(UTILS.pxFromDp(activity, 405));

            params.setMargins(0,0,0,0);
            mHolder.imgFoco.setLayoutParams(params);
            mHolder.imgFoco.requestLayout();
        } else {
            mHolder.imgFoco.setVisibility(View.GONE);
            mHolder.imgSecundariaCard.setVisibility(View.GONE);
            mHolder.imgTernariaCard.setVisibility(View.GONE);
        }

        if (sizeGallery > 3) {
            mHolder.countImgs.setText("+" + (sizeGallery - 3));
        }

        mHolder.contentImagens.setOnClickListener(view -> {
            initSlides(gData.getImages());
        });

        PrettyTime prettyTime = new PrettyTime(activity.getResources().getConfiguration().locale);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormatData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        try {
            date = dateFormatData.parse(gData.getCreated_at());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mHolder.txtCreated_at.setText(prettyTime.format(date));

        mHolder.txtLikesCount.setText((gData.getLikesCount()) + "");
        mHolder.imgLike.setImageDrawable(gData.getLiked() == 1 ? activity.getResources().getDrawable(R.drawable.ic_like) : activity.getResources().getDrawable(R.drawable.ic_unlike));

        mHolder.imgLike.setOnClickListener(v -> {
            setArenaPostLike(gData.getId());
            mHolder.txtLikesCount.setText((gData.getLikesCount() + 1) + "");

            if (gData.getLiked() == 1) {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_ARENA_DELETE_POST_UNLIKE, gData.getId()).execute();
                gData.setLiked(0);
                mHolder.imgLike.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_unlike));
                gData.setLikesCount(gData.getLikesCount() - 1);
                mHolder.txtLikesCount.setText(gData.getLikesCount() + "");
            } else {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_ARENA_DELETE_POST_LIKE, gData.getId()).execute();
                gData.setLiked(1);
                mHolder.imgLike.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_like));
                gData.setLikesCount(gData.getLikesCount() + 1);
                mHolder.txtLikesCount.setText(gData.getLikesCount() + "");
            }
        });

        mHolder.bntDeletePost.setVisibility(UserInformation.getUserId().equals(gData.getIdUser()) ? View.VISIBLE : View.GONE);


        if ((gData.getCommentsCount() - gData.getComments().size()) > 0) {
            mHolder.textMore.setText(getResources().getString(R.string.vezi_toate_comentariile) + " (" + (gData.getCommentsCount() - gData.getComments().size()) + ")");
            mHolder.contentMore.setOnClickListener(view -> {
                more = true;
                getPost(id, more);
                mHolder.contentMore.setVisibility(View.INVISIBLE);
            });
        }else {
            mHolder.contentMore.setVisibility(View.INVISIBLE);
        }

        dataComment.clear();
        dataComment.addAll(gData.getComments());
        initComment();
    }

    public void setArenaPostLike(int id) {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SET_ARENA_POST_LIKE, 999, this).execute(params);
    }

    void initSlides(ArrayList<String> imgs) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity, R.style.DialogFullScreen);
        builder.setCancelable(true);
        View view = LayoutInflater.from(activity).inflate(R.layout.include_slides, null, false);

        android.app.AlertDialog dialog = builder.create();

        ViewPager viewPager = view.findViewById(R.id.pager_next_img);
        ImageView close = view.findViewById(R.id.img_close);

        close.setOnClickListener(v -> dialog.dismiss());

        SlideAdapter slideAdapter = new SlideAdapter(activity, imgs);
        viewPager.setAdapter(slideAdapter);

        dialog.setView(view);
        dialog.show();
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

    //comentario
    private void initComment() {
        adapter = new CommentAdapter(activity, dataComment, new CommentAdapter.Listener() {
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
                ArenaViewPostFragment.this.contentItens = contentItens;
                GetPostReplies(id);
            }
        });

        mHolder.imgComentary.setOnClickListener(v -> {
            showSoftKeyboard(mHolder.contentInputText);
            idComment = -1;
        });

        mHolder.llComentary.setOnClickListener(view -> {
            showSoftKeyboard(mHolder.contentInputText);
            idComment = -1;
        });

        mHolder.recycleComments.setAdapter(adapter);
        mHolder.recycleComments.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
    }

    private void alertDeletComment(int id, LinearLayout CI, View view) {
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_ARENA_DELETE_POST_COMMENT_DELETE, id).execute();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AlertDialogCustom);
        alertDialog.setMessage("sunteți sigur că doriți să ștergeți acest comentariu?")
                .setPositiveButton(R.string.sim, (dialog, which) -> {
                    new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_ARENA_DELETE_POST_COMMENT_DELETE_CONFIRM, id).execute();
                    if (CI != null && view != null) {
                        refreshList = false;
                        CI.removeView(view);
                    }
                    setCommentDelet(id);
                })
                .setNegativeButton(R.string.nao, (dialog, which) -> {
                    new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_ARENA_DELETE_POST_COMMENT_DELETE_CANCEL, id).execute();
                    dialog.dismiss();
                });
        AlertDialog alert = alertDialog.create();

        alert.setOnShowListener(arg0 -> {
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.PrimaryColorText));
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorRed600));
        });
        alert.show();
    }

    public void showSoftKeyboard(View view) {
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
        mHolder.load.setVisibility(View.VISIBLE);
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SET_ARENA_POST_DELETE_COMENTARY, OP_DELET_COMMENT, this).execute(params);
    }

    private void setPostDelet(int id) {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SET_ARENA_POST_DELETE, OP_DELET_POST, this).execute(params);
    }


    private void GetPostReplies(int idComment) {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("idPost", id);
        params.put("idComment", idComment);
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_ARENA_POST_REPLIES, OP_POST_REPLIES, this).execute(params);
    }

    @Override
    public void bind() {
        super.bind();
        dataComment.clear();
        getPost(id, more);

        mHolder.btnSubmit.setOnClickListener(v -> setComment(id, idComment));

        mHolder.bntDeletePost.setOnClickListener(v -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_ARENA_DELETE_POST, id).execute();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AlertDialogCustom);

            alertDialog.setMessage("sunteți sigur că doriți să ștergeți acest post?")
                    .setPositiveButton(R.string.sim, (dialog, which) -> {
                        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_ARENA_DELETE_POST_CONFIRM, id).execute();
                        setPostDelet(id);
                    })
                    .setNegativeButton(R.string.nao, (dialog, which) -> {
                        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_ARENA_DELETE_POST_CANCEL, id).execute();
                        try {
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

            AlertDialog alert = alertDialog.create(); //TASK_ID_SET_ARENA_POST_DELETE_COMENTARY

            alert.setOnShowListener(arg0 -> {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.PrimaryColorText));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorRed600));
            });

            alert.show();
        });

        ((StartMenuActivity)getActivity()).changeHeader(2);
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
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SET_ARENA_COMMENT, OP_POST_COMMENT, this).execute(params);
    }

    private void getPost(int id, boolean more) {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        if (more) {
            params.put("more", true);
        }
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_POST_ARENA, OP_POST, this).execute(params);
    }

    static class ViewHolder {
        ConstraintLayout container;
        ConstraintLayout contentInputText;
        View imgSecundariaCard;
        View imgTernariaCard;
        ImageView imgFoco;
        ImageView imgSecundaria;
        ImageView imgTernaria;
        ImageView imgComentary;
        ImageView imgUserImage;
        TextView txtText;
        TextView txtUserName;
        TextView txtLikesCount;
        TextView txtCommentsCount;
        TextView txtCat;
        TextView load;
        TextView txtUtTitle;
        TextView txtCreated_at;
        TextView countImgs;
        TextView textMore;
        ImageView imgLike;
        RecyclerView recycleComments;
        EditText comment;
        Button btnSubmit;
        View bntDeletePost;
        LinearLayout sponsored, contentImagens, llImgsRight, contentMore, llComentary;
        BottomNavigationView navigation;


        public ViewHolder(View view, Activity activity) {
            container = view.findViewById(R.id.container);
            navigation = activity.findViewById(R.id.bottom_navigation);
            contentInputText = view.findViewById(R.id.content_input_text);
            btnSubmit = view.findViewById(R.id.btnSubmit);
            comment = view.findViewById(R.id.comment);
            countImgs = view.findViewById(R.id.count_imgs);
            textMore = view.findViewById(R.id.text_more);
            txtText = view.findViewById(R.id.desc);
            txtLikesCount = view.findViewById(R.id.num_likes);
            sponsored = view.findViewById(R.id.sponsored);
            txtUserName = view.findViewById(R.id.nome);
            load = view.findViewById(R.id.load);
            imgUserImage = view.findViewById(R.id.avatar);
            txtCommentsCount = view.findViewById(R.id.num_comentarios);
            txtCat = view.findViewById(R.id.cat);
            txtUtTitle = view.findViewById(R.id.nivel);
            txtCreated_at = view.findViewById(R.id.horario);
            imgComentary = view.findViewById(R.id.ic_comentarios);
            imgLike = view.findViewById(R.id.ic_like);
            recycleComments = view.findViewById(R.id.recycleComments);
            bntDeletePost = view.findViewById(R.id.bntDeletePost);
            imgSecundariaCard = view.findViewById(R.id.card_img_secundaria);
            imgTernariaCard = view.findViewById(R.id.card_img_ternario);
            imgFoco = view.findViewById(R.id.img_foco);
            imgSecundaria = view.findViewById(R.id.img_secondary);
            imgTernaria = view.findViewById(R.id.img_ternaria);
            contentImagens = view.findViewById(R.id.llImages);
            llImgsRight = view.findViewById(R.id.ll_img_right);
            contentMore = view.findViewById(R.id.content_more);
            llComentary = view.findViewById(R.id.llComentary);
        }
    }
}