package com.example.cristiano.myteam.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristiano.myteam.R;

import java.util.List;

/**
 * Created by Cristiano on 2017/4/16.
 *
 * This adapter renders the club's event list
 */

public class EventListAdapter extends BaseAdapter{

    private List<String> events;
    private Context context;

    private class ViewHolder{
        TextView tv_eventTitle, tv_eventAddress,tv_eventTime;
        FloatingActionButton fab_viewOnMap;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public String getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
//        return events.get(position).id;
        return 0;
    }

    public EventListAdapter(@NonNull Context context, @NonNull List<String> events) {
        this.events = events;
        this.context = context;
    }

    private class OnViewMapListener implements View.OnClickListener{
        private int position;
        private OnViewMapListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            Toast.makeText(context, "show on map " + position, Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        String event = events.get(position);
        if ( convertView == null ) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_card_event,null);
            viewHolder.tv_eventTitle = (TextView) convertView.findViewById(R.id.tv_eventTitle);
            viewHolder.tv_eventTime = (TextView) convertView.findViewById(R.id.tv_eventTime);
            viewHolder.tv_eventAddress = (TextView) convertView.findViewById(R.id.tv_event_address);
            viewHolder.fab_viewOnMap = (FloatingActionButton) convertView.findViewById(R.id.fab_view_on_map);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        String eventTitle = event.title;
//        String eventTime = event.time;
//        String eventAddress = event.address;
        viewHolder.tv_eventTitle.setText(event);
        viewHolder.tv_eventTime.setText("2017/07/01");
        viewHolder.tv_eventAddress.setText("203 Penn Ave, Edison, NJ 08817");
        viewHolder.fab_viewOnMap.setOnClickListener(new OnViewMapListener(position));
        return convertView;
    }
}
