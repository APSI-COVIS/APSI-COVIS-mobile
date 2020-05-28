package com.main.covis.covid_plot;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.main.covis.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class CovidPlotFragment extends Fragment implements CovidPlotContract.View, AdapterView.OnItemSelectedListener {

    private CovidPlotPresenter presenter;
    TextView homeText;
    BottomNavigationView bottomNavigationView;
    LineChart mpLineChart;
    String[] country = { "Active Cases", "New Cases", "Deceases"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // homeText = getActivity().findViewById(R.id.homeText);
        Bundle bundle=getArguments();
        if(bundle != null) {
            bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.details);
            System.out.println(bundle.getString("country"));
        }
        final View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        Spinner spin = (Spinner) rootView.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        mpLineChart=(LineChart) rootView.findViewById(R.id.line_chart);
        LineDataSet activeDataSet = new LineDataSet(dataValues1(), "Active Cases");
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

        data.setValueFormatter(new MyValueFormatter());
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


        mpLineChart.notifyDataSetChanged();
        mpLineChart.invalidate();

        return rootView;
    }

    private ArrayList<Entry> dataValues1 (){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        long timing = System.currentTimeMillis();
        float dayInMs = 86400000;
        SimpleDateFormat sdf = new SimpleDateFormat("1MMdd", Locale.ENGLISH);
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 0)), 100));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 1)), 200));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 2)), 400));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 3)), 800));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 4)), 1600));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 5)), 100));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 6)), 200));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 7)), 400));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 8)), 800));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 9)), 1600));
        return dataVals;
    }

    private ArrayList<Entry> dataValues2 (){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        long timing = System.currentTimeMillis();
        float dayInMs = 86400000;
        SimpleDateFormat sdf = new SimpleDateFormat("1MMdd", Locale.ENGLISH);
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 0)), 10));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 1)), 20));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 2)), 40));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 3)), 80));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 4)), 160));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 5)), 100));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 6)), 200));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 7)), 400));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 8)), 800));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 9)), 1600));

        return dataVals;
    }

    private ArrayList<Entry> dataValues3 (){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        long timing = System.currentTimeMillis();
        float dayInMs = 86400000;
        SimpleDateFormat sdf = new SimpleDateFormat("1MMdd", Locale.ENGLISH);
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 0)), 1));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 1)), 2));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 2)), 4));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 3)), 8));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 4)), 16));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 5)), 10));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 6)), 20));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 7)), 4));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 8)), 8));
        dataVals.add(new Entry(Float.parseFloat(sdf.format(timing + dayInMs * 9)), 16));

        return dataVals;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity().getApplicationContext(),country[position] , Toast.LENGTH_LONG).show();
        switch (position){
            case 0:
                LineDataSet activeCasesDataSet = new LineDataSet(dataValues1(), "Active Cases");
                activeCasesDataSet.setColor(Color.rgb(220,20, 20));
                if(mpLineChart.getLineData().equals(activeCasesDataSet)){
                    System.out.println("0!!!");
                    break;
                }
                activeCasesDataSet.setDrawFilled(true);
                mpLineChart.getData().addDataSet(activeCasesDataSet);
                mpLineChart.getData().removeDataSet(0);
                mpLineChart.notifyDataSetChanged();
                mpLineChart.invalidate();

                //export to pdf
                Bitmap bm = mpLineChart.getChartBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 80 , stream);
                Document doc = new Document();
                try {
                    String filePath = "/storage/emulated/0/Download" + "/ImageDemo.pdf";
                    System.out.println("dawaj" + getActivity().getFilesDir().getPath().toString());
                    PdfWriter.getInstance(doc, new FileOutputStream( filePath));
                    doc.open();
                    doc.add(new Chunk("XD"));
                    Image image = Image.getInstance(stream.toByteArray());
                    float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin()
                            - doc.rightMargin() - 0) / image.getWidth()) * 100;
                    image.scalePercent(scaler);
                    image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                    doc.add(image);
                } catch (DocumentException | IOException e) {
                    e.printStackTrace();
                } finally {
                    doc.close();
                }

                break;
            case 1:
                LineDataSet newCasesDataSet = new LineDataSet(dataValues2(), "New Cases");
                newCasesDataSet.setColor(Color.rgb(220,20, 20));
                if(mpLineChart.getLineData().equals(newCasesDataSet)){
                    System.out.println("1!!!");
                    break;
                }
                mpLineChart.getData().addDataSet(newCasesDataSet);
                mpLineChart.getData().removeDataSet(0);
                mpLineChart.notifyDataSetChanged();
                mpLineChart.invalidate();
                break;
            case 2:
                LineDataSet deceasesCasesDataSet = new LineDataSet(dataValues3(), "Deceases");
                deceasesCasesDataSet.setColor(Color.rgb(220,20, 20));
                if(mpLineChart.getLineData().equals(deceasesCasesDataSet)){
                    System.out.println("1!!!");
                    break;
                }
                mpLineChart.getData().addDataSet(deceasesCasesDataSet);
                mpLineChart.getData().removeDataSet(0);
                mpLineChart.notifyDataSetChanged();
                mpLineChart.invalidate();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    static class MyValueFormatter extends IndexAxisValueFormatter implements IAxisValueFormatter{

        @Override
        public String getFormattedValue(float value) {
            return value + " $";
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

//    public activeCasesChart(){
//        //TODO
//        return;
//    }
//
//    public newCasesChart(){
//        //TODO
//        return;
//    }
//
//    public deceaseChart(){
//        //TODO
//        return;
//    }


    @Override
    public void showMessage(String message) {
        System.out.println("Plot Fragment");
    }
}
