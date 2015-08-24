package com.dynamiclogic.tams.database.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Andreas on 8/2/2015.
 */
public class Asset {

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
}