package com.frf.app.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import java.util.zip.Inflater;


public class BottomSheetMoreFragment extends BottomSheetDialogFragment implements AsyncOperation.IAsyncOpCallback {

    private LinearLayout report;
    private LinearLayout reportComent;
    private LinearLayout block;
    private static final int OP_GET_REPORTS = 0;
    private static final int OP_SET_BLOCK_USER = 1;

    public static int ARENA_POSTS = 1;
    public static int ARENA_COMMENTS = 2;
    public static int NEWS = 3;

    private int idFeature;
    private int idPost;
    private String idUserBlock;
    private Listener listener;

    public BottomSheetMoreFragment() {
    }

    public BottomSheetMoreFragment(int idFeature, int idPost, String idUserBlock, Listener listener) {
        this.idFeature = idFeature;
        this.idPost = idPost;
        this.idUserBlock = idUserBlock;
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bottom_sheet_more, container, false);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

        report = view.findViewById(R.id.report_publication);
        reportComent = view.findViewById(R.id.report_coment);
        block = view.findViewById(R.id.block_user);

        if (idFeature != ARENA_POSTS) {
            report.setVisibility(View.GONE);
            reportComent.setVisibility(View.VISIBLE);
        }

        new AsyncOperation(getActivity(), AsyncOperation.TASK_ID_GET_REPORTS, OP_GET_REPORTS, this).execute();

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

    private void showAlertConfirmBlock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        builder.setCancelable(false);

        TextView title = new TextView(getContext());
        title.setText(R.string.confirm_block_user);
        title.setTextSize(20);
        title.setPadding(50,50,50,50);
        title.setTextColor(getContext().getResources().getColor(R.color.ColorBlueGray500));
        builder.setCustomTitle(title);

        builder.setPositiveButton(getContext().getResources().getString(R.string.yes), (dialogInterface, i) -> {
            Hashtable<String, Object> params = new Hashtable<>();
            params.put("idUserBlock", idUserBlock);
            new AsyncOperation(getActivity(), AsyncOperation.TASK_ID_BLOCK_USER, OP_SET_BLOCK_USER, BottomSheetMoreFragment.this).execute(params);
            dialogInterface.dismiss();
        });
        builder.setNegativeButton(getString(R.string.no), (dialogInterface, i) -> dialogInterface.dismiss());
        builder.create();

        try{builder.show();}catch (Exception e){e.printStackTrace();}
    }

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
        switch (opId) {
            case OP_GET_REPORTS: {

                if (response.has("Object")) {
                    report.setOnClickListener(view -> {
                        BottomSheetReportsFragment fragment = new BottomSheetReportsFragment(response.toString(), idPost, idFeature);
                        fragment.show(getChildFragmentManager(), fragment.getTag());
                    });
                    reportComent.setOnClickListener(view -> {
                        BottomSheetReportsFragment fragment = new BottomSheetReportsFragment(response.toString(), idPost, idFeature);
                        fragment.show(getChildFragmentManager(), fragment.getTag());
                    });
                    block.setOnClickListener(view -> showAlertConfirmBlock());
                }else {
                    try {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.could_not_load_list), Toast.LENGTH_SHORT).show();
                        dismiss();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.could_not_load_list), Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }
            }
            break;

            case OP_SET_BLOCK_USER: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
                builder.setCancelable(false);

                TextView title = new TextView(getContext());
                title.setText(R.string.success_block_user);
                title.setTextSize(20);
                title.setPadding(50,50,50,50);
                title.setTextColor(getContext().getResources().getColor(R.color.ColorBlueGray500));
                builder.setCustomTitle(title);

                builder.setPositiveButton(getContext().getResources().getString(R.string.ok), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    listener.onCallBack();
                });
                builder.create();
                try{builder.show();}catch (Exception e){e.printStackTrace();}
            }
            break;
        }
    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {
        switch (opId) {
            case OP_GET_REPORTS: {
                try {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.could_not_load_list), Toast.LENGTH_SHORT).show();
                    dismiss();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.could_not_load_list), Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
            break;

            case OP_SET_BLOCK_USER: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
                builder.setCancelable(false);

                TextView title = new TextView(getContext());
                title.setText(R.string.error_block_user);
                title.setTextSize(20);
                title.setPadding(50,50,50,50);
                title.setTextColor(getContext().getResources().getColor(R.color.ColorBlueGray500));
                builder.setCustomTitle(title);

                builder.setPositiveButton(getContext().getResources().getString(R.string.ok), (dialogInterface, i) -> dialogInterface.dismiss());
                builder.create();
                try{builder.show();}catch (Exception e){e.printStackTrace();}
            }
            break;
        }
    }

    public interface Listener {
        void onCallBack();
    }
}