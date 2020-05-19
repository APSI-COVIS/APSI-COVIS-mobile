package com.main.covis.covid_plot;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.main.covis.R;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class CovidPlotFragment extends Fragment implements CovidPlotContract.View {

    private CovidPlotPresenter presenter;
    TextView homeText;

    LineChart mpLineChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // homeText = getActivity().findViewById(R.id.homeText);
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        final Button button = (Button) rootView.findViewById(R.id.plot_button);
        button.setOnClickListener(new View.OnClickListener() {
            int counter = 0;
            public void onClick(View v) {
                System.out.println("Plot button");
                float value = System.currentTimeMillis() + 86400000 * counter;
                counter++;
                Date date = new Date((long)value);
                System.out.println(date);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.ENGLISH);
                System.out.println( sdf.format(date));
            }
        });

        mpLineChart=(LineChart) rootView.findViewById(R.id.line_chart);
        LineDataSet lineDataSet1 = new LineDataSet(dataValues1(), "Data Set 1");
        //lineDataSet1.setColor(Color.RED);
        LineDataSet lineDataSet2 = new LineDataSet(dataValues2(), "Data Set 2");
        //lineDataSet2.setColor(Color.rgb(220,20, 20));
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);


        mpLineChart.setBackgroundColor(Color.TRANSPARENT);
        mpLineChart.setDrawGridBackground(false);
        mpLineChart.getXAxis().setDrawGridLines(false);
        mpLineChart.setDrawBorders(true);
        //mpLineChart.setBorderColor(Color.CYAN);
        Description desc = new Description();
        desc.setText("COVID new cases");
        mpLineChart.setDescription(desc);

        LineData data = new LineData(dataSets);



        mpLineChart.setData(data);

        data.setValueFormatter(new MyValueFormatter());
        XAxis xAxis = mpLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setValueFormatter( new IndexAxisValueFormatter(getAreaCount));
        xAxis.setValueFormatter(new DateAxisValueFormatter());
        xAxis.setGranularity(1);
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
        float timing = System.currentTimeMillis();
        float dayInMs = 86400000;
        dataVals.add(new Entry(timing + dayInMs * 0, 1));
        dataVals.add(new Entry(timing + dayInMs * 1, 2));
        dataVals.add(new Entry(timing + dayInMs * 2, 4));
        dataVals.add(new Entry(timing + dayInMs * 3, 8));
        dataVals.add(new Entry(timing + dayInMs * 4, 16));
        return dataVals;
    }

    private ArrayList<Entry> dataValues2 (){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        float timing = System.currentTimeMillis();
        float dayInMs = 86400000;
        dataVals.add(new Entry(timing + dayInMs * 0, 0));
        dataVals.add(new Entry(timing + dayInMs * 1, 1));
        dataVals.add(new Entry(timing + dayInMs * 2, 4));
        dataVals.add(new Entry(timing + dayInMs * 3, 9));
        dataVals.add(new Entry(timing + dayInMs * 4, 16));
        return dataVals;
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
            Date date = new Date((long)value);
            System.out.println(date);
            //Specify the format you'd like
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.ENGLISH);
            return sdf.format(date);
        }
    }

    public ArrayList<String> getAreaCount() {

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Mon");
        xAxisLabel.add("Tue");
        xAxisLabel.add("Wed");
        xAxisLabel.add("Thu");
        xAxisLabel.add("Fri");
        xAxisLabel.add("Sat");
        xAxisLabel.add("Sun");

        return xAxisLabel ;

    }


    @Override
    public void showMessage(String message) {
        System.out.println("Plot Fragment");
    }
}
