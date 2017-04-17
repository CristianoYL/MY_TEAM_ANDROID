package com.example.cristiano.myteam.chart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.RectF;
import android.util.Log;
import android.widget.Toast;

import com.example.cristiano.myteam.activity.PlayerActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

/**
 * Created by Cristiano on 2017/4/6.
 */

public class BarChartHelper {
    private BarChart barChart;
    private String title;
    private ArrayList<String> dataX;
    private ArrayList<Float> dataY;
    private Context context;

    public BarChartHelper(BarChart barChart, String title, Context context) {
        this.barChart = barChart;
        this.title = title == null ? "" : title;
        this.dataX = new ArrayList<>();
        this.dataY = new ArrayList<>();
        this.context = context;
    }

    public void addData(String key, float value) {
        this.dataX.add(key);
        this.dataY.add(value);
    }

    public void draw(){

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);


        Description description = barChart.getDescription();
        description.setEnabled(false);
        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        barChart.setDrawGridBackground(false);

        StringAxisFormatter xAxisFormatter = new StringAxisFormatter(dataX);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // only intervals of 1 day
//        xAxis.setLabelCount(10);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setTextSize(5f);
        xAxis.setDrawGridLines(false);

        IntAxisFormatter yAxisFormatter = new IntAxisFormatter();

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1);
        leftAxis.setValueFormatter(yAxisFormatter);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(0f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setGranularity(1);
        rightAxis.setValueFormatter(yAxisFormatter);
        rightAxis.setSpaceTop(0f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)


        ArrayList<BarEntry> yEntries = new ArrayList<>(this.dataY.size());
        for ( int i = 0; i < dataY.size(); i++ ) {
            yEntries.add(new BarEntry(i,this.dataY.get(i)));
        }
        BarDataSet barDataSet = new BarDataSet(yEntries,title);
        ArrayList<Integer> colors = new ArrayList<>();
        for ( int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }
        for ( int color : ColorTemplate.JOYFUL_COLORS) {
            colors.add(color);
        }
        for ( int color : ColorTemplate.COLORFUL_COLORS) {
            colors.add(color);
        }
        barDataSet.setColors(colors);

        BarData barData = new BarData(barDataSet);
        barData.setValueTextSize(7f);
        barChart.setData(barData);
//        barChart.getXAxis().setValueFormatter(new StringAxisFormatter(dataX));
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setTouchEnabled(true);
        barChart.animateY(2000);

        CustomMarkerView markerView = new CustomMarkerView(context,xAxisFormatter);
        markerView.setChartView(barChart); // For bounds control
        barChart.setMarker(markerView); // Set the marker to the chart
    }
}
