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

public class TournamentListAdapter extends ArrayAdapter {

    public TournamentListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if ( view == null ) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_card_tournament,null);
        }
        HashMap<String,Object> tournamnentMap = (HashMap<String,Object>) getItem(position);
        String tournamentName = (String) tournamnentMap.get(Constant.TOURNAMENT_NAME);
        String tournamentInfo = (String) tournamnentMap.get(Constant.TOURNAMENT_INFO);

        if ( view != null ) {
            TextView tv_tournamentName = (TextView) view.findViewById(R.id.tv_tournamentName);
            TextView tv_tournamentInfo = (TextView) view.findViewById(R.id.tv_tournamentInfo);
            tv_tournamentName.setText(tournamentName);
            tv_tournamentInfo.setText(tournamentInfo);
        }
        return view;
    }

}
