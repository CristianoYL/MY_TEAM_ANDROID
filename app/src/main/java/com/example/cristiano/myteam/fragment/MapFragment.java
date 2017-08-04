package com.example.cristiano.myteam.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.service.location.FetchAddressIntentService;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by Cristiano on 2017/4/18.
 *
 * this fragment presents the club map page,
 * and allows the club member to share and view each other's locations
 */

public class MapFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener{

    private static final String ARG_CLUB = "club";
    private static final String ARG_PLAYER = "player";
    private static final String TAG = "MapFragment";
    private static final int ENOUGH_LOCATION_UPDATES = 10;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10086;

    private GoogleMap mMap;
    private Marker selfMarker,eventMarker;
    private HashMap<Integer,Marker> teammateMarkerMap; // map teammate's playerID to marker
    private ArrayList<Address> addressList;
    private Address eventAddress;

    private View layout_mapOptions, layout_searchResult, layout_resultDetail;
    private FloatingActionButton fab_locate, fab_share, fab_view, fab_hide,fab_addEvent,fab_more;
    private TextView tv_status;
    private SearchView sv_address;
    private ListView lv_address;
    private Button btn_more, btn_less;

    private int locationUpdateCount;
    private Location currentBestLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private AddressResultReceiver mResultReceiver;

    private Club club;
    private Player player;
    private OnCreateEventRequestListener onCreateEventRequestListener;

    private View view;

    public interface OnCreateEventRequestListener{
        void createEvent(Address address);
    }

    public MapFragment() {
    }

    public static MapFragment newInstance(Club club, Player player){
        MapFragment fragment = new MapFragment();
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
        view = inflater.inflate(R.layout.fragment_map, container, false);
        fab_locate = (FloatingActionButton) view.findViewById(R.id.fab_locate);
        fab_share = (FloatingActionButton) view.findViewById(R.id.fab_share);
        fab_view = (FloatingActionButton) view.findViewById(R.id.fab_view);
        fab_hide = (FloatingActionButton) view.findViewById(R.id.fab_hide);
        fab_more = (FloatingActionButton) view.findViewById(R.id.fab_more);
        fab_addEvent = (FloatingActionButton) view.findViewById(R.id.fab_addEvent);
        tv_status = (TextView) view.findViewById(R.id.tv_locatingStatus);
        sv_address = (SearchView) view.findViewById(R.id.sv_address);
        lv_address = (ListView) view.findViewById(R.id.lv_address);
        btn_more = (Button) view.findViewById(R.id.btn_more);
        btn_less = (Button) view.findViewById(R.id.btn_less) ;
        layout_searchResult = view.findViewById(R.id.layout_search_results);
        layout_resultDetail = view.findViewById(R.id.layout_result_detail);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            onCreateEventRequestListener = (OnCreateEventRequestListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement OnCreateEventRequestListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermission();  // check if user allows location service
    }

    @Override
    public void onDetach() {
        super.onDetach();
        locationManager.removeUpdates(locationListener);
        selfMarker = null;
        eventMarker = null;
        if ( teammateMarkerMap != null ) {
            teammateMarkerMap.clear();
        }
    }

    /**
     *  start the Google Maps Service, find the fragment and render the map in the fragment
     */
    private void startMapService(){
        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentByTag(Constant.FRAGMENT_MAP);
        if ( mapFragment == null ) {
            mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.frame_map,mapFragment,Constant.FRAGMENT_MAP);
            transaction.commit();
            fragmentManager.executePendingTransactions();
        }
        mapFragment.getMapAsync(this);
    }

    private Marker addMarkerOnMap(Double latitude, Double longitude, String title, String lastUpdate) {
        LatLng latLng = new LatLng(latitude,longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
        if ( title != null) {
            marker.setTitle(title);
        }
        if ( lastUpdate != null ) {
            marker.setSnippet(getResources().getString(R.string.label_location_as_of)+ lastUpdate);
        }
        return marker;
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            Log.d(TAG,"first location");
            return true;
        }

        Log.d(TAG,"Comparing new location("+location.getLatitude()+","+location.getLongitude()+")" +
                " to currentBestLocation("+currentBestLocation.getLatitude()+","+currentBestLocation.getLongitude()+")");

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            Log.d(TAG,"is sig newer");
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            Log.d(TAG,"is sig older");
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            Log.d(TAG,"more accurate");
            return true;
        } else if (isNewer && !isLessAccurate) {
            Log.d(TAG,"newer but less accurate");
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            Log.d(TAG,"newer sig less accurate same provider");
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * starts listening for location updates
     */
    private void startLocating(){
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationUpdateCount = 0;
            currentBestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if ( currentBestLocation != null ) {
                LatLng currentBestLatLng = new LatLng(currentBestLocation.getLatitude(),currentBestLocation.getLongitude());
                if ( selfMarker == null ) {
                    selfMarker = mMap.addMarker(new MarkerOptions()
                            .position(currentBestLatLng)
                            .title(getString(R.string.label_my_location))
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_my_location)));
                } else {
                    selfMarker.setPosition(currentBestLatLng);
                    selfMarker.setVisible(true);
                    selfMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_my_location));
                }
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
            tv_status.setVisibility(View.VISIBLE);
        }
    }

    /**
     * stop listening for location updates, and move camera to the current best estimated location
     */
    private void updateCurrentLocation() {
        tv_status.setVisibility(View.INVISIBLE);
        locationUpdateCount = ENOUGH_LOCATION_UPDATES;
        if ( currentBestLocation == null ) {
            Toast.makeText(getContext(),"Failed to get location, please try again.",Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager.removeUpdates(locationListener);    // stop listening for updates
        Log.d(TAG,"LAT:"+currentBestLocation.getLatitude());
        Log.d(TAG,"LONG:"+currentBestLocation.getLongitude());
        LatLng myLocation = new LatLng(currentBestLocation.getLatitude(),currentBestLocation.getLongitude());
        DateFormat format = Constant.MARKER_DATE_FORMAT;
        String currentTime = format.format(new Date());
        if ( selfMarker != null ) {
            selfMarker.setPosition(myLocation);
            selfMarker.setTitle(getString(R.string.label_my_location));
            selfMarker.setSnippet(getString(R.string.label_location_as_of) + currentTime);
        } else {
            selfMarker = addMarkerOnMap(currentBestLocation.getLatitude(),
                    currentBestLocation.getLongitude(),
                    getString(R.string.label_my_location),
                    getString(R.string.label_location_as_of) + currentTime);
        }
        selfMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_my_location));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));    // zoom of 12 is between city and state
        mMap.animateCamera(CameraUpdateFactory.zoomOut());
    }

    /**
     * check if user grants Location permission, if not, request the permission
     */
    private void checkPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            startMapService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    // this app has to use the location permission, if the user rejected, ask again
                    checkPermission();
                }
            }
        }
    }

    private AlertDialog.Builder builder;
    private AlertDialog eventDialog;
    /**
     *  This method is called when the Google Maps service is ready.
     *  It will create a locationListener to listen for the location updates
     *  and initialize all the necessary components for the location service
     * @param googleMap the GoogleMaps instance
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        locationListener =  new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationUpdateCount++;
                if ( isBetterLocation(location,currentBestLocation) ) {
                    currentBestLocation = location;
                    LatLng newLocation = new LatLng(location.getLatitude(),location.getLongitude());
                    if ( selfMarker == null ) {
                        selfMarker = mMap.addMarker(new MarkerOptions()
                                .position(newLocation)
                                .title(getString(R.string.label_my_location))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_my_location)));
                    } else {
                        selfMarker.setPosition(newLocation);
                        selfMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_my_location));
                        selfMarker.setVisible(true);
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14)); // move to a finer viewer
                    Log.d(TAG,"Use new location");
                } else {
                    Log.d(TAG,"Use previous best location");
                }
                if ( locationUpdateCount >= ENOUGH_LOCATION_UPDATES ) {
                    updateCurrentLocation();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        fab_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelLocating();
                getAllLocations();
            }
        });

        fab_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_hide.setVisibility(View.GONE);
                fab_view.setVisibility(View.VISIBLE);
                if ( teammateMarkerMap != null ) {
                    for ( int playerID : teammateMarkerMap.keySet() ) {
                        teammateMarkerMap.get(playerID).setVisible(false);
                    }
                }
            }
        });

        fab_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocating();
            }
        });

        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( locationUpdateCount >= ENOUGH_LOCATION_UPDATES ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.notice);
                    builder.setMessage(R.string.prompt_share_location);
                    builder.setPositiveButton(R.string.label_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            uploadLocation(currentBestLocation);
                        }
                    });
                    builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.setCancelable(true);
                    builder.show();
                } else {
                    Toast.makeText(getContext(),R.string.prompt_share_location_fail,Toast.LENGTH_SHORT).show();
                }
            }
        });

        fab_addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do you want to create a new event?");
                builder.setPositiveButton(R.string.label_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onCreateEventRequestListener.createEvent(eventAddress);
                    }
                });
                builder.setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
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
                layout_searchResult.setVisibility(View.GONE);
                return false;
            }
        });

        lv_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                layout_resultDetail.setVisibility(View.GONE);
                btn_more.setVisibility(View.VISIBLE);
                if ( position < addressList.size() ) {
                    eventAddress = addressList.get(position);
                    if ( eventMarker == null ) {
                        eventMarker = addMarkerOnMap(eventAddress.getLatitude(),eventAddress.getLongitude(),"Event Location",null);
                    } else {
                        eventMarker.setPosition(new LatLng(eventAddress.getLatitude(),eventAddress.getLongitude()));
                        eventMarker.setVisible(true);
                    }
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(eventMarker.getPosition(),9)));
                } else {
                    Log.e(TAG,"Address result list index out of bound.");
                }
            }
        });

                layout_mapOptions = view.findViewById(R.id.layout_options);
        fab_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( layout_mapOptions.getVisibility() == View.VISIBLE ) {
                    layout_mapOptions.setVisibility(View.GONE);
                } else {
                    layout_mapOptions.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // expand result list
                layout_resultDetail.setVisibility(View.VISIBLE);
                btn_more.setVisibility(View.GONE);
            }
        });
        btn_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide result list
                layout_resultDetail.setVisibility(View.GONE);
                btn_more.setVisibility(View.VISIBLE);
            }
        });

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        startLocating();
    }

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

    /**
     *  Force to stop listening for location update and
     *    used the current best estimate as the current location.
     *  This method is called when user requests other map events
     */
    private void cancelLocating(){
        updateCurrentLocation();
    }

    /**
     * send a PUT request to upload my current location
     */
    private void uploadLocation(Location location){
        RequestAction actionPutLocation = new RequestAction() {
            @Override
            public void actOnPre() {

            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 || responseCode == 201 ) {    // success
                    Toast.makeText(getContext(),"Location uploaded!",Toast.LENGTH_SHORT).show();
                } else { // show error
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString(Constant.KEY_MSG);
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        };
        String latitude = location.getLatitude()+"";
        String longitude = location.getLongitude()+"";
        com.example.cristiano.myteam.structure.Location locationData = new com.example.cristiano.myteam.structure.Location(club.id,player.getId(),latitude,longitude,null);
        String url = UrlHelper.urlLocationByClubPlayer(club.id, player.getId());
        RequestHelper.sendPutRequest(url,locationData.toJson(),actionPutLocation);
    }

    /**
     *  Send a get request to server to retrieve all teammates last known locations
     *   and show them on the map
     */
    private void getAllLocations() {
        RequestAction actionGetAllLocations = new RequestAction() {
            @Override
            public void actOnPre() {
                if ( teammateMarkerMap != null ) {
                    for ( int playerID: teammateMarkerMap.keySet()) {
                        teammateMarkerMap.get(playerID).setVisible(false);   // hide all previous teammate markers
                    }
                } else {
                    teammateMarkerMap = new HashMap<>();
                }
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {    // success
                    try {
                        JSONObject jsonObject = new JSONObject((response));
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.TABLE_LOCATION);
                        for ( int i = 0; i < jsonArray.length(); i++ ) {
                            JSONObject jsonLocation = jsonArray.getJSONObject(i);
                            int playerID = jsonLocation.getInt(Constant.LOCATION_P_ID);
                            Double latitude = Double.parseDouble(jsonLocation.getString(Constant.LOCATION_LAT));
                            Double longitude = Double.parseDouble(jsonLocation.getString(Constant.LOCATION_LNG));
                            String lastUpdate = jsonLocation.getString(Constant.LOCATION_LAST_UPDATE);
                            try {
                                Date date = Constant.getServerDateFormat().parse(lastUpdate);
                                lastUpdate = Constant.MARKER_DATE_FORMAT.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if ( playerID != player.getId() ) { // ignore if it's the user himself
                                if ( teammateMarkerMap.containsKey(playerID) ) {
                                    Marker marker = teammateMarkerMap.get(playerID);
                                    marker.setPosition(new LatLng(latitude,longitude));
                                    marker.setVisible(true);
                                    marker.setTitle("Player "+playerID);
                                    marker.setSnippet(lastUpdate);
                                } else {
                                    Marker marker = addMarkerOnMap(latitude, longitude, "Player "+playerID,lastUpdate);
                                    teammateMarkerMap.put(playerID,marker);
                                    marker.setVisible(true);
                                }
                            }
                        }
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(9));
                        if ( jsonArray.length() > 0 ) {
                            fab_hide.setVisibility(View.VISIBLE);
                            fab_view.setVisibility(View.GONE);
                        } else {
                            fab_hide.setVisibility(View.GONE);
                            fab_view.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(getContext(), (jsonArray.length() - 1 ) + " club member(s)' locations are shown on map!", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Failed to show member' locations", Toast.LENGTH_SHORT).show();
                    }
                } else { // show error
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString(Constant.KEY_MSG);
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        };
        String url = UrlHelper.urlLocationByClub(club.id);
        RequestHelper.sendGetRequest(url,actionGetAllLocations);
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
                addressList = resultData.getParcelableArrayList(Constant.RESULT_DATA_KEY);
                if ( addressList == null ) {
                    addressList = new ArrayList<>();
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
                layout_searchResult.setVisibility(View.VISIBLE);
                btn_more.setVisibility(View.GONE);
                layout_resultDetail.setVisibility(View.VISIBLE);
                lv_address.setAdapter(new ArrayAdapter<String>(
                        getContext(),android.R.layout.simple_list_item_1,addresses
                ));
                lv_address.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            } else {
                layout_searchResult.setVisibility(View.GONE);
                String errorMessage = resultData.getString(Constant.RESULT_DATA_KEY);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if ( eventMarker != null && marker.equals(eventMarker) ) {
            fab_addEvent.setVisibility(View.VISIBLE);
            Log.d(TAG,"onMarkerClick:"+marker.getTitle());
        }
        return false;
    }
}
