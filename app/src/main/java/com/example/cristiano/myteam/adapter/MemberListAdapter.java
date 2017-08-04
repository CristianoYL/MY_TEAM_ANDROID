package com.example.cristiano.myteam.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.structure.Member;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Cristiano on 2017/4/19.
 */

public class MemberListAdapter extends BaseAdapter {

    private Context context;
    private List<Player> playerList;
    private List<Member> memberInfo;
    private int selfID;
    private int selfPriority;
    private Resources resources;

    public MemberListAdapter(@NonNull Context context, @NonNull List<Player> playerList,
                             @NonNull List<Member> memberInfo, int selfID, int selfPriority) {
        this.context = context;
        this.playerList = playerList;
        this.memberInfo = memberInfo;
        this.selfID = selfID;
        this.selfPriority = selfPriority;
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
                viewHolder.tv_clubRole.setText(R.string.priority_applicant);
                if ( selfPriority > Constant.PRIORITY_REGULAR ) {
                    viewHolder.view_admin.setVisibility(View.VISIBLE);
                    viewHolder.iv_positive.setVisibility(View.VISIBLE);
                    viewHolder.iv_negative.setVisibility(View.VISIBLE);
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
                if ( selfPriority > Constant.PRIORITY_REGULAR ) {
                    viewHolder.view_admin.setVisibility(View.VISIBLE);
                    viewHolder.iv_positive.setVisibility(View.VISIBLE);
                    viewHolder.iv_negative.setVisibility(View.VISIBLE);
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
                if ( selfPriority > Constant.PRIORITY_CO_CAP ) {
                    viewHolder.view_admin.setVisibility(View.VISIBLE);
                    viewHolder.iv_negative.setImageResource(R.drawable.ic_arrow_downward_grey_24dp);
                    viewHolder.iv_positive.setVisibility(View.GONE);
                    viewHolder.iv_negative.setVisibility(View.VISIBLE);
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
        if ( member.getPlayerID() == selfID ) {
            convertView.setBackgroundResource(R.drawable.card_border_light_green);
            viewHolder.view_admin.setVisibility(View.GONE);
        } else {
            convertView.setBackgroundResource(R.drawable.background_light_grey_square);
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

    private class OnMemberListClickListener implements View.OnClickListener{

        private int position;

        public OnMemberListClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if ( v.getId() == R.id.iv_negative ) {
                if ( memberInfo.get(position).getPriority() == Constant.PRIORITY_APPLICANT ) {
                    rejectApplicant(memberInfo.get(position));
                } else if ( memberInfo.get(position).getPriority() == Constant.PRIORITY_REGULAR ) {
                    kickMember(memberInfo.get(position));
                } else if ( memberInfo.get(position).getPriority() == Constant.PRIORITY_CO_CAP ) {
                    demoteMember(memberInfo.get(position));
                }
            } else if ( v.getId() == R.id.iv_positive ) {
                if ( memberInfo.get(position).getPriority() == Constant.PRIORITY_APPLICANT ) {
                    acceptApplicant(memberInfo.get(position));
                } else if ( memberInfo.get(position).getPriority() == Constant.PRIORITY_REGULAR ) {
                    promoteMember(memberInfo.get(position));
                }
            }
        }

        /**
         *  change player's selfPriority
         * @param member the member whose selfPriority is to be changed
         * @param isPromotion true if it's increasing the selfPriority
         */
        private void manageMember(final Member member, boolean isPromotion){
            RequestAction action = new RequestAction() {
                @Override
                public void actOnPre() {
                }

                @Override
                public void actOnPost(int responseCode, String response) {
                    if ( responseCode == 200 ) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            try {
                                JSONObject jsonMember = jsonObject.getJSONObject(Constant.TABLE_MEMBER);
                                int priority = jsonMember.getInt(Constant.MEMBER_PRIORITY);
                                int previousPriority = member.getPriority();
                                member.setPriority(priority);
                                if ( priority > previousPriority ) {
                                    if ( previousPriority == 0 ) {
                                        Toast.makeText(context, playerList.get(position).getDisplayName()
                                                + " has been accepted to the club!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, playerList.get(position).getDisplayName()
                                                + " has been promoted!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, playerList.get(position).getDisplayName()
                                            + " has been demoted!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                // do nothing
                                playerList.remove(position);
                                memberInfo.remove(position);
                            }
                            try {
                                String message = jsonObject.getString(Constant.KEY_MSG);
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                // do nothing
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            String message = new JSONObject(response).getString(Constant.KEY_MSG);
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                    notifyDataSetChanged();
                }
            };
            String url = UrlHelper.urlMemberManagement(member.getClubID(),member.getPlayerID(),isPromotion);
            RequestHelper.sendPostRequest(url,null,action);
        }

        private void rejectApplicant(final Member member){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(resources.getString(R.string.notice));
            builder.setMessage(resources.getString(R.string.prompt_reject));
            builder.setPositiveButton(resources.getString(R.string.label_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    manageMember(member,false);
                }
            });
            builder.setNegativeButton(resources.getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
        private void acceptApplicant(final Member member){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(resources.getString(R.string.notice));
            builder.setMessage(resources.getString(R.string.prompt_accept));
            builder.setPositiveButton(resources.getString(R.string.label_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    manageMember(member,true);
                }
            });
            builder.setNegativeButton(resources.getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
        private void promoteMember(final Member member){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(resources.getString(R.string.notice));
            builder.setMessage(resources.getString(R.string.prompt_promote));
            builder.setPositiveButton(resources.getString(R.string.label_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    manageMember(member,true);
                }
            });
            builder.setNegativeButton(resources.getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
        private void demoteMember(final Member member){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(resources.getString(R.string.notice));
            builder.setMessage(resources.getString(R.string.prompt_demote));
            builder.setPositiveButton(resources.getString(R.string.label_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    manageMember(member,false);
                }
            });
            builder.setNegativeButton(resources.getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
        private void kickMember(final Member member){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(resources.getString(R.string.notice));
            builder.setMessage(resources.getString(R.string.prompt_kick));
            builder.setPositiveButton(resources.getString(R.string.label_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    manageMember(member,false);
                }
            });
            builder.setNegativeButton(resources.getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
    }
}
