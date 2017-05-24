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

    private class ViewHolder{
        TextView tv_tournament;
        TextView tv_home;
        TextView tv_away;
        TextView tv_score;
        TextView tv_penScore;
        ListView lv_home;
        ListView lv_away;
    }

    public ResultListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if ( convertView == null ) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_card_result,null);
            viewHolder = new ViewHolder();
            viewHolder.tv_tournament = (TextView) convertView.findViewById(R.id.tv_tournament);
            viewHolder.tv_home = (TextView) convertView.findViewById(R.id.tv_home);
            viewHolder.tv_away = (TextView) convertView.findViewById(R.id.tv_away);
            viewHolder.tv_score = (TextView) convertView.findViewById(R.id.tv_score);
            viewHolder.tv_penScore = (TextView) convertView.findViewById(R.id.tv_penScore);
            viewHolder.lv_home = (ListView) convertView.findViewById(R.id.lv_home);
            viewHolder.lv_away = (ListView) convertView.findViewById(R.id.lv_away);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        HashMap<String,Object> resultMap = (HashMap<String,Object>) getItem(position);
        String tournament = (String) resultMap.get(Constant.RESULT_KEY_TOURNAMENT);
        String home = (String) resultMap.get(Constant.RESULT_KEY_HOME);
        String away = (String) resultMap.get(Constant.RESULT_KEY_AWAY);
        String score = (String) resultMap.get(Constant.RESULT_KEY_SCORE);
        String penScore = (String) resultMap.get(Constant.RESULT_KEY_PEN);
        SimpleAdapter homeAdapter = (SimpleAdapter) resultMap.get(Constant.RESULT_KEY_HOME_EVENT);
        SimpleAdapter awayAdapter = (SimpleAdapter) resultMap.get(Constant.RESULT_KEY_AWAY_EVENT);

        viewHolder.tv_tournament.setText(tournament);
        viewHolder.tv_home.setText(home);
        viewHolder.tv_away.setText(away);
        viewHolder.tv_score.setText(score);
        viewHolder.tv_penScore.setText(penScore);
        viewHolder.lv_home.setAdapter(homeAdapter);
        viewHolder.lv_away.setAdapter(awayAdapter);
        return convertView;
    }
}
