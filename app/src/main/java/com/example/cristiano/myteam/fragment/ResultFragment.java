package com.example.cristiano.myteam.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.structure.Event;
import com.example.cristiano.myteam.structure.Result;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.adapter.ResultListAdapter;
import com.example.cristiano.myteam.util.UrlHelper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultFragment extends Fragment {

    private int clubID, tournamentID;
    private Result[] results;

    public ResultFragment() {
        // Required empty public constructor
    }

    public static ResultFragment newInstance(int clubID, int tournamentID) {
        ResultFragment fragment = new ResultFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.CLUB_ID,clubID);
        bundle.putInt(Constant.TOURNAMENT_ID,tournamentID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            clubID = bundle.getInt(Constant.CLUB_ID);
            tournamentID = bundle.getInt(Constant.TOURNAMENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        getResult(view);
        return view;
    }

    private void getResult(final View view){
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
                        ArrayList<String> homeEvents = new ArrayList<>();
                        ArrayList<String> awayEvents = new ArrayList<>();
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
//                            JSONArray eventArray = jsonArray.getJSONObject(i).getJSONArray(Constant.RESULT_HOME_EVENTS);
//                            try {
//                                for ( int j = 0; j < eventArray.length(); j++ ) {
//                                    eventType = eventArray.getJSONObject(j).getString(Constant.EVENT_TYPE);
//                                    eventPlayer = eventArray.getJSONObject(j).getString(Constant.EVENT_PLAYER);
//                                    eventTime = eventArray.getJSONObject(j).getString(Constant.EVENT_TIME);
//                                    results[i].addEvent(eventType,eventPlayer,eventTime,true);
//                                }
//                                eventArray = jsonArray.getJSONObject(i).getJSONArray(Constant.RESULT_AWAY_EVENTS);
//                                for ( int j = 0; j < eventArray.length(); j++ ) {
//                                    eventType = eventArray.getJSONObject(j).getString(Constant.EVENT_TYPE);
//                                    eventPlayer = eventArray.getJSONObject(j).getString(Constant.EVENT_PLAYER);
//                                    eventTime = eventArray.getJSONObject(j).getString(Constant.EVENT_TIME);
//                                    results[i].addEvent(eventType,eventPlayer,eventTime,false);
//                                }
//                            } catch (Exception e) {
//                                Log.e("CLUB_RESULT","Error when parsing club results");
//                            }
                        }
                        showResults(view);

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
        String url = UrlHelper.urlGetClubResults(clubID);
        RequestHelper.sendGetRequest(url,actionGetClubResults);
    }

    private void showResults(View view){
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
            LayoutInflater inflater = LayoutInflater.from(getActivity());
//            View view = inflater.inflate(R.layout.layout_card_result,null);
            resultMap.put(Constant.RESULT_KEY_HOME_EVENT, getEventListAdapter(results[i],true));
            // awayID events
            resultMap.put(Constant.RESULT_KEY_AWAY_EVENT, getEventListAdapter(results[i],false));
            resultItems.add(resultMap);
        }
        ResultListAdapter resultListAdapter = new ResultListAdapter(getActivity(),R.layout.layout_card_result,resultItems);
        lv_result.setAdapter(resultListAdapter);
    }

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
        return new SimpleAdapter(getActivity(), eventListItems, R.layout.layout_event_detail,
                new String[]{Constant.EVENT_TYPE, Constant.EVENT_PLAYER, Constant.EVENT_TIME},
                new int[]{R.id.iv_eventIcon, R.id.tv_eventPlayer, R.id.tv_eventTime});
    }
}
