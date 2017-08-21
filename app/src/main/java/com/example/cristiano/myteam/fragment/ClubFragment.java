package com.example.cristiano.myteam.fragment;


import android.graphics.Color;
import android.location.Address;
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
import com.example.cristiano.myteam.util.AppUtils;
import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/4/20.
 *
 * this fragment shows the club page
 */

public class ClubFragment extends Fragment implements MapFragment.OnCreateEventRequestListener {

    private static final String ARG_CLUB = "club";
    private static final String ARG_PLAYER = "player";

    private TabLayout tab_club;
    public ViewPager viewPager_club;
    private EventFragment eventFragment;

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
        AppUtils.setNavigationMenu(getActivity(), AppUtils.MENU_CLUB);
        return clubView;
    }

    @Override
    public void onResume() {
        super.onResume();
        showClub();
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
                AppUtils.hideKeyboard(getContext(),clubView);
            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = tab_club.getTabAt(position);
                if ( tab != null ) {
                    tab.select();
                }
                AppUtils.hideKeyboard(getContext(),clubView);
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
        tab_club.addTab(tab_club.newTab().setText("Events"));
//        tab_club.addTab(tab_club.newTab().setText("Profile"));
        tab_club.setTabMode(TabLayout.MODE_FIXED);
        // set fragments into view pagers
        Fragment[] fragments = new Fragment[4];
        ChatFragment chatFragment = ChatFragment.newInstance(null, club,null,player);
        fragments[0] = chatFragment;
        ClubMemberFragment memberFragment = ClubMemberFragment.newInstance(club,player);
        fragments[1] = memberFragment;
        MapFragment mapFragment = MapFragment.newInstance(club,player);
        fragments[2] = mapFragment;
        eventFragment = EventFragment.newInstance(club,player);
        fragments[3] = eventFragment;
//        ClubProfileFragment clubProfileFragment = ClubProfileFragment.newInstance(club,player);
//        fragments[4] = clubProfileFragment;

        CustomFragmentAdapter adapter = new CustomFragmentAdapter(getChildFragmentManager());
        adapter.setFragments(fragments);
        viewPager_club.setAdapter(adapter);
    }

    /**
     * defines the onBackPressed behavior for ClubFragment
     * navigate back to previous tab if the first tab is not being displayed
     * @return if the onBackPressed event is handled by this fragment
     */
    public boolean onBackPressed(){
        int position = tab_club.getSelectedTabPosition();
        if ( position > 0 ) {
            TabLayout.Tab tab = tab_club.getTabAt(--position);
            if ( tab != null ) {
                tab.select();
                return true;
            }
        }
        return false;
    }

    @Override
    public void createEvent(Address eventAddress) {
        TabLayout.Tab tab = tab_club.getTabAt(3);
        if ( tab != null ) {
            tab.select();
            eventFragment.showCreateEventDialog(eventAddress);
        }
    }
}
