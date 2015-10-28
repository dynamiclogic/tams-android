package com.dynamiclogic.tams.database;

import android.content.Context;

import com.dynamiclogic.tams.model.Asset;
import com.dynamiclogic.tams.model.callback.AssetsListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ntessema on 10/24/15.
 */
public abstract class Database {

    private static Database instance = new IvanDatabase();
    private Context mContext;
    private List<AssetsListener> assetListenerList = new ArrayList<>();

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
        for (AssetsListener listener : assetListenerList) {
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
