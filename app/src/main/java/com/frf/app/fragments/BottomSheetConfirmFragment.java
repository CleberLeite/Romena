package com.frf.app.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.frf.app.R;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.UTILS;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Objects;


public class BottomSheetConfirmFragment extends BottomSheetDialogFragment implements AsyncOperation.IAsyncOpCallback {

    private int idPost;
    private int idFeature;
    private int idReportType;
    private int inputText;
    private String txtTitle;

    TextView title;
    EditText edit;
    Button send;

    public BottomSheetConfirmFragment(int idPost, int idFeature, int idReportType, String txtTitle, int inputText) {
        this.idPost = idPost;
        this.idFeature = idFeature;
        this.idReportType = idReportType;
        this.txtTitle = txtTitle;
        this.inputText = inputText;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_confirm, container, false);

        title = view.findViewById(R.id.title);
        edit = view.findViewById(R.id.edit);
        send = view.findViewById(R.id.send);

        title.setText(getContext().getResources().getString(R.string.report) + " - " + txtTitle);

        if (inputText == 1) {
            edit.setVisibility(View.VISIBLE);
        }

        send.setOnClickListener(view1 -> {
            if (inputText == 1) {
                if (edit.getText().toString().isEmpty()) {
                    edit.setError(getString(R.string.insert_your_report));
                }else {
                    Hashtable<String, Object> params = new Hashtable<>();
                    params.put("idPost", idPost);
                    params.put("idFeature", idFeature);
                    params.put("idReportType", idReportType);
                    params.put("note", edit.getText().toString());
                    new AsyncOperation(getActivity(), AsyncOperation.TASK_ID_SET_REPORT, 999, BottomSheetConfirmFragment.this).execute(params);
                }
            }else {
                Hashtable<String, Object> params = new Hashtable<>();
                params.put("idPost", idPost);
                params.put("idFeature", idFeature);
                params.put("idReportType", idReportType);
                new AsyncOperation(getActivity(), AsyncOperation.TASK_ID_SET_REPORT, 999, BottomSheetConfirmFragment.this).execute(params);
            }
        });

        return view;
    }

    private final Handler handlerOp = new Handler(message -> {
        int opId = -1;
        JSONObject response;
        boolean success = false;

        try{
            opId = message.getData().getInt("opId");
            response = new JSONObject(Objects.requireNonNull(message.getData().getString("response")));
            success = message.getData().getBoolean("success");
        }
        catch (JSONException e){
            UTILS.DebugLog("Error", "Error getting handlers params.");
            return false;
        }

        if(success) {
            OnAsyncOperationSuccess(opId, response);
        }
        else{
            OnAsyncOperationError(opId, response);
        }
        return false;
    });

    @Override
    public void CallHandler(int opId, JSONObject response, boolean success) {
        Message message = new Message();
        Bundle b = new Bundle();
        b.putInt("opId", opId);
        b.putString("response", response.toString());
        b.putBoolean("success", success);
        message.setData(b);
        handlerOp.sendMessage(message);
    }

    @Override
    public void OnAsyncOperationSuccess(int opId, JSONObject response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        builder.setCancelable(false);

        TextView title = new TextView(getContext());
        title.setText(R.string.received_you_report);
        title.setTextSize(20);
        title.setPadding(50,50,50,50);
        title.setTextColor(getContext().getResources().getColor(R.color.ColorBlueGray500));
        builder.setCustomTitle(title);

        builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
            try {
                dialogInterface.dismiss();
                dismiss();
                getChildFragmentManager().getFragments().clear();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        builder.create();
        try{builder.show();}catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
            builder.setCancelable(false);

            TextView title = new TextView(getContext());
            title.setText(R.string.an_error_has_occurred);
            title.setTextSize(20);
            title.setPadding(50,50,50,50);
            title.setTextColor(getContext().getResources().getColor(R.color.ColorBlueGray500));
            builder.setCustomTitle(title);

            builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                try {
                    dialogInterface.dismiss();
                    dismiss();
                    getChildFragmentManager().getFragments().clear();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });

            builder.create();
            try{builder.show();}catch (Exception e){e.printStackTrace();}
            dismiss();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}