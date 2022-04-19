package com.frf.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.ViewPager;

import com.frf.app.R;
import com.frf.app.adapter.ViewPagerAdapter;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.CONSTANTS;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

public class ShopFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {

    private ViewHolder mHolder;
    private int frag = -1;
    private String link = "";

    public ShopFragment() {
    }

    public ShopFragment(int frag) {
        this.frag = frag;
    }

    public ShopFragment(int frag, String link) {
        this.frag = frag;
        this.link = link;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        mHolder = new ViewHolder(view);
        activity.setTheme(R.style.AppTheme_TransparentTheme);

        return view;
    }

    @Override
    public void CallHandler(int opId, JSONObject response, boolean success) {

    }

    @Override
    public void OnAsyncOperationSuccess(int opId, JSONObject response) {

    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {

    }

    @Override
    public void bind() {
        super.bind();

        setupViewPager(mHolder.viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this.getChildFragmentManager());
        viewPager.setOffscreenPageLimit(2);

        if (CONSTANTS.link_ticketing != null && !CONSTANTS.link_ticketing.equals("")) {
            adapter.addFragment(new TicketFragment(), "Bilete");
        }else if (!link.equals("")) {
            adapter.addFragment(new TicketFragment(), "Bilete");
        }
        if (CONSTANTS.link_ecommerce != null && !CONSTANTS.link_ecommerce.equals("")) {
            adapter.addFragment(new MagazinFragment(), "Articole");
        }

        viewPager.setAdapter(adapter);

        if (frag > -1) {
            viewPager.setCurrentItem(frag);
            frag = -1;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHolder.tabLayout.removeAllTabs();
//        mHolder.viewPager.cle
    }

    static class ViewHolder {
        private TabLayout tabLayout;
        private ViewPager viewPager;

        public ViewHolder(View view){
            viewPager =  view.findViewById(R.id.viewpager);
            tabLayout = view.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
        }
    }
}