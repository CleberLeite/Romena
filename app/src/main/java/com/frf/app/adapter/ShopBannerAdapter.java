package com.frf.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.frf.app.R;
import com.frf.app.data.ShopBannerData;
import com.frf.app.utils.UTILS;

import java.util.ArrayList;

public class ShopBannerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<ShopBannerData> banners;
    private Listener listener;


    public ShopBannerAdapter(Context context, ArrayList<ShopBannerData> banners, Listener listener) {
        this.context = context;
        this.banners = banners;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return banners.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_banner_shop, container, false);

        ImageView imgBanner = view.findViewById(R.id.img);

        ShopBannerData banner = banners.get(position);

        UTILS.getImageAt(context, banner.getPromopic(), imgBanner);

        imgBanner.setOnClickListener(view1 -> listener.onClick(banner.getId()));

        container.addView(view);

        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public interface Listener {
        void onClick(int id);
    }
}
