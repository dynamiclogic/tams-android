package com.dynamiclogic.tams.model;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;
import java.util.Random;
import java.util.UUID;

public class Asset{

    //Not sure if we need mId as a string or a UUID???
    private String mId;
    private String mName, mDescription, mCreatedAt, mUpdatedAt;
    private String mDeleted, mNeedsSync, mIsNew; // TODO why aren't these booleans (Nati)
    private Bitmap mPicture;
    private LatLng mLatLng;


    public Asset(LatLng latLng){
        // TODO Looks like the SQL DB wants a number for an id?
        mId = new Random().nextInt(500000) + "";
        mLatLng = latLng;
    }

    public LatLng getLatLng(){
        return mLatLng;
    }

    public Double getLatitude() { return mLatLng.latitude;}

    public Double getLongitude() { return mLatLng.longitude;}

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
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

    public String getCreatedAt() { return mCreatedAt; }

    public void setCreatedAt(String createdAt) { this.mCreatedAt = createdAt;}

    public String getUpdatedAt() { return mUpdatedAt; }

    public void setUpdatedAt(String updatedAt) { this.mUpdatedAt = updatedAt;}

    public String getIsNew() { return mIsNew; }

    public void setIsNew(String isNew) { this.mIsNew = isNew; }

    public String getDeleted() { return mDeleted; }

    public void setDeleted(String deleted) { this.mDeleted = deleted; }

    public String getNeedsSync() { return mNeedsSync; }

    public void setNeedsSync(String needsSync) { this.mNeedsSync = needsSync; }

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