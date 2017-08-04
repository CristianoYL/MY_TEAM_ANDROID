package com.example.cristiano.myteam.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.chart.PieChartHelper;
import com.github.mikephil.charting.charts.PieChart;

/**
 * this fragment renders a Pie chart
 */
public class PieChartFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_CENTER_TEXT = "center";
    private static final String ARG_DATA_X = "dateX";
    private static final String ARG_DATA_Y = "dateY";
    private static final String ARG_IS_DATA_INT = "isInt";

    private String title, centerText;
    private String[] dataX;
    private float[] dataY;
    private boolean isDataInt;

    public PieChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param title
     * @param centerText
     * @param dataX
     * @param dataY
     * @return A new instance of fragment PieChartFragment.
     */
    public static PieChartFragment newInstance(String title, String centerText,
                                               String[] dataX, float[] dataY, boolean isDataInt) {
        PieChartFragment fragment = new PieChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE,title);
        args.putString(ARG_CENTER_TEXT,centerText);
        args.putStringArray(ARG_DATA_X,dataX);
        args.putFloatArray(ARG_DATA_Y,dataY);
        args.putBoolean(ARG_IS_DATA_INT,isDataInt);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getArguments() != null) {
            this.title = this.getArguments().getString(ARG_TITLE);
            this.centerText = this.getArguments().getString(ARG_CENTER_TEXT);
            this.dataX = this.getArguments().getStringArray(ARG_DATA_X);
            this.dataY = this.getArguments().getFloatArray(ARG_DATA_Y);
            this.isDataInt = getArguments().getBoolean(ARG_IS_DATA_INT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_piechart, container, false);

        PieChart pieChart = (PieChart) rootView.findViewById(R.id.pieChart);
        PieChartHelper pieChartHelper = new PieChartHelper(pieChart,title,centerText,this.getActivity());
        if ( dataX.length != dataY.length ) {
            return null;
        }
        for ( int i = 0; i < dataX.length; i++ ) {
            pieChartHelper.addData(dataX[i],dataY[i]);
        }
        pieChartHelper.setDataInteger(isDataInt);
        pieChartHelper.draw();
        return rootView;
    }
}
