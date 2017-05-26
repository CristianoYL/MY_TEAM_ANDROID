package com.example.cristiano.myteam.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.adapter.SquadListAdapter;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.structure.Squad;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Cristiano on 2017/4/17.
 */

public class ClubSquadFragment extends Fragment {
    private int clubID, tournamentID;
    private ArrayList<Squad> squadList;
    private boolean isEditingSquad;

    private View rootView;
    private Button btn_editPlayer;
    private FloatingActionButton fab_delete, fab_add;

    public ClubSquadFragment() {
        // Required empty public constructor
    }

    public static ClubSquadFragment newInstance(int tournamentID, int clubID) {
        ClubSquadFragment fragment = new ClubSquadFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.KEY_CLUB_ID,clubID);
        bundle.putInt(Constant.KEY_TOURNAMENT_ID,tournamentID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            clubID = bundle.getInt(Constant.KEY_CLUB_ID);
            tournamentID = bundle.getInt(Constant.KEY_TOURNAMENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_squad, container, false);
        getResult();
        return rootView;
    }

    private void getResult(){
        RequestAction actionGetTournamentClubSquad = new RequestAction() {
            @Override
            public void actOnPre() {
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.TABLE_SQUAD);
                        int number;
                        String name, role;
                        squadList = new ArrayList<>(jsonArray.length());
                        for ( int i = 0; i < jsonArray.length(); i++ ) {
                            JSONObject jsonSquad = jsonArray.getJSONObject(i);
                            int playerID = jsonSquad.getInt(Constant.SQUAD_PLAYER_ID);
                            number = jsonSquad.getInt(Constant.SQUAD_NUMBER);
                            name = jsonSquad.getString(Constant.PLAYER_DISPLAY_NAME);
                            role = jsonSquad.getString(Constant.PLAYER_ROLE);
                            squadList.add(new Squad(tournamentID,clubID,playerID,name,role,number));
                        }
                        Collections.sort(squadList,sortBy("name"));
                        showSquad();
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
        String url = UrlHelper.urlGetTournamentClubSquad(tournamentID,clubID);
        RequestHelper.sendGetRequest(url,actionGetTournamentClubSquad);
    }

    private void showSquad(){
        isEditingSquad = false;

        ListView lv_squad = (ListView) rootView.findViewById(R.id.lv_squad);
        btn_editPlayer = (Button) rootView.findViewById(R.id.btn_editSquad);
        fab_add = (FloatingActionButton) rootView.findViewById(R.id.fab_addPlayer);
        fab_delete = (FloatingActionButton) rootView.findViewById(R.id.fab_deletePlayer);

        SquadListAdapter squadListAdapter = new SquadListAdapter(getActivity(),R.layout.layout_card_squad,squadList);
        lv_squad.setAdapter(squadListAdapter);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: popup add player dialog
                // addPlayer(4,20);
            }
        });

        btn_editPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( isEditingSquad ) {
                    fab_add.setVisibility(View.GONE);
                    fab_delete.setVisibility(View.GONE);
                    btn_editPlayer.setText(R.string.btn_edit_squad);
                    isEditingSquad = false;
                } else {
                    fab_add.setVisibility(View.VISIBLE);
                    fab_delete.setVisibility(View.VISIBLE);
                    btn_editPlayer.setText(R.string.btn_cancel_edit_squad);
                    isEditingSquad = true;
                }
            }
        });

    }

    private Comparator<Squad> sortBy(final String key) {
        return new Comparator<Squad>() {
            @Override
            public int compare(Squad o1, Squad o2) {
                switch ( key ) {
                    case "name":
                        return o1.getName().charAt(0) - o2.getName().charAt(0);
                    case "number":
                        return o1.getNumber()-o2.getNumber();
                    case "role":
                        return o1.getRole().charAt(0) - o2.getRole().charAt(0);
                    default:
                        return o1.getName().charAt(0) - o2.getName().charAt(0);
                }
            }
        };

    }

    private void addPlayer(int playerID, int number) {
        Squad newSquad = new Squad(tournamentID,clubID,playerID,null,null,number);
        RequestAction actionPostSquad = new RequestAction() {
            @Override
            public void actOnPre() {

            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 201 ) {
                    Toast.makeText(getContext(),"Create new squad player.",Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString(Constant.KEY_MSG);
                        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(),response,Toast.LENGTH_SHORT).show();
                    }
                }

            }
        };
        String url = UrlHelper.urlPostSquad();
        RequestHelper.sendPostRequest(url,newSquad.toJson(),actionPostSquad);
    }
}
