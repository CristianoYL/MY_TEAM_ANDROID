package com.example.cristiano.myteam.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.fragment.ChartFragmentFactory;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.structure.Member;
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

/**
 *  this activity displays the main profile page of the player, which presents:
 *  1) the player's basic info
 *  2) the access to player's clubs
 *  3) stats data visualization
 *
 *  to start this activity, a bundle containing playerID or email must be passed within the intent
 *  an optional isVisitor boolean value can be passed to identify if the user is viewing other's profile
 */
public class PlayerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tv_name, tv_role,tv_playerTournament;
    private ImageView iv_avatar;
    private ConstraintLayout layout_profile,layout_club;
    private ListView lv_club;
    private Button btn_club, btn_createClub, btn_joinClub;
    private NavigationView navigationView;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Spinner sp_playerClub, sp_playerTournament;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;

    private String email;
    private int playerID;
    private PlayerInfo playerInfo;
    private boolean isVisitor = false;
    private int selectedClubID = 0;
    private ArrayList<String> clubNames, tournamentNames;
    private ArrayList<Club> clubList;
    private Stats playerClubStats;
    private SharedPreferences sharedPreferences;

    private int pageID = PAGE_PROFILE;
    private static final int PAGE_PROFILE = 0;
    private static final int PAGE_CLUB = 1;
    private static final int PAGE_LOGOUT = 2;
    private static final int PAGE_SETTINGS = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        toolbar = (Toolbar) findViewById(R.id.toolbar_player);
        setSupportActionBar(toolbar);
        layout_profile = (ConstraintLayout) findViewById(R.id.layout_profile);
        layout_club = (ConstraintLayout) findViewById(R.id.layout_club_list);
        tv_name = (TextView) findViewById(R.id.et_name);
        tv_role = (TextView) findViewById(R.id.tv_role);
        iv_avatar = (ImageView) findViewById(R.id.iv_otherAvatar);
        btn_club = (Button) findViewById(R.id.btn_club);
        btn_createClub = (Button) findViewById(R.id.btn_createClub);
        btn_joinClub = (Button) findViewById(R.id.btn_joinClub);
        lv_club = (ListView) findViewById(R.id.lv_club);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view_player);
        navigationView.setNavigationItemSelectedListener(this);

        // to start this activity, the intent must contain the player's email or ID
        Bundle bundle = getIntent().getExtras();
        if ( bundle == null ) {
            return;
        }
        if ( !bundle.containsKey(Constant.PLAYER_EMAIL) && !bundle.containsKey(Constant.KEY_PLAYER_ID) ) {
            Log.e("PlayerActivity","Missing player email / id");
            return;
        }
        if ( bundle.containsKey(Constant.KEY_IS_VISITOR) ) {
            this.isVisitor = bundle.getBoolean(Constant.KEY_IS_VISITOR);
            if ( isVisitor ) {
                setVisitorMode();
            }
        }
        this.email = bundle.getString(Constant.PLAYER_EMAIL,null);
        this.playerID = bundle.getInt(Constant.KEY_PLAYER_ID,0);
        sharedPreferences = getSharedPreferences(Constant.KEY_USER_PREF,MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ( this.isVisitor ) {
            this.setVisitorMode();
        }
        loadPlayerInfo();   // load player info in onResume() allows info updated when navigating back to this activity
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) { // if the drawer menu is open
            drawer.closeDrawer(GravityCompat.START);    // close menu
        }else if( pageID != PAGE_PROFILE ){ // if it's in other page of this activity
            showPlayerProfilePage();    // return to profile page
        }else if (isVisitor){   // if it's other user's profile page
            super.onBackPressed();  // return to the previous page
        } else {
            showLogoutPage();   // ask if to log out
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

    /**
     *  presents the player's profile page
     */
    private void showPlayerProfilePage(){
        // initialize profile page
        pageID = PAGE_PROFILE;
        setTitle(R.string.title_activity_profile);
        layout_profile.setVisibility(View.VISIBLE);
        layout_club.setVisibility(View.GONE);
        tv_name.setText(playerInfo.getPlayer().getDisplayName());
        tv_role.setText(playerInfo.getPlayer().getRole());
        View view = findViewById(R.id.layout_playerStats);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        sp_playerClub = (Spinner) findViewById(R.id.sp_playerClub);
        sp_playerTournament = (Spinner) findViewById(R.id.sp_playerTournament);
        tv_playerTournament = (TextView) findViewById(R.id.tv_playerTournament);
        clubNames = new ArrayList<>(playerInfo.getClubs().size());
        clubNames.add(Constant.OPTION_ALL_CLUBS);
        for ( int i = 0; i < playerInfo.getClubs().size(); i++ ) {
            clubNames.add(playerInfo.getClubs().get(i).name);
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

        // listen to the selection of the player's club spinner
        sp_playerClub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SELECTION CLUB",parent.getItemAtPosition(position).toString());
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
                    Log.e("PlayerStatsViewPager","ERROR: No Tab Found For Selected Page.");
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
     * go to the player registration page
     * @param email the email used to login
     * @param player  the Player instance
     */
    private void showRegistrationPage(String email, Player player){
        Intent intent = new Intent(PlayerActivity.this,PlayerRegistrationActivity.class);
        intent.putExtra(Constant.PLAYER_EMAIL,email);
        if ( player != null ) {
            intent.putExtra(Constant.KEY_PLAYER,player.toJson());
        }
        if ( isVisitor ) {
            intent.putExtra(Constant.KEY_IS_VISITOR,true);
        }
        startActivity(intent);
    }

    /**
     * display list of clubs the player is in
     */
    private void showPlayerClubPage(){
        pageID = PAGE_CLUB;
        layout_profile.setVisibility(View.GONE);
        layout_club.setVisibility(View.VISIBLE);
        setTitle("My Clubs");
        ClubListAdapter clubListAdapter = new ClubListAdapter(PlayerActivity.this,playerInfo.getClubs());
        lv_club.setAdapter(clubListAdapter);
        lv_club.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Club club = playerInfo.getClubs().get(i);
                if ( club.priority > 0 ) {
                    viewClub(playerInfo.getClubs().get(i));
                } else {
                    Toast.makeText(PlayerActivity.this, R.string.error_view_pending_club, Toast.LENGTH_SHORT).show();
                }

            }
        });
        btn_createClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateClubPage();
            }
        });

        btn_joinClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJoinClubPage();
            }
        });
    }

    /**
     * show a pop-up dialog to let the user create a new club
     */
    private void showCreateClubPage() {
        LayoutInflater inflater = LayoutInflater.from(PlayerActivity.this);
        View v_event = inflater.inflate(R.layout.layout_reg_name_info, null);
        final TextView tv_name = (TextView) v_event.findViewById(R.id.et_name);
        final TextView tv_info = (TextView) v_event.findViewById(R.id.et_info);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Create a club");
        dialogBuilder.setPositiveButton(R.string.label_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String clubName = tv_name.getText().toString();
                String clubInfo = tv_info.getText().toString();
                if ( clubName.equals("") ) {
                    Toast.makeText(PlayerActivity.this, R.string.error_empty_club_name, Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( clubInfo.equals("") ) {
                    Toast.makeText(PlayerActivity.this,R.string.error_empty_club_info, Toast.LENGTH_SHORT).show();
                    return;
                }
                int clubID = 0;
                Club regClub = new Club(clubID,clubName,clubInfo);
                RequestAction actionPostClub = new RequestAction() {
                    @Override
                    public void actOnPre() {

                    }

                    @Override
                    public void actOnPost(int responseCode, String response) {
                        if ( responseCode == 201 ) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                int clubID = jsonObject.getInt(Constant.CLUB_ID);
                                String name = jsonObject.getString(Constant.CLUB_NAME);
                                String info = jsonObject.getString(Constant.CLUB_INFO);
                                Club newClub = new Club(clubID,name,info);
                                newClub.priority = 3;
                                playerInfo.addClub(newClub);
                                Toast.makeText(PlayerActivity.this,"Club Created!",Toast.LENGTH_LONG).show();
                                showPlayerClubPage();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(PlayerActivity.this,response,Toast.LENGTH_LONG).show();
                            }
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString(Constant.KEY_MSG);
                                Toast.makeText(PlayerActivity.this,message,Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(PlayerActivity.this,response,Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                };
                String url = UrlHelper.urlPostRegClub(playerInfo.getPlayer().getId());
                RequestHelper.sendPostRequest(url,regClub.toJson(),actionPostClub);
            }
        });
        dialogBuilder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
            }
        });
        dialogBuilder.setView(v_event);
        dialogBuilder.setCancelable(true);
        dialogBuilder.show();
    }

    /**
     *  show the join club page to let the user search and join club
     */

    EditText et_search;
    ListView lv_searchResult;
    Button btn_join;
    AlertDialog dialog;
    private void showJoinClubPage(){
        LayoutInflater inflater = LayoutInflater.from(PlayerActivity.this);
        View view_search = inflater.inflate(R.layout.layout_search_club, null);
        Spinner sp_searchKey = (Spinner) view_search.findViewById(R.id.sp_searchKey);
        et_search = (EditText) view_search.findViewById(R.id.et_search);
        FloatingActionButton fab_search = (FloatingActionButton) view_search.findViewById(R.id.fab_search);
        btn_join = (Button) view_search.findViewById(R.id.btn_join);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PlayerActivity.this);
        dialogBuilder.setTitle("Join Club");
        dialogBuilder.setView(view_search);
        dialogBuilder.setCancelable(true);
        lv_searchResult = (ListView) view_search.findViewById(R.id.lv_searchResult);

        String[] keys = {"Club ID", "Club Name"};
        sp_searchKey.setAdapter(new ArrayAdapter<String>(PlayerActivity.this,android.R.layout.simple_list_item_1,keys));
        sp_searchKey.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ( parent.getSelectedItem().toString().equals("Club ID")) {
                    et_search.setHint("Club ID");
                    et_search.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    et_search.setHint("Club Name");
                    et_search.setInputType(InputType.TYPE_NULL);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_search.getText())) {
                    return;
                }

                RequestAction actionGetClubByID = new RequestAction() {
                    @Override
                    public void actOnPre() {
                    }

                    @Override
                    public void actOnPost(int responseCode, String response) {
                        if ( responseCode == 200 ) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                int clubID = jsonObject.getInt(Constant.CLUB_ID);
                                String name = jsonObject.getString(Constant.CLUB_NAME);
                                String info = jsonObject.getString(Constant.CLUB_INFO);
                                Club club = new Club(clubID,name,info);
                                ArrayList<String> clubNames = new ArrayList<>();
                                clubList = new ArrayList<Club>();
                                clubList.add(club);
                                clubNames.add(club.name);
                                lv_searchResult.setAdapter(new ArrayAdapter<String>(
                                        PlayerActivity.this,
                                        android.R.layout.simple_list_item_single_choice,
                                        clubNames
                                ));
                                lv_searchResult.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(PlayerActivity.this, "Error while searching for club", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString(Constant.KEY_MSG);
                                Toast.makeText(PlayerActivity.this, message, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(PlayerActivity.this, "Error while searching for club", Toast.LENGTH_SHORT).show();
                                Log.e("SEARCH CLUB",response);
                            }
                        }
                    }
                };

                RequestAction actionGetClubByName = new RequestAction() {
                    @Override
                    public void actOnPre() {
                    }

                    @Override
                    public void actOnPost(int responseCode, String response) {
                        if ( responseCode == 200 ) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.CLUB_LIST);
                                ArrayList<String> clubNames = new ArrayList<>();
                                clubList = new ArrayList<Club>();
                                if ( jsonArray.length() == 0 ) {
                                    Toast.makeText(PlayerActivity.this, "No club named<" + et_search.getText() + "> found!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                for ( int i = 0; i < jsonArray.length(); i++ ) {
                                    JSONObject jsonClub = jsonArray.getJSONObject(i);
                                    int clubID = jsonClub.getInt(Constant.CLUB_ID);
                                    String name = jsonClub.getString(Constant.CLUB_NAME);
                                    String info = jsonClub.getString(Constant.CLUB_INFO);
                                    Club club = new Club(clubID,name,info);
                                    clubList.add(club);
                                    clubNames.add(club.name);
                                }
                                lv_searchResult.setAdapter(new ArrayAdapter<String>(
                                        PlayerActivity.this,
                                        android.R.layout.simple_list_item_single_choice,
                                        clubNames
                                ));
                                lv_searchResult.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(PlayerActivity.this, "Error while searching for club", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString(Constant.KEY_MSG);
                                Toast.makeText(PlayerActivity.this, message, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(PlayerActivity.this, "Error while searching for club", Toast.LENGTH_SHORT).show();
                                Log.e("SEARCH CLUB",response);
                            }
                        }
                    }
                };
                String url;
                if ( et_search.getInputType() == InputType.TYPE_CLASS_NUMBER) {
                    url = UrlHelper.urlGetClubByID(Integer.parseInt(et_search.getText().toString()));
                    RequestHelper.sendGetRequest(url,actionGetClubByID);
                } else {
                    url = UrlHelper.urlGetClubByName(et_search.getText().toString());
                    RequestHelper.sendGetRequest(url,actionGetClubByName);
                }
            }
        });
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( lv_searchResult.getCheckedItemCount() == 0 ) {
                    Toast.makeText(PlayerActivity.this, "Please select a club.", Toast.LENGTH_SHORT).show();
                    return;
                }
                RequestAction actionPostClubRequest = new RequestAction() {
                    @Override
                    public void actOnPre() {

                    }

                    @Override
                    public void actOnPost(int responseCode, String response) {
                        if ( responseCode == 201 ) {
                            Toast.makeText(PlayerActivity.this, "Join club request sent. Please wait for club admins to handle the request.", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString(Constant.KEY_MSG);
                                Toast.makeText(PlayerActivity.this, message, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(PlayerActivity.this, response, Toast.LENGTH_SHORT).show();
                            }
                        }
                        dialog.dismiss();
                    }
                };
                int clubID = clubList.get(lv_searchResult.getCheckedItemPosition()).id;
                String url = UrlHelper.urlPostClubRequest(clubID);
                Member member = new Member(clubID,playerID,null,false,0);
                RequestHelper.sendPostRequest(url,member.toJson(),actionPostClubRequest);
            }
        });
        dialog = dialogBuilder.show();

    }

    /**
     * show a pop-up dialog to ask the user whether to logout
     */
    private void showLogoutPage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this,0);
        builder.setTitle("Warning");
        builder.setMessage("Are you sure to logout?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(PlayerActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    /**
     * currently, the setting page is the player profile update page
     */
    private void showSettingsPage() {
        showRegistrationPage(email,playerInfo.getPlayer());
    }

    /**
     * go to the according club activity
     * @param club the club to view
     */
    private void viewClub(Club club) {
        Intent intent = new Intent(PlayerActivity.this,ClubActivity.class);
        intent.putExtra(Constant.TABLE_CLUB,club.toJson());
        intent.putExtra(Constant.TABLE_PLAYER,playerInfo.getPlayer().toJson());
        startActivity(intent);
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
                        playerID = jsonPlayer.getInt(Constant.PLAYER_ID);
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
                        Player player = new Player(playerID,email,firstName,lastName,displayName,role,phone,age,weight,height,leftFooted,avatar);

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
                        int defaultClubID = sharedPreferences.getInt(Constant.KEY_DEFAULT_CLUB_ID,0);
                        if ( defaultClubID != 0 ) { // if default club has been set
                            for ( Club club : myClubs ) {
                                if ( club.id == defaultClubID ) {
                                    club.isDefault = true;
                                }
                            }
                        } else {    // if not set
                            if ( myClubs.size() > 0 ) {
                                myClubs.get(0).isDefault = true;
                            }
                        }
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
                } else if ( responseCode == 404 ){  // if the player's info is not found
                    // it means it's a new user, let the user create his player profile first
                    showRegistrationPage(email,null);
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
        String url;
        if ( playerID != 0) {   // if the playerID is specified, use the playerID to retrieve the info
            url = UrlHelper.urlGetPlayerInfo(playerID);
            Log.d("PlayerActivity","Load by ID");
        } else {    // otherwise use the email to retrieve the info
            url = UrlHelper.urlGetPlayerInfo(email);
            Log.d("PlayerActivity","Load by email");
        }
        RequestHelper.sendGetRequest(url,actionGetPlayerInfo);
    }

    /**
     * send a GET request to retrieve the player's total stats in the specified club
     * @param clubID    ID of the specified club
     */
    private void loadPlayerClubStats(int clubID){
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
                        Tournament[] tournaments = new Tournament[jsonTournaments.length()];
                        tournamentNames = new ArrayList<String>();
                        tournamentNames.add(Constant.OPTION_ALL_TOURNAMENTS);
                        for ( int i = 0; i < jsonTournaments.length(); i++ ) {
                            JSONObject jsonTournament = jsonTournaments.getJSONObject(i);
                            int tournamentID = jsonTournament.getInt(Constant.TOURNAMENT_ID);
                            String tournamentName = jsonTournament.getString(Constant.TOURNAMENT_NAME);
                            String tournamentInfo = jsonTournament.getString(Constant.TOURNAMENT_INFO);
                            tournaments[i] = new Tournament(tournamentID,tournamentName,tournamentInfo);
                            tournamentNames.add(tournamentName);
                        }
                        playerInfo.addClubTournament(selectedClubID,tournaments);
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
        String url = UrlHelper.urlGetPlayerClubInfo(selectedClubID,playerInfo.getPlayer().getId());
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
                        Toast.makeText(PlayerActivity.this, message, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PlayerActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        String url = UrlHelper.urlGetPlayerTournamentStats(tournamentID,selectedClubID,playerInfo.getPlayer().getId());
        RequestHelper.sendGetRequest(url,actionGetPlayerTournamentStats);
    }

    /**
     * visualize the stats in the chart ViewPager fragment
     * @param stats the stats to be displayed
     */
    private void showStats(Stats stats) {
        tabLayout.removeAllTabs();
        tabLayout.setBackgroundResource(R.drawable.card_border_light_grey);
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
        Tournament[] tournaments = playerInfo.getClubTournaments(clubID);
        if ( tournaments != null && tournaments.length > 0 ) {
            for (Tournament tournament : tournaments ) {
                tournamentNames.add(tournament.name);
            }
        }
        sp_playerTournament.setAdapter(new ArrayAdapter<String>(
                PlayerActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                tournamentNames));
        sp_playerTournament.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SELECTION TOUR",parent.getItemAtPosition(position).toString());
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
    private void setVisitorMode(){
        btn_club.setVisibility(View.INVISIBLE);
        ActionBar actionBar = getSupportActionBar();
        if ( actionBar != null ) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
