package com.frf.app.fragments;

import static com.frf.app.activitys.MainActivity.emptyAsync;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ShareCompat;

import com.frf.app.R;
import com.frf.app.activitys.TermsAndPrivacyPolicyActivity;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;

import java.io.ByteArrayOutputStream;

public class HelpFragment extends MainFragment {

    private ViewHolder mHolder;

    public HelpFragment(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        mHolder = new ViewHolder(view);
        initActions();
        return view;
    }

    void initActions() {
        mHolder.questions.setOnClickListener(v -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_HELP_OPEN_FREQUENT_QUESTIONS).execute();
            Intent intent = new Intent(activity, TermsAndPrivacyPolicyActivity.class);
            intent.putExtra("link", getString(R.string.link_faq));
            intent.putExtra("title", "Întrebări Frecvente");
            startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        mHolder.reportProblem.setOnClickListener(v -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_HELP_OPEN_REPORT_PROBLEM).execute();
            sendEmail(getResources().getString(R.string.raporteaz_o_problem));
        });
        mHolder.contact.setOnClickListener(v -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_HELP_OPEN_CONTACT).execute();
            sendEmail(getResources().getString(R.string.contact));
        });
    }

    private void sendEmail(String subject) {
        Intent email = new Intent(Intent.ACTION_SEND);

        email.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_support)});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TITLE, getString(R.string.send_email));

        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, null));
    }

    static class ViewHolder {
        View questions, reportProblem, contact;
        public ViewHolder(View view) {
            questions = view.findViewById(R.id.questions);
            reportProblem = view.findViewById(R.id.report_problem);
            contact = view.findViewById(R.id.contact);
        }
    }
}