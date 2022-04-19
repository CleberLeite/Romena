package com.frf.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.frf.app.R;
import com.frf.app.activitys.SplashActivity;
import com.frf.app.informations.UserInformation;
import com.frf.app.repository.MainRepository;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class AsyncOperation extends AsyncTask<Hashtable<String, Object>, Integer, Boolean> {

    private static final String TAG = "AsyncOperation";
    public static String BUNDLE_KEY_TOKEN_MESSAGE = "";

    @SuppressLint("StaticFieldLeak")
    private final Activity activity;
    private final int TASK_ID;
    private final int OP_ID;
    IAsyncOpCallback callback;

    private boolean taskCancelled = false;

    private int BTN_ID = -1;
    private String BTN_VALUE = "";

    public static final int TASK_ID_GET_VERSION = 0;
    public static final int TASK_ID_SIGN_IN = 1;
    public static final int TASK_ID_SIGN_UP = 2;
    public static final int TASK_ID_PASSWORD_FORGOTTEN = 3;
    public static final int TASK_ID_SET_END_SESSION = 4;
    public static final int TASK_ID_SAVE_TRACKING = 5;
    public static final int TASK_ID_GET_NOTIFICATION_HISTORY = 6;
    public static final int TASK_ID_SET_NOTIFICATION = 7;
    public static final int TASK_ID_GET_NOTIFICATION_MARK_ALL = 8;
    public static final int TASK_ID_GET_BADGE = 9;
    public static final int TASK_ID_SET_USER_APNS = 10;
    public static final int TASK_ID_GET_USER_PROFILE = 11;
    public static final int TASK_ID_UPLOAD_PICTURE = 12;
    public static final int TASK_ID_GET_ALL_NEWS = 13;
    public static final int TASK_ID_GET_HOME = 14;
    public static final int TASK_ID_GET_ALL_VIDEOS = 15;
    public static final int TASK_ID_GET_ARENA = 16;
    public static final int TASK_ID_GET_RANK = 17;
    public static final int TASK_ID_GET_POST_ARENA = 18;
    public static final int TASK_ID_GET_PRIZES = 19;
    public static final int TASK_ID_GET_PRIZES_DETAILS = 20;
    public static final int TASK_ID_SET_ARENA_POST_LIKE = 21;
    public static final int TASK_ID_SET_ARENA_POST_DELETE = 22;
    public static final int TASK_ID_SET_ARENA_POST_DELETE_COMENTARY = 23;
    public static final int TASK_ID_GET_MATCH_COMPETITIONS = 24;
    public static final int TASK_ID_GET_VIDEOS_CATEGORIES = 25;
    public static final int TASK_ID_GET_ARENA_CATEGORIES = 26;
    public static final int TASK_ID_SET_UPDATE_USER = 27;
    public static final int TASK_ID_GET_NEWS = 28;
    public static final int TASK_ID_POST_DELETE_ACCOUNT = 29;
    public static final int TASK_ID_GET_RANK_TITLE = 30;
    public static final int TASK_ID_GET_NOTIFICATION_PERMISSIONS = 31;
    public static final int TASK_ID_GET_BADGE_DETAIL = 32;
    public static final int TASK_ID_GET_QUIZ = 33;
    public static final int TASK_ID_GET_NOTIFICATION_PERMISSION_READ = 34;
    public static final int TASK_ID_SET_NEWS_FAVORITE = 35;
    public static final int TASK_ID_SET_QUIZ_ANSWER = 36;
    public static final int TASK_ID_SET_ARENA_COMMENT = 37;
    public static final int TASK_ID_GET_COMPETITION_TABLES = 38;
    public static final int TASK_ID_POST_ARENA = 39;
    public static final int TASK_ID_GET_ARENA_POST_REPLIES = 40;
    public static final int TASK_ID_GET_COMPETITION_MATCHES = 41;
    public static final int TASK_ID_GET_PROFILE_COINS = 42;
    public static final int TASK_ID_GET_COMPETITION_MATCH = 43;
    public static final int TASK_ID_SET_FOLLOW_VIDEOS_CATEGORY = 44;
    public static final int TASK_ID_SET_FOLLOW_NEWS_CATEGORY = 45;
    public static final int TASK_ID_SET_NOTIFICATION_MATCH = 46;
    public static final int TASK_ID_GET_NOTIFICATION_NEWS = 47;
    public static final int TASK_ID_DELETE_NOTIFICATION_NEWS = 48;
    public static final int TASK_ID_SET_NOTIFICATION_NEWS = 49;
    public static final int WS_METHOD_GET_NEWS_COMMENT_REPLIES = 50;
    public static final int TASK_ID_SET_RESCUE = 51;
    public static final int TASK_ID_POST_AUTH = 52;
    public static final int TASK_ID_GET_SHOP_HOMEPAGE = 53;
    public static final int TASK_ID_GET_SHOP_CATEGORIES = 54;
    public static final int TASK_ID_GET_SHOP_ITENS = 55;
    public static final int TASK_ID_GET_SEARCH_SHOP = 56;
    public static final int TASK_ID_GET_COMPETITIONS_TICKETS = 57;
    public static final int TASK_ID_GET_MATCHES_TICKETS = 58;
    public static final int TASK_ID_GET_SECTORS_TICKETS = 59;
    public static final int TASK_ID_GET_OFFERS_TICKETS = 60;
    public static final int TASK_ID_GET_SECTORS_SVG_TICKETS = 61;
    public static final int TASK_ID_GET_REPORTS = 62;
    public static final int TASK_ID_SET_REPORT = 63;
    public static final int TASK_ID_BLOCK_USER = 64;

    //endregion
    public AsyncOperation(Activity activity, int taskId, int opId, IAsyncOpCallback callback) {
        this.activity = activity;
        this.TASK_ID = taskId;
        this.OP_ID = opId;
        this.callback = callback;
    }

    public AsyncOperation(Activity activity, int taskId, int opId, IAsyncOpCallback callback, int btnId) {
        this.activity = activity;
        this.TASK_ID = taskId;
        this.OP_ID = opId;
        this.callback = callback;
        this.BTN_ID = btnId;

        FirebaseCrashlytics.getInstance().setCustomKey("id", btnId);
    }

    public AsyncOperation(Activity activity, int taskId, int opId, IAsyncOpCallback callback, int btnId, int btnVar) {
        this.activity = activity;
        this.TASK_ID = taskId;
        this.OP_ID = opId;
        this.callback = callback;
        this.BTN_ID = btnId;
        this.BTN_VALUE = String.valueOf(btnVar);
    }

    public AsyncOperation(Activity activity, int taskId, int opId, IAsyncOpCallback callback, int btnId, String btnVar) {
        this.activity = activity;
        this.TASK_ID = taskId;
        this.OP_ID = opId;
        this.callback = callback;
        this.BTN_ID = btnId;
        this.BTN_VALUE = btnVar;
    }

    @SafeVarargs
    @Override
    protected final Boolean doInBackground(Hashtable<String, Object>... params) {

        if (!(UTILS.isNetworkAvailable(activity))) {
            CallNoConnectionMessage();
            return false;
        }

        taskCancelled = false;

        try {
            switch (TASK_ID) {
                case TASK_ID_GET_VERSION: {
                    getVersion();
                }
                break;

                case TASK_ID_SIGN_IN: {
                    CallTaskSignIn(params[0]);
                }
                break;

                case TASK_ID_SIGN_UP: {
                    CallTaskSignUp(params[0]);
                }
                break;

                case TASK_ID_PASSWORD_FORGOTTEN: {
                    CallTaskForgotPassword(params[0]);
                }
                break;

                case TASK_ID_SET_END_SESSION: {
                    CallTaskSetEndSession(params[0]);
                }
                break;

                case TASK_ID_SAVE_TRACKING: {
                    CallTaskSaveTracking();
                }
                break;

                case TASK_ID_GET_NOTIFICATION_HISTORY: {
                    CallTaskGetNotificationHistory(params[0]);
                }
                break;

                case TASK_ID_GET_NOTIFICATION_MARK_ALL: {
                    CallTaskGetNotificationMarkAll();
                }
                break;

                case TASK_ID_SET_NOTIFICATION: {
                    CallTaskSetNotification(params[0]);
                }
                break;

                case TASK_ID_GET_BADGE: {
                    CallTaskGetBadge(params[0]);
                }
                break;

                case TASK_ID_SET_USER_APNS: {
                    CallTaskSetUserApns(params[0]);
                }
                break;

                case TASK_ID_GET_USER_PROFILE: {
                    CallTaskGetUserProfile();
                }
                break;

                case TASK_ID_UPLOAD_PICTURE: {
                    CallTaskUploadPicture(params[0]);
                }
                break;

                case TASK_ID_GET_ALL_NEWS: {
                    CallTaskGetAllNews(params[0]);
                }
                break;

                case TASK_ID_GET_HOME: {
                    CallTaskGetHome();
                }
                break;

                case TASK_ID_GET_ALL_VIDEOS: {
                    CallTaskGetAllVideos(params[0]);
                }
                break;

                case TASK_ID_GET_ARENA: {
                    CallTaskGetArena(params[0]);
                }
                break;

                case TASK_ID_GET_RANK: {
                    CallTaskGetRanking(params[0]);
                }
                break;

                case TASK_ID_GET_RANK_TITLE: {
                    CallTaskGetRankingTitle();
                }
                break;

                case TASK_ID_GET_POST_ARENA: {
                    CallTaskGetPostArena(params[0]);
                }
                break;

                case TASK_ID_GET_PRIZES: {
                    CallTaskGetPrizes(params[0]);
                }
                break;

                case TASK_ID_GET_PRIZES_DETAILS: {
                    CallTaskGetPrizeDetails(params[0]);
                }
                break;

                case TASK_ID_SET_ARENA_POST_LIKE: {
                    CallTaskGetArenaPostLike(params[0]);
                }
                break;

                case TASK_ID_SET_ARENA_POST_DELETE: {
                    CallTaskPostDelete(params[0]);
                }
                break;

                case TASK_ID_SET_ARENA_POST_DELETE_COMENTARY: {
                    CallTaskSetDeliteComentary(params[0]);
                }
                break;

                case TASK_ID_GET_MATCH_COMPETITIONS: {
                    CallTaskSetMatchCompetitions(params[0]);
                }
                break;

                case TASK_ID_SET_UPDATE_USER: {
                    CallTaskSetUpdateUser(params[0]);
                }
                break;

                case TASK_ID_GET_NEWS: {
                    CallTaskGetNews(params[0]);
                }
                break;

                case TASK_ID_GET_ARENA_POST_REPLIES: {
                    CallTaskGetArenaPostReplies(params[0]);
                }
                break;

                case TASK_ID_GET_ARENA_CATEGORIES: {
                    CallTaskGetArenaCategories();
                }
                break;

                case TASK_ID_POST_DELETE_ACCOUNT: {
                    CallTaskDeleteAccount(params[0]);
                }
                break;

                case TASK_ID_GET_NOTIFICATION_PERMISSIONS: {
                    CallTaskPosNotificationsPermissions(params[0]);
                }
                break;

                case TASK_ID_GET_BADGE_DETAIL: {
                    CallTaskGetBadgeDetails(params[0]);
                }
                break;

                case TASK_ID_GET_NOTIFICATION_PERMISSION_READ: {
                    CallTaskSetNotificationsPermissions(params[0]);
                }
                break;

                case TASK_ID_SET_NEWS_FAVORITE: {
                    CallTaskSetNewsFavorite(params[0]);
                }
                break;

                case TASK_ID_SET_QUIZ_ANSWER: {
                    CallTaskSetQuizAnswer(params[0]);
                }
                break;

                case TASK_ID_GET_QUIZ: {
                    CallTaskGetQuiz();
                }
                break;

                case TASK_ID_SET_ARENA_COMMENT: {
                    CallTaskSetArenaComment(params[0]);
                }
                break;

                case TASK_ID_GET_COMPETITION_TABLES: {
                    CallTaskGetCompetitionTables(params[0]);
                }
                break;

                case TASK_ID_POST_ARENA: {
                    CallTaskSetPostArena(params[0]);
                }
                break;

                case TASK_ID_GET_COMPETITION_MATCHES: {
                    CallTaskGetCompetitionMatches(params[0]);
                }
                break;

                case TASK_ID_GET_PROFILE_COINS: {
                    CallTaskGetEditProfileCoins();
                }
                break;

                case TASK_ID_GET_COMPETITION_MATCH: {
                 CallTaskGetCompetitionMatch(params[0]);
                }
                break;

                case TASK_ID_SET_FOLLOW_VIDEOS_CATEGORY: {
                 CallTaskSetFollowVideosCategory(params[0]);
                }
                break;

                case TASK_ID_SET_FOLLOW_NEWS_CATEGORY: {
                 CallTaskSetFollowNewsCategory(params[0]);
                }
                break;

                case TASK_ID_SET_NOTIFICATION_MATCH: {
                    CallTaskSetNotificationMatch(params[0]);
                }
                break;

                case TASK_ID_GET_NOTIFICATION_NEWS: {
                    CallTaskGetNewsComment(params[0]);
                }
                break;

                case TASK_ID_DELETE_NOTIFICATION_NEWS: {
                        CallTaskDeleteNewsComment(params[0]);
                }
                break;

                case TASK_ID_SET_NOTIFICATION_NEWS: {
                        CallTaskSetNewsComment(params[0]);
                }
                break;

                case WS_METHOD_GET_NEWS_COMMENT_REPLIES: {
                    CallTaskGetNewsCommentReplies(params[0]);
                }
                break;

                case TASK_ID_SET_RESCUE: {
                    CallTaskSetRescue(params[0]);
                }
                break;

                case TASK_ID_POST_AUTH: {
                    CallTaskSetAuth(params[0]);
                }
                break;

                case TASK_ID_GET_SHOP_HOMEPAGE: {
                    CallTaskGetShopHomepage();
                }
                break;

                case TASK_ID_GET_SHOP_CATEGORIES: {
                    CallTaskGetShopCategories();
                }
                break;

                case TASK_ID_GET_SHOP_ITENS: {
                    CallTaskGetShopItens(params[0]);
                }
                break;

                case TASK_ID_GET_SEARCH_SHOP: {
                    CallTaskGetSearchShop(params[0]);
                }
                break;

                case TASK_ID_GET_COMPETITIONS_TICKETS: {
                    CallTaskGetCompetitionsTickets();
                }
                break;

                case TASK_ID_GET_MATCHES_TICKETS: {
                    CallTaskGetMatchesTickets(params[0]);
                }
                break;

                case TASK_ID_GET_SECTORS_TICKETS: {
                    CallTaskGetSectorsTickets(params[0]);
                }
                break;

                case TASK_ID_GET_OFFERS_TICKETS: {
                    CallTaskGetOffersTickets(params[0]);
                }
                break;

                case TASK_ID_GET_SECTORS_SVG_TICKETS: {
                    CallTaskGetSectorsSvg(params[0]);
                }
                break;

                case TASK_ID_GET_REPORTS: {
                    CallTaskGetReports();
                }
                break;

                case TASK_ID_SET_REPORT: {
                    CallTaskSetReport(params[0]);
                }
                break;

                case TASK_ID_BLOCK_USER: {
                    CallTaskSetBlockUser(params[0]);
                }
                break;
            }
        } catch (Throwable e) {
            UTILS.DebugLog(TAG, e);
        }

        return false;
    }

    private void CallTaskSetBlockUser(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SET_BLOCK_USER;

        JSONObject params = new JSONObject();
        try {
            params.put("idUserBlock", _params.get("idUserBlock"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UTILS.DebugLog(TAG, "POST " + method + " Send this " + params.toString());

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("status")) {
                    if ((response.getInt("status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSetReport(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SET_REPOST;

        JSONObject params = new JSONObject();
        try {
            params.put("idFeature", _params.get("idFeature"));
            params.put("idPost", _params.get("idPost"));
            params.put("idReportType", _params.get("idReportType"));
            if (_params.containsKey("note")) {
                params.put("note", _params.get("note"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UTILS.DebugLog(TAG, "POST " + method + " Send this " + params.toString());

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("status")) {
                    if ((response.getInt("status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetReports() {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_REPOSTS;

        UTILS.DebugLog(TAG, "POST " + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("status")) {
                    if ((response.getInt("status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetSectorsSvg(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());
        String method = CONSTANTS.mainServerTickets + String.format(CONSTANTS.WS_METHOD_GET_SECTORS_SVG, _params.get("id"));

        UTILS.DebugLog(TAG, "GET " + method);


        StringRequest postReq = new StringRequest(Request.Method.GET, method, respons -> {
            UTILS.DebugLog(TAG, "Response: " + respons);
            boolean success = false;
            JSONObject response = null;
            try {
                response = new JSONObject();
                response.put("object", respons);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                response.put("Status", 200);
                success = true;
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, error.getMessage());
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                UTILS.parseVolleyError(error);
                JSONObject jsonError = new JSONObject();
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", CONSTANTS.access_token_tickets);
                headers.put("Host", "k2plqg2got.bilete.ro");
                headers.put("external-user-id", "123");
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetOffersTickets(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());
        String method = CONSTANTS.mainServerTickets + String.format(CONSTANTS.WS_METHOD_GET_OFFERS, _params.get("id"));

        UTILS.DebugLog(TAG, "GET " + method);


        StringRequest postReq = new StringRequest(Request.Method.GET, method, respons -> {
            UTILS.DebugLog(TAG, "Response: " + respons);
            boolean success = false;
            JSONObject response = null;
            try {
                response = new JSONObject();
                response.put("object", respons);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (respons.contains("offers")) {
                    response.put("Status", 200);
                    success = true;
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, error.getMessage());
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                UTILS.parseVolleyError(error);
                JSONObject jsonError = new JSONObject();
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", CONSTANTS.access_token_tickets);
                headers.put("Host", "k2plqg2got.bilete.ro");
                headers.put("external-user-id", "123");
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetSectorsTickets(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());
        String method = CONSTANTS.mainServerTickets + String.format(CONSTANTS.WS_METHOD_GET_SECTORS, _params.get("id"));

        UTILS.DebugLog(TAG, "GET " + method);


        StringRequest postReq = new StringRequest(Request.Method.GET, method, respons -> {
            UTILS.DebugLog(TAG, "Response: " + respons);
            boolean success = false;
            JSONObject response = null;
            try {
                response = new JSONObject();
                response.put("object", respons);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (respons.contains("availableSectors")) {
                    response.put("Status", 200);
                    success = true;
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, error.getMessage());
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                UTILS.parseVolleyError(error);
                JSONObject jsonError = new JSONObject();
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", CONSTANTS.access_token_tickets);
                headers.put("Host", "k2plqg2got.bilete.ro");
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetMatchesTickets(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());
        String method = CONSTANTS.mainServerTickets + String.format(CONSTANTS.WS_METHOD_GET_MATCHES, _params.get("id"));

        UTILS.DebugLog(TAG, "GET " + method);


        StringRequest postReq = new StringRequest(Request.Method.GET, method, respons -> {
            UTILS.DebugLog(TAG, "Response: " + respons);
            boolean success = false;
            JSONObject response = null;
            try {
                response = new JSONObject();
                response.put("items", respons);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (respons.contains("[") && respons.contains("{")) {
                    response.put("Status", 200);
                    success = true;
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, error.getMessage());
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                UTILS.parseVolleyError(error);
                JSONObject jsonError = new JSONObject();
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", CONSTANTS.access_token_tickets);
                headers.put("Host", "k2plqg2got.bilete.ro");
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetCompetitionsTickets() {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());
        String method = CONSTANTS.mainServerTickets + CONSTANTS.WS_METHOD_GET_COMPETITIONS;

        UTILS.DebugLog(TAG, "GET " + method);


        StringRequest postReq = new StringRequest(Request.Method.GET, method, respons -> {
            UTILS.DebugLog(TAG, "Response: " + respons);
            boolean success = false;
            JSONObject response = null;
            try {
                response = new JSONObject();
                response.put("items", respons);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (respons.contains("[") && respons.contains("{")) {
                    response.put("Status", 200);
                    success = true;
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, error.getMessage());
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                UTILS.parseVolleyError(error);
                JSONObject jsonError = new JSONObject();
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", CONSTANTS.access_token_tickets);
                headers.put("Host", "k2plqg2got.bilete.ro");
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetSearchShop(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());
        String method = CONSTANTS.mainServerShop + CONSTANTS.WS_METHOD_GET_SEARCH_SHOP + _params.get("keyword");

        UTILS.DebugLog(TAG, "GET " + method);


        StringRequest postReq = new StringRequest(Request.Method.GET, method, respons -> {
            UTILS.DebugLog(TAG, "Response: " + respons);
            boolean success = false;
            JSONObject response = null;
            try {
                response = new JSONObject(respons);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (response.has("statusCode")) {
                    if (response.getInt("statusCode") == HttpURLConnection.HTTP_OK) {
                        response.put("Status", 200);
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, error.getMessage());
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                UTILS.parseVolleyError(error);
                JSONObject jsonError = new JSONObject();
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", CONSTANTS.access_token_shop);
                headers.put("Host", "api.contentspeed.ro");
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetShopItens(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());
        String method = CONSTANTS.mainServerShop + CONSTANTS.WS_METHOD_GET_SHOP_PRODUCTS + "?categoryid="+_params.get("categoryid");

        UTILS.DebugLog(TAG, "GET " + method);


        StringRequest postReq = new StringRequest(Request.Method.GET, method, respons -> {
            UTILS.DebugLog(TAG, "Response: " + respons);
            boolean success = false;
            JSONObject response = null;
            try {
                response = new JSONObject(respons);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (response.has("statusCode")) {
                    if (response.getInt("statusCode") == HttpURLConnection.HTTP_OK) {
                        response.put("Status", 200);
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, error.getMessage());
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                UTILS.parseVolleyError(error);
                JSONObject jsonError = new JSONObject();
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", CONSTANTS.access_token_shop);
                headers.put("Host", "api.contentspeed.ro");
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetShopCategories() {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());
        String method = CONSTANTS.mainServerShop + CONSTANTS.WS_METHOD_GET_CATEGORIES;

        UTILS.DebugLog(TAG, "GET " + method);


        StringRequest postReq = new StringRequest(Request.Method.GET, method, respons -> {
            UTILS.DebugLog(TAG, "Response: " + respons);
            boolean success = false;
            JSONObject response = null;
            try {
                response = new JSONObject(respons);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (response.has("statusCode")) {
                    if (response.getInt("statusCode") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, error.getMessage());
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                UTILS.parseVolleyError(error);
                JSONObject jsonError = new JSONObject();
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", CONSTANTS.access_token_shop);
                headers.put("Host", "api.contentspeed.ro");
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetShopHomepage() {
       RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());
        String method = CONSTANTS.mainServerShop + CONSTANTS.WS_METHOD_GET_HOMEPAGE;

        UTILS.DebugLog(TAG, "GET " + method);

        StringRequest postReq = new StringRequest(Request.Method.GET, method, respons -> {
            UTILS.DebugLog(TAG, "Response: " + respons);
            boolean success = false;
            JSONObject response = null;
            try {
                response = new JSONObject(respons);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (response.has("statusCode")) {
                    if (response.getInt("statusCode") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, error.getMessage());
            if (!taskCancelled && error.networkResponse != null && error.networkResponse.data != null) {

                UTILS.parseVolleyError(error);
                JSONObject jsonError = new JSONObject();
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", CONSTANTS.access_token_shop);
                headers.put("Host", "api.contentspeed.ro");
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSetAuth(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        final int os = CONSTANTS.OS_ID;  //(IOS envie 1 e Android envie 2)
        final int osv = Build.VERSION.SDK_INT;  //(versão do sistema operacional)
        final String dev = UTILS.getDeviceName();  //(modelo do device)
        String _apv = "";
        try {
            _apv = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            UTILS.DebugLog(TAG, e);
        }
        final String apv = _apv;

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SET_AUTH;

        UTILS.DebugLog(TAG, "POST " + method);

        String v = CONSTANTS.serverVersion.replace("/", "");

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", _params.get("user_id"));
            params.put("os", os);
            params.put("osv", osv);
            params.put("dev", dev);
            params.put("apv", apv);
            params.put("api", v);
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSetRescue(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SET_RESCUE;

        UTILS.DebugLog(TAG, "POST " + method);

        JSONObject params = new JSONObject();
        try {
            params.put("id", _params.get("id"));
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetArenaPostReplies(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());
        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_ARENA_POST_REPLIES + "?idPost=" + _params.get("idPost") + "&idComment=" + _params.get("idComment");

        UTILS.DebugLog(TAG, "GET " + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskDeleteAccount(Hashtable<String, Object> _params) {

        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        JSONObject params = new JSONObject();
        try {
            params.put("id", _params.get("id"));
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }

        UTILS.DebugLog(TAG, "Sending this: " + params.toString() + " to " + CONSTANTS.serverURL + CONSTANTS.WS_METHOD_DELETE_ACCOUNT);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, CONSTANTS.serverURL + CONSTANTS.WS_METHOD_DELETE_ACCOUNT, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("status")) {
                    if (response.has("Status")) {
                        if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                            CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                            return;
                        } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                            success = true;
                        }
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);

    }

    private void CallTaskSetNotificationMatch(Hashtable<String, Object> _params) {

        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        JSONObject params = new JSONObject();
        try {
            params.put("idGame", _params.get("id"));
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }

        UTILS.DebugLog(TAG, "Sending this: " + params.toString());

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SET_NOTIFICATION_MATCH, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);

    }

    private void CallTaskGetUserProfile() {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        UTILS.DebugLog(TAG, CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_USER_PROFILE);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_USER_PROFILE, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSetUserApns(Hashtable<String, Object> _params) {

        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        JSONObject params = new JSONObject();
        try {
            params.put("apns", _params.get("apns"));
            params.put("allow", _params.get("allow"));
            params.put("os", CONSTANTS.OS_ID);
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }

        UTILS.DebugLog(TAG, "Sending this: " + params.toString());

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SET_USER_APNS, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);

    }

    private void getVersion() {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        final int osv = Build.VERSION.SDK_INT;  //(versão do sistema operacional)
        final String dev = UTILS.getDeviceName();  //(modelo do device)

        SecurePreferences prefs = new SecurePreferences(activity.getApplicationContext(), CONSTANTS.SHARED_PREFS_KEY_FILE, CONSTANTS.SHARED_PREFS_SECRET_KEY, true);
        String idUser = "";
        if (prefs.containsKey(MainRepository.SHARED_KEY_USER)) {
            try {
                JSONObject autologinData = new JSONObject(prefs.getString(MainRepository.SHARED_KEY_USER, ""));
                idUser = (((autologinData.has("id")) && (autologinData.get("id") != JSONObject.NULL)) ? autologinData.getString("id") : "");
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
            }
        }

        JSONObject params = new JSONObject();
        try {
            String apv = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
            params.put("os", CONSTANTS.OS_ID);
            params.put("v", apv);
            params.put("osv", osv);
            params.put("dev", dev);

            if ((idUser != null && !idUser.isEmpty()) && (UserInformation.getSessionId() <= 0)) {
                params.put("idUser", idUser);
            }
        } catch (JSONException | PackageManager.NameNotFoundException e) {
            UTILS.DebugLog(TAG, e);
        }

        String method = CONSTANTS.getVersion + CONSTANTS.WS_METHOD_VERSION_CHECK;

        UTILS.DebugLog(TAG, "Sending this: " + params + "\nto:" + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_OK) ||
                            (response.getInt("Status") == CONSTANTS.ERROR_CODE_UPDATE_SUGGESTED) ||
                            (response.getInt("Status") == HttpURLConnection.HTTP_FORBIDDEN) ||
                            (response.getInt("Status") == CONSTANTS.ERROR_CODE_MAINTENANCE)) { //server em manutencao ou qualquer outra mensagem dinamica
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSignIn(Hashtable<String, Object> _params) {

        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        final int os = CONSTANTS.OS_ID;  //(IOS envie 1 e Android envie 2)
        final int osv = Build.VERSION.SDK_INT;  //(versão do sistema operacional)
        final String dev = UTILS.getDeviceName();  //(modelo do device)
        String _apv = "";
        try {
            _apv = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            UTILS.DebugLog(TAG, e);
        }
        final String apv = _apv;

        JSONObject params = new JSONObject();
        try {
            params.put("email", _params.get("email"));
            params.put("password", _params.get("password"));
            if ((_params.containsKey("apns")) && (String.valueOf(_params.get("apns")).length() > 0)) {
                params.put("apns", _params.get("apns"));
            }
            params.put("os", os);
            params.put("osv", osv);
            params.put("dev", dev);
            params.put("apv", apv);


        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SIGN_IN;

        UTILS.DebugLog(TAG, "Sending this: " + params.toString() + "to this:" + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_NOT_FOUND) {
                        response.put("error", activity.getString(R.string.error_invalid_email_or_password));
                    }
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled)) {
                UTILS.DebugLog(TAG, "Error: " + error.toString());
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", error.toString());
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8"
                );
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);

    }

    private void CallTaskSignUp(final Hashtable<String, Object> _params) {

        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        JSONObject params = new JSONObject();
        try {
            params.put("name", _params.get("nome"));
            params.put("email", _params.get("email"));
            if (_params.containsKey("birthDay")) {
                params.put("birthDay", _params.get("birthDay"));
            }
            params.put("password", _params.get("senha"));
            params.put("apns", _params.get("apns"));
            params.put("id", UTILS.randomId());

        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }

        UTILS.DebugLog(TAG, "Sending this: " + params.toString() + "\nto: " + CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SIGN_UP);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SIGN_UP, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        if ((response.has("Object")) && (response.get("Object") != JSONObject.NULL)) {

                            JSONObject json = response.getJSONObject("Object");

                            json.put("nickname", _params.get("nome"));
                            json.put("email", _params.get("email"));
                            json.put("acceptNews", _params.get("news"));

                            success = true;
                        }
                    } else if (response.getInt("Status") == 403) {
                        CallTaskSignUp(_params);
                    }
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled)) {
                UTILS.DebugLog(TAG, "Error: " + error.toString());
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", error.toString());
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);

    }

    private void CallTaskForgotPassword(Hashtable<String, Object> _params) throws JSONException {

        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String email = String.valueOf(_params.get("email"));

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_FORGOT_PASSWORD;

        JSONObject params = new JSONObject();
        params.put("email", email);

        UTILS.DebugLog(TAG, "Sending this: " + params.toString() + " to: " + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK || response.getInt("Status") == HttpURLConnection.HTTP_NOT_FOUND) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);

    }

    private void CallTaskSetEndSession(Hashtable<String, Object> _params) {

        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        JSONObject params = new JSONObject();
        try {
            params.put("sId", _params.get("sId"));

            if (_params.containsKey("endType")) {
                params.put("endType", _params.get("endType"));
            } else {
                params.put("endType", CONSTANTS.END_SESSION_TYPE_ANDROID);
            }

            if (_params.containsKey("time"))
                params.put("end", _params.get("time"));
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }

        String[] server = CONSTANTS.serverURL.split("/v");
        String method = server[0] + "/" + CONSTANTS.WS_METHOD_SET_END_SESSION;
        UTILS.DebugLog(TAG, "Sending this: " + params + "to:" + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG + "(singOut)", "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_FORBIDDEN) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error1 " + error.toString());
            UTILS.DebugLog(TAG, "Error2 " + error.getMessage());
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);

    }

    private void CallTaskSaveTracking() {

        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());


        String method = CONSTANTS.serverURL.contains("/v") ? CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SAVE_TRACKING : CONSTANTS.serverURL + "v1/" + CONSTANTS.WS_METHOD_SAVE_TRACKING;

        UTILS.DebugLog(TAG, "Saving tracking! " + method + " sending this: " + getApiHeader());

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);

    }

    private void CallTaskGetNotificationHistory(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + String.format(CONSTANTS.WS_METHOD_GET_NOTIFICATION_HISTORY, _params.get("page"));

        UTILS.DebugLog(TAG, "Getting this: " + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                } else if (((response.has("Moedas")) && (response.get("Moedas") != JSONObject.NULL)) &&
                        ((response.has("Rank")) && (response.get("Rank") != JSONObject.NULL)) &&
                        ((response.has("Pontos")) && (response.get("Pontos") != JSONObject.NULL))) {
                    success = true;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetNotificationMarkAll() {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_NOTIFICATION_MARK_ALL;

        UTILS.DebugLog(TAG, "Getting this: " + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                } else if (((response.has("Moedas")) && (response.get("Moedas") != JSONObject.NULL)) &&
                        ((response.has("Rank")) && (response.get("Rank") != JSONObject.NULL)) &&
                        ((response.has("Pontos")) && (response.get("Pontos") != JSONObject.NULL))) {
                    success = true;
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSetNotification(final Hashtable<String, Object> _params) throws JSONException {

        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        final int idPush = (int) _params.get("idPush");

        String method = CONSTANTS.serverURL + String.format(CONSTANTS.WS_METHOD_SET_NOTIFICATION, idPush);

        UTILS.DebugLog(TAG, "Getting this: " + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                UTILS.DebugLog(TAG, "Response: " + response);
                boolean success = false;
                try {

                    if (response.has("Status")) {
                        if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                            CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                            return;
                        } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK ||
                                (response.getInt("Status") == HttpURLConnection.HTTP_ACCEPTED)) {
                            success = true;
                        }
                    } else {
                        success = false;
                    }

                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                    success = false;
                }

                if (!taskCancelled) {
                    try {
                        response.put("idPush", _params.get("idPush"));
                    } catch (JSONException e) {
                        UTILS.DebugLog(TAG, e);
                    }
                    callback.CallHandler(OP_ID, response, success);
                    UTILS.checkBadgeResponse(activity, response);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UTILS.DebugLog(TAG + " (SetNotification)", "Error! ");
                if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                    String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                    UTILS.DebugLog(TAG, "Error: " + errorStr);
                    JSONObject jsonError = new JSONObject();
                    try {
                        jsonError.put("error", errorStr);
                    } catch (JSONException e) {
                        UTILS.DebugLog(TAG, e);
                    }
                    callback.CallHandler(OP_ID, jsonError, false);
                } else if (!taskCancelled) {
                    callback.CallHandler(OP_ID, new JSONObject(), false);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);

    }

    private void CallTaskGetBadge(Hashtable<String, Object> _params) {

        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = (CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_BADGES);

        if (_params.containsKey("page")) {
            method = CONSTANTS.serverURL + String.format(CONSTANTS.WS_METHOD_GET_BADGES_PAGE, _params.get("page"));
        }

        UTILS.DebugLog(TAG, "GET " + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                } else if (((response.has("Moedas")) && (response.get("Moedas") != JSONObject.NULL)) &&
                        ((response.has("Rank")) && (response.get("Rank") != JSONObject.NULL)) &&
                        ((response.has("Pontos")) && (response.get("Pontos") != JSONObject.NULL))) {
                    success = true;
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskUploadPicture(Hashtable<String, Object> _params) {
        String path = String.valueOf(_params.get("sourceFile"));

        //fix parameter
        if (path.startsWith("file:")) {
            path = path.replace("file:", "");
        }

        UTILS.DebugLog(TAG, "path: " + path);

        File newFile = new File(path);
        String fileName = newFile.getName();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(newFile.getPath(), options);
        int width = options.outWidth;
        int height = options.outHeight;

        Log.d("Async", "Original width: " + width + " Original height: " + height);

        try {
            if (width > 500) {
                long kilobytes = (newFile.length() / 1024);
                long megabytes = (kilobytes / 1024);

                Log.d("Async", "@File size " + (megabytes == 0 ? kilobytes + " KB" : megabytes + " MB"));

                int newWidth = width;
                int newHeight = 0;

                while (newWidth > 500) {
                    newWidth--;
                }

                double div = (double) newWidth / (double) width;
                double mult = div * 100;
                double percentDecremented = mult - 100;


                newHeight = (int) (height - ((Math.abs(percentDecremented) * height) / 100));

                Log.d("Async", "New width: " + newWidth + "\nNew height: " + newHeight);

                /*if(megabytes > 2) {*/
                Bitmap compressed = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(newFile.getPath()), newWidth, newHeight, true);

                ByteArrayOutputStream out = new ByteArrayOutputStream();

                compressed.compress(Bitmap.CompressFormat.JPEG, 70, out);

                byte[] compressedImg = out.toByteArray();

                OutputStream out2;

                File createDir = new File(activity.getCacheDir() + "/" + File.separator + "temporary.jpg");
                createDir.createNewFile();

                if (!createDir.exists()) {
                    createDir.mkdir();
                }

                out2 = new FileOutputStream(createDir);

                out2.write(compressedImg);
                out.close();
                out2.close();

                newFile = createDir;

                if (newFile != null) {
                    kilobytes = (compressedImg.length / 1024);
                    megabytes = (kilobytes / 1024);
                    Log.d("Async", "New file size " + (megabytes == 0 ? kilobytes + " KB" : megabytes + " MB"));
                }
            }

            Log.d("Async", newFile.getPath());

            OkHttpClient client = new OkHttpClient();

            MediaType fileContentType = MediaType.parse("File/*");

            assert newFile != null;
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName,
                            RequestBody.create(fileContentType, newFile))
                    .build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(CONSTANTS.serverURL + CONSTANTS.WS_METHOD_UPLOAD_PICTURE)
                    .addHeader(CONSTANTS.WS_API_HEADER_KEY, getApiHeader())
                    .post(requestBody)
                    .build();


            try (okhttp3.Response res = client.newCall(request).execute()) {
                if (!res.isSuccessful()) {
                    UTILS.DebugLog(TAG, "Image Upload Failed: " + res);
                    if (!taskCancelled) {
                        try {
                            assert res.body() != null;
                            JSONObject response = new JSONObject(res.body().string());
                            callback.CallHandler(OP_ID, response, false);
                        } catch (JSONException e) {
                            UTILS.DebugLog(TAG, e);
                        }
                    }
                } else {
                    UTILS.DebugLog(TAG, "Image Upload Success:  " + res);

                    try {
                        assert res.body() != null;
                        JSONObject response = new JSONObject(res.body().string());
                        callback.CallHandler(OP_ID, response, true);

                        UTILS.checkBadgeResponse(activity, response);
                    } catch (JSONException e) {
                        UTILS.DebugLog(TAG, e);
                    }
                }
            }

        } catch (IOException ex) {
            UTILS.DebugLog(TAG, ex);
        }

    }

    private void CallTaskGetAllNews(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_ALL_NEWS;


        if (_params.containsKey("page") && _params.get("page") != null && !_params.get("page").toString().equals("")) {
            method += "?page=" + _params.get("page");
        }

        if (_params.containsKey("category") && _params.get("category") != null && !_params.get("category").toString().equals("")) {
            if (!method.contains("?")) {
                method += "?category=" + _params.get("category");
            }else {
                method += "&category=" + _params.get("category");
            }
        }

        if (_params.containsKey("fav") && _params.get("fav") != null && !_params.get("fav").toString().equals("")) {
            if (!method.contains("?")) {
                method += "?fav=" + _params.get("fav");
            }else {
                method += "&fav=" + _params.get("fav");
            }
        }

        if (_params.containsKey("order") && _params.get("order") != null && !_params.get("order").toString().equals("")) {
            if (!method.contains("?")) {
                method += "?order=" + _params.get("order");
            }else {
                method += "&order=" + _params.get("order");
            }
        }

        if (_params.containsKey("bookmarked") && _params.get("bookmarked") != null && !_params.get("bookmarked").toString().equals("")) {
            if (!method.contains("?")) {
                method += "?bookmarked=" + _params.get("bookmarked");
            }else {
                method += "&bookmarked=" + _params.get("bookmarked");
            }
        }

        UTILS.DebugLog(TAG, method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK ||
                            (response.getInt("Status") == HttpURLConnection.HTTP_ACCEPTED)) {
                        success = true;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetHome() {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        UTILS.DebugLog(TAG, CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_HOME);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_HOME, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK ||
                            (response.getInt("Status") == HttpURLConnection.HTTP_ACCEPTED)) {
                        success = true;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetAllVideos(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_ALL_VIDEOS;


        if (_params.containsKey("page") && _params.get("page") != null && !_params.get("page").toString().equals("")) {
            method += "?page=" + _params.get("page");
        }

        if (_params.containsKey("category") && _params.get("category") != null && !_params.get("category").toString().equals("")) {
            if (!method.contains("?")) {
                method += "?category=" + _params.get("category");
            }else {
                method += "&category=" + _params.get("category");
            }
        }

        if (_params.containsKey("fav") && _params.get("fav") != null && !_params.get("fav").toString().equals("")) {
            if (!method.contains("?")) {
                method += "?fav=" + _params.get("fav");
            }else {
                method += "&fav=" + _params.get("fav");
            }
        }

        if (_params.containsKey("order") && _params.get("order") != null && !_params.get("order").toString().equals("")) {
            if (!method.contains("?")) {
                method += "?order=" + _params.get("order");
            }else {
                method += "&order=" + _params.get("order");
            }
        }

        UTILS.DebugLog(TAG, method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK ||
                            (response.getInt("Status") == HttpURLConnection.HTTP_ACCEPTED)) {
                        success = true;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetArena(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_ARENA_FEED;

        if (_params.containsKey("idUser") && _params.get("idUser") != null && !_params.get("idUser").toString().equals("")) {
            method += "?idUser=" + _params.get("idUser");
        }

        if (_params.containsKey("order") && _params.get("order") != null && !_params.get("order").toString().equals("")) {
            if (!method.contains("?")) {
                method += "?order=" + _params.get("order");
            }else {
                method += "&order=" + _params.get("order");
            }
        }

        if (_params.containsKey("page") && _params.get("page") != null && !_params.get("page").toString().equals("")) {
            if (!method.contains("?")) {
                method += "?page=" + _params.get("page");
            }else {
                method += "&page=" + _params.get("page");
            }
        }

        if (_params.containsKey("category") && _params.get("category") != null && !_params.get("category").toString().equals("")) {
            if (!method.contains("?")) {
                method += "?category=" + _params.get("category");
            }else {
                method += "&category=" + _params.get("category");
            }
        }

        UTILS.DebugLog(TAG, method);


        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK ||
                            (response.getInt("Status") == HttpURLConnection.HTTP_ACCEPTED)) {
                        success = true;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetRanking(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_RANK;

        if (_params.containsKey("page")) {
            method = CONSTANTS.serverURL + "getRanking?page=" + _params.get("page");
        }

        UTILS.DebugLog(TAG, "GET " + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);

            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    } else {
                        success = false;
                    }
                } else if (((response.has("Moedas")) && (response.get("Moedas") != JSONObject.NULL)) &&
                        ((response.has("Rank")) && (response.get("Rank") != JSONObject.NULL)) &&
                        ((response.has("Pontos")) && (response.get("Pontos") != JSONObject.NULL))) {
                    success = true;
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UTILS.DebugLog(TAG, "Error! ");
                if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                    String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                    UTILS.DebugLog(TAG, "Error: " + errorStr);
                    JSONObject jsonError = new JSONObject();
                    try {
                        jsonError.put("error", errorStr);
                    } catch (JSONException e) {
                        UTILS.DebugLog(TAG, e);
                    }
                    callback.CallHandler(OP_ID, jsonError, false);
                } else if (!taskCancelled) {
                    callback.CallHandler(OP_ID, new JSONObject(), false);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }


    private void CallTaskGetPostArena(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_POST_ARENA + "?id=" + _params.get("id").toString() + (_params.containsKey("more") ? "&all=1" : "");

        UTILS.DebugLog(TAG, method);


        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK ||
                            (response.getInt("Status") == HttpURLConnection.HTTP_ACCEPTED)) {
                        success = true;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetPrizes(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_PRIZES;

        method += "?page=" + _params.get("page");

        if (_params.containsKey("order")) {
            method += "&order=" + _params.get("order");
        }

        UTILS.DebugLog(TAG, method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK ||
                            (response.getInt("Status") == HttpURLConnection.HTTP_ACCEPTED)) {
                        success = true;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSetMatchCompetitions(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_MATCH_COMPETITIONS + "?page="+_params.get("page");
        UTILS.DebugLog(TAG, method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK ||
                            (response.getInt("Status") == HttpURLConnection.HTTP_ACCEPTED)) {
                        success = true;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetPrizeDetails(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_PRIZES_DETAILS + _params.get("id");

        if (_params.containsKey("idCupom")) {
            method += "&idCupom=" + _params.get("idCupom");
        }

        UTILS.DebugLog(TAG, "GET " + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                } else if (((response.has("Moedas")) && (response.get("Moedas") != JSONObject.NULL)) &&
                        ((response.has("Rank")) && (response.get("Rank") != JSONObject.NULL)) &&
                        ((response.has("Pontos")) && (response.get("Pontos") != JSONObject.NULL))) {
                    success = true;
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetArenaPostLike(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SET_ARENA_POST_LIKE;

        UTILS.DebugLog(TAG, "GET " + method);
        JSONObject params = new JSONObject();
        try {
            params.put("id", _params.get("id"));
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }
        UTILS.DebugLog(TAG, "Params: " + params);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                } else if (((response.has("Moedas")) && (response.get("Moedas") != JSONObject.NULL)) &&
                        ((response.has("Rank")) && (response.get("Rank") != JSONObject.NULL)) &&
                        ((response.has("Pontos")) && (response.get("Pontos") != JSONObject.NULL))) {
                    success = true;
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSetDeliteComentary(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SET_ARENA_POST_DELETE_COMENTARY;

        UTILS.DebugLog(TAG, "POST " + method);
        JSONObject params = new JSONObject();
        try {
            params.put("id", _params.get("id"));
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }
        UTILS.DebugLog(TAG, "Params: " + params);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                } else if (((response.has("Moedas")) && (response.get("Moedas") != JSONObject.NULL)) &&
                        ((response.has("Rank")) && (response.get("Rank") != JSONObject.NULL)) &&
                        ((response.has("Pontos")) && (response.get("Pontos") != JSONObject.NULL))) {
                    success = true;
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSetDeleteArenaPost(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SET_ARENA_POST_DELETE_COMENTARY;

        UTILS.DebugLog(TAG, "POST " + method);
        JSONObject params = new JSONObject();
        try {
            params.put("id", _params.get("id"));
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }
        UTILS.DebugLog(TAG, "Params: " + params);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                } else if (((response.has("Moedas")) && (response.get("Moedas") != JSONObject.NULL)) &&
                        ((response.has("Rank")) && (response.get("Rank") != JSONObject.NULL)) &&
                        ((response.has("Pontos")) && (response.get("Pontos") != JSONObject.NULL))) {
                    success = true;
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSetArenaComment(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SET_ARENA_COMMENT;

        UTILS.DebugLog(TAG, "POST " + method);
        Log.e("params", new Gson().toJson(_params));
        JSONObject params = new JSONObject();
        try {
            params.put("idPost", _params.get("idPost"));
            params.put("text", _params.get("text"));
            if (_params.containsKey("idReplyComment")) {
                params.put("idReplyComment", _params.get("idReplyComment"));
            }
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }
        UTILS.DebugLog(TAG, "Params: " + params);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSetPostArena(Hashtable<String, Object> _params) {

        ArrayList<String> images_path = new ArrayList<>();
        String desc = "";
        String id = "0";


        try {
            images_path = new ArrayList<>(Arrays.asList(new Gson().fromJson(_params.get("imgs").toString(), String[].class)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            desc = _params.get("desc").toString();
            id = _params.get("idCategory").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }


        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        for (int i = 0; i < images_path.size(); i++) {

            File file = new File(images_path.get(i));

            if (file.exists()) {
                final MediaType MEDIA_TYPE = MediaType.parse(MimeTypeMap.getFileExtensionFromUrl(images_path.get(i)));
                builder.addFormDataPart("images[]", file.getName(), RequestBody.create(MEDIA_TYPE, file));
            } else {
                Log.d(TAG, "file not exist ");
            }
        }


        if (desc != null && !desc.replaceAll(" ","").equals("")) {
            builder.addFormDataPart("text", desc);
        }
        builder.addFormDataPart("idCategory", id);

        RequestBody requestBody = builder.build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SET_ARENA_POST)
                .post(requestBody)
                .addHeader("X-Smapikey", getApiHeader())
                .build();

        OkHttpClient client = new OkHttpClient();

        try (okhttp3.Response res = client.newCall(request).execute()) {
            if (!res.isSuccessful()) {
                UTILS.DebugLog(TAG, "Image Upload Failed: " + res);
                if (!taskCancelled) {
                    try {
                        assert res.body() != null;
                        JSONObject response = new JSONObject(res.body().string());
                        callback.CallHandler(OP_ID, response, false);
                    } catch (JSONException e) {
                        UTILS.DebugLog(TAG, e);
                    }
                }
            } else {
                String json = res.body().string();
                UTILS.DebugLog(TAG, "Image Upload Success:  " + json);

                try {
                    assert res.body() != null;
                    JSONObject response = new JSONObject(json);

                    boolean success = false;
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }

                    callback.CallHandler(OP_ID, response, success);

                    UTILS.checkBadgeResponse(activity, response);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                    callback.CallHandler(OP_ID, new JSONObject(), false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            callback.CallHandler(OP_ID, new JSONObject(), false);
        }
    }


    private void CallTaskSetUpdateUser(Hashtable<String, Object> _params) {

        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        JSONObject params = new JSONObject();
        try {
            if (_params.containsKey("name")) {
                params.put("name", _params.get("name"));
            }
            if (_params.containsKey("city")) {
                params.put("city", _params.get("city"));
            }
            if (_params.containsKey("bornDate")) {
                params.put("bornDate", _params.get("bornDate"));
            }
            if (_params.containsKey("gender")) {
                params.put("gender", _params.get("gender"));
            }
            if (_params.containsKey("email")) {
                params.put("email", _params.get("email"));
            }
            if (_params.containsKey("password")) {
                params.put("password", _params.get("password"));
            }
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_UPDATE_USER;

        UTILS.DebugLog(TAG, "Sending this: " + params.toString() + "to this:" + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_NOT_FOUND) {
                        response.put("error", activity.getString(R.string.error_invalid_email_or_password));
                    }
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled)) {
                UTILS.DebugLog(TAG, "Error: " + error.toString());
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", error.toString());
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8"
                );
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetNews(Hashtable<String, Object> _params) {

        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_NEWS + "?id=" + _params.get("id");

        UTILS.DebugLog(TAG, "Sending this: " + "to this:" + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_NOT_FOUND) {
                        response.put("error", activity.getString(R.string.error_invalid_email_or_password));
                    }
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled)) {
                UTILS.DebugLog(TAG, "Error: " + error.toString());
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", error.toString());
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8"
                );
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetArenaCategories() {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());
        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_ARENA_CATEGORIES;

        UTILS.DebugLog(TAG, "GET " + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                } else if (((response.has("Moedas")) && (response.get("Moedas") != JSONObject.NULL)) &&
                        ((response.has("Rank")) && (response.get("Rank") != JSONObject.NULL)) &&
                        ((response.has("Pontos")) && (response.get("Pontos") != JSONObject.NULL))) {
                    success = true;
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskPostDelete(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SET_ARENA_POST_DELETE;

        UTILS.DebugLog(TAG, "GET " + method);
        JSONObject params = new JSONObject();
        try {
            params.put("id", _params.get("id"));
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }
        UTILS.DebugLog(TAG, "Params: " + params);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                } else if (((response.has("Moedas")) && (response.get("Moedas") != JSONObject.NULL)) &&
                        ((response.has("Rank")) && (response.get("Rank") != JSONObject.NULL)) &&
                        ((response.has("Pontos")) && (response.get("Pontos") != JSONObject.NULL))) {
                    success = true;
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetRankingTitle() {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_RANK_TITLE;

        UTILS.DebugLog(TAG, "GET " + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    } else {
                        success = false;
                    }
                } else if (((response.has("Moedas")) && (response.get("Moedas") != JSONObject.NULL)) &&
                        ((response.has("Rank")) && (response.get("Rank") != JSONObject.NULL)) &&
                        ((response.has("Pontos")) && (response.get("Pontos") != JSONObject.NULL))) {
                    success = true;
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UTILS.DebugLog(TAG, "Error! ");
                if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                    String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                    UTILS.DebugLog(TAG, "Error: " + errorStr);
                    JSONObject jsonError = new JSONObject();
                    try {
                        jsonError.put("error", errorStr);
                    } catch (JSONException e) {
                        UTILS.DebugLog(TAG, e);
                    }
                    callback.CallHandler(OP_ID, jsonError, false);
                } else if (!taskCancelled) {
                    callback.CallHandler(OP_ID, new JSONObject(), false);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskPosNotificationsPermissions(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_NOTIFICATIONS_PERMISSION;

        UTILS.DebugLog(TAG, "Getting this: " + method);

        JSONObject params = new JSONObject();
        try {
            params.put("idUser", _params.get("idUser"));
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }
        UTILS.DebugLog(TAG, "Params: " + params);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                } else if (((response.has("Moedas")) && (response.get("Moedas") != JSONObject.NULL)) &&
                        ((response.has("Rank")) && (response.get("Rank") != JSONObject.NULL)) &&
                        ((response.has("Pontos")) && (response.get("Pontos") != JSONObject.NULL))) {
                    success = true;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetBadgeDetails(Hashtable<String, Object> _params) throws JSONException {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_BADGES_DETAILS + "?badgeId=" + _params.get("badgeId");

        UTILS.DebugLog(TAG, "GET " + method + " params: " + _params);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    } else {
                        success = false;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UTILS.DebugLog(TAG, "Error! ");
                if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                    String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                    UTILS.DebugLog(TAG, "Error: " + errorStr);
                    JSONObject jsonError = new JSONObject();
                    try {
                        jsonError.put("error", errorStr);
                    } catch (JSONException e) {
                        UTILS.DebugLog(TAG, e);
                    }
                    callback.CallHandler(OP_ID, jsonError, false);
                } else if (!taskCancelled) {
                    callback.CallHandler(OP_ID, new JSONObject(), false);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetNotificationRead(Hashtable<String, Object> _params) throws JSONException {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_NOTIFICATIONS_PERMISSION_READ + "?idPush=" + _params.get("idPush");

        JSONObject params = new JSONObject();
        try {
            params.put("idType", _params.get("idType"));
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }

        UTILS.DebugLog(TAG, "GET " + method + " params: " + params);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    } else {
                        success = false;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UTILS.DebugLog(TAG, "Error! ");
                if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                    String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                    UTILS.DebugLog(TAG, "Error: " + errorStr);
                    JSONObject jsonError = new JSONObject();
                    try {
                        jsonError.put("error", errorStr);
                    } catch (JSONException e) {
                        UTILS.DebugLog(TAG, e);
                    }
                    callback.CallHandler(OP_ID, jsonError, false);
                } else if (!taskCancelled) {
                    callback.CallHandler(OP_ID, new JSONObject(), false);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSetNewsFavorite(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_NEWS_FAVORITE;

        JSONObject params = new JSONObject();
        try {
            params.put("idUser", _params.get("idUser"));
            params.put("newsId", _params.get("newsId"));
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }

        UTILS.DebugLog(TAG, "GET " + method + " params: " + params);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    } else {
                        success = false;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UTILS.DebugLog(TAG, "Error! ");
                if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                    String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                    UTILS.DebugLog(TAG, "Error: " + errorStr);
                    JSONObject jsonError = new JSONObject();
                    try {
                        jsonError.put("error", errorStr);
                    } catch (JSONException e) {
                        UTILS.DebugLog(TAG, e);
                    }
                    callback.CallHandler(OP_ID, jsonError, false);
                } else if (!taskCancelled) {
                    callback.CallHandler(OP_ID, new JSONObject(), false);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSetQuizAnswer(Hashtable<String, Object> _params) throws JSONException {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_QUIZ_ANSWER;

        JSONObject params = new JSONObject();
        try {
            params.put("idQuestion", _params.get("idQuestion"));
            params.put("idAnswer", _params.get("idAnswer"));
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }

        UTILS.DebugLog(TAG, "GET " + method + " params: " + params);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    } else {
                        success = false;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UTILS.DebugLog(TAG, "Error! ");
                if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                    String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                    UTILS.DebugLog(TAG, "Error: " + errorStr);
                    JSONObject jsonError = new JSONObject();
                    try {
                        jsonError.put("error", errorStr);
                    } catch (JSONException e) {
                        UTILS.DebugLog(TAG, e);
                    }
                    callback.CallHandler(OP_ID, jsonError, false);
                } else if (!taskCancelled) {
                    callback.CallHandler(OP_ID, new JSONObject(), false);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetQuiz() {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_QUIZ;

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    } else {
                        success = false;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSetNotificationsPermissions(Hashtable<String, Object> _params) throws JSONException {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_NOTIFICATIONS_PERMISSION_READ;

        JSONObject params = new JSONObject();
        try {
            params.put("idType", _params.get("idType"));
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }

        UTILS.DebugLog(TAG, "POST " + method + " params: " + params);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    } else {
                        success = false;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UTILS.DebugLog(TAG, "Error! ");
                if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                    String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                    UTILS.DebugLog(TAG, "Error: " + errorStr);
                    JSONObject jsonError = new JSONObject();
                    try {
                        jsonError.put("error", errorStr);
                    } catch (JSONException e) {
                        UTILS.DebugLog(TAG, e);
                    }
                    callback.CallHandler(OP_ID, jsonError, false);
                } else if (!taskCancelled) {
                    callback.CallHandler(OP_ID, new JSONObject(), false);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetCompetitionTables(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_COMPETITION_TABLES + _params.get("idCompetition");

        UTILS.DebugLog(TAG, "POST " + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    } else {
                        success = false;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UTILS.DebugLog(TAG, "Error! ");
                if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                    String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                    UTILS.DebugLog(TAG, "Error: " + errorStr);
                    JSONObject jsonError = new JSONObject();
                    try {
                        jsonError.put("error", errorStr);
                    } catch (JSONException e) {
                        UTILS.DebugLog(TAG, e);
                    }
                    callback.CallHandler(OP_ID, jsonError, false);
                } else if (!taskCancelled) {
                    callback.CallHandler(OP_ID, new JSONObject(), false);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetCompetitionMatches(Hashtable<String, Object> _params) {

        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_COMPETITION_MATCHES + "?idCompetition=" + _params.get("idCompetition");

        UTILS.DebugLog(TAG, "Sending this: " + "to this:" + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_NOT_FOUND) {
                        response.put("error", activity.getString(R.string.error_invalid_email_or_password));
                    }
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled)) {
                UTILS.DebugLog(TAG, "Error: " + error.toString());
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", error.toString());
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8"
                );
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetEditProfileCoins() {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());
        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_PROFILE_COINS;

        UTILS.DebugLog(TAG, "GET " + method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                } else if (((response.has("Moedas")) && (response.get("Moedas") != JSONObject.NULL)) &&
                        ((response.has("Rank")) && (response.get("Rank") != JSONObject.NULL)) &&
                        ((response.has("Pontos")) && (response.get("Pontos") != JSONObject.NULL))) {
                    success = true;
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetCompetitionMatch(Hashtable<String, Object> _param) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_COMPETITION_MATCHE + "?id=" + _param.get("id");
        UTILS.DebugLog(TAG, method);


        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK ||
                            (response.getInt("Status") == HttpURLConnection.HTTP_ACCEPTED)) {
                        success = true;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSetFollowVideosCategory(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SET_FOLLOW_VIDEOS;

        UTILS.DebugLog(TAG, method);
        JSONObject params = new JSONObject();
        try {
            params.put("idCategory", _params.get("idCategory"));
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK ||
                            (response.getInt("Status") == HttpURLConnection.HTTP_ACCEPTED)) {
                        success = true;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSetFollowNewsCategory(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SET_FOLLOW_NEWS;

        UTILS.DebugLog(TAG, method);
        JSONObject params = new JSONObject();
        try {
            params.put("idCategory", _params.get("idCategory"));
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {

                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK ||
                            (response.getInt("Status") == HttpURLConnection.HTTP_ACCEPTED)) {
                        success = true;
                    }
                } else {
                    success = false;
                }

            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetNewsComment(Hashtable<String, Object> _params) {

        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_NEWS_COMMENT + _params.get("id") + (_params.containsKey("all") ? "&all=1" : "");

        UTILS.DebugLog(TAG, "Sending this: " + method.toString());

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskDeleteNewsComment(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_DELETE_NEWS_COMMENT;

        UTILS.DebugLog(TAG, "POST " + method);
        JSONObject params = new JSONObject();
        try {
            params.put("id", _params.get("id"));
        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }
        UTILS.DebugLog(TAG, "Params: " + params);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                } else if (((response.has("Moedas")) && (response.get("Moedas") != JSONObject.NULL)) &&
                        ((response.has("Rank")) && (response.get("Rank") != JSONObject.NULL)) &&
                        ((response.has("Pontos")) && (response.get("Pontos") != JSONObject.NULL))) {
                    success = true;
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskSetNewsComment(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_SET_NEWS_COMMENT;

        UTILS.DebugLog(TAG, "POST " + method);
        JSONObject params = new JSONObject();
        try {
            params.put("idNews", _params.get("idPost"));
            params.put("text", _params.get("text"));
            if (_params.containsKey("idReplyComment")) {
                params.put("idReplyComment", _params.get("idReplyComment"));
            }
            UTILS.DebugLog(TAG, "Sending this: " + params );

        } catch (JSONException e) {
            UTILS.DebugLog(TAG, e);
        }
        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, method, params, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;
            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    private void CallTaskGetNewsCommentReplies(Hashtable<String, Object> _params) {
        RequestQueue rq = Volley.newRequestQueue(activity.getApplicationContext());

        String method = CONSTANTS.serverURL + CONSTANTS.WS_METHOD_GET_NEWS_COMMENT_REPLIES;

        UTILS.DebugLog(TAG, "POST " + method);
        method += "?idNews=" + _params.get("idPost") + "&idComment=" + _params.get("idComment");

        UTILS.DebugLog(TAG,   "Getting this: " +  method);

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.GET, method, null, response -> {
            UTILS.DebugLog(TAG, "Response: " + response);
            boolean success = false;

            try {
                if (response.has("Status")) {
                    if ((response.getInt("Status") == HttpURLConnection.HTTP_NOT_ACCEPTABLE)) {
                        CallTokenAuthError(response.has("msg") ? response.getString("msg") : "");
                        return;
                    } else if (response.getInt("Status") == HttpURLConnection.HTTP_OK) {
                        success = true;
                    }
                }
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, e);
                success = false;
            }

            if (!taskCancelled) {
                callback.CallHandler(OP_ID, response, success);
                UTILS.checkBadgeResponse(activity, response);
            }
        }, error -> {
            UTILS.DebugLog(TAG, "Error! ");
            if ((error != null) && (!taskCancelled) && (error.networkResponse != null) && (error.networkResponse.data != null)) {

                String errorStr = UTILS.getVolleyErrorDataString(error.networkResponse.data);
                UTILS.DebugLog(TAG, "Error: " + errorStr);
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError.put("error", errorStr);
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                callback.CallHandler(OP_ID, jsonError, false);
            } else if (!taskCancelled) {
                callback.CallHandler(OP_ID, new JSONObject(), false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(CONSTANTS.WS_API_HEADER_KEY, getApiHeader());
                return headers;
            }
        };

        int socketTimeout = CONSTANTS.CONNECTION_TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postReq.setRetryPolicy(policy);

        rq.add(postReq);
    }

    public interface IAsyncOpCallback {
        void CallHandler(int opId, JSONObject response, boolean success);

        void OnAsyncOperationSuccess(int opId, JSONObject response);

        void OnAsyncOperationError(int opId, JSONObject response);
    }

    Handler noConnectionMessageHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            Toast.makeText(activity, activity.getResources().getString(R.string.sem_conexao), Toast.LENGTH_SHORT).show();
            return false;
        }
    });

    public String getApiHeader() {
        if (!BTN_VALUE.isEmpty()) {
            return TRACKING.buildHeader(BTN_ID, BTN_VALUE);
        }
        int BTN_VAR = -1;
        return TRACKING.buildHeader(BTN_ID, BTN_VAR);
    }

    private void CallNoConnectionMessage() {
        noConnectionMessageHandler.sendEmptyMessage(0);
    }

    private void CallTokenAuthError(String msg) {
        if (!msg.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogCustom);
            builder.setCancelable(false);
            builder.setMessage("Te rugăm să te autentifici din nou.");

            builder.setTitle(activity.getResources().getString(R.string.sesiune_expirata));

            builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                try {
                    dialogInterface.dismiss();
                    UserInformation.setSessionId(0);
                    UTILS.ClearInformations(activity);
                    Intent newIntent = new Intent(activity, SplashActivity.class);
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    newIntent.putExtra("resetSSO", true);
                    activity.startActivity(newIntent);
                    activity.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    UserInformation.setSessionId(0);
                    UTILS.ClearInformations(activity);
                    Intent newIntent = new Intent(activity, SplashActivity.class);
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    newIntent.putExtra("resetSSO", true);
                    activity.startActivity(newIntent);
                    activity.finish();
                }
            });

            try {builder.create().show();} catch (Exception e) {e.printStackTrace();}
        }
    }
}