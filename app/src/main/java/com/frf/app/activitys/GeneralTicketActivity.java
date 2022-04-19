package com.frf.app.activitys;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.frf.app.R;

public class GeneralTicketActivity extends AppCompatActivity {
    private ViewHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_ticket);
        mHolder = new ViewHolder();

        getImageAt(GeneralTicketActivity.this, "https://imagensemoldes.com.br/wp-content/uploads/2020/09/Sticker-C%C3%B3digo-de-Barras-PNG-1024x563.png", mHolder.imgQr);

        mHolder.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void getImageAt(Context activity, String link, ImageView imageView){
        try{
            if(activity != null){
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round);

                Glide.with(this).load(link).apply(options).into(imageView);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class ViewHolder {
        ImageView imgBack;
        ImageView imgQr;
        public ViewHolder(){
            imgQr = findViewById(R.id.img_qr_code);
            imgBack = findViewById(R.id.img_back);
        }
    }
}