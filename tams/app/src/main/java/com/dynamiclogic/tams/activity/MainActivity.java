package com.dynamiclogic.tams.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dynamiclogic.tams.R;
import com.dynamiclogic.tams.activity.fragment.PanelFragment.OnPanelFragmentInteractionListener;
import com.dynamiclogic.tams.database.Database;
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
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
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
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private int fineLocationPermissionCheck;
    private boolean mRequestingLocationUpdates = true;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Database.getInstance();

        // TODO: 12/12/2015 Fix the overflow menu background color so it is white with black labels
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("TAMS");

        ((SlidingUpPanelLayout) getWindow().getDecorView().findViewById(R.id.sliding_layout))
                .setPanelSlideListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();
        mLocationRequest = createLocationRequest();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Restoring the markers on configuration changes
        restoreFromDestroyed(savedInstanceState);


        //Intent to start the AddAsset Activity
        final Intent addAssetIntent = new Intent(this, AddAsset.class);


        // Get a reference to the floating button's to start appropriate activities
        final FloatingActionButton newNode = (FloatingActionButton) findViewById(R.id.node);
        newNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAssetIntent.putExtra(AddAssetFragment.EXTRA_ASSET_LOCATION, mCurrentLocation);
                startActivity(addAssetIntent);
                drawMarkers();
            }
        });

    }



    private void restoreFromDestroyed(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("points")) {
                mListLatLngs = savedInstanceState.getParcelableArrayList("points");
            }
        } else {
            mListLatLngs.addAll(database.getListOfLatLngs());
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
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
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
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        database.addAssetListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            stopLocationUpdates();
        }

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

            Log.d(TAG, "onMapReady: mCurrentLatLng = " + mCurrentLatLng);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 19);
            map.animateCamera(cameraUpdate);
            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(map.getMyLocation().getLatitude(),map.getMyLocation().getLongitude()),13));
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            // Place dot on current location
            map.setMyLocationEnabled(true);

            // Turns traffic layer on
        //    map.setTrafficEnabled(true);

            // Enables indoor maps
        //    map.setIndoorEnabled(true);

            // Turns on 3D buildings
            map.setBuildingsEnabled(true);

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
            Log.d(TAG, "Server Settings");
            Intent settingsIntent = new Intent(this, Settings.class);
            startActivity(settingsIntent);
            //return true;
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
