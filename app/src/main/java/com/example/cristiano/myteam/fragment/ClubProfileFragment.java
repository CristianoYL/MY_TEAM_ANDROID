package com.example.cristiano.myteam.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.structure.ClubInfo;
import com.example.cristiano.myteam.structure.Member;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.structure.Tournament;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Cristiano on 2017/4/18.
 *
 * this fragment presents the club profile page,
 * and offer the user access to teamsheet page and tournament page
 */

public class ClubProfileFragment extends Fragment {

    private static final String ARG_CLUB = "club";
    private static final String ARG_PLAYER = "player";

    private Club club;
    private Player player;
    private ClubInfo clubInfo;

    private View view;

    public ClubProfileFragment() {
    }

    public static ClubProfileFragment newInstance(Club club, Player player){
        ClubProfileFragment fragment = new ClubProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_CLUB,club.toJson());
        bundle.putString(ARG_PLAYER,player.toJson());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            club = gson.fromJson(bundle.getString(ARG_CLUB),Club.class);
            player = gson.fromJson(bundle.getString(ARG_PLAYER),Player.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_club_profile, container, false);
        getProfile();
        return view;
    }

    /**
     * send a GET request to retrieve the club's basic info
     */
    private void getProfile(){
        RequestAction actionGetClubInfo = new RequestAction() {
            @Override
            public void actOnPre() {
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonClubInfo = jsonObject.getJSONObject(Constant.KEY_CLUB_INFO);
                        // get club
                        JSONObject jsonClub = jsonClubInfo.getJSONObject(Constant.TABLE_CLUB);
                        int id = jsonClub.getInt(Constant.CLUB_ID);
                        String name = jsonClub.getString(Constant.CLUB_NAME);
                        String info = jsonClub.getString(Constant.CLUB_INFO);
                        Club club = new Club(id,name,info);
                        // get club's tournaments
                        JSONArray tournamentList = jsonClubInfo.getJSONArray(Constant.TOURNAMENT_LIST);
                        List<Tournament> tournaments = new ArrayList<>(tournamentList.length());
                        for ( int i = 0; i < tournamentList.length(); i++ ) {
                            JSONObject jsonTournament = tournamentList.getJSONObject(i);
                            int tournamentID = jsonTournament.getInt(Constant.TOURNAMENT_ID);
                            String tournamentName = jsonTournament.getString(Constant.TOURNAMENT_NAME);
                            String tournamentInfo = jsonTournament.getString(Constant.TOURNAMENT_INFO);
                            tournaments.add(new Tournament(tournamentID,tournamentName,tournamentInfo));
                        }
                        // get club's member
                        JSONArray memberList = jsonClubInfo.getJSONArray(Constant.TABLE_MEMBER);
                        List<Member> member = new ArrayList<>(memberList.length());
                        for ( int i = 0; i < memberList.length(); i++ ) {
                            JSONObject jsonMember = memberList.getJSONObject(i);
                            int playerID = jsonMember.getInt(Constant.MEMBER_P_ID);
                            int clubID = jsonMember.getInt(Constant.MEMBER_C_ID);
                            String memberSince = jsonMember.getString(Constant.MEMBER_SINCE);
                            boolean isActive = jsonMember.getBoolean(Constant.MEMBER_IS_ACTIVE);
                            int priority = jsonMember.getInt(Constant.MEMBER_PRIORITY);
                            member.add(new Member(playerID,clubID,memberSince,isActive,priority));
                        }
                        clubInfo = new ClubInfo(club,tournaments, member);
                        showProfile();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Toast.makeText(getActivity(),jsonObject.getString(Constant.KEY_MSG),
                                Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(),response,Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        String url = UrlHelper.urlGetClubInfo(club.id);
        RequestHelper.sendGetRequest(url,actionGetClubInfo);
    }

    /**
     * after retrieving the club info, call this method to display it
     */
    private void showProfile() {
        TextView tv_clubName = (TextView) view.findViewById(R.id.tv_clubName);
        TextView tv_clubInfo = (TextView) view.findViewById(R.id.tv_clubInfo);
        TextView tv_totalPlayerCount = (TextView) view.findViewById(R.id.tv_totalCount);
        TextView tv_activePlayerCount = (TextView) view.findViewById(R.id.tv_activeCount);
        TextView tv_tournamentCount = (TextView) view.findViewById(R.id.tv_tournamentCount);
        Button btn_viewTeamsheet = (Button) view.findViewById(R.id.btn_teamsheet);
        Button btn_viewTournaments = (Button) view.findViewById(R.id.btn_tournament);

        getActivity().setTitle(clubInfo.getClub().name);

        tv_clubName.setText(clubInfo.getClub().name);
        tv_clubInfo.setText(clubInfo.getClub().info);
        tv_totalPlayerCount.setText(String.format(Locale.US,"%d",clubInfo.getMember().size()));
        int count = 0;
        for ( Member member : clubInfo.getMember() ) {
            if ( member.isActive() ) {
                count++;
            }
        }
        tv_activePlayerCount.setText(String.format(Locale.US,"%d",count));
        tv_tournamentCount.setText(String.format(Locale.US,"%d",clubInfo.getTournaments().size()));

        btn_viewTournaments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewTournamentList();
            }
        });

        btn_viewTeamsheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewTeamsheet();
            }
        });
    }

    /**
     * replace the current fragment with teamsheet fragment
     */
    private void viewTeamsheet() {
        ClubMemberFragment fragment = ClubMemberFragment.newInstance(club,player);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_content,fragment,Constant.FRAGMENT_CLUB_MEMBER);
        transaction.commit();
    }

    /**
     * view tournament list fragment
     */
    private void viewTournamentList() {
        TournamentListFragment fragment = TournamentListFragment.newInstance(clubInfo.getTournaments(),clubInfo.getClub(),player);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_content,fragment,Constant.FRAGMENT_CLUB_TOURNAMENT_LIST);
        transaction.commit();
    }
}
