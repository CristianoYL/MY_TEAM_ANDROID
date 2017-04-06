package com.example.cristiano.myteam.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.request.RequestHelper;

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
    private String[] clubs,info;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
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

        navigationView = (NavigationView) findViewById(R.id.nav_view_profile);
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
                showClubPage();
            }
        } else if (id == R.id.nav_logout) {
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
                   } else if ( pageID == PAGE_SETTINGS ) {
                       navigationView.setCheckedItem(R.id.nav_settings);
                   }
                }
            });
            builder.show();
        } else if (id == R.id.nav_settings) {
//            pageID = PAGE_SETTINGS;
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
                showClubPage();
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
        if ( selectedStats == null ) {
            selectedStats = new HashMap<>(8);
            selectedStats.put(Constant.STATS_WIN,10);
            selectedStats.put(Constant.STATS_DRAW,2);
            selectedStats.put(Constant.STATS_LOSS,1);
            selectedStats.put(Constant.STATS_ATTENDANCE,10);
            selectedStats.put(Constant.STATS_APPEARANCE,10);
            selectedStats.put(Constant.STATS_START,4);
            selectedStats.put(Constant.STATS_GOAL,2);
            selectedStats.put(Constant.STATS_ASSIST,6);
//            return;
        }
        View[] cards = new View[8];
        cards[0] = findViewById(R.id.card1);
        cards[1] = findViewById(R.id.card2);
        cards[2] = findViewById(R.id.card3);
        cards[3] = findViewById(R.id.card4);
        cards[4] = findViewById(R.id.card5);
        cards[5] = findViewById(R.id.card6);
        cards[6] = findViewById(R.id.card7);
        cards[7] = findViewById(R.id.card8);
        View view;
        TextView tv_title, tv_stats;
        int counter = 0;
        for ( String statsKey : selectedStats.keySet() ) {
            view = cards[counter++];
            tv_title = (TextView) view.findViewById(R.id.tv_cardTitle);
            tv_stats = (TextView) view.findViewById(R.id.tv_cardContent);
            tv_title.setText(statsKey);
            tv_stats.setText(selectedStats.get(statsKey)+"");
        }
    }

    private void showClubPage(){
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
                        for ( int i = 0; i < jsonArray.length(); i++ ) {
                            clubs[i] = jsonArray.getJSONObject(i).getString(Constant.COLUMN_CLUB_NAME);
                        }
                        lv_club.setAdapter(new ArrayAdapter<String>(PlayerActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, clubs));
                        lv_club.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Toast.makeText(PlayerActivity.this,clubs[i],Toast.LENGTH_SHORT).show();
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
}
