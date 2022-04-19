package com.frf.app.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.frf.app.R;
import com.frf.app.utils.UTILS;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MenuActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ViewHolder mHolder;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        mHolder = new ViewHolder();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mHolder.itmSetings.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this, StartMenuActivity.class);
            intent.putExtra("from", "SettingsFragment");
            startActivity(intent);
        });
        mHolder.itmMatchesResults.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this, StartMenuActivity.class);
            intent.putExtra("from", "EventsActivity");
            startActivity(intent);
        });
        mHolder.itmRequests.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this, StartMenuActivity.class);
            intent.putExtra("from", "RequestsFragment");
            startActivity(intent);
        });

        mHolder.itmTickets.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this, StartMenuActivity.class);
            intent.putExtra("from", "TicketFragment");
            startActivity(intent);
        });

        mHolder.itmLogout.setOnClickListener(view -> {


            ///Logout com do google
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Session not close", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });

        mHolder.itmProfile.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this, StartMenuActivity.class);
            intent.putExtra("from", "PerfilFragment");
            startActivity(intent);
        });

        mHolder.itmPuncte.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this, StartMenuActivity.class);
            intent.putExtra("from", "RewardsFragment");
            startActivity(intent);
        });
        mHolder.itmClasament.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this, StartMenuActivity.class);
            intent.putExtra("from", "RankingFragment");
            startActivity(intent);
        });
        mHolder.itmQuiz.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this, StartMenuActivity.class);
            intent.putExtra("from", "QuizFragment");
            startActivity(intent);
//            Toast.makeText(this, "Tela em desenvolvimento", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class ViewHolder {
        View itmSetings;
        LinearLayout itmMatchesResults;
        LinearLayout itmRequests;
        LinearLayout itmTickets;
        LinearLayout itmLogout;
        LinearLayout itmProfile;
        LinearLayout itmPuncte;
        LinearLayout itmClasament;
        LinearLayout itmQuiz;

        public ViewHolder() {
            itmSetings = findViewById(R.id.item_settings);
            itmMatchesResults = findViewById(R.id.ll_MatchesResults);
            itmRequests = findViewById(R.id.ll_requests);
            itmTickets = findViewById(R.id.ll_tickets);
            itmLogout = findViewById(R.id.ll_loggout);
            itmLogout = findViewById(R.id.ll_loggout);
            itmProfile = findViewById(R.id.ll_menu_perfil);
            itmPuncte = findViewById(R.id.ll_puncte);
            itmClasament = findViewById(R.id.ll_clasament);
            itmQuiz = findViewById(R.id.ll_quiz);
        }
    }
}