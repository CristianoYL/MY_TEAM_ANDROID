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
import com.example.cristiano.myteam.structure.Teamsheet;
import com.example.cristiano.myteam.structure.Tournament;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Cristiano on 2017/4/18.
 */

public class ClubProfileFragment extends Fragment {

    private int clubID, playerID;
    private ClubInfo clubInfo;
    private View view;

    public ClubProfileFragment() {
    }

    public static ClubProfileFragment newInstance(int clubID, int playerID){
        ClubProfileFragment fragment = new ClubProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.KEY_CLUB_ID,clubID);
        bundle.putInt(Constant.KEY_PLAYER_ID,playerID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            clubID = bundle.getInt(Constant.KEY_CLUB_ID);
            playerID = bundle.getInt(Constant.KEY_PLAYER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_club_profile, container, false);
        getProfile();
        return view;
    }

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
                        Tournament[] tournaments = new Tournament[tournamentList.length()];
                        for ( int i = 0; i < tournamentList.length(); i++ ) {
                            JSONObject jsonTournament = tournamentList.getJSONObject(i);
                            int tournamentID = jsonTournament.getInt(Constant.TOURNAMENT_ID);
                            String tournamentName = jsonTournament.getString(Constant.TOURNAMENT_NAME);
                            String tournamentInfo = jsonTournament.getString(Constant.TOURNAMENT_INFO);
                            tournaments[i] = new Tournament(tournamentID,tournamentName,tournamentInfo);
                        }
                        // get club's teamsheet
                        JSONArray teamsheetList = jsonClubInfo.getJSONArray(Constant.TABLE_TEAMSHEET);
                        Teamsheet[] teamsheet = new Teamsheet[teamsheetList.length()];
                        for ( int i = 0; i < teamsheetList.length(); i++ ) {
                            JSONObject jsonTeamsheet = teamsheetList.getJSONObject(i);
                            int playerID = jsonTeamsheet.getInt(Constant.TEAMSHEET_P_ID);
                            int clubID = jsonTeamsheet.getInt(Constant.TEAMSHEET_C_ID);
                            String memberSince = jsonTeamsheet.getString(Constant.TEAMSHEET_MEMBER_SINCE);
                            boolean isActive = jsonTeamsheet.getBoolean(Constant.TEAMSHEET_IS_ACTIVE);
                            boolean isAdmin = jsonTeamsheet.getBoolean(Constant.TEAMSHEET_IS_ADMIN);
                            teamsheet[i] = new Teamsheet(playerID,clubID,memberSince,isActive,isAdmin);
                        }
                        clubInfo = new ClubInfo(club,tournaments,teamsheet);
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
        String url = UrlHelper.urlGetClubInfo(clubID);
        RequestHelper.sendGetRequest(url,actionGetClubInfo);
    }

    private void showProfile() {
        TextView tv_clubName = (TextView) view.findViewById(R.id.tv_clubName);
        TextView tv_clubInfo = (TextView) view.findViewById(R.id.tv_clubInfo);
        TextView tv_totalPlayerCount = (TextView) view.findViewById(R.id.tv_totalCount);
        final TextView tv_activePlayerCount = (TextView) view.findViewById(R.id.tv_activeCount);
        TextView tv_tournamentCount = (TextView) view.findViewById(R.id.tv_tournamentCount);
        Button btn_viewTeamsheet = (Button) view.findViewById(R.id.btn_teamsheet);
        Button btn_viewTournaments = (Button) view.findViewById(R.id.btn_tournament);

        tv_clubName.setText(clubInfo.getClub().name);
        tv_clubInfo.setText(clubInfo.getClub().info);
        tv_totalPlayerCount.setText(clubInfo.getTeamsheet().length+"");
        int count = 0;
        for ( Teamsheet teamsheet : clubInfo.getTeamsheet() ) {
            if ( teamsheet.isActive() ) {
                count++;
            }
        }
        tv_activePlayerCount.setText(count+"");
        tv_tournamentCount.setText(clubInfo.getTournaments().length+"");

        btn_viewTournaments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TournamentListFragment fragment = TournamentListFragment.newInstance(clubInfo.getTournaments(),clubInfo.getClub(),playerID);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_content,fragment);
                transaction.commit();
            }
        });

        btn_viewTeamsheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamsheetFragment fragment = TeamsheetFragment.newInstance(clubID);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_content,fragment);
                transaction.commit();
            }
        });
    }
}
