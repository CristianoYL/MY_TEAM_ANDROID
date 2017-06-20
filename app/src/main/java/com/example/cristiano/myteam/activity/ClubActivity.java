package com.example.cristiano.myteam.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.example.cristiano.myteam.R;

import com.example.cristiano.myteam.fragment.ClubFragment;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.LogOutHelper;
import com.google.gson.Gson;

/**
 *  this activity holds a frame that contains certain fragments,
 *  which will present the club profile, members or tournaments
 */
public class ClubActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Club club;
    private Player player;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);
        sharedPreferences = getSharedPreferences(Constant.KEY_USER_PREF,MODE_PRIVATE);
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
        if ( !bundle.containsKey(Constant.TABLE_CLUB) ||
                !bundle.containsKey(Constant.TABLE_PLAYER) ) {
            Log.e("ClubActivity","club/player not specified!");
            return;
        }
        Gson gson = new Gson();
        this.club = gson.fromJson(bundle.getString(Constant.TABLE_CLUB),Club.class);
        this.player = gson.fromJson(bundle.getString(Constant.TABLE_PLAYER),Player.class);
        showClubPage();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            ClubFragment fragment = (ClubFragment)fragmentManager.findFragmentByTag(Constant.FRAGMENT_CLUB);
            if ( fragment != null && fragment.isVisible() ) {
                if ( !fragment.onBackPressed() ) {
                    showLogoutPage();
                }
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
            // do nothing
        } else if (id == R.id.nav_profile) {
            showPlayerPage(player.getId());
        } else if (id == R.id.nav_logout) {
            showLogoutPage();
            return false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * use a fragment to display the club's profile page
     */
    private void showClubPage(){
        ClubFragment clubFragment = ClubFragment.newInstance(club,player);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content,clubFragment,Constant.FRAGMENT_CLUB);
        fragmentTransaction.commit();
    }

    private void showPlayerPage(int playerID) {
        Intent intent = new Intent(ClubActivity.this,PlayerActivity.class);
        intent.putExtra(Constant.KEY_PLAYER_ID,playerID);
        startActivity(intent);
    }

    /**
     * show a pop-up dialog to ask the user whether to logout
     */
    private void showLogoutPage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClubActivity.this);
        builder.setTitle("Warning");
        builder.setMessage("Are you sure to logout?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LogOutHelper.logOut(ClubActivity.this);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }
}
