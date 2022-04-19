package com.frf.app.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.frf.app.R;


public class TicketsActivity extends AppCompatActivity {

    private ViewHolder mHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        mHolder = new ViewHolder();
        mHolder.cardQrCode.setOnClickListener(view -> {
            Intent intent = new Intent(TicketsActivity.this, GeneralTicketActivity.class);
            startActivity(intent);
        });

        mHolder.cardGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TicketsActivity.this, SeatTicketActivity.class);
                startActivity(intent);
            }
        });

    }

    class ViewHolder {
        ImageView imgBack;
        CardView cardQrCode;
        CardView cardGeneral;
        public ViewHolder(){
            cardQrCode = findViewById(R.id.card_qr_code);
            cardGeneral = findViewById(R.id.card_general);
        }
    }
}