package com.frf.app.activitys;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.frf.app.R;

public class HelpActivity extends AppCompatActivity {

    private ViewHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        mHolder = new ViewHolder();
        mHolder.imgBack.setOnClickListener(view -> finish());
    }
    class ViewHolder {
        ImageView imgBack;
        public ViewHolder(){
            imgBack = findViewById(R.id.img_back);
        }
    }
}