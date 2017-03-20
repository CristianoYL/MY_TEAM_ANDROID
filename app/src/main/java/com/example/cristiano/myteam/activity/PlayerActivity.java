package com.example.cristiano.myteam.activity;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.util.Constant;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tv_name, tv_role, tv_club;
    private ImageView iv_avatar;
    private ConstraintLayout layout_registration, layout_profile;
    private FloatingActionButton fab_right, fab_left;

    private boolean isEditing;
    private HashMap<String,String> playerInfo;
    private ArrayList<String> selectedStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        layout_registration = (ConstraintLayout) findViewById(R.id.layout_registration);
        layout_profile = (ConstraintLayout) findViewById(R.id.layout_profile);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_role = (TextView) findViewById(R.id.tv_role);
        tv_club = (TextView) findViewById(R.id.tv_club);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        fab_right = (FloatingActionButton) findViewById(R.id.fab_right);
        fab_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if ( isEditing ) {
                    showProfilePage(playerInfo);
                } else {
                    showRegistrationPage();
                }
            }
        });

        fab_left = (FloatingActionButton) findViewById(R.id.fab_left);
        fab_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if ( isEditing ) {
                    showProfilePage(playerInfo);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_profile);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle bundle = getIntent().getExtras();
        if ( !bundle.containsKey(Constant.PLAYER_INFO) ) {
            showRegistrationPage();
        } else {
            playerInfo = (HashMap<String, String>) bundle.get(Constant.PLAYER_INFO);
            if ( !bundle.containsKey(Constant.PLAYER_SELECTED_STATS) ) {
                showProfilePage(playerInfo);
            } else {
                selectedStats = (ArrayList<String>) bundle.get(Constant.PLAYER_SELECTED_STATS);
                showProfilePage(playerInfo,selectedStats);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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

        } else if (id == R.id.nav_club) {

        } else if (id == R.id.nav_logout) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     *  use the data received within Intent to render the profile page.
     *  if their is user specific stats to display, render the stats cards accordingly
     * @param info  user's info stored in HashMap
     * @param selectedStats keys of user's selected stats entries
     */
    private void renderPlayerInfo(HashMap<String, String> info, ArrayList<String> selectedStats) {
        if ( info == null ) {
            return;
        }
        tv_name.setText(info.get(Constant.PLAYER_DISPLAY_NAME));
        tv_role.setText(info.get(Constant.PLAYER_ROLE));
        tv_club.setText(info.get(Constant.PLAYER_CLUB));
        if ( selectedStats == null ) {
            return;
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

        for ( int i = 0; i < selectedStats.size(); i++ ) {
            view = cards[i];
            tv_title = (TextView) view.findViewById(R.id.tv_cardTitle);
            tv_stats = (TextView) view.findViewById(R.id.tv_cardContent);
            tv_title.setText(selectedStats.get(i));
            tv_stats.setText(info.get(selectedStats.get(i)));
        }
    }

    /**
     * displays the registration page
     */
    private void showRegistrationPage(){
        layout_registration.setVisibility(View.VISIBLE);
        layout_profile.setVisibility(View.GONE);
        fab_left.setVisibility(View.VISIBLE);
        fab_right.setImageResource(android.R.drawable.ic_menu_save);
        isEditing = true;
    }

    /**
     *  display the user's profile page
     * @param playerInfo user's info stored in HashMap
     * @param selectedStats keys of user's selected stats entries
     */
    private void showProfilePage(HashMap<String,String> playerInfo, ArrayList selectedStats){
        layout_registration.setVisibility(View.GONE);
        layout_profile.setVisibility(View.VISIBLE);
        fab_left.setVisibility(View.INVISIBLE);
        fab_right.setImageResource(android.R.drawable.ic_menu_edit);
        isEditing = false;
        renderPlayerInfo(playerInfo,selectedStats);
    }

    /**
     * display profile page with default stats options
     * @param playerInfo
     */
    private void showProfilePage(HashMap<String,String> playerInfo){
        showProfilePage(playerInfo, null);
    }
}
