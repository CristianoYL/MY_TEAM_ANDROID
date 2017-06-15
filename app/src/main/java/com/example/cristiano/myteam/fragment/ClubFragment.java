package com.example.cristiano.myteam.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.adapter.CustomFragmentAdapter;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.structure.Player;
import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/4/20.
 *
 * this fragment shows the club page
 */

public class ClubFragment extends Fragment {

    private static final String ARG_CLUB = "club";
    private static final String ARG_PLAYER = "player";

    private TabLayout tab_club;
    public ViewPager viewPager_club;

    private Club club;
    private Player player;
    private View clubView;

    public static ClubFragment newInstance(Club club, Player player){
        ClubFragment fragment = new ClubFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_CLUB,club.toJson());
        bundle.putString(ARG_PLAYER,player.toJson());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            club = gson.fromJson(bundle.getString(ARG_CLUB),Club.class);
            player = gson.fromJson(bundle.getString(ARG_PLAYER),Player.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        clubView = inflater.inflate(R.layout.fragment_club, container, false);
        showClub();
        return clubView;
    }

    /**
     * render the ViewPager to display the club info
     */
    private void showClub(){
        getActivity().setTitle(club.name);
        tab_club = (TabLayout) clubView.findViewById(R.id.tabLayout);
        viewPager_club = (ViewPager) clubView.findViewById(R.id.viewPager);

        tab_club.setBackgroundResource(R.color.colorPrimaryDark);
        tab_club.setSelectedTabIndicatorColor(Color.CYAN);
        tab_club.setTabTextColors(Color.WHITE,Color.CYAN);

        tab_club.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager_club.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager_club.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                hideKeyboard();
            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = tab_club.getTabAt(position);
                if ( tab != null ) {
                    tab.select();
                }
                hideKeyboard();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // set tabs
        tab_club.removeAllTabs();
        tab_club.addTab(tab_club.newTab().setText("Chat"));
        tab_club.addTab(tab_club.newTab().setText("Members"));
        tab_club.addTab(tab_club.newTab().setText("Map"));
        tab_club.addTab(tab_club.newTab().setText("Profile"));
        tab_club.setTabMode(TabLayout.MODE_FIXED);
        // set fragments into view pagers
        Fragment[] fragments = new Fragment[4];
        ChatFragment chatFragment = ChatFragment.newInstance(null, club,null,player);
        fragments[0] = chatFragment;
        ClubMemberFragment memberFragment = ClubMemberFragment.newInstance(club,player);
        fragments[1] = memberFragment;
        ClubMapFragment mapFragment = ClubMapFragment.newInstance(club,player);
        fragments[2] = mapFragment;
        ClubProfileFragment clubProfileFragment = ClubProfileFragment.newInstance(club,player);
        fragments[3] = clubProfileFragment;

        CustomFragmentAdapter adapter = new CustomFragmentAdapter(getChildFragmentManager());
        adapter.setFragments(fragments);
        viewPager_club.setAdapter(adapter);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(clubView.getWindowToken(), 0);
    }
}
