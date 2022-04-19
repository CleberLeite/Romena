package com.frf.app.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.frf.app.R;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.vmodels.fragments.FragmentHolderViewModel;

import org.json.JSONObject;

public class RequestsFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {

    private ViewHolder mHolder;
    private FragmentHolderViewModel mfragmentViewModel;

    public RequestsFragment(Activity activity) {
        this.activity = activity;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        mHolder = new ViewHolder(view);

        try {
            ((StartMenuActivity)requireActivity()).resetBottomMenu();
        }catch (Exception e){
            e.printStackTrace();
        }

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
        mfragmentViewModel = new ViewModelProvider(requireActivity()).get(FragmentHolderViewModel.class);
        /*mHolder.comandaProcesare.setOnClickListener(view -> {
            mfragmentViewModel.changeFragment(new GeneralTicketFragment(1), "GeneralTicketFragment", 3);
        });*/
    }

    static class ViewHolder {
        RecyclerView list;
        public ViewHolder(View view){
            list = view.findViewById(R.id.list);
        }
    }
}