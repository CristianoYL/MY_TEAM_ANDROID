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
import com.example.cristiano.myteam.structure.Squad;

import java.util.List;

/**
 * Created by Cristiano on 2017/4/17.
 */

public class SquadListAdapter extends ArrayAdapter {
    public SquadListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if ( view == null ) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_card_squad,null);
        }
        Squad squad = (Squad) getItem(position);

        if ( view != null ) {
            TextView tv_name = (TextView) view.findViewById(R.id.et_name);
            TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
            TextView tv_role = (TextView) view.findViewById(R.id.tv_role);
            tv_name.setText(squad.getName());
            tv_number.setText(squad.getNumber()+"");
            tv_role.setText(squad.getRole());
        }
        return view;
    }
}
