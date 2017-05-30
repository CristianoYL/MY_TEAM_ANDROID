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
}
