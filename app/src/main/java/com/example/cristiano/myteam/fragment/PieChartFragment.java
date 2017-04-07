package com.example.cristiano.myteam.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.util.PieChartHelper;
import com.github.mikephil.charting.charts.PieChart;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PieChartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PieChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PieChartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ARG_TITLE = "title";
    private static final String ARG_CENTER_TEXT = "center";
    private static final String ARG_SHOW_CENTER = "showCenter";
    private static final String ARG_DATA_X = "dateX";
    private static final String ARG_DATA_Y = "dateY";

    // TODO: Rename and change types of parameters
    private String title, centerText;
    private boolean showCenter;
    private String[] dataX;
    private float[] dataY;

    private OnFragmentInteractionListener mListener;

    public PieChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param title
     * @param centerText
     * @param showCenter
     * @param dataX
     * @param dataY
     * @return A new instance of fragment PieChartFragment.
     */
    public static PieChartFragment newInstance(String title, String centerText, boolean showCenter,
                                               String[] dataX, float[] dataY) {
        PieChartFragment fragment = new PieChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE,title);
        args.putString(ARG_CENTER_TEXT,centerText);
        args.putBoolean(ARG_SHOW_CENTER,showCenter);
        args.putStringArray(ARG_DATA_X,dataX);
        args.putFloatArray(ARG_DATA_Y,dataY);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            centerText = getArguments().getString(ARG_CENTER_TEXT);
            showCenter = getArguments().getBoolean(ARG_SHOW_CENTER);
            dataX = getArguments().getStringArray(ARG_DATA_X);
            dataY = getArguments().getFloatArray(ARG_DATA_Y);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_piechart, container, false);

        PieChart pieChart = (PieChart) rootView.findViewById(R.id.pieChart);
        PieChartHelper pieChartHelper = new PieChartHelper(pieChart,title,centerText);
        if ( dataX.length != dataY.length ) {
            return null;
        }
        for ( int i = 0; i < dataX.length; i++ ) {
            pieChartHelper.addData(dataX[i],dataY[i]);
        }
        pieChartHelper.setShowTotal(showCenter);
        pieChartHelper.draw();

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
