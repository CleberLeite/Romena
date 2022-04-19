package com.frf.app.vmodels.fragments;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.frf.app.data.FragmentData;
import com.frf.app.vmodels.MainViewModel;


public class FragmentHolderViewModel extends MainViewModel {

    MutableLiveData<FragmentData> mFragment;
    MutableLiveData<Boolean> mChange;
    MutableLiveData<Integer> mChangeHeader;

    public LiveData<FragmentData> getFragment (){
        if(mFragment == null){
            mFragment = new MutableLiveData<>();
        }
        return mFragment;
    }

    public LiveData<Boolean> canChange(){
        if(mChange == null){
            mChange = new MutableLiveData<>();
        }
        return mChange;
    }

    public LiveData<Integer> canChangeHeader(){
        if(mChangeHeader == null){
            mChangeHeader = new MutableLiveData<>();
        }
        return mChangeHeader;
    }

    public void setCanChangeHeader(int change){
        if(mChangeHeader == null){
            mChangeHeader = new MutableLiveData<>();
        }
        mChangeHeader.postValue(change);
    }

    public void setHasToChange(boolean change){
        mChange.postValue(change);
    }

    public void changeFragment (Fragment fragment, String tag, int type){
        mChange.postValue(true);
        mChangeHeader.postValue(type);
        mFragment.postValue(new FragmentData(fragment, tag));
    }
}
