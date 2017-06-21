package com.example.cristiano.myteam.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.util.Constant;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Cristiano on 2017/4/16.
 *
 * This adapter renders the player's current clubs into the ListView
 *  it shows the club's name and basic info as well as player's status in the club (active/pending)
 *  it also shows the player's default club
 */

public class ClubListAdapter extends BaseAdapter{

    private List<Club> clubs;
    private Context context;

    private class ViewHolder{
        TextView tv_clubName, tv_clubInfo,tv_status;
        ImageView iv_default;
    }

    @Override
    public int getCount() {
        return clubs.size();
    }

    @Override
    public Club getItem(int position) {
        return clubs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return clubs.get(position).id;
    }

    public ClubListAdapter(@NonNull Context context, @NonNull List<Club> clubs) {
        this.clubs = clubs;
        this.context = context;
    }

    private class OnListButtonClickListener implements View.OnClickListener{
        private int position;
        private OnListButtonClickListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            if ( clubs.get(position).priority == 0 ) {
                Toast.makeText(context, R.string.error_pending_club_as_default, Toast.LENGTH_SHORT).show();
                return;
            }
            if ( !clubs.get(position).isDefault ) {
                for ( Club club : clubs ) {
                    if ( club.isDefault ) {
                        club.isDefault = false;
                        break;
                    }
                }
                clubs.get(position).isDefault = true;
                SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.KEY_USER_PREF,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Constant.CACHE_DEFAULT_CLUB_ID,clubs.get(position).id);
                editor.apply();
                Toast.makeText(context, R.string.set_club_as_default, Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        Club club = clubs.get(position);
        if ( convertView == null ) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_card_club,null);
            viewHolder.tv_clubName = (TextView) convertView.findViewById(R.id.tv_clubName);
            viewHolder.tv_clubInfo = (TextView) convertView.findViewById(R.id.tv_clubInfo);
            viewHolder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            viewHolder.iv_default = (ImageView) convertView.findViewById(R.id.iv_default);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String clubName = club.name;
        String clubInfo = club.info;
        viewHolder.tv_clubName.setText(clubName);
        viewHolder.tv_clubInfo.setText(clubInfo);
        if ( club.priority == 0 ) {
            viewHolder.tv_status.setText(R.string.label_pending);
            viewHolder.tv_status.setTextColor(ContextCompat.getColor(context,R.color.colorRed));
            viewHolder.tv_clubName.setTextColor(ContextCompat.getColor(context,R.color.colorDarkGrey));
        } else {
            viewHolder.tv_status.setText(R.string.label_active);
            viewHolder.tv_status.setTextColor(ContextCompat.getColor(context,R.color.colorGreen));
            viewHolder.tv_clubName.setTextColor(ContextCompat.getColor(context,R.color.colorGreen));
        }
        if ( club.isDefault ) {
            viewHolder.iv_default.setImageResource(android.R.drawable.star_big_on);
        } else {
            viewHolder.iv_default.setImageResource(android.R.drawable.star_big_off);
        }

        viewHolder.iv_default.setOnClickListener(new OnListButtonClickListener(position));
        return convertView;
    }
}
