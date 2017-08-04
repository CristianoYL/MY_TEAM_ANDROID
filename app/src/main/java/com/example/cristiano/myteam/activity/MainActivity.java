package com.example.cristiano.myteam.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
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

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.fragment.ClubFragment;
import com.example.cristiano.myteam.fragment.ClubListFragment;
import com.example.cristiano.myteam.fragment.MapFragment;
import com.example.cristiano.myteam.fragment.PlayerProfileFragment;
import com.example.cristiano.myteam.fragment.TournamentFragment;
import com.example.cristiano.myteam.fragment.TournamentListFragment;
import com.example.cristiano.myteam.fragment.VisitorViewFragment;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.structure.Token;
import com.example.cristiano.myteam.util.AppController;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.util.UrlHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *  this activity displays the main profile page of the player, which presents:
 *  1) the player's basic info
 *  2) the access to player's clubs
 *  3) stats data visualization
 *
 *  to start this activity, a bundle containing playerID or email must be passed within the intent
 *  an optional isVisitor boolean value can be passed to identify if the user is viewing other's profile
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ClubListFragment.OnClubListChangeListener, PlayerProfileFragment.OnClubListLoadedListener,
        MapFragment.OnCreateEventRequestListener{

    private final static String TAG = "MainActivity";

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;

    private Player player;  // self
    private Club club;  // the current club to view
    private ArrayList<Club> clubs;  // list of all clubs the player is in

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavigationView();

        sharedPreferences = getSharedPreferences(Constant.KEY_USER_PREF,MODE_PRIVATE);
        // to start this activity, the intent must contain the player's email or ID
        Bundle bundle = getIntent().getExtras();
        if ( bundle == null ) {
            return;
        }
        Gson gson = new Gson();
        String jsonPlayer = bundle.getString(Constant.TABLE_PLAYER,null);
        if ( jsonPlayer == null ) {
            Log.e(TAG,"Missing player info");
            return;
        }
        player = gson.fromJson(jsonPlayer,Player.class);

        // update cached user's player ID
        int myPlayerID = sharedPreferences.getInt(Constant.CACHE_PLAYER_ID,0);
        if ( myPlayerID == 0 || myPlayerID != player.getId() ){   // update cached playerID
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Constant.CACHE_PLAYER_ID,player.getId());
            editor.apply();
        }
        uploadIIDToken();   // update the FireBase Instance ID Token on app server

        String jsonClub = bundle.getString(Constant.TABLE_CLUB,null);
        if ( jsonClub != null ) {
            club = gson.fromJson(jsonClub,Club.class);
            Log.d(TAG,"Go to default club page. Club<ID:" + club.id + ">");
            showClubPage();
        } else {
            showPlayerProfilePage();
        }
    }

    public void setNavigationView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_player);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view_player);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) { // if the drawer menu is open
            drawer.closeDrawer(GravityCompat.START);    // close menu
        } else {
//            AppController.minimizeOnDoubleBack(this);    // minimizeOnDoubleBack the app on double click
            FragmentManager fragmentManager = getSupportFragmentManager();
            PlayerProfileFragment playerProfileFragment =
                    (PlayerProfileFragment) fragmentManager.findFragmentByTag(Constant.FRAGMENT_PLAYER_PROFILE);
            ClubFragment clubFragment =
                    (ClubFragment) fragmentManager.findFragmentByTag(Constant.FRAGMENT_CLUB);
            TournamentFragment tournamentFragment =
                    (TournamentFragment) fragmentManager.findFragmentByTag(Constant.FRAGMENT_TOURNAMENT);
            VisitorViewFragment visitorViewFragment =
                    (VisitorViewFragment) fragmentManager.findFragmentByTag(Constant.FRAGMENT_VISITOR);
            if ( playerProfileFragment != null && playerProfileFragment.isVisible() ) {
                if ( playerProfileFragment.onBackPressed() ) {
                    return;
                }
            } else if ( clubFragment != null && clubFragment.isVisible() ) {
                if ( clubFragment.onBackPressed() ) {
                    return;
                }
            } else if ( tournamentFragment != null && tournamentFragment.isVisible() ) {
                if ( tournamentFragment.onBackPressed() ) {
                    return;
                }
            } else if ( visitorViewFragment != null && visitorViewFragment.isVisible() ) {
                setNavigationView();
                super.onBackPressed();
                return;
            }
            int count = fragmentManager.getBackStackEntryCount();
            if ( count == 1 ) {
                AppController.minimizeOnDoubleBack(MainActivity.this);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player_options, menu);
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
//            showSettingsPage();
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

        if (id == R.id.nav_player_profile) {
            showPlayerProfilePage();
        } else if (id == R.id.nav_club_list) {
            showPlayerClubList();
        } else if (id == R.id.nav_logout) {
            showLogoutPage();
            return false;
        } else if (id == R.id.nav_settings) {
            showSettingsPage();
            return false;
        } else if (id == R.id.nav_club) {
            showClubPage();
        } else if (id == R.id.nav_tournament) {
            viewTournamentList();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     *  presents the player's profile page using PlayerProfileFragment
     *  PlayerProfileFragment will set the MainActivity.clubs using callback method setClubs()
     */
    private void showPlayerProfilePage(){
        if ( player == null ) {
            Log.e(TAG,"player instance is null, failed to show player profile");
            return;
        }
        PlayerProfileFragment playerProfileFragment = PlayerProfileFragment.newInstance(player.getId());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_content,playerProfileFragment,Constant.FRAGMENT_PLAYER_PROFILE);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * display list of clubs the player is in
     */
    private void showPlayerClubList(){
        if ( player == null || clubs == null ) {
            Log.e(TAG,"failed to show player club list");
            return;
        }
        ClubListFragment fragment = ClubListFragment.newInstance(clubs,player);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_content,fragment,Constant.FRAGMENT_PLAYER_CLUB_LIST);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * go to the player registration page
     * @param player  the Player instance
     */
    private void showRegistrationPage(Player player){
        Intent intent = new Intent(MainActivity.this,PlayerRegistrationActivity.class);
        intent.putExtra(Constant.KEY_PLAYER,player.toJson());
        startActivity(intent);
    }

    /**
     * use a fragment to display the club's profile page
     */
    private void showClubPage(){
        if ( player == null || club == null ) {
            Log.e(TAG,"failed to show player club page");
            return;
        }
        ClubFragment clubFragment = ClubFragment.newInstance(club,player);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_content,clubFragment,Constant.FRAGMENT_CLUB);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * view tournament list fragment
     */
    private void viewTournamentList() {
        if ( player == null || club == null ) {
            Log.e(TAG,"failed to show club fragment list");
            return;
        }
        TournamentListFragment fragment = TournamentListFragment.newInstance(club,player);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_content,fragment,Constant.FRAGMENT_CLUB_TOURNAMENT_LIST);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * show a pop-up dialog to ask the user whether to logout
     */
    private void showLogoutPage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,0);
        builder.setTitle(R.string.warning);
        builder.setMessage(R.string.prompt_logout);
        builder.setPositiveButton(R.string.label_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AppController.logOut(MainActivity.this);
            }
        });
        builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    /**
     * currently, the setting page is the player profile update page
     */
    private void showSettingsPage() {
        showRegistrationPage(player);
    }

    /**
     * upload player's FireBase Instance ID Token to server
     */
    private void uploadIIDToken(){
        String instanceToken = FirebaseInstanceId.getInstance().getToken();
        if ( instanceToken == null ) {
            Log.e(TAG,"Null IID Token error.");
            return;
        }
        // upload the token to server
        Token token = new Token(player.getId(),instanceToken);
        RequestAction actionPutToken = new RequestAction() {
            @Override
            public void actOnPre() {
                Log.d(TAG, "Preparing to send new token to server");
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 201 ) {
                    Log.d(TAG, "New token created!");
                } else if ( responseCode == 200 ) {
                    Log.d(TAG, "Token Updated!");
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString(Constant.KEY_MSG);
                        Log.e(TAG, "Uploading token failed with response message:" + message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Uploading token failed with response:" + response);
                    }
                }
            }
        };
        String url = UrlHelper.urlPutToken(player.getId());
        RequestHelper.sendPutRequest(url,token.toJson(),actionPutToken);
    }

    @Override
    public void addNewClub(Club club) {
        clubs.add(club);
        FragmentManager fragmentManager = getSupportFragmentManager();
        PlayerProfileFragment playerProfileFragment = (PlayerProfileFragment) fragmentManager.findFragmentByTag(Constant.FRAGMENT_PLAYER_PROFILE);
        playerProfileFragment.onClubListChanged(clubs);
    }

    @Override
    public void setClubs(ArrayList<Club> clubList) {
        clubs = clubList;
    }

    @Override
    public void selectClub(Club club) {
        this.club = club;
    }

    @Override
    public void createEvent(Address address) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ClubFragment clubFragment = (ClubFragment) fragmentManager.findFragmentByTag(Constant.FRAGMENT_CLUB);
        if ( clubFragment != null ) {
            clubFragment.createEvent(address);
        }
    }
}