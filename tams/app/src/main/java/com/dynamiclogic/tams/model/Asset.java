package com.dynamiclogic.tams.model;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;
import java.util.UUID;

public class Asset{

    //Not sure if we need mId as a string or a UUID???
    private UUID mId;
    private String mName, mDescription;
    private Bitmap mPicture;
    private LatLng mLatLng;


    public Asset(LatLng latLng){
        mId = UUID.randomUUID();
        mLatLng = latLng;
    }

    public LatLng getLatLng(){
        return mLatLng;
    }

    public UUID getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Bitmap getPicture() {
        return mPicture;
    }

    public void setPicture(Bitmap picture) {
        mPicture = picture;
    }



    @Override
    public boolean equals(Object o) {
        return this.mLatLng.latitude == ((Asset)o).mLatLng.latitude
                && this.mLatLng.longitude == ((Asset)o).mLatLng.longitude
                && this.mId == ((Asset)o).mId;
    }

    @Override
    public int hashCode() {
        return (int)(mLatLng.latitude * mLatLng.longitude * 1000000);
    }

    // Going to be used to determine the sorting for assets in the ListView
    public static class AssetDistanceComparator implements Comparator<Asset> {
        Location currentLocation;
        public AssetDistanceComparator(Location location) {
            currentLocation = location;
        }
        public int compare(Asset a1, Asset a2) {
            if (currentLocation == null) { return 0; }
            Location a1Loc = new Location("a1");
            a1Loc.setLatitude(a1.getLatLng().latitude);
            a1Loc.setLongitude(a1.getLatLng().longitude);

            Location a2Loc = new Location("a2");
            a2Loc.setLatitude(a2.getLatLng().latitude);
            a2Loc.setLongitude(a2.getLatLng().longitude);

            float a1Dist = currentLocation.distanceTo(a1Loc);
            float a2Dist = currentLocation.distanceTo(a2Loc);
            return (int)(a1Dist - a2Dist);
        }
    }
}