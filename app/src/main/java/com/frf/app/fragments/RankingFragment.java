package com.frf.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.frf.app.R;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class RankingFragment  extends MainFragment {

    private ViewHolder mHolder;
    private static final int OP_GET_RANK                     = 1;

    public RankingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
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
        ViewPagerAdapter adapter = new ViewPagerAdapter(this.getChildFragmentManager());
        adapter.addFragment(new RankingTopFragment(), "Top");
        adapter.addFragment(new TitleRankingFragment(), "Titluri");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void bind() {
        super.bind();
        ((StartMenuActivity)getActivity()).setRankingIsOpen(true);
    }

    @Override
    public void unbind() {
        super.unbind();
        ((StartMenuActivity)getActivity()).setRankingIsOpen(false);
    }

    static class ViewHolder {
        ImageView imgUser;
        TextView txtNomeUser;
        TextView typeFan;
        ViewPager viewPager;
        private TabLayout tabLayout;

        public ViewHolder(View view){
            imgUser = view.findViewById(R.id.imgUser);
            typeFan = view.findViewById(R.id.typeFan);
            txtNomeUser = view.findViewById(R.id.txt_nome_user);
            viewPager =  view.findViewById(R.id.viewpager_ranking);
            tabLayout = view.findViewById(R.id.tabs_ranking);
            tabLayout.setupWithViewPager(viewPager);
        }
    }
}