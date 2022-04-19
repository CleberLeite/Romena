package com.frf.app.data;


import androidx.fragment.app.Fragment;

public class FragmentData {

    Fragment fragment;
    String name;

    public FragmentData(Fragment fragment, String name) {
        this.fragment = fragment;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

}
