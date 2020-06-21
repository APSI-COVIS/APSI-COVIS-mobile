package com.main.covis.covid_plot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.main.covis.R;
import com.main.covis.network.ApiClient;
import com.main.covis.network.ApiService;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        FORECAST(14, "Forecast");
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
    private static CovidCasesType TYPE = CovidCasesType.ACTIVE;
    private static DateRange DATERANGE = DateRange.WHOLE_PERIOD;
    private static CovidCasesType newTYPE = CovidCasesType.ACTIVE;
    private static DateRange newDATERANGE = DateRange.WHOLE_PERIOD;

    public static String country = null;
    String population = null;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BottomNavigationView bottomNavigationView = Objects.requireNonNull(getActivity()).findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.details);
        final View rootView = inflater.inflate(R.layout.fragment_details, container, false);
//        final Bundle bundle = getArguments();
//        if(bundle != null && bundle.containsKey("country")) {
//            System.out.println("BUNDLE");
//            //bundleHandler(rootView, bundle, bundle.getString("country"), getPopulation(bundle.getString("country")));
//            super.onViewCreated(rootView, savedInstanceState);
//            super.onCreate(savedInstanceState);
//            country = bundle.getString("country");
//            population = getPopulation(bundle.getString("country"));
//            System.out.println(country + ": " + population);
//            TextView tvCountry = (TextView) rootView.findViewById(R.id.country_name);
//            TextView tvPopulation = (TextView) rootView.findViewById(R.id.population);
//            tvCountry.setText(country);
//            tvPopulation.setText(population);
//            tvCountry.invalidate();
//            tvPopulation.invalidate();
//            rootView.invalidate();
//            super.onViewCreated(rootView, savedInstanceState);
//            super.onCreate(savedInstanceState);
//        }
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            super.onViewCreated(rootView, savedInstanceState);
            super.onCreate(savedInstanceState);


//            final Bundle bundle = getArguments();
//            if(bundle != null && bundle.containsKey("country")) {
//                System.out.println("BUNDLE");
//                //bundleHandler(rootView, bundle, bundle.getString("country"), getPopulation(bundle.getString("country")));
//                super.onViewCreated(rootView, savedInstanceState);
//                super.onCreate(savedInstanceState);
//                country = bundle.getString("country");
//                population = getPopulation(bundle.getString("country"));
//                System.out.println(country + ": " + population);
//                TextView tvCountry = (TextView) rootView.findViewById(R.id.country_name);
//                TextView tvPopulation = (TextView) rootView.findViewById(R.id.population);
//                tvCountry.setText(country);
//                tvPopulation.setText(population);
//                tvCountry.invalidate();
//                tvPopulation.invalidate();
//                rootView.invalidate();
//                super.onViewCreated(rootView, savedInstanceState);
//                super.onCreate(savedInstanceState);
//            }
//                tvCountry.setText(country);
//                tvPopulation.setText(population);
//                System.out.println("BUNDLE");
//                tvCountry.invalidate();
//                tvPopulation.invalidate();
//                rootView.invalidate();

            population = getPopulation(country);
            System.out.println(country + ": " + population);
            TextView tvCountry = (TextView) rootView.findViewById(R.id.country_name);
            TextView tvPopulation = (TextView) rootView.findViewById(R.id.population);
            tvCountry.setText(country);
            tvPopulation.setText(population);
            tvCountry.invalidate();
            tvPopulation.invalidate();
            rootView.invalidate();


            mpLineChartHandler(rootView);
            spinnerHandler(rootView);

            Button clickButton = (Button) rootView.findViewById(R.id.pdfButton);
            clickButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toPdfButton();
                }
            });

        }
        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void mpLineChartHandler(View rootView) {
        mpLineChart = (LineChart) rootView.findViewById(R.id.line_chart);
        LineDataSet activeDataSet = new LineDataSet(getRandomCovidData(DateRange.WHOLE_PERIOD), CovidCasesType.ACTIVE.label);
        activeDataSet.setDrawValues(false);
        activeDataSet.setColor(Color.RED);
        activeDataSet.setDrawFilled(true);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(activeDataSet);

        mpLineChart.setBackgroundColor(Color.rgb(253,253,253));
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
        xAxis.setGranularity(1);
        //xAxis.setLabelCount(8);
        YAxis yAxisLeft = mpLineChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0);
        YAxis yAxisRight = mpLineChart.getAxisRight();
        yAxisRight.setAxisMinimum(0);

        mpLineChart.setTouchEnabled(true);
        IMarker marker = new MyMarkerView(rootView.getContext(), R.layout.custom_marker_view_layout);
        mpLineChart.setMarker(marker);

        mpLineChart.getDescription().setText(country + ": " + population);
        mpLineChart.notifyDataSetChanged();
        mpLineChart.invalidate();
        System.out.println("KAPPA");
    }

    private void spinnerHandler(View rootView) {
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
    }

    private void bundleHandler(View rootView, Bundle bundle, String country, String population){
        if (bundle != null) {
            System.out.println(bundle.getString("country"));
            TextView tvCountry = (TextView) rootView.findViewById(R.id.country_name);
            TextView tvPopulation = (TextView) rootView.findViewById(R.id.population);
            tvCountry.setText(country);
            tvPopulation.setText(population);
            System.out.println("BUNDLE");
            tvCountry.invalidate();
            tvPopulation.invalidate();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void toPdfButton(){

        Toast pdfToast = Toast.makeText(getActivity(),"PDF file created.", Toast.LENGTH_LONG);
        pdfToast.setGravity(Gravity.CENTER, 0, 0);
        pdfToast.show();
        Bitmap bm = mpLineChart.getChartBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80 , stream);
        Document doc = new Document();
        try {
            LocalDate now = LocalDate.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String filePath = "/storage/emulated/0/Download/" + TYPE.label + "-" + DATERANGE.label + "-" + dtf.format(now) + ".pdf";
//            System.out.println("dawaj" + Objects.requireNonNull(getActivity()).getFilesDir().getPath().toString());
            PdfWriter.getInstance(doc, new FileOutputStream( filePath));
            doc.open();
            doc.add(new Chunk(DATERANGE.label));
            Image image = Image.getInstance(stream.toByteArray());
            float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin()
                    - doc.rightMargin() - 0) / image.getWidth()) * 100;
            image.scalePercent(scaler);
            image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            doc.add(image);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        } finally {
            doc.close();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getPopulation(String cSlug) {
        System.out.println("TEST: !!!");
        int population = 3942312;
        LocalDate now = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Retrofit retrofit = ApiClient.getClient();
        ApiService apiService = retrofit.create(ApiService.class);
        //Call<JsonObject> call = apiService.getListEpidemyDataInCountry("POL", "2020-05-15", "2020-05-24", "ACTIVE");
        Call<JsonObject> call = apiService.getCountryPopulation("Poland", dtf.format(now));
        //Call<JsonObject> call = apiService.getListEpidemyForecastInCountry("POL", "2020-05-15", "2020-05-24", "ACTIVE");
        try {
            JsonObject json = call.execute().body();
            System.out.println("TEST: " + json);
            if(json != null){
                population = Integer.parseInt(String.valueOf(json.get("population")));
            }else{
                population = 0;
            }

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
        System.out.println("KAPPA0");
        if(parent.getId() == R.id.typeSpinner)
        {
            typeSpinnerHandler(position);
        }
        else if(parent.getId() == R.id.dateSpinner)
        {
            dateSpinnerHandler(position);
        }
        if(newDATERANGE == DATERANGE && newTYPE==TYPE){
            System.out.println("KAPPA1");
            return;
        } else{
            System.out.println("KAPPA2");
            DATERANGE = newDATERANGE;
            TYPE=newTYPE;
        }
        System.out.println("KAPPA3");
        LineDataSet dataSet = new LineDataSet( getCovidData(DATERANGE, "Poland"), TYPE.label);
        dataSet.setDrawValues(false);
        dataSet.setColor(Color.rgb(220,20, 20));
        dataSet.setDrawFilled(true);
        mpLineChart.getData().addDataSet(dataSet);
        mpLineChart.getData().removeDataSet(0);
        mpLineChart.notifyDataSetChanged();
        mpLineChart.invalidate();
        System.out.println("KAPPA4");
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
            return "";
        }
    }

    static class DateAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public String getFormattedValue(float value) {
            LocalDate now = LocalDate.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd");
            System.out.println(dtf.format(now.plusDays((int) -value)));
            return dtf.format(now.plusDays( (int)value -DATERANGE.days + ((DATERANGE != DateRange.FORECAST) ? 0 : 7)));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<Entry> getRandomCovidData (DateRange range){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        for(int i = 0; i < range.days; i++){
            System.out.println(i + " / " + range.days);
            dataVals.add(new Entry(i, new Random().nextInt((100 - 10) + 1) + 10));
        }
        return dataVals;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<Entry> getCovidData (DateRange range, String cslug){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();

        LocalDate now = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Retrofit retrofit = ApiClient.getClient();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<JsonObject> call = null;
        switch(range){
            case WHOLE_PERIOD:{
                System.out.println("LABEL: WHOLE");
                call = apiService.getListEpidemyDataInCountry("Poland", "2020-01-01", dtf.format(now), TYPE.name());
                break;
            }
            case MONTH:{
                System.out.println("LABEL: MONTH");
                call = apiService.getListEpidemyDataInCountry("Poland", dtf.format(now.plusDays(range.days)), dtf.format(now), TYPE.name());
                break;
            }
            case WEEK:{
                call = apiService.getListEpidemyDataInCountry("Poland", dtf.format(now.plusDays(range.days)), dtf.format(now), TYPE.name());
                System.out.println("LABEL: WEEK");
                break;
            }
            case FORECAST:{
                call = apiService.getListEpidemyForecastInCountry("Poland", dtf.format(now.plusDays(-7)), dtf.format(now.plusDays(7)), TYPE.name());
                System.out.println("LABEL: Forecast");
                break;
            }
        }

        System.out.println("TEST: @@@");

        try {
            JsonObject json = call.execute().body();
            System.out.println("TEST: " + json);
            JSONArray arr = new JSONArray(json);
            for(int i = 0; i < arr.length(); i++){
                System.out.println("CASES: " + arr.getJSONObject(i).get("cases"));
                dataVals.add(new Entry(i, Integer.parseInt(String.valueOf(arr.getJSONObject(i).get("cases")))));
            }

        } catch (IOException | JSONException e) {
            System.out.println("TEST: !");
            e.printStackTrace();
        }

        return dataVals;
    }

    @Override
    public void showMessage(String message) {
        System.out.println("Plot Fragment");
    }

    public class MyMarkerView extends MarkerView {

        private TextView tvContent;

        public MyMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);

            // find your layout components
            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
// content (user-interface)
        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            LocalDate now = LocalDate.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd");
            tvContent.setText( dtf.format(now.plusDays( (int)e.getX() -DATERANGE.days + ((DATERANGE != DateRange.FORECAST) ? 0 : 7))) + "\n" + (int)e.getY());

            // this will perform necessary layouting
            super.refreshContent(e, highlight);
        }

        private MPPointF mOffset;

        @Override
        public MPPointF getOffset() {

            if(mOffset == null) {
                // center the marker horizontally and vertically
                mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
            }

            return mOffset;
        }}

}