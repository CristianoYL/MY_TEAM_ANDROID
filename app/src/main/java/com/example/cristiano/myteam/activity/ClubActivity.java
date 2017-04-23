package com.example.cristiano.myteam.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristiano.myteam.R;

import com.example.cristiano.myteam.fragment.ClubProfileFragment;
import com.example.cristiano.myteam.fragment.ClubStatsFragment;
import com.example.cristiano.myteam.fragment.ClubResultFragment;
import com.example.cristiano.myteam.fragment.ClubSquadFragment;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.structure.Event;
import com.example.cristiano.myteam.structure.Result;
import com.example.cristiano.myteam.structure.Tournament;
import com.example.cristiano.myteam.structure.TournamentRegistration;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.adapter.CustomFragmentAdapter;
import com.example.cristiano.myteam.adapter.ResultListAdapter;
import com.example.cristiano.myteam.adapter.TournamentListAdapter;
import com.example.cristiano.myteam.util.UrlHelper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ClubActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView lv_result, lv_event,lv_tournament;
    private LinearLayout layout_result;
    private ViewPager viewPager,viewPager_tournament;
    private TabLayout tab_clubStats,tab_tournament;
    private Button btn_addTournament;
    private View profilePage, tournamentListPage,tournamentPage;

    private Result[] results;
    private Tournament[] tournaments;
    private int clubID, playerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_club);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_club);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle bundle = getIntent().getExtras();
        if ( !bundle.containsKey(Constant.KEY_CLUB_ID) ||
                !bundle.containsKey(Constant.KEY_PLAYER_ID) ) {
            Log.e("ClubActivity","clubID/playerID not specified!");
            return;
        }
        this.clubID = bundle.getInt(Constant.KEY_CLUB_ID);
        this.playerID = bundle.getInt(Constant.KEY_PLAYER_ID);
        Log.d("CLUB_ID","="+this.clubID);
//        profilePage = findViewById(R.id.layout_clubProfile);
//        tournamentListPage = findViewById(R.id.layout_tournamentList);
//        tournamentPage = findViewById(R.id.layout_tournamentDetail);
        showProfilePage();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(Constant.FRAGMENT_CLUB_PROFILE);
            if ( fragment != null && fragment.isVisible() ) {
                super.onBackPressed();
            } else {
                showProfilePage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.club, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_result) {

        } else if (id == R.id.nav_stats) {

        } else if (id == R.id.nav_recordMatch) {

        } else if (id == R.id.nav_logOut) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showProfilePage(){
        ClubProfileFragment clubProfileFragment = ClubProfileFragment.newInstance(clubID,playerID);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content,clubProfileFragment,Constant.FRAGMENT_CLUB_PROFILE);
        fragmentTransaction.commit();

    }

    private void showTournamentList() {
        profilePage.setVisibility(View.GONE);
        tournamentListPage.setVisibility(View.VISIBLE);
        tournamentPage.setVisibility(View.GONE);
        btn_addTournament = (Button) findViewById(R.id.btn_addTournament);
        lv_tournament = (ListView) findViewById(R.id.lv_clubTournaments);
        RequestAction actionGetClubTournaments = new RequestAction() {
            @Override
            public void actOnPre() {
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray tournamentArray = jsonObject.getJSONArray(Constant.TOURNAMENT_LIST);
                        tournaments = new Tournament[tournamentArray.length()];
                        ArrayList<HashMap<String,Object>> tournamentList = new ArrayList<>(tournaments.length);
                        for ( int i = 0; i < tournamentArray.length(); i++ ) {
                            HashMap<String,Object> tournamentMap = new HashMap<>(2);
                            int tournamentID = tournamentArray.getJSONObject(i).getInt(Constant.TOURNAMENT_ID);
                            String tournamentName = tournamentArray.getJSONObject(i).getString(Constant.TOURNAMENT_NAME);
                            String tournamentInfo = tournamentArray.getJSONObject(i).getString(Constant.TOURNAMENT_INFO);
                            tournaments[i] = new Tournament(tournamentID,tournamentName,tournamentInfo);
                            tournamentMap.put(Constant.TOURNAMENT_NAME,tournamentName);
                            tournamentMap.put(Constant.TOURNAMENT_INFO,tournamentInfo);
                            tournamentList.add(tournamentMap);
                        }
                        TournamentListAdapter tournamentListAdapter = new TournamentListAdapter(ClubActivity.this,R.layout.layout_card_tournament,tournamentList);
                        lv_tournament.setAdapter(tournamentListAdapter);
                        lv_tournament.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                showTournamentPage(tournaments[position].id);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        String url = UrlHelper.urlGetClubTournaments(clubID);
        RequestHelper.sendGetRequest(url,actionGetClubTournaments);
        btn_addTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateTournamentPage();
            }
        });
    }

    private void showCreateTournamentPage() {
        LayoutInflater inflater = LayoutInflater.from(ClubActivity.this);
        View v_event = inflater.inflate(R.layout.layout_reg_name_info, null);
        final TextView tv_name = (TextView) v_event.findViewById(R.id.tv_name);
        final TextView tv_info = (TextView) v_event.findViewById(R.id.tv_info);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Create a new Tournament");
        dialogBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tournamentName = tv_name.getText().toString();
                String tournamentInfo = tv_info.getText().toString();
                if ( tournamentName.equals("") ) {
                    Toast.makeText(ClubActivity.this, "Tournament Name cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( tournamentInfo.equals("") ) {
                    Toast.makeText(ClubActivity.this, "Tournament Info cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                TournamentRegistration tournamentReg = new TournamentRegistration(clubID,tournamentName,tournamentInfo);
                RequestAction actionPostClub = new RequestAction() {
                    @Override
                    public void actOnPre() {

                    }

                    @Override
                    public void actOnPost(int responseCode, String response) {
                        if ( responseCode == 201 ) {
                            showTournamentList();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString(Constant.KEY_MSG);
                                Toast.makeText(ClubActivity.this,message,Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ClubActivity.this,response,Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                };
                String url = UrlHelper.urlPostTournament();
                RequestHelper.sendPostRequest(url,tournamentReg.toJson(),actionPostClub);
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
            }
        });
        dialogBuilder.setView(v_event);
        dialogBuilder.setCancelable(true);
        dialogBuilder.show();
    }

    private void showTournamentPage(int tournamentID){
        profilePage.setVisibility(View.GONE);
        tournamentListPage.setVisibility(View.GONE);
        tournamentPage.setVisibility(View.VISIBLE);
        View tournamentView = tournamentPage.findViewById(R.id.tabViewPager_tournament);
        tab_tournament = (TabLayout) tournamentView.findViewById(R.id.tabLayout);
        viewPager_tournament = (ViewPager) tournamentView.findViewById(R.id.viewPager);
        tab_tournament.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager_tournament.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager_tournament.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tab_tournament.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tab_tournament.removeAllTabs();
        tab_tournament.addTab(tab_tournament.newTab().setText("Results"));
        tab_tournament.addTab(tab_tournament.newTab().setText("Stats"));
        tab_tournament.addTab(tab_tournament.newTab().setText("Squad"));
        tab_tournament.setTabMode(TabLayout.MODE_FIXED);
        Fragment[] fragments = new Fragment[3];
        ClubResultFragment clubResultFragment = ClubResultFragment.newInstance(tournamentID,null);
        fragments[0] = clubResultFragment;

        ClubStatsFragment clubStatsFragment = ClubStatsFragment.newInstance(clubID,tournamentID);
        fragments[1] = clubStatsFragment;

        ClubSquadFragment clubSquadFragment = ClubSquadFragment.newInstance(clubID,tournamentID);
        fragments[2] = clubSquadFragment;

        CustomFragmentAdapter adapter = new CustomFragmentAdapter(getSupportFragmentManager());
        adapter.setFragments(fragments);
        viewPager_tournament.setAdapter(adapter);
    }


    private void showResultPage(){
        setTitle("Tournament");
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
                        final ArrayList<String> tournaments = new ArrayList<>();
                        tournaments.add(Constant.OPTION_ALL_TOURNAMENTS);
                        for ( Result result : results ) {
                            if ( !tournaments.contains(result.tournamentName) ) {
                                tournaments.add(result.tournamentName);
                            }
                        }
                        final String[] tournamentNames = new String[tournaments.size()];
                        for ( int i = 0; i < tournamentNames.length; i++ ) {
                            tournamentNames[i] = tournaments.get(i);
                        }
                        Spinner sp_tournament = (Spinner) findViewById(R.id.sp_tournament);
                        sp_tournament.setAdapter(new ArrayAdapter<>(ClubActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, tournamentNames));
                        sp_tournament.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                showResults(tournamentNames[position]);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Toast.makeText(ClubActivity.this,jsonObject.getString(Constant.KEY_MSG),
                                Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ClubActivity.this,response,Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        String url = UrlHelper.urlGetClubResults(this.clubID);
        RequestHelper.sendGetRequest(url,actionGetClubResults);
    }

    private void showResults(String tournamentName){
        if ( tournamentName == null ) {
            Log.e("Show Results","Tournament name unspecified.");
            return;
        }
        showChart(tournamentName);
        ListView lv_result = (ListView) findViewById(R.id.lv_result);
        ArrayList<HashMap<String,Object>> resultItems = new ArrayList<>();
        for ( int i = 0; i < results.length; i++ ) {
            if ( !tournamentName.equals(Constant.OPTION_ALL_TOURNAMENTS) && !tournamentName.equals(results[i].tournamentName) ) {
                continue;
            }
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
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.layout_card_result,null);
            resultMap.put(Constant.RESULT_KEY_HOME_EVENT,getAdapter(results[i],true));
            // awayID events
            resultMap.put(Constant.RESULT_KEY_AWAY_EVENT,getAdapter(results[i],false));
            resultItems.add(resultMap);
        }
        ResultListAdapter resultListAdapter = new ResultListAdapter(ClubActivity.this,R.layout.layout_card_result,resultItems);
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

    private SimpleAdapter getAdapter(Result result,boolean isHome){
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
        return new SimpleAdapter(ClubActivity.this, eventListItems, R.layout.layout_event_detail,
                new String[]{Constant.EVENT_TYPE, Constant.EVENT_PLAYER, Constant.EVENT_TIME},
                new int[]{R.id.iv_eventIcon, R.id.tv_eventPlayer, R.id.tv_eventTime});
    }


    private void showChart(String tournamentName) {
        View tabViewPager = findViewById(R.id.tabViewPager_club);
        viewPager = (ViewPager) tabViewPager.findViewById(R.id.viewPager);
        tab_clubStats = (TabLayout) tabViewPager.findViewById(R.id.tabLayout);

        // count W-D-L
        String[] resultLabels = Constant.LABEL_GAME_PERFORMANCE;
        float[] resultValues = new float[]{0,0,0};
        for( Result result : results ) {
            if ( !tournamentName.equals(Constant.OPTION_ALL_TOURNAMENTS)
                    && !result.tournamentName.equals(tournamentName) ) {
                Log.d("SKIP",result.tournamentName);
                continue;
            }
            int homeScore, awayScore;
            String finalScore;
            if ( result.penScore != null && !result.penScore.equals("null") ) {
                finalScore = result.penScore;
            } else if ( result.extraScore != null && !result.extraScore.equals("null")) {
                finalScore = result.extraScore;
            } else {
                finalScore = result.ftScore;
            }
            String[] scores = finalScore.split(":");
            if ( scores.length != 2 ) {
                Log.e("CLUB_ACTIVITY","Wrong score format.");
                return;
            }
            homeScore = Integer.parseInt(scores[0]);
            awayScore = Integer.parseInt(scores[1]);
            if ( homeScore == awayScore ) {
                resultValues[1]++;  // DRAW
            } else if ( homeScore > awayScore ) {
                if ( result.homeID == this.clubID ) {
                    resultValues[0]++;  // WIN
                } else {
                    resultValues[2]++;  // LOSS
                }
            } else {
                if ( result.homeID == this.clubID ) {
                    resultValues[2]++;  // LOSS
                } else {
                    resultValues[0]++;  // WIN
                }
            }
            Log.d("W-D-L",resultValues[0]+"-"+resultValues[1]+"-"+resultValues[2]);
        }
        ArrayList<float[]> dataY = new ArrayList<>(2);
        dataY.add(resultValues);

        Fragment[] fragments = new Fragment[Constant.CLUB_STATS_TABS.length];
        tab_clubStats.removeAllTabs();
        tab_clubStats.addTab(tab_clubStats.newTab().setText(Constant.CLUB_STATS_TABS[0]));
//        fragments[0] = PieChartFragment.newInstance(Constant.CLUB_STATS_TABS[0],
//                Constant.CLUB_STATS_CENTER_TEXT[0],Constant.CLUB_STATS_SHOW_CENTER[0],
//                resultLabels,dataY.get(0),Constant.CLUB_STATS_IS_INT[0]);
//        resultLabels = new String[]{"Goals Scored","Goals Conceded","Assists","Win","Draw","Loss"};
//        dataY.add(new float[]{16,5,8,2,2,0});
//        tab_clubStats.addTab(tab_clubStats.newTab().setText(Constant.CLUB_STATS_TABS[1]));
//        fragments[1] = BarChartFragment.newInstance(Constant.CLUB_STATS_TABS[1],
//                Constant.CLUB_STATS_CENTER_TEXT[1],Constant.CLUB_STATS_SHOW_CENTER[1],
//                resultLabels,dataY.get(1),Constant.CLUB_STATS_IS_INT[1]);
        tab_clubStats.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tab_clubStats.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        CustomFragmentAdapter adapter = new CustomFragmentAdapter(getSupportFragmentManager());
        adapter.setFragments(fragments);
        viewPager.setAdapter(adapter);
    }
}
