package com.frf.app.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {

    protected Activity activity;

    private boolean mUserSeen = false;
    private boolean mViewCreated = false;
    boolean valuesInitialized = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!mUserSeen && isVisibleToUser) {
            mUserSeen = true;
            onUserFirstSight();
            tryViewCreatedFirstSight();
        }
        onUserVisibleChanged(isVisibleToUser);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() != null){
            activity = getActivity();
        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewCreated = true;
        tryViewCreatedFirstSight();
    }

    @Override
    public void onResume() {
        super.onResume();
        bind();
    }

    @Override
    public void onStop() {
        super.onStop();
        unbind();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewCreated = false;
        mUserSeen = false;
    }

    private void tryViewCreatedFirstSight() {
        if (mUserSeen && mViewCreated) {
            onViewCreatedFirstSight(getView());
        }
    }

    public void refreshQuiz(){

    }

    public void onReload (){

    };

    public void onBackPressed() {

    }

    public void bind (){
    }

    public void unbind (){

    }

    //vendo com a view já criada
    protected void onViewCreatedFirstSight(View view) {
        // handling here
    }

    //usuario viu, mas não se criou a view exatamente
    protected void onUserFirstSight() {
    }

    //não esta vendo maisMain
    protected void onUserVisibleChanged(boolean visible) {
    }

}
