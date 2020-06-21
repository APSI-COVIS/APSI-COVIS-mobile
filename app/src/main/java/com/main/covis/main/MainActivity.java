package com.main.covis.main;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.main.covis.R;
import com.main.covis.covid_map.CovidMapFragment;
import com.main.covis.covid_plot.CovidPlotFragment;

public class MainActivity extends AppCompatActivity implements MainContract.View, BottomNavigationView.OnNavigationItemSelectedListener {
    TextView messageTV;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home_map);


    }
    CovidMapFragment covidMapFragment = new CovidMapFragment();
    CovidPlotFragment covidPlotFragment = new CovidPlotFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, covidMapFragment).commit();
                return true;

            case R.id.details:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, covidPlotFragment).commit();
                return true;
        }
        return false;
    }
    @Override
    public void showMessage(String message){
        messageTV.setText(message);
    }


}
