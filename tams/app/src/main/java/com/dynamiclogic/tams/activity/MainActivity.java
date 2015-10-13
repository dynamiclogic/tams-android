package com.dynamiclogic.tams.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dynamiclogic.tams.R;
import com.dynamiclogic.tams.activity.fragment.PanelFragment.OnPanelFragmentInteractionListener;
import com.dynamiclogic.tams.database.DBController;
import com.dynamiclogic.tams.model.Asset;
import com.dynamiclogic.tams.model.callback.AssetsListener;
import com.dynamiclogic.tams.utils.SlidingUpPanelLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
        LocationListener,
        OnPanelFragmentInteractionListener,
        AssetsListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    protected Location mCurrentLocation;
    protected LatLng mCurrentLatLng;
    protected GoogleApiClient mGoogleApiClient;
    protected GoogleMap map;
    private LocationManager mLocationManager;
    private DBController database;
    protected ArrayList<LatLng> mListLatLngs = new ArrayList<>();
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        database = DBController.getInstance(this);

        ((SlidingUpPanelLayout) getWindow().getDecorView().findViewById(R.id.sliding_layout))
                .setPanelSlideListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        // Restoring the markers on configuration changes
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("points")) {
                mListLatLngs = savedInstanceState.getParcelableArrayList("points");
            }
        } else {
            // TODO
            //mListLatLngs.addAll(database.getListOfLatLngs());
        }


        //Intent to start the AddAsset Activity
        final Intent addAssetIntent = new Intent(this, AddAsset.class);


        // Get a reference to the floating button's to start appropriate activities
        final FloatingActionButton newNode = (FloatingActionButton) findViewById(R.id.node);
        newNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick for FAB");
                mCurrentLocation = mLastLocation;

                addAssetIntent.putExtra(AddAssetFragment.EXTRA_ASSET_LOCATION, mCurrentLocation);

                startActivity(addAssetIntent);
                drawMarkers();

            }
        });

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
        //database.addAssetListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //database.removeAssetListener(this);
    }

    @Override
    public void onAssetsUpdated(List<Asset> assets) {
        Log.d(TAG, "onAssetsUpdated");
        mListLatLngs.clear();
        // TODO
        //mListLatLngs.addAll(database.getListOfLatLngs());
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


        // add marker on long press
        if (map != null) {
            final GoogleMap finalMap = map;

            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                public void onMapLongClick(LatLng point) {
                    MarkerOptions newMarker = new MarkerOptions().position(point);
                    finalMap.addMarker(newMarker);
                    mListLatLngs.add(newMarker.getPosition());

                    Asset newAsset = new Asset(newMarker.getPosition());
                    database.insertAsset(newAsset);
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

        /*              LocationListener - Start              */

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        if (location == null){

            Log.d(TAG, "location is null for some reason");
        }
        mCurrentLocation = location;
        mCurrentLatLng = new LatLng(location.getLatitude(),location.getLongitude());

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 19);
        map.animateCamera(cameraUpdate);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

        /*              LocationListener - End              */

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
        Log.d(TAG, "onConnected");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
