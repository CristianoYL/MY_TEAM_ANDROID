package com.example.cristiano.myteam.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.structure.Event;
import com.example.cristiano.myteam.structure.GameResultAndStats;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.structure.Result;
import com.example.cristiano.myteam.structure.Squad;
import com.example.cristiano.myteam.structure.Stats;
import com.example.cristiano.myteam.structure.Tournament;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.adapter.ResultListAdapter;
import com.example.cristiano.myteam.util.UrlHelper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * this fragment show the results of the club in the given tournament
 */
public class ClubResultFragment extends Fragment {
    private Club club;
    private Tournament tournament;
    private Result[] results;
    private Result newResult;
    private Stats newStats[];
    private Squad squads[];
    private String players[] = {};
    private String opponentName;
    private int myScore, opponentScore, myFTScore,opponentFTScore, myPKScore, opponentPKScore;

    private TextView tv_home, tv_away, tv_homeScore, tv_awayScore, tv_homePKScore, tv_awayPKScore;
    private TextInputEditText et_opponentName, et_date;
    private EditText et_specificTime;
    private Button btn_setOpponent;
    private Spinner sp_eventType, sp_eventHalf,sp_eventPlayer,sp_subOffPlayer;
    private Switch sw_myClubEvent, sw_specificTime, sw_isAwayGame;
    private ListView lv_home, lv_away;
    private RadioGroup rg_goalPart, rg_goalMethod;
    private View goalDetailView, specificTimeView, subPlayerView;
    View view;

    public ClubResultFragment() {
        // Required empty public constructor
    }

    public static ClubResultFragment newInstance(Tournament tournament, Club club) {
        ClubResultFragment fragment = new ClubResultFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.TABLE_CLUB,club.toJson());
        bundle.putString(Constant.TABLE_TOURNAMENT,tournament.toJson());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            club = new Gson().fromJson(bundle.getString(Constant.TABLE_CLUB),Club.class);
            tournament = new Gson().fromJson(bundle.getString(Constant.TABLE_TOURNAMENT),Tournament.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_result, container, false);
        getResult();
        return view;
    }

    /**
     * send a GET request to retrieve the club's tournament results
     */
    private void getResult(){
        RequestAction actionGetClubResults = new RequestAction() {
            @Override
            public void actOnPre() {
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.RESULT_LIST);
                        results = new Result[jsonArray.length()];
                        int id, home, away, tournamentID;
                        String homeName, awayName, tournamentName, date, stage, ftScore, extraScore,
                                penScore, info, eventType,eventPlayer,eventTime;
                        for ( int i = 0; i < jsonArray.length(); i++ ) {
                            id = jsonArray.getJSONObject(i).getInt(Constant.RESULT_ID);
                            home = jsonArray.getJSONObject(i).getInt(Constant.RESULT_HOME_ID);
                            away = jsonArray.getJSONObject(i).getInt(Constant.RESULT_AWAY_ID);
                            tournamentID = jsonArray.getJSONObject(i).getInt(Constant.RESULT_TOURNAMENT_ID);
                            homeName = jsonArray.getJSONObject(i).getString(Constant.RESULT_HOME_NAME);
                            awayName = jsonArray.getJSONObject(i).getString(Constant.RESULT_AWAY_NAME);
                            tournamentName = jsonArray.getJSONObject(i).getString(Constant.RESULT_TOURNAMENT_NAME);
                            date = jsonArray.getJSONObject(i).getString(Constant.RESULT_DATE);
                            stage = jsonArray.getJSONObject(i).getString(Constant.RESULT_STAGE);
                            ftScore = jsonArray.getJSONObject(i).getString(Constant.RESULT_FT_SCORE);
                            extraScore = jsonArray.getJSONObject(i).getString(Constant.RESULT_EXTRA_SCORE);
                            penScore = jsonArray.getJSONObject(i).getString(Constant.RESULT_PEN_SCORE);
                            info = jsonArray.getJSONObject(i).getString(Constant.RESULT_INFO);
                            results[i] = new Result(id,home,away,tournamentID,homeName,awayName,
                                    tournamentName,date,stage,ftScore,extraScore,penScore,info);
                            JSONArray eventArray;
                            try {
                                eventArray = jsonArray.getJSONObject(i).getJSONArray(Constant.RESULT_HOME_EVENTS);
                                for ( int j = 0; j < eventArray.length(); j++ ) {
                                    eventType = eventArray.getJSONObject(j).getString(Constant.EVENT_TYPE);
                                    eventPlayer = eventArray.getJSONObject(j).getString(Constant.EVENT_PLAYER);
                                    eventTime = eventArray.getJSONObject(j).getString(Constant.EVENT_TIME);
                                    results[i].addEvent(eventType,eventPlayer,eventTime,true);
                                }
                            } catch (Exception e) {
                                Log.d("CLUB_RESULT","Error when parsing home results");
                            }
                            try {
                                eventArray = jsonArray.getJSONObject(i).getJSONArray(Constant.RESULT_AWAY_EVENTS);
                                for ( int j = 0; j < eventArray.length(); j++ ) {
                                    eventType = eventArray.getJSONObject(j).getString(Constant.EVENT_TYPE);
                                    eventPlayer = eventArray.getJSONObject(j).getString(Constant.EVENT_PLAYER);
                                    eventTime = eventArray.getJSONObject(j).getString(Constant.EVENT_TIME);
                                    results[i].addEvent(eventType,eventPlayer,eventTime,false);
                                }
                            } catch (Exception e) {
                                Log.d("CLUB_RESULT","Error when parsing away results");
                            }
                        }
                        showResults();

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
        String url = UrlHelper.urlGetClubTournamentResults(tournament.id,club.id);
        RequestHelper.sendGetRequest(url,actionGetClubResults);
    }

    /**
     * fill the ListView with the club's tournament results
     */
    private void showResults(){
        ListView lv_result = (ListView) view.findViewById(R.id.lv_result);
        ArrayList<HashMap<String,Object>> resultItems = new ArrayList<>();
        for ( int i = 0; i < results.length; i++ ) {
            HashMap<String,Object> resultMap = new HashMap<>();
            resultMap.put(Constant.RESULT_KEY_TOURNAMENT,results[i].tournamentName);
            resultMap.put(Constant.RESULT_KEY_HOME,results[i].homeName);
            resultMap.put(Constant.RESULT_KEY_AWAY,results[i].awayName);
            resultMap.put(Constant.RESULT_KEY_SCORE,results[i].ftScore);
            if ( results[i].extraScore != null && !results[i].extraScore.equals("") && !results[i].extraScore.equals("null") ) {
                resultMap.put(Constant.RESULT_KEY_SCORE,results[i].extraScore);
            }
            resultMap.put(Constant.RESULT_KEY_PEN,"");
            if ( results[i].penScore != null && results[i].penScore != "" && results[i].penScore != "null" ) {
                resultMap.put(Constant.RESULT_KEY_PEN,results[i].penScore);
            }
            // initialize homeID event list for this game result[i]
            resultMap.put(Constant.RESULT_KEY_HOME_EVENT, getEventListAdapter(results[i],true));
            // awayID events
            resultMap.put(Constant.RESULT_KEY_AWAY_EVENT, getEventListAdapter(results[i],false));
            resultItems.add(resultMap);
        }
        ResultListAdapter resultListAdapter = new ResultListAdapter(getActivity(),R.layout.layout_card_result,resultItems);
        lv_result.setAdapter(resultListAdapter);

        Button btn_addResult = (Button) view.findViewById(R.id.btn_addResult);
        btn_addResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddResultDialog();
            }
        });
    }

    /**
     * set the game event icons accordingly
     * @param eventMap  the map that contains the event info
     * @param eventType the type of the game event, e.g. goal, yellow card...
     */
    private void setEventIcon(HashMap<String,Object> eventMap, String eventType) {
        switch ( eventType ) {
            case Constant.EVENT_TYPE_GOAL:
                eventMap.put(Constant.EVENT_TYPE,R.drawable.ic_goal);
                break;
            case Constant.EVENT_TYPE_OG:
                eventMap.put(Constant.EVENT_TYPE,R.drawable.ic_og);
                break;
            case Constant.EVENT_TYPE_YELLOW:
                eventMap.put(Constant.EVENT_TYPE,R.drawable.ic_yellow);
                break;
            case Constant.EVENT_TYPE_SECOND_YELLOW:
                eventMap.put(Constant.EVENT_TYPE,R.drawable.ic_yellow_red);
                break;
            case Constant.EVENT_TYPE_RED:
                eventMap.put(Constant.EVENT_TYPE,R.drawable.ic_red);
                break;
            case Constant.EVENT_TYPE_SUB_ON:
                eventMap.put(Constant.EVENT_TYPE,R.drawable.ic_sub_on);
                break;
            case Constant.EVENT_TYPE_SUB_OFF:
                eventMap.put(Constant.EVENT_TYPE,R.drawable.ic_sub_off);
                break;
            default:
                break;
        }
    }

    /**
     * @param result  the Result structure that contains the full info of a game
     * @param isHome  if this adapter is rendering the home event ListView
     * @return  a customized adapter that tells the ListView how to display the events
     */
    private SimpleAdapter getEventListAdapter(Result result, boolean isHome){
        ArrayList<HashMap<String,Object>> eventListItems = new ArrayList<>();
        Gson gson = new Gson();
        ArrayList<String> eventList;
        if ( isHome ) {
            eventList = result.homeEvents;
        } else {
            eventList = result.awayEvents;
        }
        for ( int k = 0; k < eventList.size(); k++ ) {
            HashMap<String,Object> eventMap = new HashMap<>(3);
            Event event = gson.fromJson(eventList.get(k),Event.class);
            setEventIcon(eventMap,event.type);
            eventMap.put(Constant.EVENT_PLAYER,event.player);
            eventMap.put(Constant.EVENT_TIME,event.time);
            eventListItems.add(eventMap);
        }
        return new SimpleAdapter(getContext(), eventListItems, R.layout.layout_event_detail,
                new String[]{Constant.EVENT_TYPE, Constant.EVENT_PLAYER, Constant.EVENT_TIME},
                new int[]{R.id.iv_eventIcon, R.id.tv_eventPlayer, R.id.tv_eventTime});
    }

    /**
     * show a pop-up dialog that let the user upload a new game result
     */
    private void showAddResultDialog() {
        myScore = 0;
        opponentScore = 0;
        myFTScore = 0;
        opponentFTScore = 0;
        myPKScore = 0;
        opponentPKScore = 0;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.layout_record_match,null);

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        newResult = new Result(0,0,0,tournament.id,club.name,opponentName,tournament.name,date,null,null,null,null,null);

        FloatingActionButton fab_addEvent = (FloatingActionButton) dialogView.findViewById(R.id.fab_addEvent);
        fab_addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEventDialog();
            }
        });

        tv_home = (TextView) dialogView.findViewById(R.id.tv_homeName);
        tv_home.setText(club.name);
        tv_away = (TextView) dialogView.findViewById(R.id.tv_awayName);
        opponentName = "Opponent";
        tv_away.setText(opponentName);

        tv_homeScore = (TextView) dialogView.findViewById(R.id.tv_homeScore);
        tv_awayScore = (TextView) dialogView.findViewById(R.id.tv_awayScore);
        tv_homePKScore = (TextView) dialogView.findViewById(R.id.tv_penHome);
        tv_awayPKScore = (TextView) dialogView.findViewById(R.id.tv_penAway);

        lv_home = (ListView) dialogView.findViewById(R.id.lv_home);
        lv_away = (ListView) dialogView.findViewById(R.id.lv_away);

        btn_setOpponent = (Button) dialogView.findViewById(R.id.btn_setOpponent);
        et_opponentName = (TextInputEditText) dialogView.findViewById(R.id.et_opponentName);
        et_date = (TextInputEditText) dialogView.findViewById(R.id.et_date);
        et_date.setText(date);


        btn_setOpponent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opponentName = et_opponentName.getText().toString();
                if ( sw_isAwayGame.isChecked() ) {
                    tv_home.setText(opponentName);
                } else {
                    tv_away.setText(opponentName);
                }

            }
        });

        sw_isAwayGame = (Switch) dialogView.findViewById(R.id.sw_homeAway);
        sw_isAwayGame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked ) {
                    tv_home.setText(opponentName);
                    tv_away.setText(club.name);
                } else {
                    tv_home.setText(club.name);
                    tv_away.setText(opponentName);
                }
                updateScore();
                ArrayList<String> temp = new ArrayList<String>(newResult.homeEvents);
                newResult.homeEvents = newResult.awayEvents;
                newResult.awayEvents = temp;
                showEventList(true);
                showEventList(false);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        builder.setTitle("Upload a new game result.");
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ( sw_isAwayGame.isChecked() ) {
                    newResult.awayID = club.id;
                    newResult.awayName = club.name;
                    newResult.homeName = opponentName;
                } else {
                    newResult.homeID = club.id;
                    newResult.homeName = club.name;
                    newResult.awayName = opponentName;
                }
                newResult.date = et_date.getText().toString();
                String ftScore;
                if ( sw_isAwayGame.isChecked() ) {
                    ftScore = opponentFTScore + ":" + myFTScore;
                } else {
                    ftScore = myFTScore + ":" + opponentFTScore;
                }
                newResult.ftScore = ftScore;
                GameResultAndStats gameResultAndStats = new GameResultAndStats(newResult,newStats);
                uploadResult(gameResultAndStats);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setCancelable(true);
        builder.show();

        loadTournamentClubSquad();
    }

    /**
     *  show a pop-up dialog that let the user select the game events
     */
    private void showEventDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View eventView = inflater.inflate(R.layout.layout_event_dialog,null);
        sp_eventType = (Spinner) eventView.findViewById(R.id.sp_eventType);
        sp_eventHalf = (Spinner) eventView.findViewById(R.id.sp_eventHalf);
        sp_eventPlayer = (Spinner) eventView.findViewById(R.id.sp_eventPlayer);
        sp_subOffPlayer = (Spinner) eventView.findViewById(R.id.sp_subOnPlayer);
        sw_myClubEvent = (Switch) eventView.findViewById(R.id.sw_myClubEvent);
        sw_specificTime = (Switch) eventView.findViewById(R.id.sw_specificTime);
        et_specificTime = (EditText) eventView.findViewById(R.id.et_time);
        goalDetailView = eventView.findViewById(R.id.layout_goalType);
        specificTimeView = eventView.findViewById(R.id.layout_specificTime);
        subPlayerView = eventView.findViewById(R.id.layout_sub);
        rg_goalMethod = (RadioGroup) eventView.findViewById(R.id.rg_goalMethod);
        rg_goalPart = (RadioGroup) eventView.findViewById(R.id.rg_part);

        sw_specificTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked ) {
                    specificTimeView.setVisibility(View.VISIBLE);
                } else {
                    specificTimeView.setVisibility(View.GONE);
                }
            }
        });

        String[] eventType = Constant.EVENT_TYPES;
        String[] eventHalf = Constant.EVENT_HALVES;

        sp_eventType.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,eventType));
        sp_eventHalf.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,eventHalf));
        sp_eventPlayer.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,players));
        sp_subOffPlayer.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,players));

        sw_myClubEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked ) {
                    sp_eventPlayer.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,players));
                    sp_subOffPlayer.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,players));
                } else {
                    String[] opponentPlayers = {"Unknown"};
                    sp_eventPlayer.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,opponentPlayers));
                    sp_subOffPlayer.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,opponentPlayers));
                }
            }
        });

        sp_eventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ( parent.getItemAtPosition(position).toString().equals(Constant.EVENT_TYPE_GOAL) ) {
                    goalDetailView.setVisibility(View.VISIBLE);
                    subPlayerView.setVisibility(View.GONE);
                } else if (parent.getItemAtPosition(position).toString().equals(Constant.EVENT_TYPE_SUB)){
                    subPlayerView.setVisibility(View.VISIBLE);
                    goalDetailView.setVisibility(View.GONE);
                } else {
                    goalDetailView.setVisibility(View.GONE);
                    subPlayerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(eventView);
        builder.setTitle("Event");
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean isHomeEvent;
                if ( (sw_isAwayGame.isChecked() && sw_myClubEvent.isChecked())||
                        (!sw_isAwayGame.isChecked() && !sw_myClubEvent.isChecked()) ) {
                    isHomeEvent = false;
                } else {
                    isHomeEvent = true;
                }

                String eventPlayer = sp_eventPlayer.getSelectedItem().toString();
                String eventTime = sp_eventHalf.getSelectedItem().toString();
                switch ( sp_eventHalf.getSelectedItem().toString() ) {
                    case Constant.EVENT_TIME_FIRST_HALF:
                        eventTime = "FH";
                        break;
                    case Constant.EVENT_TIME_SECOND_HALF:
                        eventTime = "SH";
                        break;
                    case Constant.EVENT_TIME_EXTRA_FIRST_HALF:
                        eventTime = "EFH";
                        break;
                    case Constant.EVENT_TIME_EXTRA_SECOND_HALF:
                        eventTime = "ESH";
                        break;
                }

                if ( sw_specificTime.isChecked() ) {
                    eventTime = eventTime + " " + et_specificTime.getText() + "'";
                }

                if ( sp_eventType.getSelectedItem().toString().equals(Constant.EVENT_TYPE_SUB) ) {
                    addEvent(Constant.EVENT_TYPE_SUB_OFF, eventPlayer,eventTime, isHomeEvent);
                    addEvent(Constant.EVENT_TYPE_SUB_ON, sp_eventPlayer.getSelectedItem().toString(),
                            eventTime, isHomeEvent);
                } else {
                    addEvent(sp_eventType.getSelectedItem().toString(),
                            eventPlayer, eventTime, isHomeEvent);
                }
                Toast.makeText(getContext(),"Add Event!",Toast.LENGTH_SHORT).show();
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

    /**
     *  Add events to the new game result
     * @param eventType goal,yellow,...
     * @param eventPlayer   who
     * @param eventTime when
     * @param isHomeEvent   home/away
     */
    private void addEvent(String eventType, String eventPlayer, String eventTime, boolean isHomeEvent) {
        String eventHalf = sp_eventHalf.getSelectedItem().toString();
        int index = sp_eventPlayer.getSelectedItemPosition();
        if ( sw_myClubEvent.isChecked() ) {
            switch ( eventType ) {
                case Constant.EVENT_TYPE_GOAL:
                    myScore++;
                    if ( eventHalf.equals(Constant.EVENT_TIME_FIRST_HALF) ||
                            eventHalf.equals(Constant.EVENT_TIME_SECOND_HALF) ) { // update ftScore if regular time goal
                        myFTScore++;
                    }
                    updateScore();
                    newStats[index].goal++;
                    switch ( rg_goalMethod.getCheckedRadioButtonId() ) {
                        case R.id.rb_freekick:
                            newStats[index].freekick++;
                            break;
                        case R.id.rb_penalty:
                            newStats[index].penalty++;
                            newStats[index].penaltyTaken++;
                            break;
                    }
                    switch ( rg_goalPart.getCheckedRadioButtonId() ) {
                        case R.id.rb_weakFoot:
                            newStats[index].weakFootGoal++;
                            break;
                        case R.id.rb_head:
                            newStats[index].header++;
                            break;
                        case R.id.rb_other:
                            newStats[index].otherGoal++;
                            break;
                    }
                    break;

                case Constant.EVENT_TYPE_YELLOW:
                    newStats[index].yellow++;
                    if ( newStats[index].yellow == 2 ) {
                        newStats[index].red++;
                        eventType = Constant.EVENT_TYPE_SECOND_YELLOW;
                    }
                    break;

                case Constant.EVENT_TYPE_RED:
                    newStats[index].red++;
                    break;

                case Constant.EVENT_TYPE_OG:
                    opponentScore++;
                    if ( eventHalf.equals(Constant.EVENT_TIME_FIRST_HALF) ||
                            eventHalf.equals(Constant.EVENT_TIME_SECOND_HALF) ) { // update ftScore if regular time OG
                        opponentFTScore++;
                    }
                    updateScore();
                    newStats[index].ownGoal++;
                    break;

                // TODO: create start and sub list, update start, attendance and appearance accordingly.
                case Constant.EVENT_TYPE_SUB_ON:
                    newStats[index].appearance = 1;
                    newStats[index].attendance = 1;
                    break;

                case Constant.EVENT_TYPE_SUB_OFF:
                    newStats[index].appearance = 1;
                    newStats[index].attendance = 1;
                    break;

            }
        } else {
            switch ( eventType ) {
                case Constant.EVENT_TYPE_GOAL:
                    opponentScore++;
                    if ( eventHalf.equals(Constant.EVENT_TIME_FIRST_HALF) ||
                            eventHalf.equals(Constant.EVENT_TIME_SECOND_HALF) ) { // update ftScore if regular time goal
                        opponentFTScore++;
                    }
                    updateScore();
                    break;

                case Constant.EVENT_TYPE_OG:
                    myScore++;
                    if ( eventHalf.equals(Constant.EVENT_TIME_FIRST_HALF) ||
                            eventHalf.equals(Constant.EVENT_TIME_SECOND_HALF) ) { // update ftScore if regular time own goal
                        myFTScore++;
                    }
                    updateScore();
                    break;
            }
        }
        newResult.addEvent(eventType,eventPlayer,eventTime,isHomeEvent);
        showEventList(isHomeEvent);
    }

    /**
     * render the event list
     * @param isHome if it's home event list
     */
    private void showEventList(boolean isHome){
        if ( isHome ) {
            lv_home.setAdapter(getEventListAdapter(newResult,isHome));
        } else {
            lv_away.setAdapter(getEventListAdapter(newResult,isHome));
        }
    }

    /**
     * update the result score
     */
    private void updateScore() {
        if ( sw_isAwayGame.isChecked() ) {
            tv_homeScore.setText(opponentScore+"");
            tv_awayScore.setText(myScore+"");
            tv_homePKScore.setText(opponentPKScore+"");
            tv_awayPKScore.setText(myPKScore+"");
        } else {
            tv_homeScore.setText(myScore+"");
            tv_awayScore.setText(opponentScore+"");
            tv_homePKScore.setText(myPKScore+"");
            tv_awayPKScore.setText(opponentPKScore+"");
        }
    }

    /**
     * send a GET request to retrieve the club's tournament squad
     */
    private void loadTournamentClubSquad(){
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
                        squads = new Squad[jsonArray.length()];
                        players = new String[jsonArray.length()];
                        newStats = new Stats[jsonArray.length()];
                        for ( int i = 0; i < jsonArray.length(); i++ ) {
                            JSONObject jsonSquad = jsonArray.getJSONObject(i);
                            int playerID = jsonSquad.getInt(Constant.SQUAD_PLAYER_ID);
                            number = jsonSquad.getInt(Constant.SQUAD_NUMBER);
                            name = jsonSquad.getString(Constant.PLAYER_DISPLAY_NAME);
                            role = jsonSquad.getString(Constant.PLAYER_ROLE);
                            squads[i] = new Squad(tournament.id,club.id,playerID,name,role,number);
                            players[i] = number + ". " + name;
                            newStats[i] = new Stats(tournament.id,club.id,playerID);
                        }

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
        String url = UrlHelper.urlGetTournamentClubSquad(tournament.id,club.id);
        RequestHelper.sendGetRequest(url,actionGetTournamentClubSquad);
    }

    /**
     * send a Post request to upload the new game result
     * @param gameResultAndStats  a data structure that contains the new result and player stats
     */
    private void uploadResult(final GameResultAndStats gameResultAndStats) {
        RequestAction actionPostResultAndStats = new RequestAction() {
            @Override
            public void actOnPre() {
                String toJson = gameResultAndStats.toJson();
                Log.d("RESULT_FRAGMENT","add result: " + toJson);
                JSONArray eventArray;
                try {
                    JSONObject json = new JSONObject(toJson);
                    eventArray = json.getJSONArray(Constant.RESULT_HOME_EVENTS);
                    for ( int j = 0; j < eventArray.length(); j++ ) {
                        String eventType = eventArray.getJSONObject(j).getString(Constant.EVENT_TYPE);
                        String eventPlayer = eventArray.getJSONObject(j).getString(Constant.EVENT_PLAYER);
                        String eventTime = eventArray.getJSONObject(j).getString(Constant.EVENT_TIME);
                        Log.d("EVENT_TYPE",eventType);
                        Log.d("EVENT_PLAYER",eventPlayer);
                        Log.d("EVENT_TIME",eventTime);
                    }
                } catch (Exception e) {
                    Log.d("CLUB_RESULT","Error when parsing home results");
                }
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 201 ) {
                    Toast.makeText(getContext(),"Result uploaded!",Toast.LENGTH_SHORT).show();
                    getResult();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_CLUB_TOURNAMENT_DETAIL);
                    transaction.detach(fragment);
                    transaction.attach(fragment);
                    transaction.commit();
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
        String url = UrlHelper.urlPostClubTournamentResults(tournament.id,club.id);
        RequestHelper.sendPostRequest(url,gameResultAndStats.toJson(),actionPostResultAndStats);
    }
}
