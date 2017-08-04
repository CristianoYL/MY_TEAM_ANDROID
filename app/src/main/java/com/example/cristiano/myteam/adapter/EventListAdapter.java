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
import com.example.cristiano.myteam.structure.Event;

import java.util.List;

/**
 * Created by Cristiano on 2017/4/16.
 *
 * This adapter renders the club's event list
 */

public class EventListAdapter extends BaseAdapter{

    private List<Event> events;
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
    public Event getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return events.get(position).id;
    }

    public EventListAdapter(@NonNull Context context, @NonNull List<Event> events) {
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
            Event event = getItem(position);
            Toast.makeText(context, "show on map(" +
                    event.latitude + "," + event.longitude +")", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        Event event = getItem(position);
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
        String eventTitle = event.eventTitle;
        String eventTime = event.eventTime;
        String eventAddress = event.eventAddress;
        viewHolder.tv_eventTitle.setText(eventTitle);
        viewHolder.tv_eventTime.setText(eventTime);
        viewHolder.tv_eventAddress.setText(eventAddress);
        viewHolder.fab_viewOnMap.setOnClickListener(new OnViewMapListener(position));
        return convertView;
    }
}
