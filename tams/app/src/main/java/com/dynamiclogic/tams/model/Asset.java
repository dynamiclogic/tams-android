package com.dynamiclogic.tams.model;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Andreas on 8/2/2015.
 */
public class Asset {

    private String mId;
    private String mName, mDescription;
    private Bitmap mPicture;
    private LatLng mLatLng;

    // a URL to the image online
    //private String mImageURL;

    // a path to the image on the device
    //private String mImagePathLocal;

    public Asset(LatLng latLng){
        mLatLng = latLng;
    }

    public LatLng getLatLng(){
        return mLatLng;
    }

    /**
   public String getImageURL() {
        return mImageURL;
    }

    public String getImagePathLocal() {
        return mImagePathLocal;
    }
    */

    public String getName(){
        return mName;
    }

    public void setName(String name){
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
}