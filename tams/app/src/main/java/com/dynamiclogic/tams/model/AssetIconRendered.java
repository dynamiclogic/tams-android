package com.dynamiclogic.tams.model;

import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by Andreas on 11/21/2015.
 */
public class AssetIconRendered extends DefaultClusterRenderer<Asset>{
    public AssetIconRendered(Context context, GoogleMap map,
                             ClusterManager<Asset> clusterManager){
        super(context,map,clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(Asset point, MarkerOptions markerOptions) {

        super.onBeforeClusterItemRendered(point, markerOptions);
        //markerOptions = new MarkerOptions().position(point.getLatLng())
               // .title(point.getName()).snippet(point.getDescription());
        markerOptions.title(point.getName()).snippet(point.getDescription());
    }

    protected void onClusterItemRendered(Asset clusterItem,
                                         Marker marker) {

        super.onClusterItemRendered(clusterItem, marker);
    }
    protected boolean shouldRenderAsCluster(Cluster<Asset> cluster) {
        return cluster.getSize() > 10; // when count of markers is more than 3, render as cluster
    }

}
