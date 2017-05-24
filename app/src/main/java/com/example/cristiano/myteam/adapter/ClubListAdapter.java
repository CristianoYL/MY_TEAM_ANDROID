package com.example.cristiano.myteam.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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

    private class ViewHolder{
        TextView tv_clubName, tv_clubInfo;
    }
    public ClubListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if ( convertView == null ) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_card_club,null);
            viewHolder.tv_clubName = (TextView) convertView.findViewById(R.id.tv_clubName);
            viewHolder.tv_clubInfo = (TextView) convertView.findViewById(R.id.tv_clubInfo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        HashMap<String,Object> clubMap = (HashMap<String,Object>) getItem(position);
        String clubName = (String) clubMap.get(Constant.CLUB_NAME);
        String clubInfo = (String) clubMap.get(Constant.CLUB_INFO);
        viewHolder.tv_clubName.setText(clubName);
        viewHolder.tv_clubInfo.setText(clubInfo);
        return convertView;
    }
}
