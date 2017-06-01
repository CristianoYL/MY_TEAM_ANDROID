package com.example.cristiano.myteam.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.activity.PlayerActivity;
import com.example.cristiano.myteam.chart.BarChartHelper;
import com.github.mikephil.charting.charts.BarChart;

/**
 * this fragment renders a BarChart
 */
public class BarChartFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DATA_X = "dateX";
    private static final String ARG_DATA_Y = "dateY";
    private static final String ARG_IS_DATA_INT = "isInt";

    private String title;
    private String[] dataX;
    private float[] dataY;
    private boolean isDataInt;


    public BarChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param title
     * @param dataX
     * @param dataY
     * @param isDataInt
     * @return A new instance of fragment PieChartFragment.
     */
    public static BarChartFragment newInstance(String title, String[] dataX, float[] dataY, boolean isDataInt) {
        BarChartFragment fragment = new BarChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE,title);
        args.putStringArray(ARG_DATA_X,dataX);
        args.putFloatArray(ARG_DATA_Y,dataY);
        args.putBoolean(ARG_IS_DATA_INT,isDataInt);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            dataX = getArguments().getStringArray(ARG_DATA_X);
            dataY = getArguments().getFloatArray(ARG_DATA_Y);
            isDataInt = getArguments().getBoolean(ARG_IS_DATA_INT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_barchart, container, false);

        BarChart barChart = (BarChart) rootView.findViewById(R.id.barChart);
        BarChartHelper barChartHelper = new BarChartHelper(barChart,title, this.getActivity());
        if ( dataX.length != dataY.length ) {
            return null;
        }
        for ( int i = 0; i < dataX.length; i++ ) {
            barChartHelper.addData(dataX[i],dataY[i]);
        }
        barChartHelper.draw();
        return rootView;
    }
}
