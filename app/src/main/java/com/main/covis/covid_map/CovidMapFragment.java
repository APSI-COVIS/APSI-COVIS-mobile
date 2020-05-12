package com.main.covis.covid_map;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.main.covis.R;

public class CovidMapFragment extends Fragment implements CovidMapContract.View {

    private CovidMapPresenter presenter;
    TextView homeText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeText = getActivity().findViewById(R.id.homeTextView);
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void showMessage(String message) {
        System.out.println("Fragment");
    }


}
