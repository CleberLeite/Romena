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

public class GeneralTicketFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {
    private ViewHolder mHolder;
    private int id;

    public GeneralTicketFragment(int id) {
        this.id = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general_ticket, container, false);
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
        UTILS.getImageAt(activity, "https://imagensemoldes.com.br/wp-content/uploads/2020/09/Sticker-C%C3%B3digo-de-Barras-PNG-1024x563.png", mHolder.imgQr);
    }

    class ViewHolder {
        ImageView imgQr;
        public ViewHolder(View view){
            imgQr = view.findViewById(R.id.img_qr_code);
        }
    }
}