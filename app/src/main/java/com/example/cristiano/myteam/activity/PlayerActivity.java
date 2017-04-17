package com.example.cristiano.myteam.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.fragment.ChartFragmentFactory;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.structure.PlayerInfo;
import com.example.cristiano.myteam.structure.Stats;
import com.example.cristiano.myteam.structure.Tournament;
import com.example.cristiano.myteam.adapter.ClubListAdapter;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.adapter.CustomFragmentAdapter;
import com.example.cristiano.myteam.util.UrlHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tv_name, tv_role,tv_playerTournament;
    private ImageView iv_avatar;
    private ConstraintLayout layout_profile,layout_club;
    private ListView lv_club;
    private Button btn_club;
    private NavigationView navigationView;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Spinner sp_playerClub, sp_playerTournament;

    private String email;
    private PlayerInfo playerInfo;
    private int selectedClubID = 0, selectedTournamentID = 0;
    private ArrayList<String> clubNames, tournamentNames;

    private int pageID = PAGE_PROFILE;
    private static final int PAGE_PROFILE = 0;
    private static final int PAGE_CLUB = 1;
    private static final int PAGE_LOGOUT = 2;
    private static final int PAGE_SETTINGS = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_player);
        setSupportActionBar(toolbar);
        layout_profile = (ConstraintLayout) findViewById(R.id.layout_profile);
        layout_club = (ConstraintLayout) findViewById(R.id.layout_club_list);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_role = (TextView) findViewById(R.id.tv_role);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        btn_club = (Button) findViewById(R.id.btn_club);
        lv_club = (ListView) findViewById(R.id.lv_club);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view_player);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle bundle = getIntent().getExtras();
        if ( bundle == null || !bundle.containsKey(Constant.PLAYER_EMAIL) ) {
            Log.e("PlayerActivity","Missing player email bundle");
            return;
        }
        this.email = bundle.getString(Constant.PLAYER_EMAIL);
        loadPlayerInfo();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if( pageID != PAGE_PROFILE ){
            showPlayerProfilePage();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.player, menu);
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
            showSettingsPage();
            return true;
        } else if (id == R.id.action_logout) {
            showLogoutPage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            if ( pageID != PAGE_PROFILE ) {
                showPlayerProfilePage();
            }
        } else if (id == R.id.nav_club) {
            if ( pageID != PAGE_CLUB ) {
                showPlayerClubPage();
            }
        } else if (id == R.id.nav_logout) {
            showLogoutPage();
        } else if (id == R.id.nav_settings) {
            showSettingsPage();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showPlayerProfilePage(){
        // initialize profile page
        pageID = PAGE_PROFILE;
        setTitle(R.string.title_activity_profile);
        layout_profile.setVisibility(View.VISIBLE);
        layout_club.setVisibility(View.GONE);
        tv_name.setText(playerInfo.getPlayer().displayName);
        tv_role.setText(playerInfo.getPlayer().role);
        View view = findViewById(R.id.layout_playerStats);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        sp_playerClub = (Spinner) findViewById(R.id.sp_playerClub);
        sp_playerTournament = (Spinner) findViewById(R.id.sp_playerTournament);
        tv_playerTournament = (TextView) findViewById(R.id.tv_playerTournament);
        clubNames = new ArrayList<>(playerInfo.getClubs().length+1);
        clubNames.add(Constant.OPTION_ALL_CLUBS);
        for ( int i = 0; i < playerInfo.getClubs().length; i++ ) {
            clubNames.add(playerInfo.getClubs()[i].name);
        }
        sp_playerClub.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,clubNames));
//        tabLayout.removeAllTabs();

        // set view listeners
        btn_club.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlayerClubPage();
            }
        });

        sp_playerClub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ( position == 0 ) {  // hide tournament spinner, show player total stats
                    sp_playerTournament.setVisibility(View.GONE);
                    tv_playerTournament.setVisibility(View.GONE);
                    showStats(playerInfo.getTotalStats());
                } else {    // show sp_playerTournament
                    sp_playerTournament.setVisibility(View.VISIBLE);
                    tv_playerTournament.setVisibility(View.VISIBLE);
                    for ( Club club : playerInfo.getClubs() ) {
                        if ( club.name.equals(parent.getSelectedItem().toString()) ) {
                            selectedClubID = club.id;
                            break;
                        }
                    }
                    if ( selectedClubID != 0 ) {
                        if ( playerInfo.hasClubStats(selectedClubID) ) {    // if cached, use cache to set adapter and render stats
                            tournamentNames = new ArrayList<String>();
                            tournamentNames.add(Constant.OPTION_ALL_TOURNAMENTS);
                            for ( Tournament tournament : playerInfo.getClubTournaments(selectedClubID) ) {
                                tournamentNames.add(tournament.name);
                            }
                            sp_playerTournament.setAdapter(new ArrayAdapter<String>(
                                    PlayerActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    tournamentNames));
                            Stats clubTotalStats = playerInfo.getClubStats(selectedClubID);
//                            showStats(clubTotalStats);
                        } else {    // not cached, send request to get the stats
                            loadPlayerClubStats(selectedClubID);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_playerTournament.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ( position == 0 ) {  // show player's club total stats
                    Stats playerClubStats = playerInfo.getClubStats(selectedClubID);
                } else {    // show player's tournament stats
                    Tournament[] tournaments = playerInfo.getClubTournaments(selectedClubID);
                    for ( int i = 0; i < tournaments.length; i++ ) {
                        if ( tournaments[i].name.equals(parent.getSelectedItem().toString()) ) {
                            selectedTournamentID = tournaments[i].id;
                            break;
                        }
                    }
                    if ( selectedTournamentID != 0) {
                        loadPlayerTournamentStats(selectedTournamentID);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        tabLayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
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
                try {
                    tabLayout.getTabAt(position).select();
                }catch (NullPointerException e) {
                    Log.e("PlayerStatsViewPager","ERROR: No Tab Found For Selected Page.");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // initialize avatar
        switch ( playerInfo.getPlayer().avatar ) {
            case 0:
                iv_avatar.setImageResource(R.drawable.avatar_scholes);
                break;
            case 1:
                iv_avatar.setImageResource(R.drawable.avatar_rooney);
                break;
            default:
                iv_avatar.setImageResource(R.drawable.avatar_peter);
                break;
        }

        showStats(playerInfo.getTotalStats());
    }

    private void showRegistrationPage(String email, Player player){
        Intent intent = new Intent(PlayerActivity.this,PlayerRegistrationActivity.class);
        intent.putExtra(Constant.PLAYER_EMAIL,email);
        if ( player != null ) {
            intent.putExtra(Constant.PLAYER_INFO,player);
        }
        startActivity(intent);
    }

    private void showPlayerClubPage(){
        pageID = PAGE_CLUB;
        layout_profile.setVisibility(View.GONE);
        layout_club.setVisibility(View.VISIBLE);
        setTitle("My Clubs");
        ArrayList<HashMap<String,Object>> clubList = new ArrayList<>();
        for ( Club club : playerInfo.getClubs() ) {
            HashMap<String,Object> clubMap = new HashMap<>();
            clubMap.put(Constant.CLUB_NAME,club.name);
            clubMap.put(Constant.CLUB_INFO,club.info);
            clubList.add(clubMap);
        }
        ClubListAdapter clubListAdapter = new ClubListAdapter(this,R.layout.layout_card_club,clubList);
        lv_club.setAdapter(clubListAdapter);
        lv_club.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewClub(playerInfo.getClubs()[i].id);
            }
        });
    }

    private void showLogoutPage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this,0);
        builder.setTitle("Warning");
        builder.setMessage("Are you sure to logout?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(PlayerActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SharedPreferences sharedPreferences = getSharedPreferences(Constant.KEY_USER_PREF,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constant.KEY_AUTO_LOGIN,false);
                editor.apply();
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if( pageID == PAGE_PROFILE ){
                    navigationView.setCheckedItem(R.id.nav_profile);
                } else if ( pageID == PAGE_CLUB) {
                    navigationView.setCheckedItem(R.id.nav_club);
                }
            }
        });
        builder.show();
    }

    private void showSettingsPage() {
        showRegistrationPage(email,playerInfo.getPlayer());
    }

    private void viewClub(int clubID) {
        Intent intent = new Intent(PlayerActivity.this,ClubActivity.class);
        intent.putExtra(Constant.KEY_CLUB_ID,clubID);
        startActivity(intent);
    }

    private void loadPlayerInfo(){
        RequestAction actionGetPlayerInfo = new RequestAction() {
            @Override
            public void actOnPre() {
                // show a progress bar
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                // hide the progress bar
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonPlayerInfo = new JSONObject(response);

                        // get player's profile info
                        JSONObject jsonPlayer = jsonPlayerInfo.getJSONObject(Constant.PLAYER_INFO_PLAYER);
                        int playerID = jsonPlayer.getInt(Constant.PLAYER_ID);
                        String firstName = jsonPlayer.getString(Constant.PLAYER_FIRST_NAME);
                        String lastName = jsonPlayer.getString(Constant.PLAYER_LAST_NAME);
                        String displayName = jsonPlayer.getString(Constant.PLAYER_DISPLAY_NAME);
                        String role = jsonPlayer.getString(Constant.PLAYER_ROLE);
                        String phone = jsonPlayer.getString(Constant.PLAYER_PHONE);
                        int age = jsonPlayer.getInt(Constant.PLAYER_AGE);
                        float weight = (float) jsonPlayer.getDouble(Constant.PLAYER_WEIGHT);
                        float height = (float) jsonPlayer.getDouble(Constant.PLAYER_HEIGHT);
                        boolean leftFooted = jsonPlayer.getBoolean(Constant.PLAYER_FOOT);
                        int avatar = jsonPlayer.getInt(Constant.PLAYER_AVATAR);
                        Player player = new Player(playerID,email,firstName,lastName,displayName,role,phone,age,weight,height,leftFooted,avatar);

                        // get player's all clubs' info
                        JSONArray jsonPlayerClubs = jsonPlayerInfo.getJSONArray(Constant.PLAYER_INFO_CLUBS);
//                        clubNames = new String[jsonPlayerClubs.length()];   // contains only the club names
                        Club[] myClubs = new Club[jsonPlayerClubs.length()];
                        int clubID;
                        String clubName, clubInfo;
                        for ( int i = 0; i < jsonPlayerClubs.length(); i++ ) {
                            clubID = jsonPlayerClubs.getJSONObject(i).getInt(Constant.CLUB_ID);
                            clubName = jsonPlayerClubs.getJSONObject(i).getString(Constant.CLUB_NAME);
//                            clubNames[i] = clubName;
                            clubInfo = jsonPlayerClubs.getJSONObject(i).getString(Constant.CLUB_INFO);
                            myClubs[i] = new Club(clubID,clubName,clubInfo);
                        }
                        JSONObject jsonPlayerTotalStats = jsonPlayerInfo.getJSONObject(Constant.PLAYER_INFO_TOTAL_STATS);
                        int tournamentID = -1;
                        clubID = Constant.ALL_STATS;
                        int attendance = jsonPlayerTotalStats.getInt(Constant.STATS_ATTENDANCE);
                        int appearance = jsonPlayerTotalStats.getInt(Constant.STATS_APPEARANCE);
                        int start = jsonPlayerTotalStats.getInt(Constant.STATS_START);
                        int goal = jsonPlayerTotalStats.getInt(Constant.STATS_GOAL);
                        int penalty = jsonPlayerTotalStats.getInt(Constant.STATS_PEN);
                        int penaltyShootout = jsonPlayerTotalStats.getInt(Constant.STATS_PEN_SHOOTOUT);
                        int penaltyTaken = jsonPlayerTotalStats.getInt(Constant.STATS_PEN_TAKEN);
                        int ownGoal = jsonPlayerTotalStats.getInt(Constant.STATS_OG);
                        int header = jsonPlayerTotalStats.getInt(Constant.STATS_HEADER);
                        int weakFootGoal = jsonPlayerTotalStats.getInt(Constant.STATS_WEAK_FOOT_GOAL);
                        int otherGoal = jsonPlayerTotalStats.getInt(Constant.STATS_OTHER_GOAL);
                        int assist = jsonPlayerTotalStats.getInt(Constant.STATS_ASSIST);
                        int yellow = jsonPlayerTotalStats.getInt(Constant.STATS_YELLOW);
                        int red = jsonPlayerTotalStats.getInt(Constant.STATS_RED);
                        int cleanSheet = jsonPlayerTotalStats.getInt(Constant.STATS_CLEAN_SHEET);
                        int penaltySaved = jsonPlayerTotalStats.getInt(Constant.STATS_PEN_SAVED);
                        Stats totalStats = new Stats(tournamentID, clubID, playerID, attendance, appearance,
                                start, goal, penalty, penaltyShootout, penaltyTaken, ownGoal, header,
                                weakFootGoal,otherGoal, assist, yellow, red, cleanSheet, penaltySaved);
                        JSONObject jsonPlayerGamePerformance = jsonPlayerInfo.getJSONObject(Constant.PLAYER_INFO_GAME_PERFORMANCE);
                        int win = jsonPlayerGamePerformance.getInt(Constant.PERFORMANCE_WIN);
                        int draw = jsonPlayerGamePerformance.getInt(Constant.PERFORMANCE_DRAW);
                        int loss = jsonPlayerGamePerformance.getInt(Constant.PERFORMANCE_LOSS);
                        totalStats.setWin(win);
                        totalStats.setDraw(draw);
                        totalStats.setLoss(loss);
                        playerInfo = new PlayerInfo(player,myClubs,totalStats);
                        showPlayerProfilePage();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Toast.makeText(PlayerActivity.this,jsonObject.getString(Constant.KEY_MSG),
                                Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PlayerActivity.this,response,Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        String url = UrlHelper.urlGetPlayerInfo(email);
        RequestHelper.sendGetRequest(url,actionGetPlayerInfo);
    }

    private void loadPlayerClubStats(final int clubID){
        RequestAction actionGetPlayerClubStats = new RequestAction() {
            @Override
            public void actOnPre() {
            }

            @Override
            public void actOnPost(int responseCode, String response) {

                // get club tournaments
//                Tournament[] tournaments;
//                playerInfo.addClubTournament(selectedClubID,tournaments);
//                // set sp_playerTournament adapter
//                tournamentNames = new ArrayList<String>();
//                tournamentNames.add(Constant.OPTION_ALL_TOURNAMENTS);
//                for ( Tournament tournament : tournaments ) {
//                    tournamentNames.add(tournament.name);
//                }
//                sp_playerTournament.setAdapter(new ArrayAdapter<String>(
//                        PlayerActivity.this,
//                        android.R.layout.simple_spinner_dropdown_item,
//                        tournamentNames));

                // get player club total stats
//                Stats playerClubStats;
//                playerInfo.addClubStats(selectedClubID,playerClubStats);
                // render view pager
//                showStats(playerClubStats);

            }
        };
//        String url;
//        RequestHelper.sendGetRequest(url,actionGetPlayerClubStats);
    }

    private void loadPlayerTournamentStats(int tournamentID){
        RequestAction actionGetPlayerTournamentStats = new RequestAction() {
            @Override
            public void actOnPre() {
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                // get player tournament stats
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject jsonPlayerTournamentStats = jsonResponse.getJSONObject(Constant.TABLE_STATS);
                        int tournamentID = jsonPlayerTournamentStats.getInt(Constant.STATS_T_ID);
                        int clubID = jsonPlayerTournamentStats.getInt(Constant.STATS_C_ID);
                        int playerID = jsonPlayerTournamentStats.getInt(Constant.STATS_P_ID);
                        int attendance = jsonPlayerTournamentStats.getInt(Constant.STATS_ATTENDANCE);
                        int appearance = jsonPlayerTournamentStats.getInt(Constant.STATS_APPEARANCE);
                        int start = jsonPlayerTournamentStats.getInt(Constant.STATS_START);
                        int goal = jsonPlayerTournamentStats.getInt(Constant.STATS_GOAL);
                        int penalty = jsonPlayerTournamentStats.getInt(Constant.STATS_PEN);
                        int penaltyShootout = jsonPlayerTournamentStats.getInt(Constant.STATS_PEN_SHOOTOUT);
                        int penaltyTaken = jsonPlayerTournamentStats.getInt(Constant.STATS_PEN_TAKEN);
                        int ownGoal = jsonPlayerTournamentStats.getInt(Constant.STATS_OG);
                        int header = jsonPlayerTournamentStats.getInt(Constant.STATS_HEADER);
                        int weakFootGoal = jsonPlayerTournamentStats.getInt(Constant.STATS_WEAK_FOOT_GOAL);
                        int otherGoal = jsonPlayerTournamentStats.getInt(Constant.STATS_OTHER_GOAL);
                        int assist = jsonPlayerTournamentStats.getInt(Constant.STATS_ASSIST);
                        int yellow = jsonPlayerTournamentStats.getInt(Constant.STATS_YELLOW);
                        int red = jsonPlayerTournamentStats.getInt(Constant.STATS_RED);
                        int cleanSheet = jsonPlayerTournamentStats.getInt(Constant.STATS_CLEAN_SHEET);
                        int penaltySaved = jsonPlayerTournamentStats.getInt(Constant.STATS_PEN_SAVED);
                        Stats playerTournamentStats = new Stats(tournamentID, clubID, playerID, attendance, appearance,
                                start, goal, penalty, penaltyShootout, penaltyTaken, ownGoal, header,
                                weakFootGoal,otherGoal, assist, yellow, red, cleanSheet, penaltySaved);
                        JSONObject jsonPlayerTournamentPerformance = jsonResponse.getJSONObject(Constant.PLAYER_INFO_GAME_PERFORMANCE);
                        int win = jsonPlayerTournamentPerformance.getInt(Constant.PERFORMANCE_WIN);
                        int draw = jsonPlayerTournamentPerformance.getInt(Constant.PERFORMANCE_DRAW);
                        int loss = jsonPlayerTournamentPerformance.getInt(Constant.PERFORMANCE_LOSS);
                        playerTournamentStats.setWin(win);
                        playerTournamentStats.setDraw(draw);
                        playerTournamentStats.setLoss(loss);
                        // cache stats
                        playerInfo.addTournamentStats(selectedTournamentID,playerTournamentStats);
                        // render view pager
                        showStats(playerTournamentStats);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String message = jsonResponse.getString(Constant.KEY_MSG);
                        Toast.makeText(PlayerActivity.this, message, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PlayerActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        String url = UrlHelper.urlGetPlayerTournamentStats(tournamentID,selectedClubID,playerInfo.getPlayer().id);
        RequestHelper.sendGetRequest(url,actionGetPlayerTournamentStats);
    }

    private void showStats(Stats stats) {
        tabLayout.removeAllTabs();
        // initialize stats views
        Fragment[] fragments = new Fragment[4];
        String[] dataX;
        float[] dataY;
        // player game performance
        dataX = Constant.LABEL_GAME_PERFORMANCE;
        dataY = new float[] { stats.getWin(),stats.getDraw(),stats.getLoss()};
        fragments[0] = ChartFragmentFactory.makeChartFragment(Constant.CHART_TYPE_PIE,dataX,dataY,"Game Performance","Total Games",tabLayout);

        //  player stats
        dataX = Constant.LABEL_PLAYER_TOTAL_STATS;
        float appearance = stats.appearance;
        float start = stats.start;
        float goal = stats.goal;
        float assist = stats.assist;
        float yellow = stats.yellow;
        float red = stats.red;
        dataY = new float[] { appearance,start,goal,assist,yellow,red};
        fragments[1] = ChartFragmentFactory.makeChartFragment(Constant.CHART_TYPE_BAR,dataX,dataY,"Overall Stats",null,tabLayout);

        // player penalty history
        dataX = Constant.LABEL_PENALTY_HISTORY;
        float penScored = stats.penalty + stats.penaltyShootout;
        float penMissed = stats.penaltyTaken - penScored;
        dataY = new float[] {penScored,penMissed};
        fragments[2] = ChartFragmentFactory.makeChartFragment(Constant.CHART_TYPE_PIE,dataX,dataY,"Penalty History","Penalties Taken",tabLayout);

        //  player goal distribution
        dataX = Constant.LABEL_GOAL_DISTRIBUTION;
        float weakFootGoal = stats.weakFootGoal;
        float header = stats.header;
        float otherGoal = stats.otherGoal;
        float strongFootGoal = stats.goal - (weakFootGoal + header + otherGoal);
        dataY = new float[]{strongFootGoal,weakFootGoal,header,otherGoal};
        fragments[3] = ChartFragmentFactory.makeChartFragment(Constant.CHART_TYPE_PIE,dataX,dataY,"Goal Distribution","Goals scored",tabLayout);


        CustomFragmentAdapter adapter = new CustomFragmentAdapter(getSupportFragmentManager());
        adapter.setFragments(fragments);
        viewPager.setAdapter(adapter);
    }
}
