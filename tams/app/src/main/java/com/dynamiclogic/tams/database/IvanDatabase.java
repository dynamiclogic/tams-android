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
public class IvanDatabase implements Database {

    private static final String TAG = IvanDatabase.class.getSimpleName();
    private Context mContext;
    private DBController mDBController;
    private DBSync mDBSync;
    private List<AssetsListener> assetListenerList = new ArrayList<>();

    public IvanDatabase(Context context) {
        mContext = context;
        mDBController = new DBController(mContext);
        mDBSync = new DBSync(mContext, mDBController);
    }

    @Override
    public void addNewAsset(Asset asset) {
        mDBController.insertAsset(asset);

//        for (AssetsListener listener : assetListenerList) {
//            listener.onAssetsUpdated(); // TODO needs a list of the current assets
//        }
    }

    @Override
    public void updateAsset(Asset asset) {
        mDBController.updateAsset(asset);

//        for (AssetsListener listener : assetListenerList) {
//            listener.onAssetsUpdated(); // TODO needs a list of the current assets
//        }
    }

    @Override
    public void removeAsset(String id) {
        Log.e(TAG, "removeAsset not implemented yet");
    //    mDBController.deleteAsset();

//        for (AssetsListener listener : assetListenerList) {
//            listener.onAssetsUpdated(); // TODO needs a list of the current assets
//        }
    }

    @Override
    public Asset getAssetFromUUID(UUID id) {
        return null;
    }

    @Override
    public List<Asset> getListOfAssets() {
        return new ArrayList<>();
    }

    @Override
    public List<LatLng> getListOfLatLngs() {
        List<LatLng> latLngs = new ArrayList<LatLng>();

        List<Asset> assets = new ArrayList<Asset>(getListOfAssets());
        for (Asset a : assets) {
            latLngs.add(a.getLatLng());
        }

        return latLngs;
    }

    public boolean addAssetListener(AssetsListener listener) {
        return assetListenerList.add(listener);
    }

    public boolean removeAssetListener(AssetsListener listener) {
        return assetListenerList.remove(listener);
    }
}
