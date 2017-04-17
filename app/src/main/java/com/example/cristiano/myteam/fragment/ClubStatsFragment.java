package com.example.cristiano.myteam.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.structure.Stats;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.adapter.CustomFragmentAdapter;
import com.example.cristiano.myteam.util.UrlHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Cristiano on 2017/4/17.
 */

public class ClubStatsFragment extends Fragment {
    private int clubID, tournamentID;
    private Stats clubStats;

    public ClubStatsFragment() {
        // Required empty public constructor
    }

    public static ClubStatsFragment newInstance(int clubID, int tournamentID) {
        ClubStatsFragment fragment = new ClubStatsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.CLUB_ID,clubID);
        bundle.putInt(Constant.TOURNAMENT_ID,tournamentID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            clubID = bundle.getInt(Constant.CLUB_ID);
            tournamentID = bundle.getInt(Constant.TOURNAMENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_club_stats, container, false);
        loadStats(view);
        return view;
    }

    private void loadStats(final View view) {
        RequestAction actionGetClubStats = new RequestAction() {
            @Override
            public void actOnPre() {

            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int goal = jsonObject.getInt(Constant.STATS_GOAL);
                        int penalty = jsonObject.getInt(Constant.STATS_PEN);
                        int penaltyShootout = jsonObject.getInt(Constant.STATS_PEN_SHOOTOUT);
                        int penaltyTaken = jsonObject.getInt(Constant.STATS_PEN_TAKEN);
                        int ownGoal = jsonObject.getInt(Constant.STATS_OG);
                        int header = jsonObject.getInt(Constant.STATS_HEADER);
                        int weakFootGoal = jsonObject.getInt(Constant.STATS_WEAK_FOOT_GOAL);
                        int otherGoal = jsonObject.getInt(Constant.STATS_OTHER_GOAL);
                        int assist = jsonObject.getInt(Constant.STATS_ASSIST);
                        int yellow = jsonObject.getInt(Constant.STATS_YELLOW);
                        int red = jsonObject.getInt(Constant.STATS_RED);
                        int cleanSheet = jsonObject.getInt(Constant.STATS_CLEAN_SHEET);
                        int penaltySaved = jsonObject.getInt(Constant.STATS_PEN_SAVED);
                        // TODO: calculate clean sheet
                        clubStats = new Stats(tournamentID,clubID,-1,-1,-1,-1,goal,penalty,
                                penaltyShootout,penaltyTaken,ownGoal,header,weakFootGoal,otherGoal,
                                assist,yellow,red,cleanSheet,penaltySaved);
                        showStats(view);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(),"Response format error!\n"+response,Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString(Constant.KEY_MSG);
                        Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(),"Response format error!\n"+response,Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        String url = UrlHelper.urlGetTournamentClubStats(tournamentID,clubID);
        RequestHelper.sendGetRequest(url,actionGetClubStats);
    }

    private void showStats(View view) {
        View tabViewPager = view.findViewById(R.id.tabViewPager_clubStats);
        final ViewPager viewPager = (ViewPager) tabViewPager.findViewById(R.id.viewPager);
        final TabLayout tab_clubStats = (TabLayout) tabViewPager.findViewById(R.id.tabLayout);

        Fragment[] fragments = new Fragment[Constant.CLUB_STATS_TABS.length];
        tab_clubStats.removeAllTabs();

        for ( int i = 0; i < Constant.CLUB_STATS_TABS.length; i++ ) {
            tab_clubStats.addTab(tab_clubStats.newTab().setText(Constant.CLUB_STATS_TABS[i]));
        }

        // count W-D-L
        String[] resultLabels = Constant.LABEL_GAME_PERFORMANCE;
        float[] resultValues = new float[]{7,4,3};
        ArrayList<float[]> dataY = new ArrayList<>(2);
        dataY.add(resultValues);
        fragments[0] = PieChartFragment.newInstance(Constant.CLUB_STATS_TABS[0],
                Constant.CLUB_STATS_CENTER_TEXT[0],resultLabels,dataY.get(0),Constant.CLUB_STATS_IS_INT[0]);

        resultLabels = new String[]{"goal","penalty","penaltyShootout","penaltyTaken",
                "ownGoal", "header","weakFootGoal","otherGoal","assist","yellow", "red","cleanSheet","penaltySaved"};
        dataY.add(new float[]{clubStats.goal,clubStats.penalty,clubStats.penaltyShootout,
                clubStats.penaltyTaken,clubStats.ownGoal,clubStats.header,clubStats.weakFootGoal,
                clubStats.otherGoal,clubStats.assist,clubStats.yellow,clubStats.red,
                clubStats.cleanSheet,clubStats.penaltySaved});
        fragments[1] = BarChartFragment.newInstance(Constant.CLUB_STATS_TABS[1],
                resultLabels,dataY.get(1),Constant.CLUB_STATS_IS_INT[1]);

        fragments[2] = BarChartFragment.newInstance(Constant.CLUB_STATS_TABS[2],
                resultLabels,dataY.get(1),Constant.CLUB_STATS_IS_INT[2]);

        tab_clubStats.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
                tab_clubStats.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        CustomFragmentAdapter adapter = new CustomFragmentAdapter(getChildFragmentManager());
        adapter.setFragments(fragments);
        viewPager.setAdapter(adapter);
    }
}
