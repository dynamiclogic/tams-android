package com.dynamiclogic.tams.database;

import android.content.Context;
import android.util.Log;

import com.dynamiclogic.tams.model.Asset;
import com.dynamiclogic.tams.model.callback.AssetsListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ntessema on 10/24/15.
 */
public abstract class Database {

    private static Database instance = new IvanDatabase();
    private Context mContext;
    private List<AssetsListener> assetListenerList = new ArrayList<>();
    private static final String TAG = Database.class.getSimpleName();

    protected Database() {}

    public static final synchronized Database getInstance() { return instance; }

    public void initialize(Context context) {
        mContext = context;
    }

    public abstract Asset getAssetFromID(String id);
    public abstract void updateAsset(Asset asset);

    public abstract List<Asset> getListOfAssets();
    public abstract List<LatLng> getListOfLatLngs();

    public abstract void addNewAsset(Asset asset);
    public abstract void removeAsset(String id);

    // Asset listeners
    public void notifyListeners() {
        Log.d(TAG, "notifyListeners() called with: " + "");
        Log.d(TAG, "assetListenerList: " + assetListenerList.toString());
        for (AssetsListener listener : assetListenerList) {
            Log.d(TAG, "Listener: " + listener.toString());
            listener.onAssetsUpdated(getListOfAssets());
        }
    }

    public boolean addAssetListener(AssetsListener listener) {
        return assetListenerList.add(listener);
    }

    public boolean removeAssetListener(AssetsListener listener) {
        return assetListenerList.remove(listener);
    }

}
