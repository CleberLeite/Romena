package com.frf.app.viewpresenter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.frf.app.R;

public class DropDownStoryByPresenter extends MainViewPresenter {

    ViewHolder mHolder;
    Listener listener;
    String label;

    public DropDownStoryByPresenter(Activity context, int ViewID, ViewGroup viewHolder, String label, Listener listener) {
        this.context = context;
        this.viewHolder = viewHolder;
        this.label = label;
        this.listener = listener;
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

    void initValues() {
        mHolder.btnDropdown.setText(label);
        mHolder.btnDropdown.setOnClickListener(view -> listener.OnClick());
    }

    public interface Listener {
        void OnClick();
    }

    static class ViewHolder {
        Button btnDropdown;
        public ViewHolder(View view) {
            btnDropdown = view.findViewById(R.id.btn_dropdown);
        }
    }
}
