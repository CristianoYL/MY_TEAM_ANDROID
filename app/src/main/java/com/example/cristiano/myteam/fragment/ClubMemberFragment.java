package com.example.cristiano.myteam.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.activity.PlayerActivity;
import com.example.cristiano.myteam.adapter.MemberListAdapter;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.structure.Member;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Cristiano on 2017/4/19.
 */

public class ClubMemberFragment extends Fragment{
    private ArrayList<Player> clubPlayers;
    private ArrayList<Member> memberInfo;
    private int clubID;
    private View view;
    private EditText et_firstName, et_lastName, et_displayName, et_age, et_phone, et_height, et_weight;
    private Button btn_addPlayer;
    private Switch sw_unit, sw_leftFooted;
    private Spinner sp_role, sp_position;
    private ArrayAdapter<String> roleAdapter,positionAdapter;

    public ClubMemberFragment(){
    }

    public static ClubMemberFragment newInstance(int clubID){
        ClubMemberFragment fragment = new ClubMemberFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.KEY_CLUB_ID,clubID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        clubID = bundle.getInt(Constant.KEY_CLUB_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_club_member, container, false);
        getTeamsheetPlayers();
        return view;
    }

    /**
     * send a GET request to retrieve the teamsheet info
     */
    private void getTeamsheetPlayers(){
        RequestAction actionGetTeamsheetPlayers = new RequestAction() {
            @Override
            public void actOnPre() {

            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonMemberList = jsonResponse.getJSONArray(Constant.TABLE_MEMBER);
                        clubPlayers = new ArrayList<>(jsonMemberList.length());
                        memberInfo = new ArrayList<>(jsonMemberList.length());
                        for ( int i = 0; i < jsonMemberList.length(); i++ ) {
                            JSONObject jsonObject = jsonMemberList.getJSONObject(i);
                            JSONObject jsonPlayer = jsonObject.getJSONObject(Constant.TABLE_PLAYER);
                            int playerID = jsonPlayer.getInt(Constant.PLAYER_ID);
                            String firstName = jsonPlayer.getString(Constant.PLAYER_FIRST_NAME);
                            String lastName = jsonPlayer.getString(Constant.PLAYER_LAST_NAME);
                            String displayName = jsonPlayer.getString(Constant.PLAYER_DISPLAY_NAME);
                            String email = jsonPlayer.getString(Constant.PLAYER_EMAIL);
                            int age = jsonPlayer.getInt(Constant.PLAYER_AGE);
                            int avatar = jsonPlayer.getInt(Constant.PLAYER_AVATAR);
                            float height = (float) jsonPlayer.getDouble(Constant.PLAYER_HEIGHT);
                            float weight = (float) jsonPlayer.getDouble(Constant.PLAYER_WEIGHT);
                            boolean leftFooted = jsonPlayer.getBoolean(Constant.PLAYER_FOOT);
                            String phone = jsonPlayer.getString(Constant.PLAYER_PHONE);
                            String role = jsonPlayer.getString(Constant.PLAYER_ROLE);
                            clubPlayers.add(new Player(playerID,email,firstName,lastName,displayName,role,phone,age,weight,height,leftFooted,avatar));
                            JSONObject jsonMember = jsonObject.getJSONObject(Constant.TABLE_MEMBER);
                            int clubID = jsonMember.getInt(Constant.MEMBER_C_ID);
                            int pID = jsonMember.getInt(Constant.MEMBER_P_ID);
                            String memberSince = jsonMember.getString(Constant.MEMBER_SINCE);
                            boolean isActive = jsonMember.getBoolean(Constant.MEMBER_IS_ACTIVE);
                            int priority = jsonMember.getInt(Constant.MEMBER_PRIORITY);
                            memberInfo.add(new Member(clubID,pID,memberSince,isActive,priority));
                        }
                        showTeamsheet();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString(Constant.KEY_MSG);
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        };
        String url = UrlHelper.urlGetClubMembers(clubID);
        RequestHelper.sendGetRequest(url,actionGetTeamsheetPlayers);
    }

    /**
     *  render the retrieved teamsheet info into the list view
     */
    private void showTeamsheet(){
        ListView lv_teamsheet = (ListView) view.findViewById(R.id.lv_teamsheet);
        btn_addPlayer = (Button) view.findViewById(R.id.btn_addPlayer);

        MemberListAdapter memberListAdapter = new MemberListAdapter(getContext(), clubPlayers,memberInfo);
        lv_teamsheet.setAdapter(memberListAdapter);

        // go to the player's profile page onClick() in visitor mode
        lv_teamsheet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), PlayerActivity.class);
                intent.putExtra(Constant.KEY_PLAYER_ID, clubPlayers.get(position).getId());
                intent.putExtra(Constant.KEY_IS_VISITOR,true);
                startActivity(intent);
            }
        });

        // create new player and add to the teamsheet
        btn_addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View dialogView = inflater.inflate(R.layout.layout_player_registration, null);

                et_firstName = (EditText) dialogView.findViewById(R.id.et_firstName);
                et_lastName = (EditText) dialogView.findViewById(R.id.et_lastName);
                et_displayName = (EditText) dialogView.findViewById(R.id.et_displayName);
                et_age = (EditText) dialogView.findViewById(R.id.et_age);
                et_height = (EditText) dialogView.findViewById(R.id.et_height);
                et_weight = (EditText) dialogView.findViewById(R.id.et_weight);
                et_phone = (EditText) dialogView.findViewById(R.id.et_phone);
                sw_unit = (Switch) dialogView.findViewById(R.id.sw_unit);
                sw_leftFooted = (Switch) dialogView.findViewById(R.id.sw_leftFooted);
                sp_role = (Spinner) dialogView.findViewById(R.id.sp_role);
                sp_position = (Spinner) dialogView.findViewById(R.id.sp_position);
                roleAdapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, Constant.roles);
                sp_role.setAdapter(roleAdapter);
                positionAdapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, Constant.positions);
                sp_role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if ( (sp_role.getItemAtPosition(i)).equals(Constant.ROLE_PLAYER)){
                            sp_position.setVisibility(View.VISIBLE);
                        } else {
                            sp_position.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                sp_position.setAdapter(positionAdapter);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add a player to your club");
                builder.setView(dialogView);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadPlayerInfo();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setCancelable(true);
                builder.show();
            }
        });
    }

    /**
     * send a POST request to upload the new player
     */
    private void uploadPlayerInfo(){
        int id = 0;
        String email = null;
        String firstName = et_firstName.getText().toString();
        String lastName = et_lastName.getText().toString();
        String displayName = et_displayName.getText().toString();
        // firstname and lastname cannot be empty
        if ( firstName.length() == 0 || lastName.length() == 0 ) {
            Toast.makeText(getContext(),R.string.empty_name_msg,Toast.LENGTH_SHORT).show();
            return;
        }
        // display name is full name by default
        if ( displayName.length() == 0 ) {
            displayName = firstName + " " + lastName;
        }
        String role = sp_role.getSelectedItem().toString();
        if ( role.equals(Constant.ROLE_PLAYER) ) {
            role =  sp_position.getSelectedItem().toString();
        }
        String phone = et_phone.getText().toString();
        int age = 0;
        if ( et_age.getText().length() > 0 ) {
            age = Integer.parseInt(et_age.getText().toString());
        }
        float weight = 0;
        if ( et_weight.getText().length() > 0 ) {
            weight = Float.parseFloat(et_weight.getText().toString());
        }
        float height = 0;
        if ( et_height.getText().length() > 0 ) {
            height = Float.parseFloat(et_height.getText().toString());
        }
        if ( sw_unit.isChecked() ) {
            weight = (weight / 1.6f);
            height = height * 30.3f;
        }
        boolean leftFooted = sw_leftFooted.isChecked();
        int avatar = 0;

        Player player = new Player(id,email,firstName,lastName,displayName,role,phone,age,weight,height,leftFooted,avatar);
        RequestAction actionPostRegPlayer = new RequestAction() {
            @Override
            public void actOnPre() {

            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 201 ) {    // player created
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonPlayer = jsonObject.getJSONObject(Constant.TABLE_PLAYER);
                        int playerID = jsonPlayer.getInt(Constant.PLAYER_ID);
                        String email = null;
                        String firstName = jsonPlayer.getString(Constant.PLAYER_FIRST_NAME);
                        String lastName = jsonPlayer.getString(Constant.PLAYER_LAST_NAME);
                        String displayName = jsonPlayer.getString(Constant.PLAYER_DISPLAY_NAME);
                        String phone = jsonPlayer.getString(Constant.PLAYER_PHONE);
                        String role = jsonPlayer.getString(Constant.PLAYER_ROLE);
                        int avatar = jsonPlayer.getInt(Constant.PLAYER_AVATAR);
                        int age = jsonPlayer.getInt(Constant.PLAYER_AGE);
                        float height = (float) jsonPlayer.getDouble(Constant.PLAYER_HEIGHT);
                        float weight = (float) jsonPlayer.getDouble(Constant.PLAYER_WEIGHT);
                        boolean leftFooted = jsonPlayer.getBoolean(Constant.PLAYER_FOOT);
                        Player newPlayer = new Player(playerID,email,firstName,lastName,displayName,role,phone,age,weight,height,leftFooted,avatar);
                        clubPlayers.add(newPlayer);
                        JSONObject jsonMember = jsonObject.getJSONObject(Constant.TABLE_MEMBER);
                        int clubID = jsonMember.getInt(Constant.MEMBER_C_ID);
                        String memberSince =  jsonMember.getString(Constant.MEMBER_SINCE);
                        boolean isActive =  jsonMember.getBoolean(Constant.MEMBER_IS_ACTIVE);
                        int priority =  jsonMember.getInt(Constant.MEMBER_PRIORITY);
                        Member newMember = new Member(clubID,playerID,memberSince,isActive,priority);
                        memberInfo.add(newMember);
                        showTeamsheet();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(),response,Toast.LENGTH_SHORT).show();
                    }
                } else {    // unknown error
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");
                        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        String url = UrlHelper.urlPostRegPlayer(clubID);
        RequestHelper.sendPostRequest(url, player.toJson() ,actionPostRegPlayer);
    }
}
