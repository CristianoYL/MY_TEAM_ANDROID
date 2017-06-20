package com.example.cristiano.myteam.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cristiano.myteam.R;
import com.example.cristiano.myteam.request.RequestAction;
import com.example.cristiano.myteam.request.RequestHelper;
import com.example.cristiano.myteam.structure.Club;
import com.example.cristiano.myteam.structure.Player;
import com.example.cristiano.myteam.util.Constant;
import com.example.cristiano.myteam.util.UrlHelper;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.Date;


/**
 * Created by Cristiano on 2017/4/18.
 *
 * this fragment presents the club map page,
 * and allows the club member to share and view each other's locations
 */

public class ClubMapFragment extends Fragment implements OnMapReadyCallback{

    private static final String ARG_CLUB = "club";
    private static final String ARG_PLAYER = "player";

    private GoogleMap mMap;
    private MarkerOptions selfMarker;

    private FloatingActionButton fab_locate, fab_share, fab_view;
    private static final int ENOUGH_LOCATION_UPDATES = 10;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10086;

    private int locationUpdateCount;
    private Location currentBestLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private Club club;
    private Player player;

    private View view;

    public ClubMapFragment() {
    }

    public static ClubMapFragment newInstance(Club club, Player player){
        ClubMapFragment fragment = new ClubMapFragment();
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
        view = inflater.inflate(R.layout.fragment_club_map, container, false);
        fab_locate = (FloatingActionButton) view.findViewById(R.id.fab_locate);
        fab_share = (FloatingActionButton) view.findViewById(R.id.fab_share);
        fab_view = (FloatingActionButton) view.findViewById(R.id.fab_view);
        checkPermission();

        locationListener =  new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationUpdateCount++;
                mMap.clear();
                LatLng newLocation = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(newLocation).icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
                if ( isBetterLocation(location,currentBestLocation) ) {
                    currentBestLocation = location;
                    Log.d("LOCATION_","Use new location");
                } else {
                    Log.d("LOCATION_","Use previous best location");
                }
                if ( locationUpdateCount >= ENOUGH_LOCATION_UPDATES ) {
                    removeLocationUpdateAndReport();
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
                getAllLocations();
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
                    builder.setTitle("Notice");
                    builder.setMessage("Are you sure to share your location info with your teammates?");
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
                    Toast.makeText(getContext(),"No location data available. Please try to locate yourself first.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment);
        mapFragment.getMapAsync(this);
        return view;
    }

    /**
     * send a GET request to retrieve the teamsheet info
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

    private void getAllLocations() {
        RequestAction actionGetAllLocations = new RequestAction() {
            @Override
            public void actOnPre() {
                mMap.clear();
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
                            if ( playerID != player.getId() ) {
                                addMarkerOnMap(latitude,longitude,"Player "+ playerID,lastUpdate);
                            } else {
                                addMarkerOnMap(latitude,longitude,"My Location",lastUpdate);
                            }
                        }
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(9));
                        Toast.makeText(getContext(), jsonArray.length() + " club member(s)' locations are shown on map!", Toast.LENGTH_SHORT).show();
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

    private void addMarkerOnMap(Double latitude, Double longitude, String tag, String lastUpdate) {
        LatLng latLng = new LatLng(latitude,longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).title(tag)
                .snippet("As of " + lastUpdate));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            Log.d("LOCATION_","first location");
            return true;
        }

        Log.d("LOCATION_","Comparing new location("+location.getLatitude()+","+location.getLongitude()+")" +
                " to currentBestLocation("+currentBestLocation.getLatitude()+","+currentBestLocation.getLongitude()+")");

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            Log.d("LOCATION_","is sig newer");
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            Log.d("LOCATION_","is sig older");
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
            Log.d("LOCATION_","more accurate");
            return true;
        } else if (isNewer && !isLessAccurate) {
            Log.d("LOCATION_","newer but less accurate");
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            Log.d("LOCATION_","newer sig less accurate same provider");
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
        }
    }

    /**
     * stop listening for location updates, and move camera to the current best estimated location
     */
    private void removeLocationUpdateAndReport() {
        if ( currentBestLocation == null ) {
            Toast.makeText(getContext(),"Failed to get location, please try again.",Toast.LENGTH_SHORT).show();
            return;
        }

        locationManager.removeUpdates(locationListener);
        Log.d("LOCATION_","LAT:"+currentBestLocation.getLatitude());
        Log.d("LOCATION_","LONG:"+currentBestLocation.getLongitude());
        LatLng myLocation = new LatLng(currentBestLocation.getLatitude(),currentBestLocation.getLongitude());
        mMap.clear();
        DateFormat format = Constant.MARKER_DATE_FORMAT;
        String currentTime = format.format(new Date());
        selfMarker = new MarkerOptions().position(myLocation).title("My Location")
                .snippet("As of " + currentTime);
        mMap.addMarker(selfMarker);
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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // use Sydney as the default location
        // Add a marker in Sydney and move the camera
    }
}
