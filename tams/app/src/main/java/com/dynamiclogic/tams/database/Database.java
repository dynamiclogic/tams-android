package com.dynamiclogic.tams.database;

import com.dynamiclogic.tams.model.Asset;
import com.dynamiclogic.tams.model.callback.AssetsListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.UUID;

/**
 * Created by ntessema on 10/24/15.
 */
public interface Database {

    Asset getAssetFromUUID(UUID id);
    void updateAsset(Asset asset);

    List<Asset> getListOfAssets();
    List<LatLng> getListOfLatLngs();

    void addNewAsset(Asset asset);
    void removeAsset(String id);

    // Asset listeners
    boolean addAssetListener(AssetsListener listener);
    boolean removeAssetListener(AssetsListener listener);

}
