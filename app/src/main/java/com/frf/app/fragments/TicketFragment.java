package com.frf.app.fragments;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.frf.app.R;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.adapter.TicketAdapter;
import com.frf.app.data.TicketData;
import com.frf.app.utils.CONSTANTS;
import com.frf.app.vmodels.fragments.FragmentHolderViewModel;

import java.util.ArrayList;


public class TicketFragment extends MainFragment {
    private ViewHolder mHolder;
    private FragmentHolderViewModel mfragmentViewModel;
    private String link = "";

    public TicketFragment() {

    }

    public TicketFragment(String link) {
        this.link = link;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onBackPressed() {
        if(mHolder.webView.canGoBack()){
            mHolder.webView.goBack();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket, container, false);

        mHolder = new ViewHolder(view);
        WebSettings webSettings = mHolder.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setAllowFileAccess(true);

        mHolder.webView.setWebViewClient(new WebViewClient());
        mHolder.webView.setWebChromeClient(new ChromeClient());
        mHolder.webView.loadUrl(link.equals("") ? CONSTANTS.link_ticketing : link);
        /*try {
            ((StartMenuActivity)requireActivity()).resetBottomMenu();
        }catch (Exception e){
            e.printStackTrace();
        }*/

        return view;
    }

/*    @Override
    public void bind() {
        super.bind();
        mfragmentViewModel = new ViewModelProvider(requireActivity()).get(FragmentHolderViewModel.class);

        ((StartMenuActivity)getActivity()).changeHeader(1);
    }*/

/*    void initTicketsUrmeaza(ArrayList<TicketData> data) {
        TicketAdapter adapter = new TicketAdapter(activity, data, new TicketAdapter.Listener() {
            @Override
            public void callSeat(int id) {
                mfragmentViewModel.changeFragment(new SeatTicketFragment(id), "SeatTicketFragment", 3);
            }

            @Override
            public void callGeneral(int id) {
                mfragmentViewModel.changeFragment(new GeneralTicketFragment(id), "GeneralTicketFragment", 3);
            }
        });

        mHolder.urmeazaList.setLayoutManager(new LinearLayoutManager(activity));
        mHolder.urmeazaList.setAdapter(adapter);
    }

    void initTicketsAnterioare(ArrayList<TicketData> data) {
        TicketAdapter adapter = new TicketAdapter(activity, data, new TicketAdapter.Listener() {
            @Override
            public void callSeat(int id) {
                mfragmentViewModel.changeFragment(new SeatTicketFragment(id), "SeatTicketFragment", 3);
            }

            @Override
            public void callGeneral(int id) {
                mfragmentViewModel.changeFragment(new GeneralTicketFragment(id), "GeneralTicketFragment", 3);
            }
        });

        mHolder.anterioareList.setLayoutManager(new LinearLayoutManager(activity));
        mHolder.anterioareList.setAdapter(adapter);
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
        /*LinearLayout contentUrmeaza,contentAnterioare;
        RecyclerView urmeazaList, anterioareList;*/
        WebView webView;
        ProgressBar load;

        public ViewHolder(View view){
            webView = view.findViewById(R.id.web_view);
            load = view.findViewById(R.id.load);
            /*contentUrmeaza = view.findViewById(R.id.content_urmeaza);
            contentAnterioare = view.findViewById(R.id.content_anterioare);
            urmeazaList = view.findViewById(R.id.urmeaza_list);
            anterioareList = view.findViewById(R.id.anterioare_list);*/
        }
    }
}