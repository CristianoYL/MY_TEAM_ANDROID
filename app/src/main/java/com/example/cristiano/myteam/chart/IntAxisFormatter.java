package com.example.cristiano.myteam.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by Cristiano on 2017/4/13.
 */

public class IntAxisFormatter implements IAxisValueFormatter {

    private DecimalFormat mFormat;

    public IntAxisFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormat.format((int)value);
    }
}
