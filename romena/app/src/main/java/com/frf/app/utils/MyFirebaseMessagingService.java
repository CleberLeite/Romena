package com.frf.app.utils;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.frf.app.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebase";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        boolean hasData = false;
        boolean hasNotification = false;

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        UTILS.DebugLog(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            UTILS.DebugLog(TAG, "Message data payload: " + remoteMessage.getData());
            hasData = true;
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            UTILS.DebugLog(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            hasNotification = true;
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        String nTittle = (hasNotification) ? remoteMessage.getNotification().getTitle() : getString(R.string.app_name);
        String nBody = (hasNotification) ? remoteMessage.getNotification().getBody() : getString(R.string.push_description);

        String message = ( (hasData) && (remoteMessage.getData().containsKey("m")) ) ? String.valueOf(remoteMessage.getData().get("m")) : "";
        String tittle = ( (hasData) && (remoteMessage.getData().containsKey("tit")) ) ? String.valueOf(remoteMessage.getData().get("tit")) : "";
        int pushType = ( (hasData) && (remoteMessage.getData().containsKey("t")) ) ? Integer.parseInt(String.valueOf(remoteMessage.getData().get("t"))) : 0;
        int idPush = ( (hasData) && (remoteMessage.getData().containsKey("idPush")) ) ? Integer.parseInt(String.valueOf(remoteMessage.getData().get("idPush"))) : 0;
        int v = ( (hasData) && (remoteMessage.getData().containsKey("v")) ) ? Integer.parseInt(String.valueOf(remoteMessage.getData().get("v"))) : -1;

        Bundle extras = new Bundle();
        extras.putString("nTittle", nTittle);
        extras.putString("nBody", nBody);
        extras.putString("m", message);
        extras.putString("tit", tittle);
        extras.putInt("t", pushType);
        extras.putInt("idPush", idPush);
        extras.putInt("v", v);

        CallNotification(idPush, pushType, tittle, message, extras);

        UTILS.DebugLog(TAG, "GCM Received : (" + pushType + ")  " + message);
    }


    private void CallNotification(int idPush, int pushType, String tittle, String message, Bundle b){
        RomenaNotificationManager notification = new RomenaNotificationManager(getApplicationContext());
        notification.CreateNotification(idPush, pushType, tittle, message, b);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, s);
    }
}
