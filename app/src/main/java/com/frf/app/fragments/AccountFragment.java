package com.frf.app.fragments;

import static com.frf.app.activitys.MainActivity.emptyAsync;
import static com.frf.app.activitys.MainActivity.prefs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.frf.app.R;
import com.frf.app.activitys.TermsAndPrivacyPolicyActivity;
import com.frf.app.models.user.UserModel;
import com.frf.app.repository.user.UserRepository;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.vmodels.user.UserViewModel;

import org.json.JSONObject;

public class AccountFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {

    private ViewHolder mHolder;
    private UserViewModel userViewModel;

    private static final String termsOfUse = "https://api.app-admin.frf.ro/terms/service";
    private static final String termsOfPrivacy = "https://api.app-admin.frf.ro/terms/privacy";

    public AccountFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        mHolder = new ViewHolder(view);
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
        UserRepository userRepository = new UserRepository(activity, prefs);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setmRepository(userRepository);
        userViewModel.getUserData().observe(this, this::initDelete);
        userViewModel.getUser(false);

        mHolder.modifyData.setOnClickListener(view -> {
            /*new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_SETTINGS_ACCOUNT_OPEN_CHANGE_PASSWORD).execute();
            Intent intent = new Intent(activity, ChangePasswordlActivity.class);
            startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
            Intent intent = new Intent (Intent.ACTION_VIEW);
            intent.setData (Uri.parse("https://id.frf.ro/Profile/Edit/1?ReturnUrl=frf://opentheapp"));
            startActivity(intent);
        });

        /*mHolder.cardChangeEmail.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_SETTINGS_ACCOUNT_OPEN_CHANGE_EMAIL).execute();
            Intent intent = new Intent(activity, ChangeEmailActivity.class);
            startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });*/

        mHolder.cardTerms.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_SETTINGS_ACCOUNT_OPEN_TERMS).execute();
            Intent intent = new Intent(activity, TermsAndPrivacyPolicyActivity.class);
            intent.putExtra("link", termsOfUse);
            intent.putExtra("title", getResources().getString(R.string.termeni_amp_condi_ii));
            startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        mHolder.cardPolitics.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_SETTINGS_ACCOUNT_OPEN_PRIVACY).execute();
            Intent intent = new Intent(activity, TermsAndPrivacyPolicyActivity.class);
            intent.putExtra("link", termsOfPrivacy);
            intent.putExtra("title", getResources().getString(R.string.confiden_ialitate));
            startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });


    }

    void initDelete(UserModel user) {
        mHolder.viewDeleteAccount.setOnClickListener(v -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_SETTINGS_ACCOUNT_DELETE_ACCOUNT).execute();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(getResources().getString(R.string.delete_account));
            builder.setPositiveButton(getResources().getString(R.string.sim), (dialog, which) -> {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_SETTINGS_ACCOUNT_DELETE_ACCOUNT_CONFIRM).execute();
                userViewModel.deleteAccount(user.getId(), getContext());
            });
            builder.setNegativeButton(getResources().getString(R.string.nao), (dialog, which) -> {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_SETTINGS_ACCOUNT_DELETE_ACCOUNT_CANCEL).execute();
                try{dialog.dismiss();}catch (Exception e){e.printStackTrace();}
            });

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(arg0 -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.PrimaryColorText));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorRed600));
            });
            dialog.show();
        });
    }



    static class ViewHolder {
        View modifyData;
        View cardPolitics;
        View cardChangeEmail;
        View cardTerms;
        View viewDeleteAccount;

        public ViewHolder(View view){
            modifyData = view.findViewById(R.id.card_change_password);
            cardChangeEmail = view.findViewById(R.id.card_change_email);
            cardTerms = view.findViewById(R.id.card_terms);
            cardPolitics = view.findViewById(R.id.card_politics);
            viewDeleteAccount = view.findViewById(R.id.view_delete_accaunt);
        }
    }
}