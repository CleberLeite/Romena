package com.frf.app.fragments;

import static com.frf.app.activitys.MainActivity.emptyAsync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import com.frf.app.R;
import com.frf.app.activitys.AboutActivity;
import com.frf.app.activitys.SettingsNotificationsActivity;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.models.HeaderModel;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.vmodels.fragments.FragmentHolderViewModel;

import org.json.JSONObject;

public class SettingsFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {

    private ViewHolder mHolder;
    private FragmentHolderViewModel mfragmentViewModel;

    HeaderModel headerModel;
    public SettingsFragment(Activity activity, HeaderModel headerModel) {
        this.activity = activity;
        this.headerModel = headerModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void bind() {
        super.bind();
        mfragmentViewModel = new ViewModelProvider(requireActivity()).get(FragmentHolderViewModel.class);

        mHolder.cardAccount.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_SETTINGS_OPEN_ACCOUNT).execute();
            mfragmentViewModel.changeFragment(new AccountFragment(), "AccountFragment", 3);
        });

        mHolder.cardHelp.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_SETTINGS_OPEN_HELP).execute();
            mfragmentViewModel.changeFragment(new HelpFragment(activity), "HelpFragment", 3);
        });

        mHolder.cardAbout.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_SETTINGS_OPEN_ABOUT).execute();
            Intent intent = new Intent(activity, AboutActivity.class);
            startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        mHolder.cardNotificationsSetting.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_SETTINGS_OPEN_NOTIFICATIONS).execute();
            Intent intent = new Intent(activity, SettingsNotificationsActivity.class);
            startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });


        ((StartMenuActivity)getActivity()).setSettingsIsOpenIsOpen(true);
    }

    @Override
    public void unbind() {
        super.unbind();
        ((StartMenuActivity)getActivity()).setSettingsIsOpenIsOpen(false);
    }

    static class ViewHolder {
        View cardAccount;
        View cardHelp;
        View cardAbout;
        View cardNotificationsSetting;
        public ViewHolder(View view){
            cardAccount = view.findViewById(R.id.card_account);
            cardHelp = view.findViewById(R.id.card_help);
            cardAbout = view.findViewById(R.id.card_about);
            cardNotificationsSetting = view.findViewById(R.id.card_notifications_setting);
        }
    }
}