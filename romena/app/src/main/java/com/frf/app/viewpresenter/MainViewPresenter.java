package com.frf.app.viewpresenter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainViewPresenter {

    public Activity context;
    public ViewGroup viewHolder;

    protected View initView (int viewID, ViewGroup parentView){
        return LayoutInflater.from(context).inflate(viewID, parentView, false);
    }

}