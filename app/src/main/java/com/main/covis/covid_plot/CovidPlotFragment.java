package com.main.covis.covid_plot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.main.covis.R;

public class CovidPlotFragment extends Fragment implements CovidPlotContract.View {

    private CovidPlotPresenter presenter;
    TextView homeText;
    BottomNavigationView bottomNavigationView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeText = getActivity().findViewById(R.id.homeText);
        Bundle bundle=getArguments();
        if(bundle != null) {
            bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.details);
            System.out.println(bundle.getString("country"));
        }
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void showMessage(String message) {
        System.out.println("Fragment");
    }
}
