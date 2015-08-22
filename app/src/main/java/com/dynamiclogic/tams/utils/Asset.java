package com.dynamiclogic.tams.utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Andreas on 8/2/2015.
 */
public class Asset {

    private LatLng mAssetLatLng;

    public Asset(LatLng latLng){
        mAssetLatLng = latLng;
    }

    public LatLng getLatLng(){
        return mAssetLatLng;
    }
}