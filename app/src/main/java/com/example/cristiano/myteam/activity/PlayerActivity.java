package com.example.cristiano.myteam.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.fragment.PieChartFragment;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.util.CustomPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tv_name, tv_role;
    private ImageView iv_avatar;
    private ConstraintLayout layout_profile,layout_club;
    private FloatingActionButton fab_right, fab_left;
    private ListView lv_club;
    private Button btn_club;
    private NavigationView navigationView;

    private String email;
    private String[] clubs;
    private Club[] myClubs;
    private Player player;

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
        fab_right = (FloatingActionButton) findViewById(R.id.fab_right);
        fab_left = (FloatingActionButton) findViewById(R.id.fab_left);
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
        loadPlayerProfile();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if( pageID != PAGE_PROFILE ){
            showProfilePage(null);
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
                showProfilePage(null);
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
     *
     */
    private void loadPlayerProfile() {
        String url = Constant.URL_GET_PLAYER + this.email;
        RequestAction actionGetPlayer = new RequestAction() {
            @Override
            public void actOnPre() {

            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {    // player found, render player profile
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int id = jsonObject.getInt(Constant.PLAYER_ID);
                        String firstName = jsonObject.getString(Constant.PLAYER_FIRST_NAME);
                        String lastName = jsonObject.getString(Constant.PLAYER_LAST_NAME);
                        String displayName = jsonObject.getString(Constant.PLAYER_DISPLAY_NAME);
                        String role = jsonObject.getString(Constant.PLAYER_ROLE);
                        String phone = jsonObject.getString(Constant.PLAYER_PHONE);
                        int age = jsonObject.getInt(Constant.PLAYER_AGE);
                        float weight = (float) jsonObject.getDouble(Constant.PLAYER_WEIGHT);
                        float height = (float) jsonObject.getDouble(Constant.PLAYER_HEIGHT);
                        boolean leftFooted = jsonObject.getBoolean(Constant.PLAYER_FOOT);
                        int avatar = jsonObject.getInt(Constant.PLAYER_AVATAR);

                        player = new Player(id,email,firstName,lastName,displayName,role,phone,age,weight,height,leftFooted,avatar);
                        showProfilePage(null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if ( responseCode == 404 ) { // player not found, go to registration page
                    showRegistrationPage(email,null);
                } else {    // unknown error
                    Log.e("PlayerActivity","Error in loading player profile.\nError Message:\n" + response);
                }
            }
        };
        RequestHelper.sendGetRequest(url,actionGetPlayer);
    }

    /**
     * displays the registration page
     */
    private void showRegistrationPage(String email, Player player){
        Intent intent = new Intent(PlayerActivity.this,PlayerRegistrationActivity.class);
        intent.putExtra(Constant.PLAYER_EMAIL,email);
        if ( player != null ) {
            intent.putExtra(Constant.PLAYER_INFO,player);
        }
        startActivity(intent);
    }

    /**
     *  display the user's profile page
     * @param selectedStats user's selected stats entries
     */
    private void showProfilePage(HashMap<String,Integer> selectedStats){
        pageID = PAGE_PROFILE;
        setTitle(R.string.title_activity_profile);
        layout_profile.setVisibility(View.VISIBLE);
        layout_club.setVisibility(View.GONE);
        fab_right.setVisibility(View.VISIBLE);
        fab_left.setVisibility(View.GONE);
        fab_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegistrationPage(email,player);
            }
        });
        btn_club.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlayerClubPage();
            }
        });
        switch ( player.avatar ) {
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
        tv_name.setText(player.displayName);
        tv_role.setText(player.role);

        View view = findViewById(R.id.layout_playerStats);
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);

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
                tabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        String[] titles = {"Game Results","Penalty History","Attendance"};
        String[] centerText = {"Games Played","Penalty Taken","Total Games"};
        ArrayList<String[]> dataX = new ArrayList<>();
        dataX.add(new String[]{"WIN","DRAW","LOSS"});
        dataX.add(new String[]{"Penalty Scored", "Penalty Missed"});
        dataX.add(new String[]{"SHOW","NO SHOW"});
        ArrayList<float[]> dataY = new ArrayList<>();
        dataY.add(new float[]{21,2,5});
        dataY.add(new float[]{5,1});
        dataY.add(new float[]{28,2});
        Fragment[] fragments = new Fragment[titles.length];
        for ( int i = 0; i < fragments.length; i++ ) {
            tabLayout.addTab(tabLayout.newTab().setText(titles[i]));
            fragments[i] = PieChartFragment.newInstance(titles[i],centerText[i],true,dataX.get(i),dataY.get(i));
        }
        CustomPagerAdapter adapter = new CustomPagerAdapter(getSupportFragmentManager());
        adapter.setFragments(fragments);
        viewPager.setAdapter(adapter);
    }

    private void showPlayerClubPage(){
        pageID = PAGE_CLUB;
        layout_profile.setVisibility(View.GONE);
        layout_club.setVisibility(View.VISIBLE);
        fab_right.setVisibility(View.GONE);
        fab_left.setVisibility(View.GONE);
        setTitle("My Clubs");
        fab_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Create a new club", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        RequestAction actionGetClubs = new RequestAction() {
            @Override
            public void actOnPre() {
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.TABLE_TEAMSHEET);
                        clubs = new String[jsonArray.length()];
                        myClubs = new Club[jsonArray.length()];
                        int clubID;
                        String clubName, clubInfo;
                        for ( int i = 0; i < jsonArray.length(); i++ ) {
                            clubs[i] = jsonArray.getJSONObject(i).getString(Constant.CLUB_NAME);
                            clubID = jsonArray.getJSONObject(i).getInt(Constant.CLUB_ID);
                            clubName = jsonArray.getJSONObject(i).getString(Constant.CLUB_NAME);
                            clubInfo = jsonArray.getJSONObject(i).getString(Constant.CLUB_INFO);
                            myClubs[i] = new Club(clubID,clubName,clubInfo);
                        }
                        lv_club.setAdapter(new ArrayAdapter<String>(PlayerActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, clubs));
                        lv_club.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                viewClub(i);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Toast.makeText(PlayerActivity.this,jsonObject.getString(Constant.KEY_MSG),Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PlayerActivity.this,response,Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        String url = Constant.URL_GET_PLAYER_CLUBS + player.id;
        RequestHelper.sendGetRequest(url,actionGetClubs);
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
        showRegistrationPage(email,player);
    }

    private void viewClub(int clubID) {
        Intent intent = new Intent(PlayerActivity.this,ClubActivity.class);
        startActivity(intent);
    }
}
