package com.example.cristiano.myteam.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.util.Constant;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Cristiano on 2017/4/8.
 */

public class ResultListAdapter extends ArrayAdapter{

    public ResultListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if ( view == null ) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_card_result,null);
        }
        HashMap<String,Object> resultMap = (HashMap<String,Object>) getItem(position);
        String tournament = (String) resultMap.get(Constant.RESULT_KEY_TOURNAMENT);
        String home = (String) resultMap.get(Constant.RESULT_KEY_HOME);
        String away = (String) resultMap.get(Constant.RESULT_KEY_AWAY);
        String score = (String) resultMap.get(Constant.RESULT_KEY_SCORE);
        String penScore = (String) resultMap.get(Constant.RESULT_KEY_PEN);
        SimpleAdapter homeAdapter = (SimpleAdapter) resultMap.get(Constant.RESULT_KEY_HOME_EVENT);
        SimpleAdapter awayAdapter = (SimpleAdapter) resultMap.get(Constant.RESULT_KEY_AWAY_EVENT);

        if ( view != null ) {
            TextView tv_tournament = (TextView) view.findViewById(R.id.tv_tournament);
            TextView tv_home = (TextView) view.findViewById(R.id.tv_home);
            TextView tv_away = (TextView) view.findViewById(R.id.tv_away);
            TextView tv_score = (TextView) view.findViewById(R.id.tv_score);
            TextView tv_penScore = (TextView) view.findViewById(R.id.tv_penScore);
            ListView lv_home = (ListView) view.findViewById(R.id.lv_home);
            ListView lv_away = (ListView) view.findViewById(R.id.lv_away);

            tv_tournament.setText(tournament);
            tv_home.setText(home);
            tv_away.setText(away);
            tv_score.setText(score);
            tv_penScore.setText(penScore);
            lv_home.setAdapter(homeAdapter);
            lv_away.setAdapter(awayAdapter);
        }
        return view;
    }
}
