package com.dynamiclogic.tams.activity;

import android.content.Context;
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
import com.dynamiclogic.tams.activity.fragment.PanelFragment.*;
import com.dynamiclogic.tams.utils.SlidingUpPanelLayout;
import com.google.android.gms.common.ConnectionResult;
import android.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.dynamiclogic.tams.utils.Asset;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
                                                        SlidingUpPanelLayout.PanelSlideListener,
                                                        LocationListener,
                                                        OnPanelFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    protected Location mCurrentLocation;
    protected LatLng mCurrentLatLng;
    protected GoogleMap map;
    private LocationManager mLocationManager;

    protected ArrayList<LatLng> mListLatLngs = new ArrayList<>();

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

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1000, this);

        //Restoring the markers on configuration changes
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("points")) {
                mListLatLngs =  savedInstanceState.getParcelableArrayList("points");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    public void addAsset(View view){
        Asset newAsset = new Asset(mCurrentLatLng);
        MarkerOptions mMarker = new MarkerOptions().position(newAsset.getLatLng());
        map.addMarker(mMarker);
        mListLatLngs.add(mMarker.getPosition());
    }

    public Object onRetainCustomNonConfigurationInstance() {
        return mListLatLngs;
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

            Criteria criteria = new Criteria();
            String bestProvider = mLocationManager.getBestProvider(criteria, true);
            Location location = mLocationManager.getLastKnownLocation(bestProvider);

            if (location != null) {
                onLocationChanged(location);
            }
            // locationManager.requestLocationUpdates();

            // map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(map.getMyLocation().getLatitude(),map.getMyLocation().getLongitude()),13));
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
            e.printStackTrace();
        }


        // add marker on long press
        if (map != null) {
            final GoogleMap finalMap = map;

            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                public void onMapLongClick(LatLng point) {
                    MarkerOptions newMarker = new MarkerOptions().position(point);
                    finalMap.addMarker(newMarker);
                    mListLatLngs.add(newMarker.getPosition());
                }
            });

            if (mListLatLngs != null) {
                for (int i = 0; i < mListLatLngs.size(); i++) {
                    if (mListLatLngs.get(i) != null) {
                        drawMarker(mListLatLngs.get(i));
                    }
                }
            }
        }


    }

        /*              LocationListener - Start              */

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        mCurrentLocation = location;

        mCurrentLatLng = new LatLng(location.getLatitude(),location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 19);
        map.animateCamera(cameraUpdate);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled");
    }

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
    public void onPanelSlide(View panel, float slideOffset) { Log.d(TAG, "onPanelSlide"); }

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

    /*              PanelSlideListener - End              */

    @Override
    public void onPanelFragmentInteraction() {
        Log.d(TAG, "onFragmentInteraction()");
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("points", mListLatLngs);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((SlidingUpPanelLayout)getWindow().getDecorView().findViewById(R.id.sliding_layout)).setPanelSlideListener(null);
    }

}
