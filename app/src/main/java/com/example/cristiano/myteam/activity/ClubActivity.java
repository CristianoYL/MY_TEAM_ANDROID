package com.example.cristiano.myteam.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.cristiano.myteam.R;

import com.example.cristiano.myteam.fragment.ClubFragment;
import com.example.cristiano.myteam.fragment.TournamentFragment;
import com.example.cristiano.myteam.fragment.TournamentListFragment;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.structure.ClubInfo;
import com.example.cristiano.myteam.structure.Member;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.structure.Tournament;
import com.example.cristiano.myteam.util.AppController;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *  this activity holds a frame that contains certain fragments,
 *  which will present the club profile, members or tournaments
 */
public class ClubActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Club club;
    private Player player;
    private ClubInfo clubInfo;
    private NavigationView navigationView;

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

        navigationView = (NavigationView) findViewById(R.id.nav_view_club);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle bundle = getIntent().getExtras();
        if ( !bundle.containsKey(Constant.TABLE_CLUB) ||
                !bundle.containsKey(Constant.TABLE_PLAYER) ) {
            Log.e("ClubActivity","club/player not specified!");
            return;
        }
        Gson gson = new Gson();
        this.club = gson.fromJson(bundle.getString(Constant.TABLE_CLUB),Club.class);
        this.player = gson.fromJson(bundle.getString(Constant.TABLE_PLAYER),Player.class);
        getProfile();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            ClubFragment clubFragment = (ClubFragment)fragmentManager.findFragmentByTag(Constant.FRAGMENT_CLUB);
            TournamentFragment tournamentFragment = (TournamentFragment) fragmentManager.findFragmentByTag(Constant.FRAGMENT_TOURNAMENT);
            TournamentListFragment tournamentListFragment = (TournamentListFragment) fragmentManager.findFragmentByTag(Constant.FRAGMENT_CLUB_TOURNAMENT_LIST);
            if ( clubFragment != null && clubFragment.isVisible() ) {
                if ( !clubFragment.onBackPressed() ) {
                    AppController.minimizeOnDoubleBack(this);    // minimizeOnDoubleBack the app on double click
                }
            } else if ( tournamentFragment != null && tournamentFragment.isVisible() ) {
                if ( !tournamentFragment.onBackPressed() ) {
                    showClubPage();
                }
            } else if ( tournamentListFragment != null && tournamentListFragment.isVisible() ) {
                showClubPage();
            } else {
                showClubPage();
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

        if (id == R.id.nav_club) {
            showClubPage();
        } else if (id == R.id.nav_tournament) {
            showTournamentPage();
        } else if (id == R.id.nav_profile) {
            showPlayerPage();
        } else if (id == R.id.nav_logout) {
            showLogoutPage();
            return false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * send a GET request to retrieve the club's basic info
     */
    private void getProfile(){
        RequestAction actionGetClubInfo = new RequestAction() {
            @Override
            public void actOnPre() {
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonClubInfo = jsonObject.getJSONObject(Constant.KEY_CLUB_INFO);
                        // get club
                        JSONObject jsonClub = jsonClubInfo.getJSONObject(Constant.TABLE_CLUB);
                        int id = jsonClub.getInt(Constant.CLUB_ID);
                        String name = jsonClub.getString(Constant.CLUB_NAME);
                        String info = jsonClub.getString(Constant.CLUB_INFO);
                        Club club = new Club(id,name,info);
                        // get club's tournaments
                        JSONArray tournamentList = jsonClubInfo.getJSONArray(Constant.TOURNAMENT_LIST);
                        List<Tournament> tournaments = new ArrayList<>(tournamentList.length());
                        for ( int i = 0; i < tournamentList.length(); i++ ) {
                            JSONObject jsonTournament = tournamentList.getJSONObject(i);
                            int tournamentID = jsonTournament.getInt(Constant.TOURNAMENT_ID);
                            String tournamentName = jsonTournament.getString(Constant.TOURNAMENT_NAME);
                            String tournamentInfo = jsonTournament.getString(Constant.TOURNAMENT_INFO);
                            tournaments.add(new Tournament(tournamentID,tournamentName,tournamentInfo));
                        }
                        // get club's member
                        JSONArray memberList = jsonClubInfo.getJSONArray(Constant.TABLE_MEMBER);
                        List<Member> member = new ArrayList<>(memberList.length());
                        for ( int i = 0; i < memberList.length(); i++ ) {
                            JSONObject jsonMember = memberList.getJSONObject(i);
                            int playerID = jsonMember.getInt(Constant.MEMBER_P_ID);
                            int clubID = jsonMember.getInt(Constant.MEMBER_C_ID);
                            String memberSince = jsonMember.getString(Constant.MEMBER_SINCE);
                            boolean isActive = jsonMember.getBoolean(Constant.MEMBER_IS_ACTIVE);
                            int priority = jsonMember.getInt(Constant.MEMBER_PRIORITY);
                            member.add(new Member(playerID,clubID,memberSince,isActive,priority));
                        }
                        clubInfo = new ClubInfo(club,tournaments, member);
                        showClubPage();
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
        String url = UrlHelper.urlClubInfoByID(club.id);
        RequestHelper.sendGetRequest(url,actionGetClubInfo);
    }

    /**
     * use a fragment to display the club's profile page
     */
    private void showClubPage(){
        navigationView.setCheckedItem(R.id.nav_club);
        ClubFragment clubFragment = ClubFragment.newInstance(club,player);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content,clubFragment,Constant.FRAGMENT_CLUB);
        fragmentTransaction.commit();
    }

    private void showTournamentPage(){
        TournamentListFragment fragment = TournamentListFragment.newInstance(clubInfo.getTournaments(),club,player);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_content,fragment,Constant.FRAGMENT_CLUB_TOURNAMENT_LIST);
        transaction.commit();
    }

    private void showPlayerPage() {
        Intent intent = new Intent(ClubActivity.this,PlayerActivity.class);
        intent.putExtra(Constant.KEY_PLAYER_ID,player.getId());
        startActivity(intent);
    }

    /**
     * show a pop-up dialog to ask the user whether to logout
     */
    private void showLogoutPage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClubActivity.this);
        builder.setTitle(R.string.warning);
        builder.setMessage(R.string.prompt_logout);
        builder.setPositiveButton(R.string.label_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AppController.logOut(ClubActivity.this);
            }
        });
        builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }
}
