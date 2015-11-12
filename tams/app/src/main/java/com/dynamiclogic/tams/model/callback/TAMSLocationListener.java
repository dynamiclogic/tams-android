package com.dynamiclogic.tams.model.callback;

/**
 * Created by Andreas on 10/29/2015.
 */

import android.location.Location;

/**
 * Created by ntessema on 10/25/15.
 */
public interface TAMSLocationListener {

    public void onLocationChanged(Location location);
}