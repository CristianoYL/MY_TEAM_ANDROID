package com.example.cristiano.myteam.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
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

/**
 * Created by Cristiano on 2017/4/17.
 *
 * this fragment holds a ViewPager which contains child fragments
 * that renders different stats of the club
 */

public class TournamentStatsFragment extends Fragment {
    private int clubID, tournamentID;
    private Stats clubStats;

    View view;
    private ViewPager viewPager;
    private TabLayout tab_clubStats;

    public TournamentStatsFragment() {
        // Required empty public constructor
    }

    public static TournamentStatsFragment newInstance(int tournamentID, int clubID) {
        TournamentStatsFragment fragment = new TournamentStatsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.KEY_CLUB_ID,clubID);
        bundle.putInt(Constant.KEY_TOURNAMENT_ID,tournamentID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            clubID = bundle.getInt(Constant.KEY_CLUB_ID);
            tournamentID = bundle.getInt(Constant.KEY_TOURNAMENT_ID);
        }
        Log.d("STATS_FRAGMENT","onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_club_stats, container, false);
        loadStats();
        Log.d("STATS_FRAGMENT","onCreateView");
        return view;
    }

    /**
     * send a GET request to retrieve the club's tournament stats
     */
    private void loadStats() {
        RequestAction actionGetClubStats = new RequestAction() {
            @Override
            public void actOnPre() {

            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonStats = jsonObject.getJSONObject(Constant.TABLE_STATS);
                        int goal = jsonStats.getInt(Constant.STATS_GOAL);
                        int penalty = jsonStats.getInt(Constant.STATS_PEN);
                        int freekick = jsonStats.getInt(Constant.STATS_FREEKICK);
                        int penaltyShootout = jsonStats.getInt(Constant.STATS_PEN_SHOOTOUT);
                        int penaltyTaken = jsonStats.getInt(Constant.STATS_PEN_TAKEN);
                        int ownGoal = jsonStats.getInt(Constant.STATS_OG);
                        int header = jsonStats.getInt(Constant.STATS_HEADER);
                        int weakFootGoal = jsonStats.getInt(Constant.STATS_WEAK_FOOT_GOAL);
                        int otherGoal = jsonStats.getInt(Constant.STATS_OTHER_GOAL);
                        int assist = jsonStats.getInt(Constant.STATS_ASSIST);
                        int yellow = jsonStats.getInt(Constant.STATS_YELLOW);
                        int red = jsonStats.getInt(Constant.STATS_RED);
                        int penaltySaved = jsonStats.getInt(Constant.STATS_PEN_SAVED);
                        JSONObject jsonPerformance = jsonObject.getJSONObject(Constant.KEY_GAME_PERFORMANCE);
                        int win = jsonPerformance.getInt(Constant.PERFORMANCE_WIN);
                        int draw = jsonPerformance.getInt(Constant.PERFORMANCE_DRAW);
                        int loss = jsonPerformance.getInt(Constant.PERFORMANCE_LOSS);
                        int cleanSheet = jsonPerformance.getInt(Constant.STATS_CLEAN_SHEET);
                        int goalsConceded = jsonPerformance.getInt(Constant.PERFORMANCE_GOALS_CONCEDED);
                        clubStats = new Stats(tournamentID,clubID,-1,-1,-1,-1,goal,penalty, freekick,
                                penaltyShootout,penaltyTaken,ownGoal,header,weakFootGoal,otherGoal,
                                assist,yellow,red,cleanSheet,penaltySaved);
                        clubStats.setWin(win);
                        clubStats.setDraw(draw);
                        clubStats.setLoss(loss);
                        clubStats.setGoalsConceded(goalsConceded);
                        showStats();
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
        String url = UrlHelper.urlStatsByTournamentClub(tournamentID,clubID);
        RequestHelper.sendGetRequest(url,actionGetClubStats);
    }


    /**
     * fill the ViewPager with child fragments that visualize different stats
     */
    private void showStats() {
        View tabViewPager = view.findViewById(R.id.tabViewPager_clubStats);
        viewPager = (ViewPager) tabViewPager.findViewById(R.id.viewPager);
        tab_clubStats = (TabLayout) tabViewPager.findViewById(R.id.tabLayout);

        Fragment[] fragments = new Fragment[Constant.CLUB_STATS_TABS.length];
        tab_clubStats.removeAllTabs();

        tab_clubStats.addTab(tab_clubStats.newTab().setText("Game Performance"));
        String[] resultLabels = Constant.LABEL_GAME_PERFORMANCE;
        float[] resultValues = new float[]{clubStats.getWin(),clubStats.getDraw(),clubStats.getLoss()};
        fragments[0] = PieChartFragment.newInstance(Constant.CLUB_STATS_TABS[0],
                Constant.CLUB_STATS_CENTER_TEXT[0],resultLabels,resultValues,Constant.CLUB_STATS_IS_INT[0]);

        tab_clubStats.addTab(tab_clubStats.newTab().setText("Team Stats"));
        resultLabels = new String[]{"goal","goalsConceded","penalty","freekick",
                "header","yellow", "red","cleanSheet"};
        resultValues = new float[]{clubStats.goal,clubStats.getGoalsConceded(),clubStats.penalty,
                clubStats.freekick,clubStats.header,clubStats.yellow,clubStats.red,
                clubStats.cleanSheet};
        fragments[1] = BarChartFragment.newInstance(Constant.CLUB_STATS_TABS[1],
                resultLabels,resultValues,Constant.CLUB_STATS_IS_INT[1]);

        tab_clubStats.addTab(tab_clubStats.newTab().setText("Player Stats"));
        fragments[2] = BarChartFragment.newInstance(Constant.CLUB_STATS_TABS[2],
                resultLabels,resultValues,Constant.CLUB_STATS_IS_INT[2]);

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
