package com.dynamiclogic.tams.database;

import android.content.Context;
import android.util.Log;

import com.dynamiclogic.tams.model.Asset;
import com.dynamiclogic.tams.model.callback.AssetsListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ntessema on 10/25/15.
 */
public class IvanDatabase extends Database {

    private DBController mDBController;
    private DBSync mDBSync;

    protected IvanDatabase() {}

    public void initialize(Context context) {
        super.initialize(context);
        mDBController = new DBController(context);
        mDBSync = new DBSync(context, mDBController);
    }

    @Override
    public synchronized void addNewAsset(Asset asset) {
        asset.setIsNew(true);
        asset.setCreatedAt(Asset.getCurrentUnixTime());
        mDBController.insertAsset(asset);
        notifyListeners();
        mDBSync.sync();
    }

    @Override
    public synchronized void updateAsset(Asset asset) {
        asset.setUpdatedAt(Asset.getCurrentUnixTime());
        asset.setNeedsSync(true);
        mDBController.updateAsset(asset);
        notifyListeners();
        mDBSync.sync();
    }

    @Override
    public synchronized void removeAsset(String id) {
        mDBController.deleteAsset(id);
        notifyListeners();
        mDBSync.sync();
    }

    @Override
    public synchronized Asset getAssetFromID(String id) {
        return mDBController.getAssetFromId(id);
    }

    @Override
    public synchronized List<Asset> getListOfAssets() {
        List<Asset> assets = mDBController.getListOfAssets();
        return assets;
    }

    @Override
    public synchronized List<LatLng> getListOfLatLngs() {
        List<LatLng> latLngs = new ArrayList<LatLng>();

        List<Asset> assets = new ArrayList<Asset>(getListOfAssets());
        for (Asset a : assets) {
            latLngs.add(a.getLatLng());
        }

        return latLngs;
    }

}
