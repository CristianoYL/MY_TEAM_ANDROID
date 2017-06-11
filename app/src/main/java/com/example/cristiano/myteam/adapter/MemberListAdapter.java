package com.example.cristiano.myteam.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.structure.Member;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.util.Constant;

import java.util.List;

/**
 * Created by Cristiano on 2017/4/19.
 */

public class MemberListAdapter extends BaseAdapter {

    private Context context;
    private List<Player> playerList;
    private List<Member> memberInfo;
    public MemberListAdapter(@NonNull Context context, @NonNull List<Player> playerList, @NonNull List<Member> memberInfo) {
        this.context = context;
        this.playerList = playerList;
        this.memberInfo = memberInfo;
    }
    private class ViewHolder{
        private TextView tv_position, tv_belt, tv_clubRole, tv_name;
        private ImageView iv_foot;

        ViewHolder(View view){
            tv_position = (TextView) view.findViewById(R.id.tv_position);
            tv_belt = (TextView) view.findViewById(R.id.tv_belt);
            tv_name = (TextView) view.findViewById(R.id.tv_playerName);
            tv_clubRole = (TextView) view.findViewById(R.id.tv_clubRole);
            iv_foot = (ImageView) view.findViewById(R.id.iv_foot);
        }
    }

    @Override
    public int getCount() {
        return playerList.size();
    }

    @Override
    public Member getItem(int position) {
        return memberInfo.get(position);
    }

    @Override
    public long getItemId(int position) {   // return player ID
        return playerList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        Player player = playerList.get(position);
        Member member = memberInfo.get(position);
        if ( convertView == null ) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_card_member,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_name.setText(player.getDisplayName());
        switch ( member.getPriority() ) {
            case Constant.PRIORITY_APPLICANT:
                viewHolder.tv_belt.setBackgroundResource(R.color.colorDarkGrey);
                viewHolder.tv_clubRole.setText("applicant");
                break;
            case Constant.PRIORITY_REGULAR:
                viewHolder.tv_belt.setBackgroundResource(R.color.colorBlue);
                viewHolder.tv_clubRole.setText("member");
                break;
            case Constant.PRIORITY_ADMIN:
                viewHolder.tv_belt.setBackgroundResource(R.color.colorGreen);
                viewHolder.tv_clubRole.setText("admin");
                break;
            case Constant.PRIORITY_LEADER:
                viewHolder.tv_belt.setBackgroundResource(R.color.colorRed);
                viewHolder.tv_clubRole.setText("leader");
                break;
        }
        viewHolder.tv_position.setText(player.getRole());
        if ( player.isLeftFooted() ) {
            viewHolder.iv_foot.setImageResource(R.drawable.ic_leftfoot);
        } else {
            viewHolder.iv_foot.setImageResource(R.drawable.ic_rightfoot);
        }
        return convertView;
    }
}
