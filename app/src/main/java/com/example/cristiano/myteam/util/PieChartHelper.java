package com.example.cristiano.myteam.util;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Cristiano on 2017/4/6.
 */

public class PieChartHelper {
    private PieChart pieChart;
    private HashMap<String,Float> data;
    private String title;
    private String centerText;
    private float sum;
    private boolean showTotal;
    private Description description;

    private final static int COLOR_DEFAULT = Color.argb(255,0,123,255);

    public PieChartHelper(PieChart pieChart, String title, String centerText) {
        this.pieChart = pieChart;
        this.title = title == null ? "" : title;
        this.centerText = centerText == null ? "" : centerText;
        this.data = new HashMap<>();
        this.sum = 0;
        this.showTotal = false;
    }

    public void setShowTotal(boolean showTotal){
        this.showTotal = showTotal;
    }

    public void addData(String key, float value) {
        this.data.put(key,value);
        this.sum += value;
    }

    public void draw(){
        if ( this.data.size() == 0 ) {
            return;
        }
        pieChart.setUsePercentValues(true);

        description = new Description();
        description.setText(title);
        description.setTextSize(15);
        description.setTextColor(COLOR_DEFAULT);
        pieChart.setDescription(description);


        if ( showTotal ) {
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleRadius(35);
            pieChart.setTransparentCircleRadius(40);
            pieChart.setCenterText(this.centerText + "\n" + this.sum);
            pieChart.setCenterTextColor(COLOR_DEFAULT);
            //007AFF
        }

        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.animateY(1000);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if ( e == null ) {
                    return;
                }
                description.setText(((PieEntry) e).getLabel() + ": " + (int)((PieEntry) e).getValue());
                description.setTextSize(15);
                pieChart.setDescription(description);
            }

            @Override
            public void onNothingSelected() {
            }
        });

        addData();

        pieChart.setEntryLabelColor(Color.BLACK);
        Legend legend = pieChart.getLegend();
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setXEntrySpace(10);
        legend.setYEntrySpace(10);

    }

    private void addData(){
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for ( String key : this.data.keySet() ) {
            pieEntries.add(new PieEntry(this.data.get(key), key));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntries,"");
        pieDataSet.setSliceSpace(3);
        pieDataSet.setSelectionShift(5);
        ArrayList<Integer> colors = new ArrayList<>();
        for ( int color : ColorTemplate.JOYFUL_COLORS ) {
            colors.add(color);
        }
        for ( int color : ColorTemplate.COLORFUL_COLORS ) {
            colors.add(color);
        }
        for ( int color : ColorTemplate.PASTEL_COLORS ) {
            colors.add(color);
        }
        for ( int color : ColorTemplate.VORDIPLOM_COLORS ) {
            colors.add(color);
        }
        for ( int color : ColorTemplate.LIBERTY_COLORS ) {
            colors.add(color);
        }
        colors.add(ColorTemplate.getHoloBlue());
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(12);
//        pieData.setValueTextColor(Color.GRAY);
        pieChart.setData(pieData);
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }
}
