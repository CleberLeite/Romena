package com.frf.app.activitys;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.frf.app.BuildConfig;
import com.frf.app.R;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;

public class AboutActivity extends AppCompatActivity {

    private ViewHolder mHolder;
    private static final String termsOfUse = "https://api.app-admin.frf.ro/terms/service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        UTILS.setColorStatusBar(this, R.color.TabBarBackground);

        mHolder = new ViewHolder();
        mHolder.imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        initValues();
        initActions();
    }

    @SuppressLint("SetTextI18n")
    void initValues() {
        mHolder.version.setText(getResources().getString(R.string.versiunea) + " " + BuildConfig.VERSION_NAME);
    }

    void initActions() {
        mHolder.terms.setOnClickListener(v -> {
            new AsyncOperation(this, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_ABOUT_OPEN_TERMS_OF_USE).execute();
            alertTerms();
        });
    }

    void alertTerms() {
        Dialog builder = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        WebView webView = new WebView(this);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setAllowFileAccess(true);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        webView.loadUrl(termsOfUse);

        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(lp);
        webView.requestLayout();

        builder.setContentView(webView);
        builder.create();
        builder.show();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    class ViewHolder {
        ImageView imgBack;
        TextView version, terms;

        public ViewHolder(){
            imgBack = findViewById(R.id.img_about_back);
            version = findViewById(R.id.version);
            terms = findViewById(R.id.terms);
        }
    }
}