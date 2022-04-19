package com.frf.app.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.frf.app.R;

public class RequestsDetailsActivity extends AppCompatActivity {

    private ViewHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_details);
        mHolder = new ViewHolder();

        mHolder.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    class ViewHolder {
        CardView comandaProcesare;
        ImageView imgBack;
        public ViewHolder(){
            comandaProcesare = findViewById(R.id.comanda_procesare);
            imgBack = findViewById(R.id.img_about_back);
        }
    }
}