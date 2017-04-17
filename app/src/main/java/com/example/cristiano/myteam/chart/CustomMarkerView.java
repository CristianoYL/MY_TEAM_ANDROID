package com.example.cristiano.myteam.chart;

import android.content.Context;
import android.widget.TextView;

import com.example.cristiano.myteam.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;

/**
 * Created by Cristiano on 2017/4/13.
 */

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;
    private IAxisValueFormatter xAxisValueFormatter;


    private DecimalFormat format;

    public CustomMarkerView(Context context, IAxisValueFormatter xAxisValueFormatter) {
        super(context, R.layout.layout_marker);

        this.xAxisValueFormatter = xAxisValueFormatter;
        tvContent = (TextView) findViewById(R.id.tv_marker);
        format = new DecimalFormat("###,###");
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if ( xAxisValueFormatter == null ) {    // pie chart
            tvContent.setText(((PieEntry)e).getLabel() + ": " + format.format(e.getY()));
        } else {    // other chart
            tvContent.setText(xAxisValueFormatter.getFormattedValue(e.getX(), null) + ": " + format.format(e.getY()));
        }


        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
