package com.frf.app.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.frf.app.R;
import com.frf.app.activitys.MainActivity;
import com.frf.app.activitys.SplashActivity;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.activitys.WinBadgeActivity;
import com.frf.app.informations.UserInformation;

import java.util.Hashtable;
import java.util.Objects;
import java.util.Random;

public class RomenaNotificationManager {

    public static final int PUSH_ID_DEFAUT                  = 0;
    public static final int PUSH_ID_NOTICIA                 = 1;
    public static final int PUSH_ID_QUIZ                    = 2;
    public static final int PUSH_ID_MATCHS                  = 3;
    public static final int PUSH_ID_STORE                   = 4;
    public static final int PUSH_ID_SHOP                    = 5;
    public static final int PUSH_ID_FEEDLIKE                = 6;
    public static final int PUSH_ID_FEEDCOMMENT             = 7;
    public static final int PUSH_ID_VIDEO                   = 8;

    public static final int PUSH_ID_CONTROLEVERSAO          = 1000;
    public static final int PUSH_ID_FORCECRASH              = 1001;
    public static final int PUSH_ID_FORCELOGOUT             = 1002;
    public static final int PUSH_ID_BADGE                   = 1003;


    public static final String NOTIFICATION_KEY_CONTENT_TEXT    = "contentText";
    public static final String NOTIFICATION_KEY_TITTLE_TEXT    = "contentTittle";

    int notificationId = 003;

    private final Context context;

    private final NotificationManager notificationManager;
    private final Notification.Builder builder;

    private static final String TAG = "MBNotification";

    public static String gcm = "";



    //Contructor do manager
    public RomenaNotificationManager(Context context){
        this.context = context;

        builder = new Notification.Builder(this.context);
        notificationManager = (NotificationManager)this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        SetNotificationChanel(notificationManager);
    }

    //Necessario para que seja encaminhado as notificações
    private void SetNotificationChanel (NotificationManager notific){
        String channelId = context.getResources().getString(R.string.id_channel);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel followerChannel = new NotificationChannel(channelId, "notification", NotificationManager.IMPORTANCE_DEFAULT);
            followerChannel.setLightColor(Color.parseColor("#f2f2f2"));
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            followerChannel.setShowBadge(true);
            followerChannel.enableLights(true);
            followerChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            followerChannel.enableVibration(true);
            followerChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            notific.createNotificationChannel(followerChannel);
            builder.setChannelId(channelId);
        }
    }

    //Cria as notificações
    public void CreateNotification(int idPush, int pushType, String tittle, String message, Bundle b) {
        Hashtable<String, Object> alertParams = new Hashtable<>();
        if(!message.isEmpty())
            alertParams.put(NOTIFICATION_KEY_CONTENT_TEXT, message);
        if(!tittle.isEmpty())
                alertParams.put(NOTIFICATION_KEY_TITTLE_TEXT, tittle);

        if(pushType > 999){
            CallSilentNotification(idPush,pushType);
        }else{
            SetNotificationParams(idPush, pushType, alertParams, b);
        }

    }

    private void SetDefaultNotificationParams(Bundle b){
        builder.setSmallIcon(R.mipmap.ic_launcher);

        String tittle = b.containsKey("nTittle")? !Objects.requireNonNull(b.getString("nTittle")).isEmpty()? b.getString("nTittle"): context.getString(R.string.app_name): context.getString(R.string.app_name);
        String body =  b.containsKey("nBody")?!Objects.requireNonNull(b.getString("nBody")).isEmpty() ? b.getString("nBody"): context.getString(R.string.push_description):context.getString(R.string.push_description);

        builder.setAutoCancel(true);
        builder.setContentTitle(tittle);
        builder.setContentText(body);
        builder.setPriority(Notification.PRIORITY_MAX);

        Intent resultIntent = new Intent(context, StartMenuActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(StartMenuActivity.class);

        resultIntent.setAction(Long.toString(System.currentTimeMillis()));

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        }else {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        builder.setContentIntent(resultPendingIntent);
    }

    private void SetNotificationParams(int idPush, int pushType, Hashtable<String, Object> alertParams, Bundle b){
        SetDefaultNotificationParams(b);

        if(alertParams.containsKey(NOTIFICATION_KEY_CONTENT_TEXT) && !String.valueOf(alertParams.get(NOTIFICATION_KEY_CONTENT_TEXT)).isEmpty()){
            builder.setContentText(String.valueOf(alertParams.get(NOTIFICATION_KEY_CONTENT_TEXT)));

            CharSequence cs = String.valueOf(alertParams.get(NOTIFICATION_KEY_CONTENT_TEXT));
            Notification.BigTextStyle style = new Notification.BigTextStyle();
            style.bigText(cs);
            builder.setStyle(style);
        }

        if(alertParams.containsKey(NOTIFICATION_KEY_TITTLE_TEXT) && !String.valueOf(alertParams.get(NOTIFICATION_KEY_TITTLE_TEXT)).isEmpty()){
            builder.setContentTitle(String.valueOf(alertParams.get(NOTIFICATION_KEY_TITTLE_TEXT)));
        }

        if(pushType == PUSH_ID_DEFAUT){
            CallNotificationGeneric(idPush);
        }
        else {
            CallNotification(idPush, pushType, b);
        }
    }

    private void CallNotification(int idPush, int pushType, Bundle b){
        UTILS.DebugLog(TAG, "Creating news notification");

        Bundle pushBundle = new Bundle();

        pushBundle.putInt("t", pushType);
        pushBundle.putInt("idPush", idPush);

        int v = b.getInt("v", -1) == -1 ? Integer.parseInt(b.getString("v", "-1")) : b.getInt("v", -1);

        pushBundle.putInt("v", v);

        Intent resultIntent;

        //Se esta logado
        if(UTILS.isUserLoggedIn()) {
            resultIntent = new Intent(context, StartMenuActivity.class);
            Bundle _b = new Bundle();
            _b.putBundle("pushBundle", pushBundle);
            _b.putInt(CONSTANTS.LOADING_SCREEN_KEY, CONSTANTS.SCREEN_REDIRECT_PUSH);
            resultIntent.putExtras(_b);
        }
        //Se não esta logado
        else{
            resultIntent = new Intent(context, SplashActivity.class);
            resultIntent.putExtras(pushBundle);
        }

        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.setAction(Long.toString(System.currentTimeMillis()));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(StartMenuActivity.class);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        }else {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        builder.setContentIntent(resultPendingIntent);

        Random rand = new Random();
        notificationId = rand.nextInt();
        notificationManager.notify(notificationId, builder.build());
    }

    public void CallSilentNotification(int idPush, int pushType){
        UTILS.DebugLog(TAG, "Creating news notification");

        try {
            new AsyncOperation((Activity) context, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_SILENT_PUSH, idPush).execute();
        }catch (Exception e) {
            e.printStackTrace();
        }

        Bundle pushBundle = new Bundle();
        pushBundle.putInt("t", pushType);
        pushBundle.putInt("idPush", idPush);
        Intent resultIntent =  null;

        switch (pushType){
            case PUSH_ID_CONTROLEVERSAO:{
                resultIntent = new Intent(context, SplashActivity.class);
            }
            break;

            case PUSH_ID_FORCECRASH:{
                resultIntent = new Intent(context, SplashActivity.class);
                resultIntent.putExtra("close", true);
            }
            break;

            case PUSH_ID_BADGE :{
                //Se esta logado
                if(UTILS.isUserLoggedIn()) {
                    resultIntent = new Intent(context, WinBadgeActivity.class);
                    Bundle _b = new Bundle();
                    _b.putBundle("pushBundle", pushBundle);
                    resultIntent.putExtras(_b);
                    resultIntent.putExtra(PUSH_ID_BADGE+"", true);
                    resultIntent.putExtra("idSilent", idPush);
                }
            }
            break;

            case PUSH_ID_FORCELOGOUT:{
                if(UTILS.isUserLoggedIn()) {
                    UserInformation.setSessionId(0);
                    UTILS.ClearInformations(context);
                    resultIntent = new Intent(context, SplashActivity.class);
                    resultIntent.putExtra("resetSSO", true);
                }
            }
            break;
        }

        if(resultIntent != null){
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(resultIntent);
        }
    }

    private void CallNotificationGeneric(int idPush){
        UTILS.DebugLog(TAG, "Creating generic notification");

        Intent resultIntent;

        Bundle pushBundle = new Bundle();
        pushBundle.putInt("t", PUSH_ID_DEFAUT);
        pushBundle.putInt("idPush", idPush);

        if((UTILS.isUserLoggedIn()) ) {
            resultIntent = new Intent(context, StartMenuActivity.class);
        }
        else{
            resultIntent = new Intent(context, SplashActivity.class);
        }

        resultIntent.putExtras(pushBundle);

        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.setAction(Long.toString(System.currentTimeMillis()));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(StartMenuActivity.class);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        }else {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        builder.setContentIntent(resultPendingIntent);

        Random rand = new Random();
        notificationId = rand.nextInt();
        notificationManager.notify(notificationId, builder.build());
    }
}
