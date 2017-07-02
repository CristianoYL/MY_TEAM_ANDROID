package com.example.cristiano.myteam.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.adapter.EventListAdapter;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.service.location.FetchAddressIntentService;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cristiano on 2017/4/18.
 *
 * this fragment presents a timeline of events
 */

public class EventFragment extends Fragment {

    private static final String ARG_CLUB = "club";
    private static final String ARG_PLAYER = "player";

    private static final int EVENTS_PER_REQUEST = 5;   // load 5 events per request

    private Club club;
    private Player player;
    private int offset;
    private List<String> events;
    private AddressResultReceiver mResultReceiver;
    private Address eventAddress;

    private View view;
    private FloatingActionButton fab_addEvent;
    private ListView lv_events, lv_address;
    private EditText et_eventTitle;
    private SearchView sv_address;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private SwitchCompat sw_time;
    private AlertDialog eventDialog;
    private EventListAdapter adapter;

    public EventFragment() {
    }

    public static EventFragment newInstance(Club club, Player player){
        EventFragment fragment = new EventFragment();
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
        view = inflater.inflate(R.layout.fragment_event, container, false);
        lv_events = (ListView) view.findViewById(R.id.lv_events);
        fab_addEvent = (FloatingActionButton) view.findViewById(R.id.fab_add_event);
        fab_addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateEventDialog();
            }
        });
        getEvents();
        return view;
    }

    /**
     * send a GET request to retrieve the club's events
     */
    private void getEvents(){
        events = new ArrayList<>();
        events.add("event 1");
        events.add("event 2");
        events.add("event 3");
        showEvents();
        RequestAction actionGetEvents = new RequestAction() {
            @Override
            public void actOnPre() {
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        showEvents();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(),response,Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Toast.makeText(getActivity(),jsonObject.getString(Constant.KEY_MSG),
                                Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(),response,Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        String url = UrlHelper.urlEventByClub(club.id,EVENTS_PER_REQUEST,offset);
//        RequestHelper.sendGetRequest(url,actionGetEvents);
    }

    /**
     * after retrieving the club's events, call this method to display it into the timeline
     */
    private void showEvents() {
        adapter = new EventListAdapter(getContext(),events);
        lv_events.setAdapter(adapter);
    }

    private void showCreateEventDialog(){
        showCreateEventDialog(null);
    }

    public void showCreateEventDialog(Address eventAddress){
        View eventView = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_add_event,null);
        et_eventTitle = (EditText) eventView.findViewById(R.id.et_eventTitle);
        sv_address = (SearchView) eventView.findViewById(R.id.sv_address);
        lv_address = (ListView) eventView.findViewById(R.id.lv_address);
        datePicker = (DatePicker) eventView.findViewById(R.id.datePicker);
        timePicker = (TimePicker) eventView.findViewById(R.id.timePicker);
        sw_time = (SwitchCompat) eventView.findViewById(R.id.sw_time);

        if ( eventAddress != null ) {
            this.eventAddress = eventAddress;
            String strAddress;
            ArrayList<String> addressLines = new ArrayList<>();
            for ( int i = 0; i <= eventAddress.getMaxAddressLineIndex(); i++ ) {
                addressLines.add(eventAddress.getAddressLine(i));
            }
            strAddress = TextUtils.join(System.getProperty("line.separator"),addressLines);
            sv_address.setQuery(strAddress,false);
        }

        sw_time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked ) {
                    timePicker.setVisibility(View.VISIBLE);
                } else {
                    timePicker.setVisibility(View.GONE);
                }
            }
        });

        sv_address.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mResultReceiver = new AddressResultReceiver(new Handler());
                startIntentService(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setView(eventView);
        builder.setIcon(R.drawable.ic_event_24dp);
        builder.setTitle(R.string.label_create_event);
        builder.setPositiveButton(R.string.label_event_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth() + 1;
                int day = datePicker.getDayOfMonth();
                if ( sw_time.isChecked() ) {
                    int hour, minute;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        hour = timePicker.getHour();
                        minute = timePicker.getMinute();
                    } else {
                        hour = timePicker.getCurrentHour();
                        minute = timePicker.getCurrentMinute();
                    }
                } else {
//                    Toast.makeText(getActivity(), "Event<"+et_eventTitle.getText()+"> at " +
//                            year +"/"+month+"/"+day, Toast.LENGTH_SHORT).show();
                }
                events.add(et_eventTitle.getText().toString());
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        eventDialog = builder.show();
        eventDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                builder2.setTitle(R.string.warning);
                builder2.setMessage(R.string.prompt_quit);
                builder2.setPositiveButton(R.string.label_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eventDialog.dismiss();
                    }
                });
                builder2.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder2.show();
            }
        });
    }

    /**
     * send a post request to create a new event
     */
    private void postEvent(){
        RequestAction actionPostEvents = new RequestAction() {
            @Override
            public void actOnPre() {
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 201 ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        showEvents();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(),response,Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Toast.makeText(getActivity(),jsonObject.getString(Constant.KEY_MSG),
                                Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(),response,Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        String url = UrlHelper.urlEventByClub(club.id,EVENTS_PER_REQUEST,offset);
//        RequestHelper.sendGetRequest(url,actionGetEvents);
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private void startIntentService(String queryAddress) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constant.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constant.ADDRESS_DATA_EXTRA,queryAddress);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        getActivity().startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == Constant.SUCCESS_RESULT) {
                ArrayList<Address> addressList = resultData.getParcelableArrayList(Constant.RESULT_DATA_KEY);
                if ( addressList == null ) {
                    return;
                }
                ArrayList<String> addresses = new ArrayList<>(5);
                for ( Address address : addressList ) {
                    ArrayList<String> addressLines = new ArrayList<>();
                    for ( int i = 0; i < address.getMaxAddressLineIndex(); i++ ) {
                        addressLines.add(address.getAddressLine(i));
                    }
                    String addressName = TextUtils.join(System.getProperty("line.separator"),addressLines);
                    addresses.add(addressName);
                }
                lv_address.setAdapter(new ArrayAdapter<String>(
                        getContext(),android.R.layout.simple_list_item_single_choice,addresses
                ));
            } else {
                String errorMessage = resultData.getString(Constant.RESULT_DATA_KEY);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
        }
    }
}
