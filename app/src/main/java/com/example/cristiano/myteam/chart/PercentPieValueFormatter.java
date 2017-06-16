package com.example.cristiano.myteam.chart;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Cristiano on 2017/6/15.
 */

public class PercentPieValueFormatter extends PercentFormatter {
    PieDataSet pieDataSet;

    public PercentPieValueFormatter(PieDataSet pieDataSet) {
        this.pieDataSet = pieDataSet;
    }

    // value
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if ( value == 0 ) {
            return "";
        }
        return super.getFormattedValue(value, entry, dataSetIndex, viewPortHandler);
    }
}
