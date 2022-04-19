package com.frf.app.models;

import android.view.ViewGroup;

public class HeaderModel {
    int ViewID;
    ViewGroup viewHolder;

    public HeaderModel(int viewID, ViewGroup viewHolder) {
        ViewID = viewID;
        this.viewHolder = viewHolder;
    }

    public int getViewID() {
        return ViewID;
    }

    public void setViewID(int viewID) {
        ViewID = viewID;
    }

    public ViewGroup getViewHolder() {
        return viewHolder;
    }

    public void setViewHolder(ViewGroup viewHolder) {
        this.viewHolder = viewHolder;
    }
}
