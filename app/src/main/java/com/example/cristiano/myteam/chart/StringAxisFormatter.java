package com.example.cristiano.myteam.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

/**
 * Created by Cristiano on 2017/4/13.
 */

public class StringAxisFormatter implements IAxisValueFormatter {
    private ArrayList<String> mLabels;

    public StringAxisFormatter(ArrayList<String> labels) {
        mLabels = labels;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mLabels.get((int) value);
    }
}
