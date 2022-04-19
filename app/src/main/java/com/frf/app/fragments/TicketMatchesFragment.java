package com.frf.app.fragments;

import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.frf.app.R;
import com.frf.app.adapter.TicketsMatchesAdapter;
import com.frf.app.data.TicketsMatchesData;
import com.frf.app.repository.tickets.TicketsRepository;
import com.frf.app.vmodels.fragments.FragmentHolderViewModel;
import com.frf.app.vmodels.tickets.TicketsViewModel;
import java.util.ArrayList;
import java.util.Arrays;

public class TicketMatchesFragment extends MainFragment {

    private int id;
    private String txtTitle = "";
    private TicketsViewModel ticketsViewModel;
    private FragmentHolderViewModel mfragmentViewModel;

    TextView title;
    RecyclerView list;

    public TicketMatchesFragment() {

    }

    public TicketMatchesFragment(int id, String txtTitle){
        this.id = id;
        this.txtTitle = txtTitle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matches_ticket, container, false);

        initViews(view);

        return view;
    }

    @Override
    public void bind() {
        super.bind();

        mfragmentViewModel = new ViewModelProvider(requireActivity()).get(FragmentHolderViewModel.class);
        title.setText(txtTitle);

        ticketsViewModel = new TicketsViewModel();
        TicketsRepository repository = new TicketsRepository(activity);
        ticketsViewModel.setmRepository(repository);
        ticketsViewModel.initMatches().observe(this, ticketsMatchesData -> initMatches(new ArrayList<>(Arrays.asList(ticketsMatchesData))));
        ticketsViewModel.getMatches(id);
    }

    @Override
    public void unbind() {
        super.unbind();
        try{ticketsViewModel.initMatches().removeObservers(this);}catch (Exception e){e.printStackTrace();}
    }

    private void initMatches(ArrayList<TicketsMatchesData> data) {
        TicketsMatchesAdapter adapter = new TicketsMatchesAdapter(activity, data, (eventId, title, date, locale) -> mfragmentViewModel.changeFragment(new SectorsFragment(eventId, title, date, locale), "Sectors", 6));

        list.setLayoutManager(new LinearLayoutManager(activity));
        list.setAdapter(adapter);
    }

    private void initViews(View view) {
        title = view.findViewById(R.id.title);
        list = view.findViewById(R.id.list_matches);
    }
}