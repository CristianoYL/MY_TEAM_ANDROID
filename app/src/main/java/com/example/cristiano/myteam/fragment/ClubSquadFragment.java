package com.example.cristiano.myteam.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.adapter.SquadListAdapter;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.structure.Squad;
import com.example.cristiano.myteam.structure.PlayerIDList;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Cristiano on 2017/4/17.
 */

public class ClubSquadFragment extends Fragment {
    private int clubID, tournamentID;
    private ArrayList<Squad> squadList;
    private boolean isEditingSquad;
    private HashMap<String,Integer> playerIDMap;
    private ArrayList<String> playerList;

    private View rootView;
    private Button btn_editPlayer;
    private TextView tv_name, tv_availableCount, tv_totalCount;
    private ListView lv_members, lv_squad;
    private TextInputEditText et_number;
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
        getSquad();
        return rootView;
    }

    private void getSquad(){
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

        lv_squad = (ListView) rootView.findViewById(R.id.lv_squad);
        btn_editPlayer = (Button) rootView.findViewById(R.id.btn_editSquad);
        fab_add = (FloatingActionButton) rootView.findViewById(R.id.fab_addPlayer);
        fab_delete = (FloatingActionButton) rootView.findViewById(R.id.fab_deletePlayer);

        SquadListAdapter squadListAdapter = new SquadListAdapter(getActivity(),R.layout.layout_card_squad,squadList);
        lv_squad.setAdapter(squadListAdapter);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: popup add player dialog
                showAddSquadDialog();
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
                    lv_squad.setOnItemClickListener(null);
                } else {
                    fab_add.setVisibility(View.VISIBLE);
                    fab_delete.setVisibility(View.VISIBLE);
                    btn_editPlayer.setText(R.string.btn_cancel_edit_squad);
                    isEditingSquad = true;
                    lv_squad.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            showEditSquadDialog((Squad) parent.getItemAtPosition(position));
                        }
                    });
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

    /**
     *  show a pop-up dialog
     *  let the user choose from a list of club members to join the tournament squad
     */
    private void showAddSquadDialog(){
        RequestAction actionGetTeamsheet = new RequestAction() {
            @Override
            public void actOnPre() {

            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray(Constant.TABLE_TEAMSHEET);
                        playerList = new ArrayList<>(jsonArray.length());
                        playerIDMap = new HashMap<>(jsonArray.length());
                        for ( int i = 0; i < jsonArray.length(); i++ ) {
                            JSONObject jsonTeamsheet = jsonArray.getJSONObject(i);
                            int playerID = jsonTeamsheet.getInt(Constant.PLAYER_ID);
                            String displayName = jsonTeamsheet.getString(Constant.PLAYER_DISPLAY_NAME);
                            playerList.add(displayName);
                            playerIDMap.put(displayName,playerID);
                        }
                        LayoutInflater inflater = LayoutInflater.from(getContext());
                        View dialogView = inflater.inflate(R.layout.layout_dialog_member_list,null);
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                        dialogBuilder.setTitle("Add players to the squad");
                        dialogBuilder.setView(dialogView);
                        tv_availableCount = (TextView) dialogView.findViewById(R.id.tv_availableCount);
                        tv_totalCount = (TextView) dialogView.findViewById(R.id.tv_totalCount);
                        lv_members = (ListView) dialogView.findViewById(R.id.lv_members);

                        lv_members.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        int totalCount = jsonArray.length();
                        int availableCount = totalCount;
                        for ( Squad squad : squadList ) {
                            String playerName = squad.getName();
                            if ( playerList.contains(playerName) ) {
                                playerList.remove(playerName);
                                availableCount--;
                            }
                        }
                        tv_totalCount.setText(totalCount+"");
                        tv_availableCount.setText(availableCount+"");
                        lv_members.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_multiple_choice,playerList));
                        dialogBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PlayerIDList playerIDList = new PlayerIDList();
                                for ( int i = 0; i < playerList.size(); i++ ) {
                                    if ( lv_members.isItemChecked(i) ) {
                                        String displayName = lv_members.getItemAtPosition(i).toString();
                                        int playerID = playerIDMap.get(displayName);
                                        playerIDList.add(playerID);
                                    }
                                }
                                addSquadPlayers(playerIDList);
                            }
                        });
                        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dialogBuilder.setCancelable(true);
                        dialogBuilder.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(),"Error when loading teamsheet!",Toast.LENGTH_SHORT).show();
                    }
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
        String url = UrlHelper.urlGetClubTeamsheet(clubID);
        RequestHelper.sendGetRequest(url,actionGetTeamsheet);
    }

    /**
     * send a post request to add player from teamsheet to squad
     * @param playerIDList  a list of playerID to be added to the squad
     */
    private void addSquadPlayers(PlayerIDList playerIDList) {
        if ( playerIDList.getPlayerID().size() <= 0 ) { // do not send empty-body request
            return;
        }
        RequestAction actionAddToSquad = new RequestAction() {
            @Override
            public void actOnPre() {

            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 201 ) {
                    Toast.makeText(getContext(),"Create new squad players.",Toast.LENGTH_SHORT).show();
                    getSquad();
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
        String url = UrlHelper.urlPostTournamentSquad(tournamentID,clubID);
        RequestHelper.sendPostRequest(url, playerIDList.toJson(),actionAddToSquad);
    }

    /**
     *  show a dialog to edit the squad player
     * @param squad the squad player to be edited
     */
    private void showEditSquadDialog(final Squad squad){
        final int previousNumber = squad.getNumber();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.layout_edit_squad,null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle("Edit squad");
        dialogBuilder.setView(dialogView);
        tv_name = (TextView) dialogView.findViewById(R.id.tv_name);
        et_number = (TextInputEditText) dialogView.findViewById(R.id.et_number);
        tv_name.setText(squad.getName());
        et_number.setText(previousNumber+"");
        dialogBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int newNumber = Integer.parseInt(et_number.getText().toString());
                if ( newNumber != previousNumber ) {
                    updateSquadNumber(squad,newNumber);
                }
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogBuilder.setCancelable(true);
        dialogBuilder.show();

    }

    /**
     * send a PUT request to update the player's kit number
     * @param squad contains the player's according squad info
     * @param newNumber the new kit number
     */
    private void updateSquadNumber(Squad squad, int newNumber){
        RequestAction actionUpdateNumber = new RequestAction() {
            @Override
            public void actOnPre() {

            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    Toast.makeText(getContext(),"Player kit number updated!",Toast.LENGTH_SHORT).show();
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
        squad.setNumber(newNumber);
        String url = UrlHelper.urlPutSquad();
        RequestHelper.sendPutRequest(url,squad.toJson(),actionUpdateNumber);
    }

}
