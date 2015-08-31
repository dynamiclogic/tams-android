package com.dynamiclogic.tams.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Andreas on 8/2/2015.
 */
public class Asset {

    private String mId;
    private LatLng mAssetLatLng;

    // a URL to the image online
    private String mImageURL;

    // a path to the image on the device
    private String mImagePathLocal;

    public Asset(LatLng latLng){
        mAssetLatLng = latLng;
    }

    public LatLng getLatLng(){
        return mAssetLatLng;
    }

    public String getImageURL() {
        return mImageURL;
    }

    public String getImagePathLocal() {
        return mImagePathLocal;
    }

    @Override
    public boolean equals(Object o) {
        return this.mAssetLatLng.latitude == ((Asset)o).mAssetLatLng.latitude
                && this.mAssetLatLng.longitude == ((Asset)o).mAssetLatLng.longitude
                && this.mId == ((Asset)o).mId;
    }

    @Override
    public int hashCode() {
        return (int)(mAssetLatLng.latitude * mAssetLatLng.longitude * 1000000);
    }
}