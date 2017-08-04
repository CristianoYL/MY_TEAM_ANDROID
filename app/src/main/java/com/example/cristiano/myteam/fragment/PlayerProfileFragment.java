package com.example.cristiano.myteam.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.adapter.CustomFragmentAdapter;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.structure.PlayerInfo;
import com.example.cristiano.myteam.structure.Stats;
import com.example.cristiano.myteam.structure.Tournament;
import com.example.cristiano.myteam.util.AppController;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Cristiano on 2017/4/18.
 *
 * this fragment presents the player profile page,
 * which displays basic info about the player and visualises the player's stats
 */

public class PlayerProfileFragment extends Fragment {

    private static final String ARG_CLUB = "club";
    private static final String ARG_PLAYER = "player";

    private static final String TAG = "PlayerProfileFragment";


    private TextView tv_name, tv_role,tv_playerTournament;
    private ImageView iv_avatar;
    private Button btn_club;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Spinner sp_playerClub, sp_playerTournament;
    private PlayerInfo playerInfo;
    private int selectedClubID = 0;
    private ArrayList<String> clubNames, tournamentNames;
    private Stats playerClubStats;
    private int playerID;

    private View view;

    OnClubListLoadedListener onClubListLoadedListener;

    public interface OnClubListLoadedListener{
        void setClubs(ArrayList<Club> clubList);
    }

    public PlayerProfileFragment() {
    }

    public static PlayerProfileFragment newInstance(int playerID){
        PlayerProfileFragment fragment = new PlayerProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PLAYER,playerID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            playerID = bundle.getInt(ARG_PLAYER);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            onClubListLoadedListener = (OnClubListLoadedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement OnClubListChangeListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_player_profile, container, false);
        tv_name = (TextView) view.findViewById(R.id.et_name);
        tv_role = (TextView) view.findViewById(R.id.tv_role);
        iv_avatar = (ImageView) view.findViewById(R.id.iv_otherAvatar);
        btn_club = (Button) view.findViewById(R.id.btn_club);
        AppController.setNavigationMenu(getActivity(),AppController.MENU_PLAYER);
        loadPlayerInfo();
        return view;
    }

    /**
     * send a GET request to retrieve the player's info
     */
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
                        int userID = 0;
                        try {
                            userID = jsonPlayer.getInt(Constant.PLAYER_USER_ID);
                        } catch ( JSONException e ) {
                            e.printStackTrace();
                        }
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
                        // use the retrieve info to create a Player instance
                        Player player = new Player(playerID,userID,firstName,lastName,displayName,role,phone,age,weight,height,leftFooted,avatar);

                        // get player's all clubs' info
                        JSONArray jsonPlayerClubs = jsonPlayerInfo.getJSONArray(Constant.PLAYER_INFO_CLUBS);
//                        clubNames = new String[jsonPlayerClubs.length()];   // contains only the club names
                        ArrayList<Club> myClubs = new ArrayList<>(jsonPlayerClubs.length());
                        int clubID;
                        for ( int i = 0; i < jsonPlayerClubs.length(); i++ ) {
                            JSONObject jsonClub = jsonPlayerClubs.getJSONObject(i);
                            clubID = jsonClub.getInt(Constant.CLUB_ID);
                            String clubName = jsonClub.getString(Constant.CLUB_NAME);
                            String clubInfo = jsonClub.getString(Constant.CLUB_INFO);
                            int priority = jsonClub.getInt(Constant.MEMBER_PRIORITY);
                            Club club = new Club(clubID,clubName,clubInfo);
                            club.priority = priority;
                            myClubs.add(club);
                        }
                        onClubListLoadedListener.setClubs(myClubs);
                        JSONObject jsonPlayerTotalStats = jsonPlayerInfo.getJSONObject(Constant.PLAYER_INFO_TOTAL_STATS);
                        int tournamentID = -1;
                        clubID = Constant.ALL_STATS;
                        int attendance = jsonPlayerTotalStats.getInt(Constant.STATS_ATTENDANCE);
                        int appearance = jsonPlayerTotalStats.getInt(Constant.STATS_APPEARANCE);
                        int start = jsonPlayerTotalStats.getInt(Constant.STATS_START);
                        int goal = jsonPlayerTotalStats.getInt(Constant.STATS_GOAL);
                        int penalty = jsonPlayerTotalStats.getInt(Constant.STATS_PEN);
                        int freekick = jsonPlayerTotalStats.getInt(Constant.STATS_FREEKICK);
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
                                start, goal, penalty,freekick, penaltyShootout, penaltyTaken, ownGoal, header,
                                weakFootGoal,otherGoal, assist, yellow, red, cleanSheet, penaltySaved);
                        JSONObject jsonPlayerGamePerformance = jsonPlayerInfo.getJSONObject(Constant.KEY_GAME_PERFORMANCE);
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
                } else if ( responseCode == 404 ){
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), "Failed to retrieve player info.", Toast.LENGTH_LONG).show();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString(Constant.KEY_MSG);
                        Log.e(TAG,"Error Message:" + message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG,"Error response:" + response);
                    }
                }
            }
        };
        String url;
        url = UrlHelper.urlPlayerInfoByID(playerID);
        RequestHelper.sendGetRequest(url,actionGetPlayerInfo);
    }
    /**
     *  presents the player's profile page
     */
    private void showPlayerProfilePage(){
        // initialize profile page
        getActivity().setTitle(R.string.title_activity_profile);
        tv_name.setText(playerInfo.getPlayer().getDisplayName());
        tv_role.setText(playerInfo.getPlayer().getRole());
        View pagerView = view.findViewById(R.id.layout_playerStats);
        viewPager = (ViewPager) pagerView.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) pagerView.findViewById(R.id.tabLayout);
        sp_playerClub = (Spinner) view.findViewById(R.id.sp_playerClub);
        sp_playerTournament = (Spinner) view.findViewById(R.id.sp_playerTournament);
        tv_playerTournament = (TextView) view.findViewById(R.id.tv_playerTournament);
        clubNames = new ArrayList<>(playerInfo.getClubs().size());
        clubNames.add(Constant.OPTION_ALL_CLUBS);
        for ( int i = 0; i < playerInfo.getClubs().size(); i++ ) {
            Club club = playerInfo.getClubs().get(i);
            if ( club.priority > 0 ) {
                clubNames.add(club.name);
            }
        }
        sp_playerClub.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,clubNames));

        // set view listeners
        btn_club.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlayerClubList();
            }
        });

        // listen to the selection of the player's club spinner
        sp_playerClub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ( position == 0 ) {  // position[0] == All Clubs
                    showPlayerTotalStats();
                } else {    // show sp_playerTournament and let the user choose tournaments
                    for ( Club club : playerInfo.getClubs() ) {
                        if ( club.name.equals(parent.getSelectedItem().toString()) ) {
                            loadPlayerClubStats(club.id);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // use tabs to coordinate with view pagers
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

        // view pages should also update tab selections
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab =tabLayout.getTabAt(position);
                if ( tab != null ) {
                    tab.select();
                } else {
                    Log.e(TAG,"ERROR: No Tab Found For Selected Page.");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // initialize avatar
        switch ( playerInfo.getPlayer().getAvatar() ) {
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

    /**
     * send a GET request to retrieve the player's total stats in the specified club
     * @param clubID    ID of the specified club
     */
    private void loadPlayerClubStats(int clubID){
        Log.d(TAG,"load stats: club"+clubID);
        selectedClubID = clubID;
        RequestAction actionGetPlayerClubStats = new RequestAction() {
            @Override
            public void actOnPre() {
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        // get club tournaments
                        JSONArray jsonTournaments = jsonObject.getJSONArray(Constant.TOURNAMENT_LIST);
                        tournamentNames = new ArrayList<String>();
                        tournamentNames.add(Constant.OPTION_ALL_TOURNAMENTS);
                        for ( int i = 0; i < jsonTournaments.length(); i++ ) {
                            JSONObject jsonTournament = jsonTournaments.getJSONObject(i);
                            int tournamentID = jsonTournament.getInt(Constant.TOURNAMENT_ID);
                            String tournamentName = jsonTournament.getString(Constant.TOURNAMENT_NAME);
                            String tournamentInfo = jsonTournament.getString(Constant.TOURNAMENT_INFO);
                            Tournament tournament = new Tournament(tournamentID,tournamentName,tournamentInfo);
                            playerInfo.addClubTournament(selectedClubID,tournament);
                            tournamentNames.add(tournamentName);
                        }
                        // get player club total stats
                        JSONObject jsonPlayerClubStats = jsonObject.getJSONObject(Constant.TABLE_STATS);
                        int tournamentID = -1;
                        int clubID = selectedClubID;
                        int attendance = jsonPlayerClubStats.getInt(Constant.STATS_ATTENDANCE);
                        int appearance = jsonPlayerClubStats.getInt(Constant.STATS_APPEARANCE);
                        int start = jsonPlayerClubStats.getInt(Constant.STATS_START);
                        int goal = jsonPlayerClubStats.getInt(Constant.STATS_GOAL);
                        int penalty = jsonPlayerClubStats.getInt(Constant.STATS_PEN);
                        int freekick = jsonPlayerClubStats.getInt(Constant.STATS_FREEKICK);
                        int penaltyShootout = jsonPlayerClubStats.getInt(Constant.STATS_PEN_SHOOTOUT);
                        int penaltyTaken = jsonPlayerClubStats.getInt(Constant.STATS_PEN_TAKEN);
                        int ownGoal = jsonPlayerClubStats.getInt(Constant.STATS_OG);
                        int header = jsonPlayerClubStats.getInt(Constant.STATS_HEADER);
                        int weakFootGoal = jsonPlayerClubStats.getInt(Constant.STATS_WEAK_FOOT_GOAL);
                        int otherGoal = jsonPlayerClubStats.getInt(Constant.STATS_OTHER_GOAL);
                        int assist = jsonPlayerClubStats.getInt(Constant.STATS_ASSIST);
                        int yellow = jsonPlayerClubStats.getInt(Constant.STATS_YELLOW);
                        int red = jsonPlayerClubStats.getInt(Constant.STATS_RED);
                        int cleanSheet = jsonPlayerClubStats.getInt(Constant.STATS_CLEAN_SHEET);
                        int penaltySaved = jsonPlayerClubStats.getInt(Constant.STATS_PEN_SAVED);
                        playerClubStats = new Stats(tournamentID, clubID, playerID, attendance, appearance,
                                start, goal, penalty, freekick, penaltyShootout, penaltyTaken, ownGoal, header,
                                weakFootGoal,otherGoal, assist, yellow, red, cleanSheet, penaltySaved);
                        JSONObject jsonPlayerGamePerformance = jsonObject.getJSONObject(Constant.KEY_GAME_PERFORMANCE);
                        int win = jsonPlayerGamePerformance.getInt(Constant.PERFORMANCE_WIN);
                        int draw = jsonPlayerGamePerformance.getInt(Constant.PERFORMANCE_DRAW);
                        int loss = jsonPlayerGamePerformance.getInt(Constant.PERFORMANCE_LOSS);
                        playerClubStats.setWin(win);
                        playerClubStats.setDraw(draw);
                        playerClubStats.setLoss(loss);
                        renderTournamentList(selectedClubID);
                        // render view pager
                        showStats(playerClubStats);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        String url = UrlHelper.urlPlayerClubInfo(selectedClubID,playerInfo.getPlayer().getId());
        RequestHelper.sendGetRequest(url,actionGetPlayerClubStats);
    }

    /**
     * send a GET request to retrieve the player's stats in a specific tournament
     * @param tournamentID  ID of the specific tournament
     */
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
                        int freekick = jsonPlayerTournamentStats.getInt(Constant.STATS_FREEKICK);
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
                                start, goal, penalty, freekick, penaltyShootout, penaltyTaken, ownGoal, header,
                                weakFootGoal,otherGoal, assist, yellow, red, cleanSheet, penaltySaved);
                        JSONObject jsonPlayerTournamentPerformance = jsonResponse.getJSONObject(Constant.KEY_GAME_PERFORMANCE);
                        int win = jsonPlayerTournamentPerformance.getInt(Constant.PERFORMANCE_WIN);
                        int draw = jsonPlayerTournamentPerformance.getInt(Constant.PERFORMANCE_DRAW);
                        int loss = jsonPlayerTournamentPerformance.getInt(Constant.PERFORMANCE_LOSS);
                        playerTournamentStats.setWin(win);
                        playerTournamentStats.setDraw(draw);
                        playerTournamentStats.setLoss(loss);
                        // render view pager
                        showStats(playerTournamentStats);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String message = jsonResponse.getString(Constant.KEY_MSG);
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        String url = UrlHelper.urlStatsByTournamentClubPlayer(tournamentID,selectedClubID,playerInfo.getPlayer().getId());
        RequestHelper.sendGetRequest(url,actionGetPlayerTournamentStats);
    }

    /**
     * visualize the stats in the chart ViewPager fragment
     * @param stats the stats to be displayed
     */
    private void showStats(Stats stats) {
        Log.d(TAG,"show stats:"+stats.toJson());
        tabLayout.removeAllTabs();
        tabLayout.setBackgroundResource(R.drawable.background_light_grey_square);
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


        CustomFragmentAdapter adapter = new CustomFragmentAdapter(getChildFragmentManager());
        adapter.setFragments(fragments);
        viewPager.setAdapter(adapter);
    }

    /**
     *  display the player's overall stats (in all clubs and tournaments)
     */
    private void showPlayerTotalStats(){
        // hide tournament spinner, show player total stats
        sp_playerTournament.setVisibility(View.GONE);
        tv_playerTournament.setVisibility(View.GONE);
        showStats(playerInfo.getTotalStats());
    }

    /**
     *  display the player's specific club total stats (in all tournaments)
     *  the club stats is specified in a global variable (playerClubStats)
     */
    private void showPlayerClubStats() {
        showStats(playerClubStats);
    }

    /**
     * display the player's specific tournament stats
     * @param tournamentID  the ID of the specific tournament
     */
    private void showPlayerTournamentStats(int tournamentID) {
        loadPlayerTournamentStats(tournamentID);
    }

    /**
     * Fill the tournament spinner with all the tournaments the specified club has participated in
     * @param clubID ID of the specified club
     */
    private void renderTournamentList(int clubID) {
        tournamentNames = new ArrayList<String>();
        tournamentNames.add(Constant.OPTION_ALL_TOURNAMENTS);
        ArrayList<Tournament> tournaments = playerInfo.getClubTournaments(clubID);
        if ( tournaments != null && tournaments.size() > 0 ) {
            for (Tournament tournament : tournaments ) {
                tournamentNames.add(tournament.name);
            }
        }
        sp_playerTournament.setAdapter(new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                tournamentNames));
        sp_playerTournament.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ( position == 0 ) {  // show player's club total stats
                    showPlayerClubStats();
                } else {    // show player's tournament stats
                    for (Tournament tournament : playerInfo.getClubTournaments(selectedClubID)) {
                        if (tournament.name.equals(parent.getSelectedItem().toString())) {
                            showPlayerTournamentStats(tournament.id);
                            break;
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        sp_playerTournament.setVisibility(View.VISIBLE);
        tv_playerTournament.setVisibility(View.VISIBLE);
    }

    /**
     *  if this activity is not presenting the user's own profile, but another user profile
     *  call this method to limit access
     */
//    private void setVisitorMode(){
//        btn_club.setVisibility(View.INVISIBLE);
//        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        if ( actionBar != null ) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().finish();
//            }
//        });
//    }

    /**
     * display list of clubs the player is in
     */
    private void showPlayerClubList(){
        ClubListFragment fragment = ClubListFragment.newInstance(playerInfo.getClubs(),playerInfo.getPlayer());
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_content,fragment,Constant.FRAGMENT_PLAYER_CLUB_LIST);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onClubListChanged(ArrayList<Club> clubs){
        this.playerInfo.setClubs(clubs);
        showPlayerProfilePage();
    }

    /**
     * defines the onBackPressed behavior for PlayerProfileFragment
     * navigate back to previous tab if the first tab is not being displayed
     * @return if the onBackPressed event is handled by this fragment
     */
    public boolean onBackPressed(){
        int position = tabLayout.getSelectedTabPosition();
        if ( position > 0 ) {
            TabLayout.Tab tab = tabLayout.getTabAt(--position);
            if ( tab != null ) {
                tab.select();
                return true;
            }
        }
        return false;
    }
}
