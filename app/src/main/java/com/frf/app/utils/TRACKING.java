package com.frf.app.utils;

import static com.frf.app.activitys.MainActivity.prefs;
import static com.frf.app.repository.MainRepository.SHARED_KEY_USER_TOKEN;

import android.content.Context;
import android.os.Bundle;

import com.frf.app.informations.UserInformation;
import com.google.firebase.analytics.FirebaseAnalytics;

public class TRACKING {

    //PUSH
    public static final int TRACKING_OPEN_PUSH = 1;
    public static final int TRACKING_SILENT_PUSH = 2;

    //SPLASH
    public static final int TRACKING_SPLASH_RETRY = 3;

    //BLOCK
    public static final int TRACKING_BLOCK_RETRY = 4;
    public static final int TRACKING_BLOCK_NEW_VERSION = 5;
    public static final int TRACKING_BLOCK_SKIP_VERSION = 6;

    //LOGIN
    public static final int TRACKING_LOGIN_AUTHENTICATED = 7;

    //NOTIFICATIONS
    public static final int TRACKING_NOTIFICATIONS_REQUIRED = 8;

    //LATERAL MENU
    public static final int TRACKING_LATERALMENU_PERFIL = 9;
    public static final int TRACKING_LATERALMENU_COMPATITIONS_AND_RESULTS = 10;
    public static final int TRACKING_LATERALMENU_ECOMMERCE = 11;
    public static final int TRACKING_LATERALMENU_TICKETS = 12;
    public static final int TRACKING_LATERALMENU_QUIZ = 13;
    public static final int TRACKING_LATERALMENU_RANKING = 14;
    public static final int TRACKING_LATERALMENU_BADGES_AND_RESCUES = 15;
    public static final int TRACKING_LATERALMENU_CONFIGURATIONS = 16;
    public static final int TRACKING_LATERALMENU_LOGOUT = 17;
    public static final int TRACKING_LATERALMENU_LOGOUT_CANCEL = 18;
    public static final int TRACKING_LATERALMENU_LOGOUT_CONFIRM = 19;

    //HOME
    public static final int TRACKING_HOME_BOTTOM_MENU_HOME = 20;
    public static final int TRACKING_HOME_HEADER_NOTIFICATIONS = 21;
    public static final int TRACKING_HOME_OPEN_NEWS = 22;
    public static final int TRACKING_HOME_OPEN_MATCH = 23;
    public static final int TRACKING_HOME_LIKE_ARENA = 24;
    public static final int TRACKING_HOME_UNLIKE_ARENA = 25;
    public static final int TRACKING_HOME_OPEN_ARENA = 26;
    public static final int TRACKING_HOME_OPEN_VIDEO = 27;
    public static final int TRACKING_HOME_ANSWER_QUIZ = 28;

    //ARENA
    public static final int TRACKING_ARENA_BOTTOM_MENU_ARENA = 29;
    public static final int TRACKING_ARENA_HEADER_NOTIFICATIONS = 30;
    public static final int TRACKING_ARENA_CHANGE_CATEGORY = 31;
    public static final int TRACKING_ARENA_ORDER_FEED = 32;
    public static final int TRACKING_ARENA_NEW_POST = 33;
    public static final int TRACKING_ARENA_LIKE_ARENA = 34;
    public static final int TRACKING_ARENA_UNLIKE_ARENA = 35;
    public static final int TRACKING_ARENA_OPEN_POST = 36;
    public static final int TRACKING_ARENA_DELETE_POST = 37;
    public static final int TRACKING_ARENA_DELETE_POST_CANCEL = 38;
    public static final int TRACKING_ARENA_DELETE_POST_CONFIRM = 39;
    public static final int TRACKING_ARENA_DELETE_POST_LIKE = 40;
    public static final int TRACKING_ARENA_DELETE_POST_UNLIKE = 41;
    public static final int TRACKING_ARENA_DELETE_POST_COMMENT_DELETE = 42;
    public static final int TRACKING_ARENA_DELETE_POST_COMMENT_DELETE_CANCEL = 43;
    public static final int TRACKING_ARENA_DELETE_POST_COMMENT_DELETE_CONFIRM = 44;

    //NEWS
    public static final int TRACKING_NEWS_BOTTOM_MENU_NEWS = 45;
    public static final int TRACKING_NEWS_HEADER_NOTIFICATIONS = 46;
    public static final int TRACKING_NEWS_CHANGE_CATEGORY = 47;
    public static final int TRACKING_NEWS_ORDER_NEWS = 48;
    public static final int TRACKING_NEWS_OPEN_NEWS = 49;
    public static final int TRACKING_NEWS_FOLLOW_CATEGORY = 50;
    public static final int TRACKING_NEWS_UNFOLLOW_CATEGORY = 51;
    public static final int TRACKING_NEWS_FAVORITE_NEWS = 52;
    public static final int TRACKING_NEWS_DISFAVOR_NEWS = 53;
    public static final int TRACKING_NEWS_OPEN_RELATED_NEWS = 54;
    public static final int TRACKING_NEWS_DELETE_COMMENT = 55;
    public static final int TRACKING_NEWS_DELETE_COMMENT_CANCEL = 56;
    public static final int TRACKING_NEWS_DELETE_COMMENT_CONFIRM = 57;

    //VIDEOS
    public static final int TRACKING_VIDEOS_BOTTOM_MENU_VIDEOS = 58;
    public static final int TRACKING_VIDEOS_HEADER_NOTIFICATIONS = 59;
    public static final int TRACKING_VIDEOS_CHANGE_CATEGORY = 60;
    public static final int TRACKING_VIDEOS_ORDER_VIDEOS = 61;
    public static final int TRACKING_VIDEOS_FOLLOW_CATEGORY = 62;
    public static final int TRACKING_VIDEOS_UNFOLLOW_CATEGORY = 63;

    //USER_PROFILE
    public static final int TRACKING_USER_PROFILE_EDIT = 64;
    public static final int TRACKING_USER_PROFILE_LIKE_ARENA = 65;
    public static final int TRACKING_USER_PROFILE_UNLIKE_ARENA = 66;
    public static final int TRACKING_USER_PROFILE_OPEN_POST_ARENA = 67;

    //EDIT_PROFILE
    public static final int TRACKING_EDIT_PROFILE_ALTER_IMG = 68;
    public static final int TRACKING_EDIT_PROFILE_ALTER_NICKNAME = 69;

    //NOTIFICATIONS CENTER
    public static final int TRACKING_NOTIFICATIONS_CENTER_SELECT_ITEM = 70;
    public static final int TRACKING_NOTIFICATIONS_CENTER_SELECT_ALL_ITENS = 71;

    //COMPETITIONS
    public static final int TRACKING_COMPETITIONS_HEADER_NOTIFICATIONS = 72;
    public static final int TRACKING_COMPETITIONS_OPEN_COMPATITION = 73;
    public static final int TRACKING_COMPETITIONS_OPEN_COMPATITION_DETAILS = 74;

    //MATCH
    public static final int TRACKING_MATCH_CHECK_NOTIFICATION = 75;
    public static final int TRACKING_MATCH_UNCHECK_NOTIFICATION = 76;

    //QUIZ
    public static final int TRACKING_QUIZ_HEADER_NOTIFICATIONS = 77;
    public static final int TRACKING_QUIZ_EXPAND_QUIZ = 78;
    public static final int TRACKING_QUIZ_REPLY_QUIZ = 79;

    //RANKING
    public static final int TRACKING_RANKING_HEADER_NOTIFICATIONS = 80;

    //BADGES AND AWARDS
    public static final int TRACKING_BADGES_AND_AWARDS_HEADER_NOTIFICATIONS = 81;
    public static final int TRACKING_BADGES_AND_AWARDS_BADGE_DETAILS = 82;
    public static final int TRACKING_BADGES_AND_AWARDS_OPEN_AWARD = 83;
    public static final int TRACKING_BADGES_AND_AWARDS_ORDER_AWARD = 84;

    //SETTINGS
    public static final int TRACKING_SETTINGS_HEADER_NOTIFICATIONS = 85;
    public static final int TRACKING_SETTINGS_OPEN_ACCOUNT = 86;
    public static final int TRACKING_SETTINGS_OPEN_NOTIFICATIONS = 87;
    public static final int TRACKING_SETTINGS_OPEN_ABOUT = 88;
    public static final int TRACKING_SETTINGS_OPEN_HELP = 89;

    //SETTINGS ACCOUNT
    public static final int TRACKING_SETTINGS_ACCOUNT_OPEN_CHANGE_PASSWORD = 90;
    public static final int TRACKING_SETTINGS_ACCOUNT_OPEN_CHANGE_EMAIL = 91;
    public static final int TRACKING_SETTINGS_ACCOUNT_OPEN_TERMS = 92;
    public static final int TRACKING_SETTINGS_ACCOUNT_OPEN_PRIVACY = 93;
    public static final int TRACKING_SETTINGS_ACCOUNT_DELETE_ACCOUNT = 94;
    public static final int TRACKING_SETTINGS_ACCOUNT_DELETE_ACCOUNT_CANCEL = 95;
    public static final int TRACKING_SETTINGS_ACCOUNT_DELETE_ACCOUNT_CONFIRM = 96;

    //SETTINGS NOTIFICATIONS
    public static final int TRACKING_SETTINGS_NOTIFICATIONS_CHECK = 97;
    public static final int TRACKING_SETTINGS_NOTIFICATIONS_UNCHECK = 98;

    //ABOUT
    public static final int TRACKING_ABOUT_OPEN_TERMS_OF_USE = 99;

    //HELP
    public static final int TRACKING_HELP_OPEN_FREQUENT_QUESTIONS = 100;
    public static final int TRACKING_HELP_OPEN_REPORT_PROBLEM = 101;
    public static final int TRACKING_HELP_OPEN_CONTACT = 102;

    public static final int TRACKING_VIDEOS_OPEN = 103;

    //Rescues
    public static final int TRACKING_RESCUES_RESCUE = 104;
    public static final int TRACKING_RESCUES_DOWNLOAD_FILE = 105;
    public static final int TRACKING_RESCUES_TICKET_COPY = 106;

    private static String token;

    private static String AddBtnIdToApiHeader(int btnId, int varId) {

        String str = ":";
        if (btnId != -1) {
            str += String.valueOf(btnId);
        }
        if (varId != -1) {
            str += "/" + varId;
        }

        return str;
    }

    private static String AddBtnIdToApiHeader(int btnId, String varId) {

        String str = ":";
        if (btnId != -1) {
            str += String.valueOf(btnId);
        }
        if (!varId.isEmpty()) {
            str += "/" + varId;
        }

        return str;
    }

    public static void clearToken() {
        token = "";
    }

    public static String buildHeader(int btnId, int varId) {
        String header = (UTILS.getApiHeaderValue() + AddBtnIdToApiHeader(btnId, varId) + AddTokenToApiHeader() + getSid());
        UTILS.DebugLog("Header", header);
        return header;
    }

    public static String buildHeader(int btnId, String varId) {
        String header = (UTILS.getApiHeaderValue() + AddBtnIdToApiHeader(btnId, varId) + AddTokenToApiHeader() + getSid());
        UTILS.DebugLog("Header", header);
        return header;
    }

    private static String AddTokenToApiHeader(){
        if (token == null || token.equals("")) {
            token = prefs.getString(SHARED_KEY_USER_TOKEN, "");
        }
        return (":" + token);
    }

    public static String getSid() {
        try {
            return ":" + UserInformation.getSessionId();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setTrackingButtonName(Context context, String name, boolean hasParametro, int id){
        Bundle params = new Bundle();
        if(hasParametro){
            if(name.equals("TRACKING_REGISTER_ID_VIDEO") && id != 0){
                params.putInt("v", id);
                FirebaseAnalytics.getInstance(context).logEvent(name, params);
            }else if(name.equals("TRACKING_REGISTER_ID_ITEM_PARTNER") && id != 0){
                params.putInt("v", id);
                FirebaseAnalytics.getInstance(context).logEvent(name, params);
            }

        }else {
            FirebaseAnalytics.getInstance(context).logEvent(name, null);
        }

    }

    public static void setTrackingClassName(Context context, String name){
        Bundle params = new Bundle();

        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, name);
        //params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, classe);
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params);


    }
}
