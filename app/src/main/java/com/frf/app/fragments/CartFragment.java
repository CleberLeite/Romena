package com.frf.app.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.frf.app.R;
import com.frf.app.utils.AsyncOperation;

import org.json.JSONObject;

public class CartFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {

    ViewHolder mHolder;

    public CartFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_cart, container, false);
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

        mHolder.edtCupom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() >= 3){
                    mHolder.cardCupom.setVisibility(View.VISIBLE);
                    mHolder.imgCupom.setVisibility(View.VISIBLE);
                    mHolder.txtPriceTotal.setTextColor(ContextCompat.getColor(activity, R.color.ColorGreen500));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    class ViewHolder {
        CardView cardCupom;
        ImageView imgCupom;
        EditText edtCupom;
        TextView txtPriceTotal;
        public ViewHolder(View view) {
            cardCupom = view.findViewById(R.id.cardCupom);
            imgCupom = view.findViewById(R.id.imgCupom);
            edtCupom = view.findViewById(R.id.edtCupom);
            txtPriceTotal = view.findViewById(R.id.txtPriceTotal);
        }
    }
}