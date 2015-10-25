package com.dynamiclogic.tams.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dynamiclogic.tams.R;
import com.dynamiclogic.tams.activity.fragment.PanelFragment.OnPanelFragmentInteractionListener;
import com.dynamiclogic.tams.database.Database;
import com.dynamiclogic.tams.database.SharedPrefsDatabase;
import com.dynamiclogic.tams.model.Asset;
import com.dynamiclogic.tams.model.callback.AssetsListener;
import com.dynamiclogic.tams.model.callback.TAMSLocationListener;
import com.dynamiclogic.tams.utils.SlidingUpPanelLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        SlidingUpPanelLayout.PanelSlideListener,
        com.google.android.gms.location.LocationListener,
        OnPanelFragmentInteractionListener,
        AssetsListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    protected Location mCurrentLocation;
    protected LatLng mCurrentLatLng;
    protected GoogleApiClient mGoogleApiClient;
    protected GoogleMap map;
    private LocationManager mLocationManager;
    private Database database;
    protected ArrayList<LatLng> mListLatLngs = new ArrayList<>();
    private List<TAMSLocationListener> mLocationListeners = new ArrayList<>();
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
//    private static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 0;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
//    private int coarseLocationPermissionCheck;
    private int fineLocationPermissionCheck;
    private boolean mRequestingLocationUpdates = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        Log.d(TAG, "ONCREATE---mCurrentLocation: " + mCurrentLocation);
        setContentView(R.layout.activity_main);

        database = SharedPrefsDatabase.getInstance();

        ((SlidingUpPanelLayout) getWindow().getDecorView().findViewById(R.id.sliding_layout))
                .setPanelSlideListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();
        mLocationRequest = createLocationRequest();

        //Checking for permissions on Android Marshmallow and above
        //ONLY NEED 1 of 2 location permissions, TODO fix
//        coarseLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION);
        fineLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        //If the permission isn't granted, request it
        //ONLY NEED 1 of 2 location permissions, TODO fix
//        if( coarseLocationPermissionCheck == PackageManager.PERMISSION_DENIED){
//            //Request coarse location
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
//        }
        if ( fineLocationPermissionCheck == PackageManager.PERMISSION_DENIED){
            //Request fine location
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        // Restoring the markers on configuration changes
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("points")) {
                mListLatLngs = savedInstanceState.getParcelableArrayList("points");
            }
        } else {
            mListLatLngs.addAll(database.getListOfLatLngs());
        }


        //Intent to start the AddAsset Activity
        final Intent addAssetIntent = new Intent(this, AddAsset.class);


        // Get a reference to the floating button's to start appropriate activities
        final FloatingActionButton newNode = (FloatingActionButton) findViewById(R.id.node);
        newNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick for FAB");

                addAssetIntent.putExtra(AddAssetFragment.EXTRA_ASSET_LOCATION, mCurrentLocation);

                startActivity(addAssetIntent);
                drawMarkers();
            }
        });

    }

    //Check the results of requested permissions for API 23+
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //ONLY NEED 1 of 2 location permissions, TODO fix
        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_COARSE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    Log.d(TAG, "Coarse Location permission granted");
//                    coarseLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
//                            Manifest.permission.ACCESS_COARSE_LOCATION);
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "Fine Location permission granted");
                    fineLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //Set up location request with the desired parameters for the level of accuracy we need
    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();

        //Prefered rate to receive location updates in milliseconds,
        // 10 seconds
        mLocationRequest.setInterval(10000);

        //Fasteset rate at which the app can handle location updates in milliseconds,
        // 5 seconds
        mLocationRequest.setFastestInterval(5000);

        //Priority for location accuracy. In our case we want the most accurate location possible
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return mLocationRequest;
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Connect to the Google API Client
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Disconnect from Google API Client
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        database.addAssetListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        database.removeAssetListener(this);
    }

    @Override
    public void onAssetsUpdated(List<Asset> assets) {
        mListLatLngs.clear();
        mListLatLngs.addAll(database.getListOfLatLngs());
        refreshMarkers();
    }

    public Object onRetainCustomNonConfigurationInstance() {
        return mListLatLngs;
    }

    private void drawMarker(LatLng point) {
        MarkerOptions markerOptions = new MarkerOptions().position(point);
        //markerOptions.position(point);
        if (map != null) {
            this.map.addMarker(markerOptions);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        try {
            if (map == null) {
                map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            }

            Criteria criteria = new Criteria();
            String bestProvider = mLocationManager.getBestProvider(criteria, true);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            Location location = mLocationManager.getLastKnownLocation(bestProvider);

            if (location != null) {
                onLocationChanged(location);
            }

            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(map.getMyLocation().getLatitude(),map.getMyLocation().getLongitude()),13));
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            // Place dot on current location
            map.setMyLocationEnabled(true);

            // Turns traffic layer on
        //    map.setTrafficEnabled(true);

            // Enables indoor maps
        //    map.setIndoorEnabled(true);

            // Turns on 3D buildings
        //    map.setBuildingsEnabled(true);

            // Show Zoom buttons
        //    map.getUiSettings().setZoomControlsEnabled(true);

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e);
        }

        if (map != null) {
            final GoogleMap finalMap = map;

            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                public void onMapLongClick(LatLng point) {

                    Intent addAssetIntent = new Intent(MainActivity.this, AddAsset.class);

                    Location loc = new Location("new_location");
                    loc.setLatitude(point.latitude);
                    loc.setLongitude(point.longitude);

                    addAssetIntent.putExtra(AddAssetFragment.EXTRA_ASSET_LOCATION, loc);
                    startActivity(addAssetIntent);

                }
            });

            drawMarkers();
        }
    }

    public void drawMarkers() {
        if (mListLatLngs != null) {
            for (int i = 0; i < mListLatLngs.size(); i++) {
                if (mListLatLngs.get(i) != null) {
                    drawMarker(mListLatLngs.get(i));
                }
            }
        }
    }

    public void refreshMarkers() {
        map.clear();
        drawMarkers();
    }


    //This method returns a location from location updates based on our configurations
    // from the location request
    @Override
    public void onLocationChanged(Location location) {
    //    Log.d(TAG, "onLocationChanged");
    //    Log.d(TAG, "ONLOCATIONCHANGED---mCurrentLocation: " + mCurrentLocation);
        if (location == null){
            Log.d(TAG, "location is null for some reason");
        }

        // Only pan to the current location the very first time
        if (mCurrentLatLng == null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory
                    .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 19);
            map.animateCamera(cameraUpdate);
        }

        mCurrentLocation = location;
        mCurrentLatLng = new LatLng(location.getLatitude(),location.getLongitude());

        // notify location listeners
        for (TAMSLocationListener listener : mLocationListeners) {
            listener.onLocationChanged(mCurrentLocation);
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

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*              PanelSlideListener - Start              */
    @Override
    public void onPanelSlide(View panel, float slideOffset) { }

    @Override
    public void onPanelCollapsed(View panel) { }

    @Override
    public void onPanelExpanded(View panel) { }

    @Override
    public void onPanelAnchored(View panel) { }

    @Override
    public void onPanelHidden(View panel) { }

    /*              PanelSlideListener - End              */

    @Override
    public void onPanelFragmentInteraction() { }

    //TODO save location based values in case of activity destruction from rotation
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("points", mListLatLngs);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((SlidingUpPanelLayout)getWindow().getDecorView().findViewById(R.id.sliding_layout)).setPanelSlideListener(null);
    }

    //Getting current location using the Google API Client
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "ONCONNECTED---mCurrentLocation: " + mCurrentLocation);
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        //Boolean to see if we want location updates
        // initialized to true
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    //Starts location updates
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    //Stops location updates
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void addTAMSLocationListener(TAMSLocationListener listener) {
        mLocationListeners.add(listener);
    }

    public void removeTAMSLocationListener(TAMSLocationListener listener) {
        mLocationListeners.remove(listener);
    }

}
