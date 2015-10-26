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

    private static final String TAG = IvanDatabase.class.getSimpleName();
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
        mDBController.insertAsset(asset);
        notifyListeners();
        mDBSync.sync();
    }

    @Override
    public synchronized void updateAsset(Asset asset) {
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
    public synchronized Asset getAssetFromUUID(UUID id) {
        return null;
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
