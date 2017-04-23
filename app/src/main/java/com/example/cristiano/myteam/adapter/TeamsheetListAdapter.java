package com.example.cristiano.myteam.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.util.Constant;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Cristiano on 2017/4/19.
 */

public class TeamsheetListAdapter extends ArrayAdapter {

    public TeamsheetListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if ( view == null ) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_card_teamsheet,null);
        }
        HashMap<String,Object> teamsheetMap = (HashMap<String,Object>) getItem(position);
        String playerName = (String) teamsheetMap.get(Constant.PLAYER_DISPLAY_NAME);
        String playerRole = (String) teamsheetMap.get(Constant.PLAYER_ROLE);
        boolean leftFooted = (boolean) teamsheetMap.get(Constant.PLAYER_FOOT);

        if ( view != null ) {
            TextView tv_playerName = (TextView) view.findViewById(R.id.tv_playerName);
            TextView tv_playerRole = (TextView) view.findViewById(R.id.tv_playerRole);
            ImageView iv_foot = (ImageView) view.findViewById(R.id.iv_foot);
            tv_playerName.setText(playerName);
            tv_playerRole.setText(playerRole);
            if ( leftFooted ) {
                iv_foot.setImageResource(R.drawable.ic_leftfoot);
            } else {
                iv_foot.setImageResource(R.drawable.ic_rightfoot);
            }
        }
        return view;
    }
}
