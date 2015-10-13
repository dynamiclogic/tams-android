package com.dynamiclogic.tams.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.dynamiclogic.tams.database.DBController;
import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

public class Asset{

    //Not sure if we need mId as a string or a UUID???
    private String mId, mName, mDescription, mCreated_at, mUpdated_at;
    private String deleted, needsSync, isNew;
    private Bitmap mPicture;
    private LatLng mLatLng;
    private DBController db;


    public Asset(LatLng latLng){
        //mId = UUID.randomUUID();
        mLatLng = latLng;

        mCreated_at = getCurrentTime();
        mUpdated_at = mCreated_at;
        mId = mCreated_at;
        deleted = "0";
        needsSync = "1";
        isNew = "1"; //should be renamed to neverSynced
    }

    public LatLng getLatLng(){
        return mLatLng;
    }

    public Double getLatitude() { return mLatLng.latitude;}

    public Double getLongitude() { return mLatLng.longitude;}

    public String getId() {
        return mId;
    }

    public void setId(String mId) { this.mId = mId;}

    public String getName() { return mName;}

    public void setName(String name) {
        mName = name;
    }

    public String getCreatedAt() { return mCreated_at; }

    public void setCreatedAt(String mCreated_at) { this.mCreated_at = mCreated_at;}

    public String getUpdatedAt() { return mUpdated_at; }

    public void setUpdatedAt(String mUpdated_at) { this.mUpdated_at = mUpdated_at;}

    public String getIsNew() { return isNew; }

    public void setIsNew(String isNew) { this.isNew = isNew; }

    public String getDeleted() { return deleted; }

    public void setDeleted(String deleted) { this.deleted = deleted; }

    public String getNeedsSync() { return needsSync; }

    public void setNeedsSync(String needsSync) { this.needsSync = needsSync; }

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



    public String getCurrentTime() {
        String currentTime = toString().valueOf(System.currentTimeMillis() / 1000L);
        return currentTime;
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
}