package com.frf.app.activitys;

import static com.frf.app.utils.CONSTANTS.ERROR_CODE_UPDATE_REQUIRED;
import static com.frf.app.utils.CONSTANTS.ERROR_CODE_UPDATE_SUGGESTED;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.frf.app.R;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;

public class MaintenanceLockScreenActivity extends AppCompatActivity {
    private ViewHolder mHolder;

    ArrayAdapter<String> adapter;

    int status = 0;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_lock);
        UTILS.setColorStatusBar(this, "#fcd34d");

        mHolder = new ViewHolder();

        if (getIntent().hasExtra("msg")) {
            if (getIntent().getStringExtra("msg") != null) {
                if (!getIntent().getStringExtra("msg").equals("")) {
                    mHolder.txtDescription.setText(getIntent().getStringExtra("msg"));
                }else {
                    mHolder.txtDescription.setText(R.string.msg_maintenance_default);
                }
            }else {
                mHolder.txtDescription.setText(R.string.msg_maintenance_default);
            }
        }else {
            mHolder.txtDescription.setText(R.string.msg_maintenance_default);
        }

        if (!getIntent().hasExtra("url")) {
            configMaintenance();
        } else {
            status = getIntent().getIntExtra("status", 0);
            url = getIntent().getStringExtra("url");

            if (getIntent().getIntExtra("status", 0) == ERROR_CODE_UPDATE_SUGGESTED) {
                configUpdateSuggested();
            }else if (getIntent().getIntExtra("status", 0) == ERROR_CODE_UPDATE_REQUIRED) {
                configUpdateRequired();
            }else {
                configMaintenance();
            }
        }

    }

    @SuppressLint("SetTextI18n")
    void configUpdateSuggested() {
        mHolder.btnActive.setVisibility(View.VISIBLE);
        mHolder.later.setVisibility(View.VISIBLE);
        mHolder.version.setVisibility(View.VISIBLE);

        mHolder.btnActive.setText(R.string.descarcati_versiunea_noua);

        if (url != null) {
            if (!url.isEmpty()) {
                mHolder.btnActive.setOnClickListener(v -> {
                    new AsyncOperation(this, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_BLOCK_NEW_VERSION).execute();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                });
            }else {
                mHolder.later.setVisibility(View.GONE);
                mHolder.version.setVisibility(View.GONE);
                mHolder.btnActive.setText(getResources().getString(R.string.ncearc_din_nou));
                mHolder.btnActive.setOnClickListener(v1 -> {
                    new AsyncOperation(this, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_BLOCK_RETRY).execute();
                    finish();
                    Intent intent = new Intent(MaintenanceLockScreenActivity.this, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                });
            }
        }else {
            mHolder.later.setVisibility(View.GONE);
            mHolder.version.setVisibility(View.GONE);
            mHolder.btnActive.setText(getResources().getString(R.string.ncearc_din_nou));
            mHolder.btnActive.setOnClickListener(v1 -> {
                new AsyncOperation(this, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_BLOCK_RETRY).execute();
                finish();
                Intent intent = new Intent(MaintenanceLockScreenActivity.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
        }

        mHolder.later.setOnClickListener(v -> {
            new AsyncOperation(this, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_BLOCK_SKIP_VERSION).execute();
            finish();
            Intent intent = new Intent(MaintenanceLockScreenActivity.this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("trigger",1);
            startActivity(intent);
        });

        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            mHolder.version.setText(getResources().getString(R.string.versiunea_maintenance)+" "+pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    void configUpdateRequired() {
        mHolder.btnActive.setVisibility(View.VISIBLE);
        mHolder.version.setVisibility(View.VISIBLE);

        if (url != null) {
            if (!url.isEmpty()) {
                mHolder.btnActive.setOnClickListener(v -> {
                    new AsyncOperation(this, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_BLOCK_NEW_VERSION).execute();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                });
            }else {
                mHolder.later.setVisibility(View.GONE);
                mHolder.version.setVisibility(View.GONE);
                mHolder.btnActive.setText(getResources().getString(R.string.ncearc_din_nou));
                mHolder.btnActive.setOnClickListener(v1 -> {
                    new AsyncOperation(this, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_BLOCK_RETRY).execute();
                    finish();
                    Intent intent = new Intent(MaintenanceLockScreenActivity.this, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                });
            }
        }else {
            mHolder.later.setVisibility(View.GONE);
            mHolder.version.setVisibility(View.GONE);
            mHolder.btnActive.setText(getResources().getString(R.string.ncearc_din_nou));
            mHolder.btnActive.setOnClickListener(v1 -> {
                new AsyncOperation(this, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_BLOCK_RETRY).execute();
                finish();
                Intent intent = new Intent(MaintenanceLockScreenActivity.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
        }

        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            mHolder.version.setText(getResources().getString(R.string.versiunea_maintenance)+" "+pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    void configMaintenance() {
        mHolder.btnActive.setVisibility(View.GONE);
        mHolder.later.setVisibility(View.GONE);
        mHolder.version.setVisibility(View.GONE);
    }

    class ViewHolder {

        ImageView imgExit;
        View background;
        Button btnActive;
        TextView txtDescription, later, version;

        public ViewHolder() {
            background = findViewById(R.id.ll_background);
            txtDescription = findViewById(R.id.txt_description);
            btnActive = findViewById(R.id.btn_active);
            later = findViewById(R.id.later);
            version = findViewById(R.id.version);
            imgExit = findViewById(R.id.img_exit);
        }
    }
}