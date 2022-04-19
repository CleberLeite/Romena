package com.frf.app.utils;

import static com.frf.app.activitys.MainActivity.prefs;
import static com.frf.app.utils.CONSTANTS.SHARED_KEY_USER_ID;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.frf.app.R;
import com.frf.app.activitys.MenuActivity;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.activitys.WinBadgeActivity;
import com.frf.app.adapter.AdapterWinBadge;
import com.frf.app.data.BadgeData;
import com.frf.app.informations.UserInformation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.util.Hex;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class UTILS {

    private static boolean isUserLoggedIn = false;
    private static final String TAG = "UTILS";

    public static final int FLIP_VERTICAL = 1;
    public static final int FLIP_HORIZONTAL = 2;
    public static Context context;
    public static String id;

    public static boolean isValidEmail(CharSequence target) {
        return ((!TextUtils.isEmpty(target)) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    //There is no knowledge that is not power
    public static synchronized void DebugLog(String TAG, Object o) {
        Log.d(TAG, String.valueOf(o));
    }

    public static int getNetworkStatus(ConnectivityManager connectivityManager){
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return CONSTANTS.CONNECTION_TYPE_WIFI;
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return CONSTANTS.CONNECTION_TYPE_MOBILE;
        }
        return CONSTANTS.CONNECTION_TYPE_NONE;
    }

    public static float dpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static boolean checkGooglePlayServiceAvailability(Context context) {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

    public static boolean isUserLoggedIn(){
        return isUserLoggedIn;
    }

    public static void RouterBack(Activity activity) {
        activity.finish();
    }

    public static boolean ValidateImputText(TextInputLayout til, EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            til.setError("E-mailul nu se potriveste");
            editText.setBackgroundResource(R.drawable.error_background);
            return false;
        } else {
            editText.setBackgroundResource(R.drawable.normal_background);
            return false;
        }
    }

    public static String getApiHeaderValue(){
        if (prefs == null) {
            prefs = new SecurePreferences(context, CONSTANTS.SHARED_PREFS_KEY_FILE, CONSTANTS.SHARED_PREFS_SECRET_KEY, true);
        }

        if ( id == null || id.equals("")) {
            id = prefs.getString(SHARED_KEY_USER_ID, "");
            if (id.equals("")) {
                id = UserInformation.getUserId();
            }
        }
        return id;
    }

    public static String getVolleyErrorDataString(byte[] data){
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            JSONArray errors = data.getJSONArray("errors");
            JSONObject jsonMessage = errors.getJSONObject(0);
            String message = jsonMessage.getString("message");
            DebugLog("AsyncOperation", message);
        } catch (JSONException | UnsupportedEncodingException e) {
            DebugLog("AsyncOperation", e.getMessage());
        }
    }

    public static void clearId() {
        id = "";
    }

    public static String formatDateFromDB(String dt){
        boolean success = false;
        String formatted = "";

        if(dt.contains(" ")){
            String[] hourDt = dt.split(" ");

            String justDate = hourDt[0];
            String[] unformatted = justDate.split("-");
            if (unformatted.length >= 3) {
                formatted = unformatted[2] + "/" +
                        unformatted[1] + "/" +
                        unformatted[0];

                success = true;
            }
        }
        else {
            String[] unformatted = dt.split("-");
            if (unformatted.length >= 3) {
                formatted = unformatted[2] + "/" +
                        unformatted[1] + "/" +
                        unformatted[0];

                success = true;
            }
        }

        return (success ? formatted : dt);
    }

    public static String getPath(Context context, Uri uri) {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;

        if (needToCheckUri && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return "/storage/emulated/0/" + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:", "");
                }
                uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                switch (type) {
                    case "image":
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "video":
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "audio":
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        break;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    return cursor.getString(columnIndex);
                }
            } catch (Exception e) {
                Log.e("on getPath", "Exception", e);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getRealPathFromURI(final Context context, final Uri uri) {

        if (uri.getScheme().equals("file")) {
            return uri.toString();

        } else if (uri.getScheme().equals("content")) {
            // DocumentProvider
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (DocumentsContract.isDocumentUri(context, uri)) {

                    // ExternalStorageProvider
                    if (isExternalStorageDocument(uri)) {
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];

                        if ("primary".equalsIgnoreCase(type)) {
                            return Environment.getExternalStorageDirectory() + "/" + split[1];
                        }

                        // to_do handle non-primary volumes
                    }
                    // DownloadsProvider
                    else if (isDownloadsDocument(uri)) {

                        String id = DocumentsContract.getDocumentId(uri);

                        long idl = -1l;
                        try {
                            idl = Long.valueOf(id);
                        } catch (Exception e) {
                            UTILS.DebugLog(TAG, e);
                        }

                        if (idl == -1) {
                            String prefix = "raw:";
                            if (id.startsWith(prefix)) {
                                id = id.substring(prefix.length(), id.length());
                            }
                            return id;
                        }

                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));


                        return getDataColumn(context, contentUri, null, null);
                    }
                    // MediaProvider
                    else if (isMediaDocument(uri)) {
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];

                        Uri contentUri = null;
                        if ("image".equals(type)) {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(type)) {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(type)) {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        final String selection = "_id=?";
                        final String[] selectionArgs = new String[]{
                                split[1]
                        };

                        return getDataColumn(context, contentUri, selection, selectionArgs);
                    }
                }
                // MediaStore (and general)
                else if ("content".equalsIgnoreCase(uri.getScheme())) {

                    // Return the remote address
                    if (isGooglePhotosUri(uri)) {
                        return uri.getLastPathSegment();
                    }

                    return getDataColumn(context, uri, null, null);
                }
                // File
                else if ("file".equalsIgnoreCase(uri.getScheme())) {
                    return uri.getPath();
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

        public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {

                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }

            phrase.append(c);
        }

        return phrase.toString();
    }

    public static int convertDpToPixels(float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, Resources.getSystem().getDisplayMetrics()));
    }

    public static  @NonNull
    Bitmap createBitmapFromView(@NonNull View view, int width, int height) {
        if (width > 0 && height > 0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(
                    convertDpToPixels(width), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(convertDpToPixels(height), View.MeasureSpec.UNSPECIFIED));
        }
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable background = view.getBackground();

        if (background != null) {
            background.draw(canvas);
        }
        view.draw(canvas);

        return bitmap;
    }

    public static String formatDate(String date) {
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");

        Date datex = null;
        String mDate = date;

        try {
            datex = input.parse(date);
            mDate = output.format(datex);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mDate;
    }

    public static void menuLateral(Activity activity) {
        Intent intent = new Intent(activity, MenuActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    public static void translate(View viewToMove, float y) {
        viewToMove.animate()
                .x(0.0f)
                .y(y)
                .setDuration(300)
                .start();
    }

    public static float getToolBarHeight(Context context) {
        int[] attrs = new int[]{R.attr.actionBarSize};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        int toolBarHeight = ta.getDimensionPixelSize(0, -1);
        ta.recycle();
        return toolBarHeight;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap flip(Bitmap src, int type) {
        // criar new matrix para transformacao
        Matrix matrix = new Matrix();
        // if vertical
        if (type == FLIP_VERTICAL) {
            // y = y * -1
            matrix.preScale(1.0f, -1.0f);
        }
        // if horizonal
        else if (type == FLIP_HORIZONTAL) {
            // x = x * -1
            matrix.preScale(-1.0f, 1.0f);
            // unknown type
        } else {
            return null;
        }

        // return image transformada
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public static void getImageAt(Context activity, String link, ImageView imageView) {
        try {
            if (activity != null) {
                Glide.with(activity).load(link)
                        .into(imageView);
            }
        } catch (Exception e) {
            Log.e("getImageAt()", e.getMessage());
        }
    }

    public static void getImageAt(Context activity, String link, ImageView imageView, RequestOptions options) {
        try {
            if (activity != null) {
                Glide.with(activity).load(link)
                        .apply(options)
                        .into(imageView);
            }
        } catch (Exception e) {
            Log.e("getImageAt()", e.getMessage());
        }
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static void ClearInformations(Context context){
        setUserLoggedIn(false);
        UserInformation.ClearUserData(context);
    }

    public static void setUserLoggedIn(boolean isLoggedIn){
        isUserLoggedIn = isLoggedIn;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void addViewSize(HorizontalScrollView view, ArrayList<String> data, Context context, View btn) {
        //Linear Layout
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Space space = new Space(context);
        RelativeLayout.LayoutParams paramsSpace = new RelativeLayout.LayoutParams(Math.round(UTILS.pxFromDp(context, 10)), ViewGroup.LayoutParams.WRAP_CONTENT);
        space.setLayoutParams(paramsSpace);

        linearLayout.addView(space);

        ArrayList<RelativeLayout> relativeLayouts = new ArrayList<>();
        ArrayList<TextView> txt = new ArrayList<>();
        ArrayList<Boolean> clicked = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            //TEXT
            TextView text = new TextView(context);
            RelativeLayout.LayoutParams paramsTXT = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsTXT.addRule(RelativeLayout.CENTER_IN_PARENT);
            text.setLayoutParams(paramsTXT);
            text.setText(data.get(i));
            text.setTextSize(18);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.exo2_regular);
            text.setTypeface(typeface);
            text.setTextColor(ContextCompat.getColor(context, R.color.PrimaryColorText));
            txt.add(text);

            RelativeLayout relativeLayout = new RelativeLayout(context);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Math.round(UTILS.pxFromDp(context, 48)), Math.round(UTILS.pxFromDp(context, 40)));
            params.setMargins(0, 0, 20, 0);

            clicked.add(true);

            relativeLayout.setLayoutParams(params);
            relativeLayout.setBackground(context.getDrawable(R.drawable.border_card_blue_10));
            relativeLayout.addView(text);
            int finalI = i;
            relativeLayout.setOnClickListener(view1 -> disableCheck(relativeLayouts, finalI, context, btn));
            relativeLayouts.add(relativeLayout);

            linearLayout.addView(relativeLayouts.get(i));
        }
        view.addView(linearLayout);

        // disableCheck(relativeLayouts, 0);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void addViewColor(HorizontalScrollView view, ArrayList<String> data, Context context) {

        //Linear Layout
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        Space space = new Space(context);
        RelativeLayout.LayoutParams paramsSpace = new RelativeLayout.LayoutParams(Math.round(UTILS.pxFromDp(context, 10)), ViewGroup.LayoutParams.WRAP_CONTENT);
        space.setLayoutParams(paramsSpace);

        linearLayout.addView(space);

        ArrayList<CardView> cardViews = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            CardView cardView = new CardView(context);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Math.round(UTILS.pxFromDp(context, 28)), Math.round(UTILS.pxFromDp(context, 28)));
            params.setMargins(0, 0, 40, 0);

            cardView.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor(data.get(i))));
            cardView.setRadius(50);
            cardView.setCardElevation(0);
            cardView.setElevation(0);
            cardView.setLayoutParams(params);

            if(data.get(i).equals("#FFFFFF")){
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.ThirdBackground));
                LinearLayout linearLayoutAUX = new LinearLayout(context);

                CardView cardViewWhite = new CardView(context);
                LinearLayout.LayoutParams paramsWhite = new LinearLayout.LayoutParams(Math.round(UTILS.pxFromDp(context, 26)), Math.round(UTILS.pxFromDp(context, 26)));
                paramsWhite.setMargins(2, 2, 2, 2);

                linearLayoutAUX.setLayoutParams(paramsWhite);
                cardViewWhite.setLayoutParams(paramsWhite);
                cardViewWhite.setRadius(50);

                cardViewWhite.setCardElevation(0);
                cardViewWhite.setElevation(0);

                linearLayoutAUX.setGravity(Gravity.CENTER);
                linearLayoutAUX.addView(cardViewWhite);
                cardView.addView(linearLayoutAUX);
            }

            cardViews.add(cardView);
            linearLayout.addView(cardViews.get(i));
        }

        view.addView(linearLayout);

//            relativeLayout.setBackground(getDrawable(R.drawable.color_border_rounded));

    }

    public static @SuppressLint("UseCompatLoadingForDrawables")
    void disableCheck(ArrayList<RelativeLayout> relativeLayouts, int index, Context context, View btn) {
        btn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.PrimaryBackground));
        for (int i = 0; i < relativeLayouts.size(); i++) {
            if (i == index) {
                if (relativeLayouts.get(i).getBackground() != context.getDrawable(R.drawable.border_card_amber)) {
                    relativeLayouts.get(i).setBackground(context.getDrawable(R.drawable.border_card_amber));
                }else {
                    relativeLayouts.get(i).setBackground(context.getDrawable(R.drawable.border_card_blue_10));
                }
            } else {
                relativeLayouts.get(i).setBackground(context.getDrawable(R.drawable.border_card_blue_10));
            }
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        if(context == null){
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return ( (activeNetworkInfo != null) && (activeNetworkInfo.isConnected()) );
    }

    public static String randomId() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
        StringBuilder sb = new StringBuilder(32);
        Random rand = new Random();
        for (int i = 0; i < 32; i++) {
            char c = chars[rand.nextInt(chars.length)];
            sb.append(c);
        }

        return sb.toString();
    }

    public static void checkBadgeResponse(Activity activity, JSONObject response) {
        if (response.has("newBadges")) {
            try{((StartMenuActivity)activity).setIsUpdateProfile(true);}catch (Exception e){e.printStackTrace();}
            try {
                String badges = response.getString("newBadges");
                /*Intent intent = new Intent(activity, WinBadgeActivity.class);
                intent.putExtra("badges", badges);
                activity.startActivity(intent);*/

                Gson gson = new Gson();
                BadgeData[] data = gson.fromJson(badges, BadgeData[].class);

                if (data != null && data.length > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.DialogFullScreen);
                    builder.setCancelable(true);
                    AlertDialog dialog = builder.create();

                    View view = LayoutInflater.from(activity).inflate(R.layout.dialog_badge, null, false);

                    ViewPager2 pager = view.findViewById(R.id.view_pager);
                    Button next = view.findViewById(R.id.btn_next);

                    AdapterWinBadge adapter = new AdapterWinBadge(activity, new ArrayList<>(Arrays.asList(data)), () -> {
                        try{dialog.dismiss();}catch (Exception e){e.printStackTrace();}
                    });

                    pager.setAdapter(adapter);

                    pager.setOffscreenPageLimit(3);
                    pager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int width = displayMetrics.widthPixels;

                    final int nextItemVisiblePx = 100 * width / 100;
                    final int currentItemHorizontalMarginPx = 0;
                    final int pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx;

                    final float[] offset = {-1};
                    ViewPager2.PageTransformer pageTransformer = (page, position) -> {

                        page.setTag(position);
                        page.setTranslationX(-pageTranslationX * position);

                        page.setScaleY(1 - (0.30f * abs(position)));
                        page.setScaleX(1 - (0.30f * abs(position)));
                        page.setTranslationY(1 + (90 * abs(position)));

                        if (offset[0] == -1) {
                            offset[0] = 0 / page.getMeasuredWidth();
                        }

                        if (position <= 1) {
                            float elevation = Math.max(0.5f, 1 - abs(position - offset[0]));
                            page.setElevation(elevation);
                        }
                    };

                    pager.setPageTransformer(pageTransformer);

                    final int[] pos = {0};

                    pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            pos[0] = position;
                            if (position < (adapter.getItemCount() -1)) {
                                next.setText(activity.getResources().getString(R.string.next) + " " + (position+1) + "/" + adapter.getItemCount());
                            }else {
                                next.setText(R.string.exit);
                            }
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {
                            super.onPageScrollStateChanged(state);
                        }
                    });

                    if (data.length > 1) {
                        next.setVisibility(View.VISIBLE);
                        next.setOnClickListener(view1 -> {
                            if (pos[0] < (adapter.getItemCount() -1)) {
                                pager.setCurrentItem(pager.getCurrentItem() + 1);
                            }else {
                                try{dialog.dismiss();}catch (Exception e){e.printStackTrace();}
                            }
                        });
                    }

                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.setView(view);
                    try{dialog.show();}catch (Exception e){e.printStackTrace();}
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public static void setColorStatusBar(Activity activity, @ColorRes int resColor){
        Window window = activity.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(activity, resColor));
    }

    @SuppressLint("ResourceType")
    public static void setColorStatusBar(Activity activity, String HexColor){
        Window window = activity.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(activity, Color.parseColor(HexColor)));
    }


    public static String PrettyTime(Context activity, String data){

        if (data != null) {
        PrettyTime prettyTime = new PrettyTime(activity.getResources().getConfiguration().locale);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormatData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        try {
            date = dateFormatData.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return prettyTime.format(date).toLowerCase().replaceAll("ago", "");} else {
            return "";
        }
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
