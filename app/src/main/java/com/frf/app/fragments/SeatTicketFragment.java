package com.frf.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.frf.app.R;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.UTILS;

import org.json.JSONObject;

public class SeatTicketFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {
    private ViewHolder mHolder;
    private int id;

    public SeatTicketFragment(int id) {
        this.id = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seat_ticket, container, false);
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
        UTILS.getImageAt(activity, "data:image/png;base64,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", mHolder.imgQr);
    }

    static class ViewHolder {
        ImageView imgQr;
        public ViewHolder(View view){
            imgQr = view.findViewById(R.id.img_qr_code);
        }
    }
}