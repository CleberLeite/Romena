package com.frf.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.frf.app.R;
import com.frf.app.vmodels.fragments.FragmentHolderViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class EventsMatchesFragment extends MainFragment {

    private ViewHolder mHolder;
    CollectionAdapter collectionAdapter;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> titleTab = new ArrayList<>();
    private FragmentHolderViewModel fragmentHolderViewModel;
    int id;
    String title;

    public EventsMatchesFragment(int id, FragmentHolderViewModel fragmentHolderViewModel, String title) {
        this.id = id;
        this.fragmentHolderViewModel = fragmentHolderViewModel;
        this.title = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_matches, container, false);
        mHolder = new ViewHolder(view);

        setupViewPager();

        collectionAdapter = new CollectionAdapter(this, mFragmentList);
        mHolder.viewPager.setSaveEnabled(false);
        mHolder.viewPager.setAdapter(collectionAdapter);

        new TabLayoutMediator(mHolder.tabLayout, mHolder.viewPager,
                (tab, position) -> tab.setText(titleTab.get(position))
        ).attach();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    }

    private void setupViewPager() {
        mHolder.title.setText(title);
        mFragmentList.clear();
        titleTab.clear();
        mFragmentList.add(new MatchesFragment(id, fragmentHolderViewModel));
        mFragmentList.add(new ClassificationFragment(id));
        titleTab.add("Meciuri");
        titleTab.add("Clasamente");
    }

    public static class CollectionAdapter extends FragmentStateAdapter {
        List<Fragment> fragments;

        public CollectionAdapter(Fragment fragment, List<Fragment> fragments) {
            super(fragment);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }

    static class ViewHolder {
        private TabLayout tabLayout;
        private ViewPager2 viewPager;
        private TextView title;

        public ViewHolder(View view){
            viewPager =  view.findViewById(R.id.viewpager);
            tabLayout = view.findViewById(R.id.tabs);
            title = view.findViewById(R.id.title);
        }
    }
}