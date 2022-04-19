package com.frf.app.activitys;

import static com.frf.app.activitys.MainActivity.prefs;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.frf.app.R;
import com.frf.app.repository.MainRepository;
import com.frf.app.utils.UTILS;

public class TermsAndPrivacyPolicyActivity extends AppCompatActivity {

    private ViewHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        UTILS.setColorStatusBar(this, R.color.TabBarBackground);

        mHolder = new ViewHolder();

        mHolder.imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        if (getIntent().hasExtra("title")) {
            mHolder.title.setVisibility(View.VISIBLE);
            mHolder.title.setText(getIntent().getStringExtra("title"));
        }

        initValues();
        loadWebsite(getIntent().getStringExtra("link"));
    }


    private void loadWebsite(String link) {
        ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            mHolder.webView.loadUrl(link);
        } else {
            mHolder.webView.setVisibility(View.GONE);
        }
    }


    private void initValues() {
        WebSettings webSettings = mHolder.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setAllowFileAccess(true);

        mHolder.webView.setWebViewClient(new WebViewClient());
        mHolder.webView.setWebChromeClient(new WebChromeClient());
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    class ViewHolder {
        ImageView imgBack;
        TextView title;
        WebView webView;

        public ViewHolder(){
            imgBack = findViewById(R.id.img_back);
            title = findViewById(R.id.title);
            webView = findViewById(R.id.webTerms);

        }
    }
}