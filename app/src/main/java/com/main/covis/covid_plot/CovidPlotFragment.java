package com.main.covis.covid_plot;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.JsonObject;
import com.main.covis.R;
import com.main.covis.network.ApiClient;
import com.main.covis.network.ApiService;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Retrofit;

public class CovidPlotFragment extends Fragment implements CovidPlotContract.View, AdapterView.OnItemSelectedListener {

    private CovidPlotPresenter presenter;
    TextView homeText;
    private LineChart mpLineChart;

    public enum CovidCasesType {
        ACTIVE("Active Cases"),
        DEATH("Deceases"),
        NEW("New Cases"),
        RECOVERED("Recovered");
        private final String label;
        CovidCasesType(String label){
            this.label=label;
        }
        String label(){return label;}
        static ArrayList<String> labels(){
            ArrayList<String> labels = new ArrayList<>();
            for (CovidCasesType cct : CovidCasesType.ACTIVE.getDeclaringClass().getEnumConstants()) {
                labels.add(cct.label);
            }
            return labels;
        }
    }

    public enum DateRange {
        WHOLE_PERIOD (90, "Whole Period"),
        MONTH (30, "Week"),
        WEEK (7, "Month"),
        FORECAST(-7, "Forecast");
        private final int days;
        private final String label;
        DateRange(int days, String label){
            this.days=days;
            this.label=label;
        }
        int days(){ return days;}
        static ArrayList<String> labels(){
            ArrayList<String> labels = new ArrayList<>();
            for (DateRange dr : DateRange.WHOLE_PERIOD.getDeclaringClass().getEnumConstants()) {
                labels.add(dr.label);
            }
            return labels;
        }
    }
    private CovidCasesType TYPE = CovidCasesType.ACTIVE;
    private DateRange DATERANGE = DateRange.WHOLE_PERIOD;
    private CovidCasesType newTYPE = CovidCasesType.ACTIVE;
    private DateRange newDATERANGE = DateRange.WHOLE_PERIOD;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            super.onViewCreated(rootView, savedInstanceState);



            // Inflate the layout for this fragment
            // homeText = getActivity().findViewById(R.id.homeText);
            Bundle bundle = getArguments();
            if (bundle != null) {
                BottomNavigationView bottomNavigationView = Objects.requireNonNull(getActivity()).findViewById(R.id.bottom_navigation);
                bottomNavigationView.setSelectedItemId(R.id.details);
                System.out.println(bundle.getString("country"));
            }

//            TextView country = (TextView) rootView.findViewById(R.id.country_name);
//            assert bundle != null;
//            country.setText(bundle.getString("country"));
//            TextView population = (TextView) rootView.findViewById(R.id.population);
//            population.setText(getPopulation(bundle.getString("country")));

            Spinner typeSpin = (Spinner) rootView.findViewById(R.id.typeSpinner);
            typeSpin.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

            //Creating the ArrayAdapter instance having the country list
            ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, CovidCasesType.labels());
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            typeSpin.setAdapter(aa);

            Spinner DateSpin = (Spinner) rootView.findViewById(R.id.dateSpinner);
            DateSpin.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

            //Creating the ArrayAdapter instance having the country list
            ArrayAdapter aa2 = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, DateRange.labels());
            aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            DateSpin.setAdapter(aa2);



            mpLineChart = (LineChart) rootView.findViewById(R.id.line_chart);
            LineDataSet activeDataSet = new LineDataSet(getCovidData(CovidCasesType.ACTIVE, DateRange.WHOLE_PERIOD), CovidCasesType.ACTIVE.label);
            activeDataSet.setColor(Color.RED);
            activeDataSet.setDrawFilled(true);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(activeDataSet);

            mpLineChart.setBackgroundColor(Color.TRANSPARENT);
            mpLineChart.setDrawGridBackground(false);
            mpLineChart.getXAxis().setDrawGridLines(false);
            mpLineChart.setDrawBorders(true);
            //mpLineChart.setBorderColor(Color.CYAN);
            Description desc = new Description();
            desc.setText("COVID STATISTICS");
            mpLineChart.setDescription(desc);

            LineData data = new LineData(dataSets);


            mpLineChart.setData(data);

            //data.setValueFormatter(new MyValueFormatter());
            XAxis xAxis = mpLineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new DateAxisValueFormatter());
            xAxis.setGranularityEnabled(true);
            //xAxis.setGranularity(1);
            //xAxis.setLabelCount(8);
            YAxis yAxisLeft = mpLineChart.getAxisLeft();
            yAxisLeft.setAxisMinimum(0);
            YAxis yAxisRight = mpLineChart.getAxisRight();
            yAxisRight.setAxisMinimum(0);


            mpLineChart.notifyDataSetChanged();
            mpLineChart.invalidate();

        }
        return rootView;
    }

    private String getPopulation(String cSlug) {
        System.out.println("TEST: !!!");
        int population = 39423121;
        Retrofit retrofit = ApiClient.getClient();
        ApiService apiService = retrofit.create(ApiService.class);
        //Call<JsonObject> call = apiService.getListEpidemyDataInCountry("POL", "2020-05-15", "2020-05-24", "ACTIVE");
        Call<JsonObject> call = apiService.getCountryPopulation(cSlug, "2020-05-24");
        //Call<JsonObject> call = apiService.getListEpidemyForecastInCountry("POL", "2020-05-15", "2020-05-24", "ACTIVE");
        try {
            JsonObject json = call.execute().body();
            System.out.println("TEST: " + json);
        } catch (IOException e) {
            System.out.println("TEST: !");
            e.printStackTrace();
        }
            System.out.println("TEST: !!!!");
            return String.format("%.2fM", population/ 1000000.0);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),country[position] , Toast.LENGTH_LONG).show();
        if(parent.getId() == R.id.typeSpinner)
        {
            typeSpinnerHandler(position);
        }
        else if(parent.getId() == R.id.dateSpinner)
        {
            dateSpinnerHandler(position);
        }
        if(newDATERANGE == DATERANGE && newTYPE==TYPE){
            return;
        } else{
            DATERANGE = newDATERANGE;
            TYPE=newTYPE;
        }
        LineDataSet dataSet = new LineDataSet((DATERANGE!=DateRange.FORECAST) ? getCovidData(TYPE, DATERANGE) : getForecastCovidData(TYPE), TYPE.label);
        dataSet.setColor(Color.rgb(220,20, 20));
        dataSet.setDrawFilled(true);
        mpLineChart.getData().addDataSet(dataSet);
        mpLineChart.getData().removeDataSet(0);
        mpLineChart.notifyDataSetChanged();
        mpLineChart.invalidate();
    }

    private void typeSpinnerHandler(int position) {

        switch (position){
            case 0:
                newTYPE = CovidCasesType.ACTIVE;
                break;
            case 1:
                newTYPE = CovidCasesType.DEATH;
                break;
            case 2:
                newTYPE = CovidCasesType.NEW;
                break;
            case 3:
                newTYPE = CovidCasesType.RECOVERED;

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void dateSpinnerHandler(int position) {
        switch (position){
            case 0:
                newDATERANGE = DateRange.WHOLE_PERIOD;
                break;
            case 1:
                newDATERANGE = DateRange.WEEK;
                break;
            case 2:
                newDATERANGE = DateRange.MONTH;
                break;
            case 3:
                newDATERANGE = DateRange.FORECAST;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    static class MyValueFormatter extends IndexAxisValueFormatter implements IAxisValueFormatter{

        @Override
        public String getFormattedValue(float value) {
            int intValue = (int) value;
            return String.valueOf(intValue);
        }
    }

    static class DateAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value) {
            String date = Float.toString(value);
            System.out.println(date);
            date = date.substring(1,3) + "/" + date.substring(3,5);
            System.out.println(date);
//            date = new StringBuilder(date).insert(date.length()-2, "/").toString();
            return date;
//            Date date = new Date((long)value);
//            System.out.println(date);
//            //Specify the format you'd like
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.ENGLISH);
//            return sdf.format(date);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<Entry> getCovidData (CovidCasesType type, DateRange range){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("1MMdd");
        for(int i = 0; i < range.days; i++){
            dataVals.add(new Entry(Float.parseFloat(dtf.format(now.plusDays(i-range.days))), new Random().nextInt((i*100 - i*10) + 1) + i*10));
        }
        System.out.println(type + " : " + range.days);
        return dataVals;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<Entry> getForecastCovidData(CovidCasesType type){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("1MMdd");
        for(int i = -7; i < 7; i++){
            dataVals.add(new Entry(Float.parseFloat(dtf.format(now.plusDays(i))), new Random().nextInt(((i+7)*100 - (i+7)*10) + 1) + (i+7)*10));
        }
        System.out.println(type);

        return dataVals;
    }

    @Override
    public void showMessage(String message) {
        System.out.println("Plot Fragment");
    }
}
