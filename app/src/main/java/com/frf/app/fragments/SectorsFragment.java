package com.frf.app.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.frf.app.R;
import com.frf.app.adapter.TicketsOffersAdapter;
import com.frf.app.data.TicketsOffersData;
import com.frf.app.repository.tickets.TicketsRepository;
import com.frf.app.views.TouchyWebView;
import com.frf.app.vmodels.tickets.TicketsViewModel;

public class SectorsFragment extends MainFragment {


    private int eventId;
    private String txtTitle, txtData, txtLocal;
    private int zoom = 100;

    private TicketsViewModel ticketsViewModel;

    TextView title, calendary, locale, titleList;
    LinearLayout info, contentList;
    CardView zoomIn, zoomOut;
    TouchyWebView sectors;
    RecyclerView list;
    LottieAnimationView load;


    public SectorsFragment() {
    }

    public SectorsFragment(int eventId, String txtTitle, String txtData, String txtLocal) {
        this.eventId = eventId;
        this.txtTitle = txtTitle;
        this.txtData = txtData;
        this.txtLocal = txtLocal;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sectors, container, false);

        initViews(view);
        initValues();
        initActions();

        return view;
    }

    @Override
    public void bind() {
        super.bind();
        ticketsViewModel = new TicketsViewModel();
        TicketsRepository repository = new TicketsRepository(activity);
        ticketsViewModel.setmRepository(repository);
        ticketsViewModel.initOffers().observe(this, this::initOffers);
        ticketsViewModel.initSectorsSvg().observe(this, this::initSectorsSvg);
        ticketsViewModel.getSectorsSvg(eventId);
    }

    @Override
    public void unbind() {
        super.unbind();
        try{ticketsViewModel.initOffers().removeObservers(this);}catch (Exception e){e.printStackTrace();}
    }

    private void initViews(View view) {
        title = view.findViewById(R.id.title);
        calendary = view.findViewById(R.id.calandary);
        locale = view.findViewById(R.id.locale);
        titleList = view.findViewById(R.id.title_list);

        info = view.findViewById(R.id.info);
        contentList = view.findViewById(R.id.content_list);

        zoomIn = view.findViewById(R.id.zoom_in);
        zoomOut = view.findViewById(R.id.zoom_out);

        sectors = view.findViewById(R.id.sectors);

        list = view.findViewById(R.id.list_category);

        load = view.findViewById(R.id.load_list);
    }

    private void initValues() {
        title.setText(txtTitle);
        calendary.setText(txtData);
        locale.setText(txtLocal);
    }

    private void initActions() {
        zoomIn.setOnClickListener(view -> {
            zoom += 10;
            sectors.setInitialScale(zoom);
        });

        zoomOut.setOnClickListener(view -> {
            if (zoom > 100) {
                zoom -= 10;
                sectors.setInitialScale(zoom);
            }
        });
    }

    private void initSectorsSvg(String web) {
        ticketsViewModel.getOffers(eventId);

        WebSettings webSettings = sectors.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setUseWideViewPort(true);

        sectors.setInitialScale(1);
        sectors.getSettings().setLoadWithOverviewMode(true);
        sectors.getSettings().setUseWideViewPort(true);
        sectors.setWebViewClient(new WebViewClient());
        sectors.setWebChromeClient(new ChromeClient());

        sectors.loadDataWithBaseURL(null, web, "text/html", "UTF-8", null);
    }

    private void initOffers(TicketsOffersData offers) {
        load.setVisibility(View.GONE);
        contentList.setVisibility(View.VISIBLE);
        TicketsOffersAdapter adapter = new TicketsOffersAdapter(activity, offers, new TicketsOffersAdapter.Listener() {
            @Override
            public void onClick(TicketsOffersData.Offers data, boolean isChecked) {

            }

            @Override
            public void onSelectSector(TicketsOffersData.Offers data) {

            }
        });

        list.setLayoutManager(new LinearLayoutManager(activity));
        list.setAdapter(adapter);
    }

    class WebViewClient extends android.webkit.WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            Toast.makeText(activity, (request.getUrl()+"").replaceAll("android-app://", ""), Toast.LENGTH_SHORT).show();

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }

    }

    class ChromeClient extends WebChromeClient {

    }
}