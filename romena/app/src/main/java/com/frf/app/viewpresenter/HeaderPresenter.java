package com.frf.app.viewpresenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frf.app.R;
import com.frf.app.models.user.UserModel;

public class HeaderPresenter extends MainViewPresenter {

    ViewHolder mHolder;
    ListenerHeader listener;
    int type;
    UserModel userModel;

    public HeaderPresenter(Activity context, int ViewID, ViewGroup viewHolder, int type, UserModel userModel, ListenerHeader listener) {
        this.context = context;
        this.viewHolder = viewHolder;
        this.listener = listener;
        this.type = type;
        this.userModel = userModel;
        initView(ViewID, viewHolder);
    }

    @Override
    protected View initView(int viewID, ViewGroup parentView) {
        View view = super.initView(viewID, parentView);
        mHolder = new ViewHolder(view);
        initValues();
        parentView.removeAllViews();
        parentView.addView(view);
        return view;
    }

    @SuppressLint("SetTextI18n")
    void initValues() {

        //mHolder.back.setVisibility(View.VISIBLE);
        if(type == 0){
            mHolder.notificationFavorite.setOnClickListener(v -> listener.OnNotificationClick());
            mHolder.notificationFavorite.setOnClickListener(v -> listener.OnNotificationClick());
        }if(type == 1) {
            mHolder.ll_coins.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnPointsandPrizes();
                }
            });
            mHolder.drawer.setOnClickListener(view -> listener.OnDrawerClick());
            mHolder.txtMoedas.setText(userModel.getCoins()+"");
            mHolder.notification.setOnClickListener(v -> listener.OnNotificationClick());
            mHolder.txtNotification.setVisibility(View.VISIBLE);
            if (userModel.getNotifications() > 0) {
                mHolder.txtNotification.setVisibility(View.VISIBLE);
                mHolder.txtNotification.setText(userModel.getNotifications() + "");
            }else {
                mHolder.txtNotification.setVisibility(View.GONE);
            }
            mHolder.imgCart.setVisibility(View.GONE);
            mHolder.imgBookmark.setVisibility(View.GONE);
        }

        if(type == 2){
            mHolder.back.setVisibility(View.VISIBLE);
            mHolder.drawer.setVisibility(View.GONE);
            mHolder.txtNotification.setVisibility(View.GONE);
            mHolder.notification.setVisibility(View.GONE);
            mHolder.txtMoedas.setVisibility(View.GONE);
            mHolder.imgCoin.setVisibility(View.INVISIBLE);
            mHolder.imgCart.setVisibility(View.GONE);
            mHolder.back.setOnClickListener(view -> listener.OnBackClick(type));
            mHolder.imgBookmark.setVisibility(View.GONE);
        }
        if(type == 3){
            mHolder.back.setVisibility(View.VISIBLE);
            mHolder.drawer.setVisibility(View.GONE);
            mHolder.txtNotification.setVisibility(View.GONE);
            mHolder.notification.setVisibility(View.GONE);
            mHolder.txtMoedas.setVisibility(View.GONE);
            mHolder.imgCoin.setVisibility(View.INVISIBLE);
            mHolder.imgCart.setVisibility(View.GONE);
            mHolder.back.setOnClickListener(view -> listener.OnBackClick(type));
            mHolder.imgBookmark.setVisibility(View.GONE);
        }
        if(type == 4){
            mHolder.llBackground.setBackgroundColor(Color.TRANSPARENT);
            mHolder.back.setVisibility(View.VISIBLE);
            mHolder.drawer.setVisibility(View.GONE);
            mHolder.txtNotification.setVisibility(View.GONE);
            mHolder.notification.setVisibility(View.GONE);
            mHolder.txtMoedas.setVisibility(View.GONE);
            mHolder.imgCoin.setVisibility(View.INVISIBLE);
            mHolder.imgBookmark.setVisibility(View.VISIBLE);
        }
        if (type == 5) {
            viewHolder.setVisibility(View.GONE);
        }
    }

    public interface ListenerHeader {
        void OnNotificationClick();

        void OnDrawerClick();

        void OnPointsandPrizes();

        void OnBackClick(int type);
    }

    static class ViewHolder {
        ImageButton drawer;
        ImageButton back;
        TextView txtMoedas;
        LinearLayout ll_coins;
        ImageView notification;
        ImageView imgCoin;
        ImageView imgCart;
        TextView txtNotification;
        View llBackground;
        ImageView imgBookmark;
        ImageView notificationFavorite;

        public ViewHolder(View view) {
            drawer = view.findViewById(R.id.drawer_image_menu);
            back = view.findViewById(R.id.back_image_menu);
            txtMoedas = view.findViewById(R.id.txt_moedas);
            ll_coins = view.findViewById(R.id.ll_coins);
            notification = view.findViewById(R.id.img_header_notification);
            txtNotification = view.findViewById(R.id.txt_notification);
            imgCoin = view.findViewById(R.id.img_coin);
            imgCart = view.findViewById(R.id.img_cart);
            llBackground = view.findViewById(R.id.ll_background);
            imgBookmark = view.findViewById(R.id.img_bookmark);
            notificationFavorite = view.findViewById(R.id.notification);
        }
    }
}
