package com.frf.app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import androidx.appcompat.app.AppCompatActivity;

import com.frf.app.activitys.MainActivity;

public class NetworkChangeReceiver extends BroadcastReceiver {

    public static MainActivity currentActivity = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        switch(UTILS.getNetworkStatus(connectivityManager)){
            default: {
                if(currentActivity != null){
                    ((IConnectionDialog)currentActivity).CallConnectionDialog(currentActivity);
                }
            } break;

            case CONSTANTS.CONNECTION_TYPE_WIFI:
            case CONSTANTS.CONNECTION_TYPE_MOBILE: {

                if(currentActivity != null){
                    ((IConnectionDialog)currentActivity).DismissConnectionDialog();
                }

            } break;
        }
    }

    public interface IConnectionDialog {

        void CallConnectionDialog(AppCompatActivity activity);
        void DismissConnectionDialog();

    }
}
