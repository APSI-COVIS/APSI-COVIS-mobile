package com.main.covis.covid_plot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;
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
import com.github.mikephil.charting.utils.Utils;
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

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
        MONTH (30, "Month"),
        WEEK (7, "Week"),
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

    public static String country = "Poland";
    String population = null;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("onCreateView");
        BottomNavigationView bottomNavigationView = Objects.requireNonNull(getActivity()).findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.details);
        final View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            super.onViewCreated(rootView, savedInstanceState);
            super.onCreate(savedInstanceState);

            population = getPopulation(country);
            System.out.println(country + ": " + population);
            TextView tvCountry = (TextView) rootView.findViewById(R.id.country_name);
            TextView tvPopulation = (TextView) rootView.findViewById(R.id.population);
            tvCountry.setText(country);
            tvPopulation.setText(population);
            tvCountry.invalidate();
            tvPopulation.invalidate();
            rootView.invalidate();


            try {
                mpLineChartHandler(rootView);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    private void mpLineChartHandler(View rootView) throws IOException {
        System.out.println("mpLineChartHandler");
        mpLineChart = (LineChart) rootView.findViewById(R.id.line_chart);
        LineDataSet activeDataSet = new LineDataSet(getCovidData(DATERANGE), TYPE.label);
        activeDataSet.setDrawValues(false);
        activeDataSet.setDrawFilled(true);
        activeDataSet.setColor(Color.rgb(20,20, 220));
        activeDataSet.setDrawFilled(true);
        Drawable drawable = ContextCompat.getDrawable(rootView.getContext(), R.drawable.fade_light_blue);
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            switch (TYPE) {
                case ACTIVE: {
                    drawable = ContextCompat.getDrawable(rootView.getContext(), R.drawable.fade_light_blue);
                    break;
                }
                case NEW: {
                    drawable = ContextCompat.getDrawable(rootView.getContext(), R.drawable.fade_blue);
                    break;
                }
                case RECOVERED: {
                    drawable = ContextCompat.getDrawable(rootView.getContext(), R.drawable.fade_green);
                    break;
                }
                case DEATH: {
                    drawable = ContextCompat.getDrawable(rootView.getContext(), R.drawable.fade_red);
                    break;
                }
            }
        }
        activeDataSet.setFillDrawable(drawable);
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

        //mpLineChart.getDescription().setText(country + ": " + population);
        mpLineChart.notifyDataSetChanged();
        mpLineChart.invalidate();
        System.out.println("KAPPA");
    }

    private void spinnerHandler(View rootView) {
        System.out.println("spinnerHandler");
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
            //LocalDate now = LocalDate.now();
            LocalDate now = LocalDate.of(2020, 6, 8);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String filePath = "/storage/emulated/0/Download/" + country + "-" + TYPE.label + "-" + DATERANGE.label + "-" + dtf.format(now) + ".pdf";
//            System.out.println("dawaj" + Objects.requireNonNull(getActivity()).getFilesDir().getPath().toString());
            PdfWriter.getInstance(doc, new FileOutputStream( filePath));
            doc.open();
            doc.add(new Chunk( country + ": " + DATERANGE.label));
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
        System.out.println("getPopulation");
        int population = 3942312;
        //LocalDate now = LocalDate.now();
        LocalDate now = LocalDate.of(2020, 6, 8);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Retrofit retrofit = ApiClient.getClient();
        ApiService apiService = retrofit.create(ApiService.class);
        //Call<JsonObject> call = apiService.getListEpidemyDataInCountry("POL", "2020-05-15", "2020-05-24", "ACTIVE");
        Call<JsonObject> call = apiService.getCountryPopulation(country, dtf.format(now));
        //Call<JsonObject> call = apiService.getListEpidemyForecastInCountry("POL", "2020-05-15", "2020-05-24", "ACTIVE");
        try {
            JsonObject json = call.execute().body();
            System.out.println("population json: " + json);
            if(json != null){
                population = Integer.parseInt(String.valueOf(json.get("population")));
            }else{
                population = 0;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(population < 0){
            population = 0;
        }
        return String.format("%.2fM", population/ 1000000.0);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),country[position] , Toast.LENGTH_LONG).show();
        System.out.println("onItemSelected");
        if(parent.getId() == R.id.typeSpinner)
        {
            typeSpinnerHandler(position);
        }
        else if(parent.getId() == R.id.dateSpinner)
        {
            dateSpinnerHandler(position);
        }
        if(newDATERANGE == DATERANGE && newTYPE==TYPE){
            System.out.println("newDATERANGE == DATERANGE && newTYPE==TYPE");
            return;
        } else{
            System.out.println("! newDATERANGE == DATERANGE && newTYPE==TYPE");
            DATERANGE = newDATERANGE;
            TYPE=newTYPE;
        }

        LineDataSet dataSet = null;
        try {
            dataSet = new LineDataSet( getCovidData(DATERANGE), TYPE.label);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert dataSet != null;
        dataSet.setDrawValues(false);
        dataSet.setColor(Color.rgb(20,20, 220));
        dataSet.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            Drawable drawable = ContextCompat.getDrawable(view.getContext(), R.drawable.fade_light_blue);
            // fill drawable only supported on api level 18 and above
            switch(TYPE){
                case ACTIVE:{
                    drawable = ContextCompat.getDrawable(view.getContext(), R.drawable.fade_light_blue);
                    break;
                }
                case NEW:{
                    drawable = ContextCompat.getDrawable(view.getContext(), R.drawable.fade_blue);
                    break;
                }
                case RECOVERED:{
                    drawable = ContextCompat.getDrawable(view.getContext(), R.drawable.fade_green);
                    break;
                }
                case DEATH:{
                    drawable = ContextCompat.getDrawable(view.getContext(), R.drawable.fade_red);
                    break;
                }
            }
            //Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
            dataSet.setFillDrawable(drawable);
        }
        mpLineChart.getData().addDataSet(dataSet);
        mpLineChart.getData().removeDataSet(0);
        mpLineChart.notifyDataSetChanged();
        mpLineChart.invalidate();
        System.out.println("KAPPA");
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
                newDATERANGE = DateRange.MONTH;
                break;
            case 2:
                newDATERANGE = DateRange.WEEK;
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
            //LocalDate now = LocalDate.now();
            LocalDate now = LocalDate.of(2020, 6, 8);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd");
            System.out.println(dtf.format(now.plusDays((int) -value)));
            return dtf.format(now.plusDays( (int)value -DATERANGE.days + ((DATERANGE != DateRange.FORECAST) ? 0 : 14)));
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
    private ArrayList<Entry> getCovidData(DateRange range) throws IOException {
        final ArrayList<Entry> dataVals = new ArrayList<Entry>();
        System.out.println("getCovidData");
        //LocalDate now = LocalDate.now();
        LocalDate now = LocalDate.of(2020, 6, 8);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Retrofit retrofit = ApiClient.getClient();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<CovidCase>> call = null;
        switch(range){
            case WHOLE_PERIOD:{
                System.out.println("LABEL: WHOLE " + TYPE.name());
                call = apiService.getListEpidemyDataInCountry(country, "2020-03-01", dtf.format(now), TYPE.name());
                break;
            }
            case MONTH:{
                System.out.println("LABEL: MONTH " + TYPE.name());
                call = apiService.getListEpidemyDataInCountry(country, dtf.format(now.plusDays(-range.days)), dtf.format(now), TYPE.name());
                break;
            }
            case WEEK:{
                call = apiService.getListEpidemyDataInCountry(country, dtf.format(now.plusDays(-range.days)), dtf.format(now), TYPE.name());
                System.out.println("LABEL: WEEK " + TYPE.name());
                break;
            }
            case FORECAST:{
                call = apiService.getListEpidemyForecastInCountry(country, dtf.format(now.plusDays(0)), dtf.format(now.plusDays(7)), TYPE.name());
                System.out.println("LABEL: Forecast " + TYPE.name());
                break;
            }
        }

        System.out.println("TEST: @@@");

        List<CovidCase> covidCases = call.execute().body();
        if(covidCases != null){
            System.out.println("Number of days :"+ covidCases.size());
            for(int i = 0; i < covidCases.size(); i++){
                System.out.println(i + " CASES: " + covidCases.get(i).getCases());
                dataVals.add(new Entry(i, covidCases.get(i).getCases() > 0 ? covidCases.get(i).getCases() : 0));
            }
        }


        System.out.println("SYNCHRO");
        return dataVals;
    }

    @Override
    public void showMessage(String message) {
        System.out.println("Plot Fragment");
    }

    public static class MyMarkerView extends MarkerView {

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
            //LocalDate now = LocalDate.now();
            LocalDate now = LocalDate.of(2020, 6, 8);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd");
            tvContent.setText( dtf.format(now.plusDays( (int)e.getX() -DATERANGE.days + ((DATERANGE != DateRange.FORECAST) ? 0 : 14))) + "\n" + (int)e.getY());

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