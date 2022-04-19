package com.frf.app.activitys;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.frf.app.R;

public class SeatTicketActivity extends AppCompatActivity {

    private ViewHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_ticket);
        mHolder = new ViewHolder();

        getImageAt(SeatTicketActivity.this, "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAe1BMVEX///8AAAD09PSAgICFhYX7+/uenp6amprx8fE+Pj6Tk5P39/esrKzs7Oxvb29ra2tWVlYuLi4WFhYJCQkdHR3g4OBhYWHPz8+/v7+0tLR0dHRLS0ukpKQiIiLh4eFBQUE1NTWOjo4rKyuDg4PIyMhOTk5ZWVnX19e7u7tf2Xg2AAAFtElEQVR4nO2dW3vpQBSGdRNBgmqdKYJW//8v3Dcya7U+mUwySuJ7L2fNwWvbT6ZzWGk0CCGEEEIIIYQQxL9yRNJTJEhhAFupRiXHzyH4Uo6ldLU1hR9SuEaNRhKflhzfrljWsCldfZrCnRTGqNFK4oOS46ufCw0LQkMa0tBGfsOvsOdE4tnQbfReuHI2bFtr/iTwa/juOHyj5WwYOo6QtlOGK2OgnvjQsCvx1DBwHL95D8PY/IbUD0IZJu2UWOKVMoQowz6K0/AaNLz8pDS81q6ChsewnUEY/Grn2zDoZQ4/82CYvGQS/Wrn29AyizTPoRKGvYc2XNOQhp4MOy9vZ16STj8Fjl9ZQ4PtKURDGtKQhjSk4TMY9upkuDmdmcx23TO7tcQnafy0qajhq9SCQ+1NeFdRQ9nsHMOhuiYOVoRpSEMa0pCGNHwSw52JZ+wfVs/w2GydOcSLlHmdDNWpr+qvJkLDWq2X0vBRDBtBJr/b+TbMOfx9dkj9GOaEhpeflIbX2j2VoZxrU+s0yrCD+qmU4WXwZ7+55qV5ub1hlGWIV4Qz/7ZwHL6AYS/qOLHxbNh3Gz5yNyyKJ8PC0JCGdmhIw+obqv1DeeJvpXBYcny7YWRWigrRXMCvbbA8M/iW0lZauEykMD6UG991LkQIIYQQUk3+7c0BrRjFTyY+OkrpyDRSE7Dw8zUFDjV9zeQTNhqAmqs5rHrNUCaxcDFqJnHVL5x5N6UUDvVmmUTDRjtUE06GH8Dwo4hhF9WkIQ1pSEMa3s8QJo7waDguYuj1eZiYe3QbWd85dVdntt8S35pCtZbW+hid+egjuvtRBnuVaiIybf59gkbj2HLl75qhAja0/RsJnfydQtS26gTF1YmYopmwqmNYdEWYhpZOaWiDhjT8AQ0V9Tdc5jZ06BTi9YkfrGNAKznTUvcHjxJPTFxNEecmDFN6KsMOHFRYG9q4kcSL7h/KohFeAZOPrfKXfkEvZHiy1GyhMScSd5p5Y+QPHbwwKIPBs4k3MdxInIY0pCENaUjDPzEs9MRXGQdKGsKjZP2ChtHYLJCp9VJJKn+EH0FyxauZL1xgU0jNYJOWddS5NllAU6f6p+mq3moqPYVmra+b41ybjJCgOP66Xb7DbGzTeXjA3+lvC2UI05CoFeGbGA4sncqatzrQSMMf0JCGZaEhDcs/D29tWOh5GDoZrsyZJHgmajIcXDKEXZ1mBhifAU5qcUfan6TRND191Z1uJmc2LRdDj9gyYcFfA+QLNceLsDleFuQPW+YP2z6+AH8jfViVhl6hIQ1pSMPbU39DNT2B8ffchl6f+NF0WAq1vzibG6TwW64iLuaXHNVE6GhKZ43+0FxWTC9IHuBOo33/8CZ3SBVqkgzzYlyZeW8uR3JOu/D3hlm5TXIY4v/cNKQhDatk+P7mSOUMb/W+Jxo+rSGYhD6AIbw4ABfw1KakutSImmND9bXlP8nu9R2WijVqXNawTUMa1tywP8mkBoZJ9jgZOWirYlg8yy4Nq2+4rb1hiDJVHRuAiaTfaptE7Ytl85KDXMt8k5qhiS/v/4ZHG/l3uRWF7pDSkIZFoSEN7dCQhjS0YTv1tX8kw8S89VAdQVuYpFmQrUqWIY2+TaPRXN6aaJjISKM/nbXBW7T4siVEGt3kluytDGPYCw1pSEMa0pCGtTWEcxr1YdFe+VgdBZPLebHpaa9e/yzx8E5zGojtXUFLNIjXnAp/aGjbA7YYFr0l+4SGSe0NT4tMfreroKFjOxrSkIZCVQ3hE/+Aaj6A4SJsXxIaerKZ2Tc112rjeG2qqk3HGPSkxgnzn2T3YAgzYcF+HRI1jEx4J4WF8pfCFLsZBJeGMLdJWcPy94BTw+3A8aJF5QyLQkMa0pCGNPRqCDNhlU3NJNcy91KYSKMcNyyz30dvRfUk6bEUeFaFauLPCuOBpREhhBBCCCGEEPIftBf7lg2MAy0AAAAASUVORK5CYII=", mHolder.imgQr);

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