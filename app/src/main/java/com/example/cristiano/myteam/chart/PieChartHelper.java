package com.example.cristiano.myteam.chart;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

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
    // title config
    private float titleTextSize = 15;
    private int titleTextColor = COLOR_BLUE;
    // center text config
    private float holeRadios = 35;
    private float transparentRadios = 40;
    private int centerTextColor = COLOR_BLUE;
    // rotation config
    private boolean rotateEnabled = true;
    private float rotateAngle = 0;
    private int rotateDuration = 1000;
    // data config
    private int labelColor = Color.BLACK;
    private float labelTextSize = 10;
    private int valueColor = Color.BLACK;
    private float valueTextSize = 15;
    private float legendSpace = 10;
    //pie config
    private float sliceSpace = 3;
    private float selectionShift = 5;

    private boolean isDataInteger = false;

    private PieChart pieChart;
    private HashMap<String,Float> data;
    private String title;
    private String centerText;
    private float sum;
    private Description description;
    private Context context;
    //007AFF
    private final static int COLOR_BLUE = Color.argb(255,0,123,255);

    public PieChartHelper(PieChart pieChart, String title, String centerText, Context context) {
        this.pieChart = pieChart;
        this.title = title == null ? "" : title;
        this.centerText = centerText == null ? "" : centerText;
        this.data = new HashMap<>();
        this.sum = 0;
        this.context = context;
    }


    public void addData(String key, float value) {
        Log.d("ADD Data",key+","+value);
        this.data.put(key,value);
        this.sum += value;
    }

    public void draw(){
        if ( this.data.size() == 0 ) {
            return;
        }
        this.pieChart.setUsePercentValues(true);

        this.description = new Description();
        this.description.setText(this.title);
        this.description.setTextSize(this.titleTextSize);
        this.description.setTextColor(this.titleTextColor);
        this.pieChart.setDescription(this.description);


        if ( this.centerText != null ) {
            this.pieChart.setDrawHoleEnabled(true);
            this.pieChart.setHoleRadius(this.holeRadios);
            this.pieChart.setTransparentCircleRadius(this.transparentRadios);
            if ( this.isDataInteger ) {
                this.pieChart.setCenterText(this.centerText + "\n" + (int)this.sum);
            } else {
                this.pieChart.setCenterText(this.centerText + "\n" + this.sum);
            }
            this.pieChart.setCenterTextColor(this.centerTextColor);
        } else {
            this.pieChart.setDrawHoleEnabled(false);
        }

        if ( this.rotateEnabled ) {
            this.pieChart.setRotationEnabled(this.rotateEnabled);
            this.pieChart.setRotationAngle(this.rotateAngle);
            this.pieChart.animateY(this.rotateDuration);
        }


        this.pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if ( e == null || !(e instanceof PieEntry) ) {
                    return;
                }
                if ( isDataInteger ) {
                    description.setText(((PieEntry) e).getLabel() + ": " + (int)((PieEntry) e).getValue());
                } else {
                    description.setText(((PieEntry) e).getLabel() + ": " + ((PieEntry) e).getValue());
                }
            }

            @Override
            public void onNothingSelected() {
                description.setText(title);
            }
        });

        addData();

        pieChart.setEntryLabelColor(labelColor);
        pieChart.setEntryLabelTextSize(labelTextSize);
        Legend legend = pieChart.getLegend();
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setXEntrySpace(legendSpace);

    }

    private void addData(){
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for ( String key : this.data.keySet() ) {
            pieEntries.add(new PieEntry(this.data.get(key), key));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntries,"");
        pieDataSet.setSliceSpace(sliceSpace);
        pieDataSet.setSelectionShift(selectionShift);
        ArrayList<Integer> colors = new ArrayList<>();
        for ( int color : ColorTemplate.MATERIAL_COLORS ) {
            colors.add(color);
        }
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
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentPieValueFormatter(pieDataSet));
        pieData.setValueTextSize(this.valueTextSize);
        pieData.setValueTextColor(this.valueColor);

        this.pieChart.setData(pieData);
        this.pieChart.highlightValues(null);
        this.pieChart.invalidate();
        CustomMarkerView markerView = new CustomMarkerView(context,null);
        markerView.setChartView(pieChart); // For bounds control
        pieChart.setMarker(markerView); // Set the marker to the chart
    }

    public void animate() {
        this.pieChart.animateY(this.rotateDuration);
    }

    public void setTitleTextSize(float titleTextSize) {
        this.titleTextSize = titleTextSize;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public void setHoleRadios(float holeRadios) {
        this.holeRadios = holeRadios;
    }

    public void setTransparentRadios(float transparentRadios) {
        this.transparentRadios = transparentRadios;
    }

    public void setCenterTextColor(int centerTextColor) {
        this.centerTextColor = centerTextColor;
    }

    public void setRotateEnabled(boolean rotateEnabled) {
        this.rotateEnabled = rotateEnabled;
    }

    public void setRotateAngle(float rotateAngle) {
        this.rotateAngle = rotateAngle;
    }

    public void setRotateDuration(int rotateDuration) {
        this.rotateDuration = rotateDuration;
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
    }

    public void setLabelTextSize(float labelTextSize) {
        this.labelTextSize = labelTextSize;
    }

    public void setValueColor(int valueColor) {
        this.valueColor = valueColor;
    }

    public void setValueTextSize(float valueTextSize) {
        this.valueTextSize = valueTextSize;
    }

    public void setLegendSpace(float legendSpace) {
        this.legendSpace = legendSpace;
    }

    public void setSliceSpace(float sliceSpace) {
        this.sliceSpace = sliceSpace;
    }

    public void setSelectionShift(float selectionShift) {
        this.selectionShift = selectionShift;
    }

    public void setDataInteger(boolean dataInteger) {
        isDataInteger = dataInteger;
    }
}
