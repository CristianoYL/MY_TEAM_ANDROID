package com.example.cristiano.myteam.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.activity.ClubActivity;
import com.example.cristiano.myteam.adapter.ClubListAdapter;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.structure.Member;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.util.AppController;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Cristiano on 2017/4/20.
 *
 * this fragment shows the club page
 */

public class ClubListFragment extends Fragment {

    private static final String ARG_CLUB = "club";
    private static final String ARG_PLAYER = "player";
    private static final String TAG = "ClubListFragment";

    private Button btn_createClub, btn_joinClub;
    private ListView lv_club;

    private ArrayList<Club> searchResultList;
    private ArrayList<Club> clubs;
    private Player player;
    private View clubView;

    OnClubListChangeListener onClubListChangeListener;

    public interface OnClubListChangeListener{
        public void addNewClub(Club club);
    }

    public static ClubListFragment newInstance(ArrayList<Club> clubs, Player player){
        ClubListFragment fragment = new ClubListFragment();
        Bundle bundle = new Bundle();
        ArrayList<String> jsonClubs = new ArrayList<>(clubs.size());
        for ( Club club : clubs ) {
            jsonClubs.add(club.toJson());
        }
        bundle.putStringArrayList(ARG_CLUB,jsonClubs);
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
            clubs = new ArrayList<>();
            ArrayList<String> jsonClubs = bundle.getStringArrayList(ARG_CLUB);
            if ( jsonClubs != null ) {
                for ( String jsonClub : jsonClubs ) {
                    clubs.add(gson.fromJson(jsonClub,Club.class));
                }
            }
            player = gson.fromJson(bundle.getString(ARG_PLAYER),Player.class);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            onClubListChangeListener = (OnClubListChangeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement OnClubListChangeListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        clubView = inflater.inflate(R.layout.fragment_player_club_list, container, false);
        btn_createClub = (Button) clubView.findViewById(R.id.btn_createClub);
        btn_joinClub = (Button) clubView.findViewById(R.id.btn_joinClub);
        lv_club = (ListView) clubView.findViewById(R.id.lv_club);
        showClub();
        return clubView;
    }

    /**
     * render the ViewPager to display the club info
     */
    private void showClub(){
        getActivity().setTitle(R.string.menu_my_clubs);
        ClubListAdapter clubListAdapter = new ClubListAdapter(getContext(),clubs);
        lv_club.setAdapter(clubListAdapter);
        lv_club.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Club club = clubs.get(i);
                if ( club.priority > 0 ) {
                    viewClub(club);
                } else {
                    Toast.makeText(getContext(), R.string.error_view_pending_club, Toast.LENGTH_SHORT).show();
                }

            }
        });
        btn_createClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateClubPage();
            }
        });

        btn_joinClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJoinClubPage();
            }
        });
    }

    private void viewClub(Club club) {
        Intent intent = new Intent(getActivity(),ClubActivity.class);
        intent.putExtra(Constant.TABLE_CLUB,club.toJson());
        intent.putExtra(Constant.TABLE_PLAYER,player.toJson());
        startActivity(intent);
    }

    /**
     * show a pop-up dialog to let the user create a new club
     */
    private void showCreateClubPage() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v_event = inflater.inflate(R.layout.layout_reg_name_info, null);
        final TextView tv_name = (TextView) v_event.findViewById(R.id.et_name);
        final TextView tv_info = (TextView) v_event.findViewById(R.id.et_info);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle("Create a club");
        dialogBuilder.setPositiveButton(R.string.label_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String clubName = tv_name.getText().toString();
                String clubInfo = tv_info.getText().toString();
                if ( clubName.equals("") ) {
                    Toast.makeText(getContext(), R.string.error_empty_club_name, Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( clubInfo.equals("") ) {
                    Toast.makeText(getContext(),R.string.error_empty_club_info, Toast.LENGTH_SHORT).show();
                    return;
                }
                int clubID = 0;
                Club regClub = new Club(clubID,clubName,clubInfo);
                RequestAction actionPostClub = new RequestAction() {
                    @Override
                    public void actOnPre() {

                    }

                    @Override
                    public void actOnPost(int responseCode, String response) {
                        if ( responseCode == 201 ) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                int clubID = jsonObject.getInt(Constant.CLUB_ID);
                                String name = jsonObject.getString(Constant.CLUB_NAME);
                                String info = jsonObject.getString(Constant.CLUB_INFO);
                                Club newClub = new Club(clubID,name,info);
                                newClub.priority = 3;
                                clubs.add(newClub);
                                Toast.makeText(getContext(),"Club Created!",Toast.LENGTH_LONG).show();
                                onClubListChangeListener.addNewClub(newClub);
                                showClub();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
                            }
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString(Constant.KEY_MSG);
                                Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                };
                String url = UrlHelper.urlRegClubFromPlayer(player.getId());
                RequestHelper.sendPostRequest(url,regClub.toJson(),actionPostClub);
            }
        });
        dialogBuilder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialogBuilder.setView(v_event);
        dialogBuilder.setCancelable(true);
        dialogBuilder.show();
    }

    /**
     *  show the join club page to let the user search and join club
     */
    SearchView sv_club;
    ListView lv_searchResult;
    Button btn_join;
    AlertDialog dialog;
    private void showJoinClubPage(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view_search = inflater.inflate(R.layout.layout_search_club, null);
        Spinner sp_searchKey = (Spinner) view_search.findViewById(R.id.sp_searchKey);
        sv_club = (SearchView) view_search.findViewById(R.id.sv_club);
        btn_join = (Button) view_search.findViewById(R.id.btn_join);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle("Join Club");
        dialogBuilder.setView(view_search);
        dialogBuilder.setCancelable(true);
        lv_searchResult = (ListView) view_search.findViewById(R.id.lv_searchResult);

        String[] keys = {"Club ID", "Club Name"};
        sp_searchKey.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,keys));
        sp_searchKey.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ( parent.getSelectedItem().toString().equals("Club ID")) {
                    sv_club.setQueryHint("Club ID");
                    sv_club.setInputType(InputType.TYPE_CLASS_NUMBER);
                    sv_club.setQuery(null,false);
                } else {
                    sv_club.setQueryHint("Club Name");
                    sv_club.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sv_club.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    return false;
                }
                // hide keyboard
                AppController.hideKeyboard(getContext(),sv_club);

                RequestAction actionGetClubByID = new RequestAction() {
                    @Override
                    public void actOnPre() {
                    }

                    @Override
                    public void actOnPost(int responseCode, String response) {
                        if ( responseCode == 200 ) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                int clubID = jsonObject.getInt(Constant.CLUB_ID);
                                String name = jsonObject.getString(Constant.CLUB_NAME);
                                String info = jsonObject.getString(Constant.CLUB_INFO);
                                Club club = new Club(clubID,name,info);
                                ArrayList<String> clubNames = new ArrayList<>();
                                searchResultList = new ArrayList<Club>();
                                searchResultList.add(club);
                                clubNames.add(club.name);
                                lv_searchResult.setAdapter(new ArrayAdapter<String>(
                                        getContext(),
                                        android.R.layout.simple_list_item_single_choice,
                                        clubNames
                                ));
                                lv_searchResult.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Error while searching for club", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            lv_searchResult.setAdapter(null);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString(Constant.KEY_MSG);
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Error while searching for club", Toast.LENGTH_SHORT).show();
                                Log.e(TAG,"Error response" + response);
                            }
                        }
                    }
                };

                RequestAction actionGetClubByName = new RequestAction() {
                    @Override
                    public void actOnPre() {
                    }

                    @Override
                    public void actOnPost(int responseCode, String response) {
                        if ( responseCode == 200 ) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.CLUB_LIST);
                                ArrayList<String> clubNames = new ArrayList<>();
                                searchResultList = new ArrayList<Club>();
                                if ( jsonArray.length() == 0 ) {
                                    Toast.makeText(getContext(), "No club named<" + sv_club.getQuery() + "> found!", Toast.LENGTH_SHORT).show();
                                    lv_searchResult.setAdapter(null);
                                    return;
                                }
                                for ( int i = 0; i < jsonArray.length(); i++ ) {
                                    JSONObject jsonClub = jsonArray.getJSONObject(i);
                                    int clubID = jsonClub.getInt(Constant.CLUB_ID);
                                    String name = jsonClub.getString(Constant.CLUB_NAME);
                                    String info = jsonClub.getString(Constant.CLUB_INFO);
                                    Club club = new Club(clubID,name,info);
                                    searchResultList.add(club);
                                    clubNames.add(club.name);
                                }
                                lv_searchResult.setAdapter(new ArrayAdapter<String>(
                                        getContext(),
                                        android.R.layout.simple_list_item_single_choice,
                                        clubNames
                                ));
                                lv_searchResult.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Error while searching for club", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString(Constant.KEY_MSG);
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Error while searching for club", Toast.LENGTH_SHORT).show();
                                Log.e(TAG,"Error response:" + response);
                            }
                        }
                    }
                };
                String url;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if ( sv_club.getInputType() == InputType.TYPE_CLASS_NUMBER) {
                        url = UrlHelper.urlClubByID(Integer.parseInt(sv_club.getQuery().toString()));
                        RequestHelper.sendGetRequest(url,actionGetClubByID);
                    } else {
                        url = UrlHelper.urlClubByName(sv_club.getQuery().toString());
                        RequestHelper.sendGetRequest(url,actionGetClubByName);
                    }
                } else {
                    try{
                        url = UrlHelper.urlClubByID(Integer.parseInt(sv_club.getQuery().toString()));
                        RequestHelper.sendGetRequest(url,actionGetClubByID);
                    } catch ( NumberFormatException e ) {
                        url = UrlHelper.urlClubByName(sv_club.getQuery().toString());
                        RequestHelper.sendGetRequest(url,actionGetClubByName);
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( lv_searchResult.getCheckedItemCount() == 0 ) {
                    Toast.makeText(getContext(), "Please select a club.", Toast.LENGTH_SHORT).show();
                    return;
                }
                RequestAction actionPostClubRequest = new RequestAction() {
                    @Override
                    public void actOnPre() {

                    }

                    @Override
                    public void actOnPost(int responseCode, String response) {
                        if ( responseCode == 201 ) {
                            Toast.makeText(getContext(), "Join club request sent. Please wait for club admins to handle the request.", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString(Constant.KEY_MSG);
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                            }
                        }
                        dialog.dismiss();
                        showClub();   // refresh the list
                    }
                };
                int clubID = searchResultList.get(lv_searchResult.getCheckedItemPosition()).id;
                String url = UrlHelper.urlMemberRequest(clubID);
                Member member = new Member(clubID,player.getId(),null,false,0);
                RequestHelper.sendPostRequest(url,member.toJson(),actionPostClubRequest);
            }
        });
        dialog = dialogBuilder.show();

    }

//    public boolean onBackPressed(){
//        int position = tab_club.getSelectedTabPosition();
//        if ( position > 0 ) {
//            TabLayout.Tab tab = tab_club.getTabAt(--position);
//            if ( tab != null ) {
//                tab.select();
//                return true;
//            }
//        }
//        return false;
//    }

}
