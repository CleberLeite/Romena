package com.frf.app.activitys;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.frf.app.R;
import com.frf.app.adapter.SlideAdapter;

public class ArenaViewPhotoActivity extends AppCompatActivity {

    private ViewHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena_view_photo);

        mHolder = new ViewHolder();
        mHolder.close.setOnClickListener(view -> finish());

        SlideAdapter slideAdapter = new SlideAdapter(this, getIntent().getStringArrayListExtra("imgs"));
        mHolder.viewPager.setAdapter(slideAdapter);

    }


    class ViewHolder {
        ImageView close;
        ViewPager viewPager;

        public ViewHolder() {
            close = findViewById(R.id.img_close);
            viewPager = findViewById(R.id.pager_next_img);
        }
    }
}