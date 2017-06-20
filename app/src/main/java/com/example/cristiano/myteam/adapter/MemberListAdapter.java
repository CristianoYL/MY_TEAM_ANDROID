package com.example.cristiano.myteam.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private int selfID;
    private int priority;
    private Resources resources;

    public MemberListAdapter(@NonNull Context context, @NonNull List<Player> playerList,
                             @NonNull List<Member> memberInfo, int selfID, int selfPriority) {
        this.context = context;
        this.playerList = playerList;
        this.memberInfo = memberInfo;
        this.selfID = selfID;
        this.priority = selfPriority;
        this.resources = context.getResources();

    }
    private class ViewHolder{
        private TextView tv_position, tv_belt, tv_clubRole, tv_name;
//        private ImageView iv_foot;
        private View view_admin;
        private ImageView iv_negative, iv_positive;

        ViewHolder(View view){
            tv_position = (TextView) view.findViewById(R.id.tv_position);
            tv_belt = (TextView) view.findViewById(R.id.tv_belt);
            tv_name = (TextView) view.findViewById(R.id.tv_playerName);
            tv_clubRole = (TextView) view.findViewById(R.id.tv_clubRole);
//            iv_foot = (ImageView) view.findViewById(R.id.iv_foot);
            view_admin = view.findViewById(R.id.layout_admin);
            iv_negative = (ImageView) view.findViewById(R.id.iv_negative);
            iv_positive = (ImageView) view.findViewById(R.id.iv_positive);
        }
    }

    private class OnMemberListClickListener implements View.OnClickListener{
        private int position;

        private OnMemberListClickListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            if ( v.getId() == R.id.iv_negative ) {
                if ( memberInfo.get(position).getPriority() == Constant.PRIORITY_APPLICANT ) {
                    Toast.makeText(context, "Reject!", Toast.LENGTH_SHORT).show();
                } else if ( memberInfo.get(position).getPriority() == Constant.PRIORITY_REGULAR ) {
                    Toast.makeText(context, "Kick!", Toast.LENGTH_SHORT).show();
                } else if ( memberInfo.get(position).getPriority() == Constant.PRIORITY_CO_CAP ) {
                    Toast.makeText(context, "Demote!", Toast.LENGTH_SHORT).show();
                }
            } else if ( v.getId() == R.id.iv_positive ) {
                if ( memberInfo.get(position).getPriority() == Constant.PRIORITY_APPLICANT ) {
                    Toast.makeText(context, "Accept!", Toast.LENGTH_SHORT).show();
                } else if ( memberInfo.get(position).getPriority() == Constant.PRIORITY_REGULAR ) {
                    Toast.makeText(context, "Promote!", Toast.LENGTH_SHORT).show();
                }
            }
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
        if ( member.getPlayerID() == selfID ) {
            convertView.setBackgroundResource(R.drawable.card_border_light_green);
        } else {
            convertView.setBackgroundResource(R.drawable.card_border_light_grey);
        }
        switch ( member.getPriority() ) {
            case Constant.PRIORITY_APPLICANT:
                viewHolder.tv_belt.setBackgroundResource(R.color.colorDarkGrey);
                viewHolder.tv_clubRole.setText(R.string.priority_applicant);
                if ( priority > Constant.PRIORITY_REGULAR ) {
                    viewHolder.view_admin.setVisibility(View.VISIBLE);
                    viewHolder.iv_negative.setImageResource(R.drawable.ic_block_red_24dp);
                    viewHolder.iv_positive.setImageResource(R.drawable.ic_check_circle_green_24dp);
                    OnMemberListClickListener memberListClickListener = new OnMemberListClickListener(position);
                    viewHolder.iv_negative.setOnClickListener(memberListClickListener);
                    viewHolder.iv_positive.setOnClickListener(memberListClickListener);
                } else {
                    viewHolder.view_admin.setVisibility(View.GONE);
                }
                break;
            case Constant.PRIORITY_REGULAR:
                viewHolder.tv_belt.setBackgroundResource(R.color.colorBlue);
                viewHolder.tv_clubRole.setText(R.string.priority_member);
                if ( priority > Constant.PRIORITY_REGULAR ) {
                    viewHolder.view_admin.setVisibility(View.VISIBLE);
                    viewHolder.iv_negative.setImageResource(R.drawable.ic_delete_red_24dp);
                    viewHolder.iv_positive.setImageResource(R.drawable.ic_arrow_upward_green_24dp);
                    OnMemberListClickListener memberListClickListener = new OnMemberListClickListener(position);
                    viewHolder.iv_negative.setOnClickListener(memberListClickListener);
                    viewHolder.iv_positive.setOnClickListener(memberListClickListener);
                } else {
                    viewHolder.view_admin.setVisibility(View.GONE);
                }
                break;
            case Constant.PRIORITY_CO_CAP:
                viewHolder.tv_belt.setBackgroundResource(R.color.colorGreen);
                viewHolder.tv_clubRole.setText(R.string.priority_co_cap);
                if ( priority > Constant.PRIORITY_REGULAR ) {
                    viewHolder.view_admin.setVisibility(View.VISIBLE);
                    viewHolder.iv_negative.setImageResource(R.drawable.ic_arrow_downward_grey_24dp);
                    viewHolder.iv_positive.setVisibility(View.GONE);
                    OnMemberListClickListener memberListClickListener = new OnMemberListClickListener(position);
                    viewHolder.iv_negative.setOnClickListener(memberListClickListener);
                } else {
                    viewHolder.view_admin.setVisibility(View.GONE);
                }
                break;
            case Constant.PRIORITY_CAPTAIN:
                viewHolder.tv_belt.setBackgroundResource(R.color.colorRed);
                viewHolder.tv_clubRole.setText(R.string.priority_captain);
                viewHolder.view_admin.setVisibility(View.GONE);
                break;
        }
        String roleTag = getAbbreviatedRole(player.getRole());
        viewHolder.tv_position.setText(roleTag);
//        if ( player.isLeftFooted() ) {
//            viewHolder.iv_foot.setImageResource(R.drawable.ic_leftfoot);
//        } else {
//            viewHolder.iv_foot.setImageResource(R.drawable.ic_rightfoot);
//        }
        return convertView;
    }

    private String getAbbreviatedRole(String role) {
        if ( role.equals(resources.getString(R.string.position_gk)) ) {
            return resources.getString(R.string.position_abbr_gk);
        } else if ( role.equals(resources.getString(R.string.position_cb)) ) {
            return resources.getString(R.string.position_abbr_cb);
        } else if ( role.equals(resources.getString(R.string.position_rb)) ) {
            return resources.getString(R.string.position_abbr_rb);
        } else if ( role.equals(resources.getString(R.string.position_lb)) ) {
            return resources.getString(R.string.position_abbr_lb);
        } else if ( role.equals(resources.getString(R.string.position_cm)) ) {
            return resources.getString(R.string.position_abbr_cm);
        } else if ( role.equals(resources.getString(R.string.position_cdm)) ) {
            return resources.getString(R.string.position_abbr_cdm);
        } else if ( role.equals(resources.getString(R.string.position_cam)) ) {
            return resources.getString(R.string.position_abbr_cam);
        } else if ( role.equals(resources.getString(R.string.position_lm)) ) {
            return resources.getString(R.string.position_abbr_lm);
        } else if ( role.equals(resources.getString(R.string.position_rm)) ) {
            return resources.getString(R.string.position_abbr_rm);
        } else if ( role.equals(resources.getString(R.string.position_lw)) ) {
            return resources.getString(R.string.position_abbr_lw);
        } else if ( role.equals(resources.getString(R.string.position_rw)) ) {
            return resources.getString(R.string.position_abbr_rw);
        } else if ( role.equals(resources.getString(R.string.position_st)) ) {
            return resources.getString(R.string.position_abbr_st);
        } else if ( role.equals(resources.getString(R.string.position_cf)) ) {
            return resources.getString(R.string.position_abbr_cf);
        } else {
            return role;
        }
    }
    private void rejectApplicant(){

    }
    private void acceptApplicant(){

    }
    private void promoteMember(){

    }
    private void demoteMember(){

    }
    private void kickMember(){

    }
}
