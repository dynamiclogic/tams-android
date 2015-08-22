package com.dynamiclogic.tams.activity;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dynamiclogic.tams.R;
import com.dynamiclogic.tams.activity.fragment.PanelFragment;
import com.dynamiclogic.tams.utils.SlidingUpPanelLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.common.api.GoogleApiClient.*;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.dynamiclogic.tams.utils.Asset;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
                                                        SlidingUpPanelLayout.PanelSlideListener,
                                                        ConnectionCallbacks,
                                                        LocationListener,
                                                        OnConnectionFailedListener,
                                                        PanelFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
//    protected boolean mRequestingLocationUpdates;
    protected Location mCurrentLocation;
    protected LatLng mCurrentLatLng;
    protected GoogleMap map;

    protected ArrayList<LatLng> markerArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        ((SlidingUpPanelLayout) getWindow().getDecorView().findViewById(R.id.sliding_layout))
                .setPanelSlideListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();

        //Restoring the markers on configuration changes
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("points")){
                markerArray =  savedInstanceState.getParcelableArrayList("points");

            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        if (mGoogleApiClient.isConnected()/* && !mRequestingLocationUpdates*/) {
            startLocationUpdates();
        }
    }

    /**
     * this is the button to get the present location
     * @param view
     */
    public void addAsset(View view){
        Asset newAsset = new Asset(mCurrentLatLng);
        MarkerOptions mMarker = new MarkerOptions().position(newAsset.getLatLng());
        map.addMarker(mMarker);
        markerArray.add(mMarker.getPosition());
    }

    public Object onRetainCustomNonConfigurationInstance() {
        return markerArray;
    }

    private  void drawMarker(LatLng point){
        MarkerOptions markerOptions = new MarkerOptions().position(point);
        //markerOptions.position(point);
        if(map != null) {
            this.map.addMarker(markerOptions);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "onMapReady()");

        this.map = map;
        //map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        try {
            if (map == null) {
                map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            }

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(bestProvider);

            if (location != null) {
                onLocationChanged(location);
            }
            // locationManager.requestLocationUpdates();

            // map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(map.getMyLocation().getLatitude(),map.getMyLocation().getLongitude()),13));
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            // Place dot on current location
            map.setMyLocationEnabled(true);

            // Turns traffic layer on
            map.setTrafficEnabled(true);

            // Enables indoor maps
            map.setIndoorEnabled(true);

            // Turns on 3D buildings
            map.setBuildingsEnabled(true);

            // Show Zoom buttons
            map.getUiSettings().setZoomControlsEnabled(true);

        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * adds marker to map with touch
         */
        if (map != null) {
            final GoogleMap finalMap = map;

            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                public void onMapClick(LatLng point) {
                    MarkerOptions newMarker = new MarkerOptions().position(point);
                    finalMap.addMarker(newMarker);
                    markerArray.add(newMarker.getPosition());
                }
            });

            if(markerArray != null) {
                for (int i = 0; i < markerArray.size(); i++) {
                    if (markerArray.get(i) != null) {
                        drawMarker(markerArray.get(i));
                    }
                }
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction() {
        Log.d(TAG, "onFragmentInteraction()");
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
    }

    @Override
    public void onPanelCollapsed(View panel) {
        Log.d(TAG, "onPanelCollapsed()");
    }

    @Override
    public void onPanelExpanded(View panel) {
        Log.d(TAG, "onPanelExpanded()");
    }

    @Override
    public void onPanelAnchored(View panel) {
        Log.d(TAG, "onPanelAnchored()");
    }

    @Override
    public void onPanelHidden(View panel) {
        Log.d(TAG, "onPanelHidden()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((SlidingUpPanelLayout)getWindow().getDecorView().findViewById(R.id.sliding_layout)).setPanelSlideListener(null);
    }

    /* Belongs in class for Google Api Client*/
    @Override
    public void onConnected(Bundle bundle) {
    //this gets the last known location and sets it to mLastLocation
            if(mCurrentLocation == null) {

                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
            }

            startLocationUpdates();
    }

    protected void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates");

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /* Belongs in class for Google Api Client */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        mCurrentLocation = location;

        mCurrentLatLng = new LatLng(location.getLatitude(),location.getLongitude());
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("points",markerArray);
        super.onSaveInstanceState(savedInstanceState);
    }
}
