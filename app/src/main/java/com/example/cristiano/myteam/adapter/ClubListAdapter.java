package com.example.cristiano.myteam.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.util.Constant;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Cristiano on 2017/4/16.
 */

public class ClubListAdapter extends ArrayAdapter {
    public ClubListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if ( view == null ) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_card_club,null);
        }
        HashMap<String,Object> clubMap = (HashMap<String,Object>) getItem(position);
        String clubName = (String) clubMap.get(Constant.CLUB_NAME);
        String clubInfo = (String) clubMap.get(Constant.CLUB_INFO);

        if ( view != null ) {
            TextView tv_clubName = (TextView) view.findViewById(R.id.tv_clubName);
            TextView tv_clubInfo = (TextView) view.findViewById(R.id.tv_clubInfo);
            tv_clubName.setText(clubName);
            tv_clubInfo.setText(clubInfo);
        }
        return view;
    }
}
