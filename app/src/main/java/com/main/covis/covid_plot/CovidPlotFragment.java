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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.main.covis.R;

import java.util.ArrayList;
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
            public void onClick(View v) {
                System.out.println("Plot button");
            }
        });

        mpLineChart=(LineChart) rootView.findViewById(R.id.line_chart);
        LineDataSet lineDataSet1 = new LineDataSet(dataValues1(), "Data Set 1");
        lineDataSet1.setColor(Color.RED);
        LineDataSet lineDataSet2 = new LineDataSet(dataValues2(), "Data Set 2");
        lineDataSet2.setColor(Color.rgb(220,20, 20));
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);

        mpLineChart.setBackgroundColor(Color.GRAY);
        mpLineChart.setDrawGridBackground(true);
        mpLineChart.getXAxis().setDrawGridLines(false);
        mpLineChart.setDrawBorders(true);
        mpLineChart.setBorderColor(Color.CYAN);
        Description desc = new Description();
        desc.setText("COVID new cases");
        mpLineChart.setDescription(desc);

        LineData data = new LineData(dataSets);
        mpLineChart.setData(data);
        mpLineChart.invalidate();
        return rootView;
    }

    private ArrayList<Entry> dataValues1 (){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        dataVals.add(new Entry(0, 1));
        dataVals.add(new Entry(1, 2));
        dataVals.add(new Entry(2, 4));
        dataVals.add(new Entry(3, 8));
        dataVals.add(new Entry(4, 16));
        return dataVals;
    }

    private ArrayList<Entry> dataValues2 (){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        dataVals.add(new Entry(0, 0));
        dataVals.add(new Entry(1, 1));
        dataVals.add(new Entry(2, 4));
        dataVals.add(new Entry(3, 9));
        dataVals.add(new Entry(4, 16));
        return dataVals;
    }
    @Override
    public void showMessage(String message) {
        System.out.println("Plot Fragment");
    }
}
