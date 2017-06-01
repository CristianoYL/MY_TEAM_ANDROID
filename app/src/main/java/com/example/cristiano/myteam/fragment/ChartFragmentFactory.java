package com.example.cristiano.myteam.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import com.example.cristiano.myteam.util.Constant;

/**
 * Created by Cristiano on 2017/4/13.
 *
 * this factory class produces fragment that contains according type of chart
 */

public class ChartFragmentFactory {
    public static Fragment makeChartFragment(int chartType, String[] dataX, float[] dataY, String title, String centerText, TabLayout tabLayout ) {
        Fragment fragment = null;
        if ( title == null ) {
            return fragment;
        }
        if ( tabLayout != null ) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }
        switch ( chartType ) {
            case Constant.CHART_TYPE_BAR:
                fragment = BarChartFragment.newInstance(title,dataX, dataY,false);
                break;
            case Constant.CHART_TYPE_PIE:
                fragment = PieChartFragment.newInstance(title,centerText,dataX,dataY,true);
                break;
        }
        return fragment;
    }
}
