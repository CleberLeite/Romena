package com.frf.app.utils;

import androidx.fragment.app.Fragment;

import com.frf.app.data.FragmentData;

import java.util.ArrayList;
import java.util.List;

public class FragmentHolder {
    private List<FragmentData> list;
    private int position = 0;

    public FragmentHolder(List<FragmentData> list) {
        this.list = list;
    }

    public FragmentHolder(FragmentData fragment) {
        List<FragmentData> array = new ArrayList<>();
        array.add(fragment);
        this.list = array;
    }

    public List<FragmentData> getList() {
        return list;
    }

    public void setList(List<FragmentData> list) {
        this.list = list;
    }

    public Fragment getFragment() {
        return this.list.get(this.position).getFragment();
    }

    public FragmentData getFragmentData() {
        return this.list.get(this.position);
    }

    public void addFragment(FragmentData fragment) {
        this.list.add(fragment);
        this.position++;
    }

    public void removeFragment(FragmentData fragment) {
        this.list.remove(fragment);
        this.position--;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
