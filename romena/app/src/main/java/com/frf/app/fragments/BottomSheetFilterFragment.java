package com.frf.app.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.frf.app.R;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.UTILS;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONObject;

import java.util.ArrayList;


public class BottomSheetFilterFragment extends BottomSheetDialogFragment implements AsyncOperation.IAsyncOpCallback {

    Activity activity;
    private ViewHolder mHolder;
    private boolean trigger = false;

    private Listener listener;

    public BottomSheetFilterFragment(Activity activity, Listener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_filter, container, false);


        inflater.getContext().setTheme(R.style.BottomSheetDialogTheme);
        mHolder = new ViewHolder(view);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

        initActions();

        return view;
    }

    @Override
    public void CallHandler(int opId, JSONObject response, boolean success) {

    }

    @Override
    public void OnAsyncOperationSuccess(int opId, JSONObject response) {

    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {

    }

    @Override
    public void onResume() {
        super.onResume();
        bind();
    }

    @SuppressLint("SetTextI18n")
    public void bind() {
        mHolder.imgClose.setOnClickListener(view -> dismiss());

        mHolder.crystalRangeSeekbar.setMaxValue(1000);
        mHolder.crystalRangeSeekbar.setMinValue(0);
        mHolder.tvMin.setText("0");
        mHolder.tvMax.setText("1000");
        // set listener
        mHolder.crystalRangeSeekbar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            if (trigger) {
                Log.d("CRS=>", minValue + " : " + maxValue);
                mHolder.tvMin.setText(String.valueOf(minValue));
                mHolder.tvMax.setText(String.valueOf(maxValue));
                mHolder.btnBuyEnter.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.PrimaryBackground));
            }else {
                trigger = true;
            }
        });

        ArrayList<String> size = new ArrayList<>();
        size.add("S");
        size.add("M");
        size.add("L");
        size.add("XL");
        size.add("2XL");
        UTILS.addViewSize(mHolder.layoutScrollViewSize, size, activity, mHolder.btnBuyEnter);

        ArrayList<String> colors = new ArrayList<>();
        colors.add("#FCD34D");
        colors.add("#0553C7");
        colors.add("#DC2626");
        colors.add("#10B981");
        colors.add("#F97316");
        colors.add("#FFFFFF");
        colors.add("#000000");
        UTILS.addViewColor(mHolder.layoutScrollViewColor, colors, activity);
    }

    private void initActions() {
        mHolder.btnBuyEnter.setOnClickListener(view -> {
            dismiss();
            listener.onFilter(mHolder.tvMin.getText().toString(), mHolder.tvMax.getText().toString(), "");
        });
    }

    public interface Listener {
        void onFilter(String minValue, String maxValue, String size);
    }

    static class ViewHolder {
        ImageView imgClose;
        HorizontalScrollView layoutScrollViewSize;
        HorizontalScrollView layoutScrollViewColor;
        TextView tvMin;
        TextView tvMax;
        CrystalRangeSeekbar crystalRangeSeekbar;
        View btnBuyEnter;

        public ViewHolder(View view) {
            imgClose = view.findViewById(R.id.imgClose);
            layoutScrollViewColor = view.findViewById(R.id.hozontalScrollColor);
            layoutScrollViewSize = view.findViewById(R.id.hozontalScrollSize);
            tvMin =view.findViewById(R.id.textMin);
            tvMax = view.findViewById(R.id.textMax);
            crystalRangeSeekbar = view.findViewById(R.id.crystalRangeSeekbar);
            btnBuyEnter = view.findViewById(R.id.btn_buy_enter);
        }
    }
}