package com.frf.app.fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.frf.app.R;
import com.frf.app.activitys.ProductActivity;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.adapter.AdapterShopCategoty;
import com.frf.app.adapter.ShopBannerAdapter;
import com.frf.app.data.CategoriesData;
import com.frf.app.data.ShopBannerData;
import com.frf.app.data.ShopHomepageData;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.CONSTANTS;
import com.frf.app.utils.UTILS;
import com.frf.app.vmodels.fragments.FragmentHolderViewModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MagazinFragment extends MainFragment /*implements AsyncOperation.IAsyncOpCallback*/ {
    private ViewHolder mHolder;
    private FragmentHolderViewModel mfragmentViewModel;

    private static final int OP_GET_SHOP_HOMEPAGE = 1;
    private static final int OP_GET_SHOP_CATEGORIES = 2;

    public MagazinFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_magazin, container, false);
        mHolder = new ViewHolder(view);
        WebSettings webSettings = mHolder.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setAllowFileAccess(true);

        mHolder.webView.setWebViewClient(new WebViewClient());
        mHolder.webView.setWebChromeClient(new ChromeClient());
        mHolder.webView.loadUrl(CONSTANTS.link_ecommerce);

        /*new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_SHOP_HOMEPAGE, OP_GET_SHOP_HOMEPAGE, this).execute();
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_SHOP_CATEGORIES, OP_GET_SHOP_CATEGORIES, this).execute();
*/
        return view;
    }

    @Override
    public void onBackPressed() {
        if(mHolder.webView.canGoBack()){
            mHolder.webView.goBack();
        }else{
            super.onBackPressed();
        }
    }

    /*@Override
    public void bind() {
        super.bind();
        mfragmentViewModel = new ViewModelProvider(requireActivity()).get(FragmentHolderViewModel.class);
    }

    private void initBanner(ArrayList<ShopBannerData> data) {
        ShopBannerAdapter adapter = new ShopBannerAdapter(activity, data, id -> {
            Intent intent = new Intent(activity, ProductActivity.class);
            intent.putExtra("FULL_DESCRIPTION", true);
            intent.putExtra("id", id);
            startActivity(intent);
        });

        try {
            mHolder.banner.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initCategories(ArrayList<CategoriesData> data) {
        AdapterShopCategoty adapter = new AdapterShopCategoty(activity, data, (id, cat) -> mfragmentViewModel.changeFragment(new ShopCategoryFragment(id, cat), "ShopCategoryFragment", 2));

        mHolder.recyclerShopCategory.setAdapter(adapter);
        mHolder.recyclerShopCategory.setLayoutManager(new LinearLayoutManager(activity));
        ViewCompat.setNestedScrollingEnabled(mHolder.recyclerShopCategory, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((StartMenuActivity)getActivity()).changeHeader(1);
    }

    private final Handler handlerOp = new Handler(message -> {
        int opId = -1;
        JSONObject response;
        boolean success = false;

        try{
            opId = message.getData().getInt("opId");
            response = new JSONObject(message.getData().getString("response"));
            success = message.getData().getBoolean("success");
        }
        catch (JSONException e){
            UTILS.DebugLog("Error", "Error getting handlers params.");
            return false;
        }

        if(success) {
            OnAsyncOperationSuccess(opId, response);
        }
        else{
            OnAsyncOperationError(opId, response);
        }
        return false;
    });

    @Override
    public void CallHandler(int opId, JSONObject response, boolean success) {
        Message message = new Message();
        Bundle b = new Bundle();
        b.putInt("opId", opId);
        b.putString("response", response.toString());
        b.putBoolean("success", success);
        message.setData(b);
        handlerOp.sendMessage(message);
    }

    @Override
    public void OnAsyncOperationSuccess(int opId, JSONObject response) {
        switch (opId) {
            case OP_GET_SHOP_HOMEPAGE: {
                if (response.has("data")) {
                    try {
                        ShopHomepageData data = new Gson().fromJson(response.getJSONObject("data").toString(), ShopHomepageData.class);
                        initBanner(data.getHerobanners());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;

            case OP_GET_SHOP_CATEGORIES: {
                if (response.has("data")) {
                    try {
                        CategoriesData[] data = new Gson().fromJson(response.getJSONObject("data").getJSONArray("data").toString(), CategoriesData[].class);
                        initCategories(new ArrayList<>(Arrays.asList(data)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {

    }*/

    class WebViewClient extends android.webkit.WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //mHolder.load.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //mHolder.load.setVisibility(View.GONE);
        }

    }

    class ChromeClient extends WebChromeClient {
        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        ChromeClient() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)activity.getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            activity.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            activity.setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback)
        {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = activity.getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)activity.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            activity.getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    static class ViewHolder {

        /*View item1PromocaoCardview;
        RecyclerView recyclerShopCategory;
        ViewPager banner;*/
        WebView webView;
        ProgressBar load;

        public ViewHolder(View view) {
            webView = view.findViewById(R.id.web_view);
            load = view.findViewById(R.id.load);
            /*item1PromocaoCardview = view.findViewById(R.id.card_news_promotion);
            recyclerShopCategory = view.findViewById(R.id.recycler_shop_category);
            banner = view.findViewById(R.id.pager_banner_imgs);*/
        }
    }
}