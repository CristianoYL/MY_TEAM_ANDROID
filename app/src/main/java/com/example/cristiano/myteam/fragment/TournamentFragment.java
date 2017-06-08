package com.example.cristiano.myteam.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.adapter.CustomFragmentAdapter;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.structure.Tournament;
import com.example.cristiano.myteam.util.Constant;
import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/4/20.
 *
 * this fragment show the tournament page
 */

public class TournamentFragment extends Fragment {

    private TabLayout tab_tournament;
    public ViewPager viewPager_tournament;

    private Tournament tournament;
    private Club club;
    private int playerID;
    private View tournamentView;

    public static TournamentFragment newInstance(Tournament tournament, Club club, int playerID){
        TournamentFragment fragment = new TournamentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.TABLE_TOURNAMENT,tournament.toJson());
        bundle.putString(Constant.TABLE_CLUB,club.toJson());
        bundle.putInt(Constant.KEY_PLAYER_ID,playerID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            tournament = new Gson().fromJson(bundle.getString(Constant.TABLE_TOURNAMENT),Tournament.class);
            club = new Gson().fromJson(bundle.getString(Constant.TABLE_CLUB),Club.class);
            playerID = bundle.getInt(Constant.KEY_PLAYER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tournamentView = inflater.inflate(R.layout.fragment_tournament, container, false);
        showTournament();
        return tournamentView;
    }

    /**
     * render the ViewPager to display the tournament info
     */
    private void showTournament(){
        getActivity().setTitle(club.name + "-" + tournament.name);
        tab_tournament = (TabLayout) tournamentView.findViewById(R.id.tabLayout);
        viewPager_tournament = (ViewPager) tournamentView.findViewById(R.id.viewPager);

        tab_tournament.setBackgroundResource(R.color.colorPrimaryDark);
        tab_tournament.setSelectedTabIndicatorColor(Color.CYAN);
        tab_tournament.setTabTextColors(Color.WHITE,Color.CYAN);

        tab_tournament.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager_tournament.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager_tournament.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tab_tournament.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tab_tournament.removeAllTabs();
        tab_tournament.addTab(tab_tournament.newTab().setText("Results"));
        tab_tournament.addTab(tab_tournament.newTab().setText("Squad"));
        tab_tournament.addTab(tab_tournament.newTab().setText("Stats"));
        tab_tournament.addTab(tab_tournament.newTab().setText("Chat"));
        tab_tournament.setTabMode(TabLayout.MODE_FIXED);
        Fragment[] fragments = new Fragment[4];
        // the tournament fragment contains 3 child fragments, which shows the result, squad and stats
        ClubResultFragment clubResultFragment = ClubResultFragment.newInstance(tournament,club);
        fragments[0] = clubResultFragment;

        ClubSquadFragment clubSquadFragment = ClubSquadFragment.newInstance(tournament.id,club.id);
        fragments[1] = clubSquadFragment;

        ClubStatsFragment clubStatsFragment = ClubStatsFragment.newInstance(tournament.id, club.id);
        fragments[2] = clubStatsFragment;

        ChatFragment chatFragment = ChatFragment.newInstance(tournament.id, club.id,0,playerID);
        fragments[3] = chatFragment;

        CustomFragmentAdapter adapter = new CustomFragmentAdapter(getChildFragmentManager());
        adapter.setFragments(fragments);
        viewPager_tournament.setAdapter(adapter);
    }
}
