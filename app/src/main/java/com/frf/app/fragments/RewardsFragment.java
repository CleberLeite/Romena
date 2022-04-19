package com.frf.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.frf.app.R;
import com.frf.app.activitys.StartMenuActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class RewardsFragment extends MainFragment {

    private ViewHolder mHolder;

    private int v = -1;
    private boolean push = false;

    public RewardsFragment() {
    }

    public RewardsFragment( boolean push, int v) {
        this.v = v;
        this.push = push;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);
        mHolder = new ViewHolder(view);

        setupViewPager(mHolder.viewPager);

        try {
            ((StartMenuActivity)requireActivity()).resetBottomMenu();
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new TrophiesFragment(), "Trofee");

        if (push) {
            if (v != -1) {
                adapter.addFragment(new ResaleFragment(v), "Revendică");
            }else {
                adapter.addFragment(new ResaleFragment(), "Revendică");
            }
        }else {
            adapter.addFragment(new ResaleFragment(), "Revendică");
        }

        viewPager.setAdapter(adapter);

        if (push) {
            mHolder.tabLayout.getTabAt(1).select();
        }
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void bind() {
        super.bind();
        ((StartMenuActivity)getActivity()).setBadgesAndAwardsIsOpen(true);
    }

    @Override
    public void unbind() {
        super.unbind();
        ((StartMenuActivity)getActivity()).setBadgesAndAwardsIsOpen(false);
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