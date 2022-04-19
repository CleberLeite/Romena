package com.frf.app.fragments;

import static com.frf.app.fragments.BottomSheetMoreFragment.ARENA_POSTS;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.frf.app.R;
import com.frf.app.adapter.ReportsAdapter;
import com.frf.app.data.ReportsData;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class BottomSheetReportsFragment extends BottomSheetDialogFragment {

    private String object = "";
    private int idPost;
    private int idFeature;

    private RecyclerView list;
    private TextView title;

    public BottomSheetReportsFragment(String object, int idPost, int idFeature) {
        this.object = object;
        this.idPost = idPost;
        this.idFeature = idFeature;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_reports, container, false);

        title = view.findViewById(R.id.title);
        list = view.findViewById(R.id.list_reports);

        if (idFeature == ARENA_POSTS) {
            title.setText(getContext().getResources().getString(R.string.report_post));
        }else {
            title.setText(getContext().getResources().getString(R.string.report_coment));
        }

        try {
            JSONObject json = new JSONObject(object);
            ReportsData[] reportsData = new Gson().fromJson(json.getJSONObject("Object").getString("itens"), ReportsData[].class);

            ReportsAdapter adapter = new ReportsAdapter(getContext(), new ArrayList<>(Arrays.asList(reportsData)), data -> {
                BottomSheetConfirmFragment fragment = new BottomSheetConfirmFragment(idPost, idFeature, data.getId(), data.getTitle(), data.getTextObg());
                fragment.show(getChildFragmentManager(), fragment.getTag());
            });

            list.setLayoutManager(new LinearLayoutManager(getContext()));
            list.setAdapter(adapter);

        }catch (JSONException e) {
            e.printStackTrace();
            try {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.could_not_load_list), Toast.LENGTH_SHORT).show();
                dismiss();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        return view;
    }
}