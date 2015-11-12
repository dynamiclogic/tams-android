package com.dynamiclogic.tams.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dynamiclogic.tams.model.Asset;
import com.dynamiclogic.tams.model.callback.AssetsListener;
import com.dynamiclogic.tams.utils.BaseApplication;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ntessema on 8/23/15.
 */
public abstract class Database {

    private static final String TAG = Database.class.getSimpleName();
    private static Database instance = new IvanDatabase();
    private SharedPreferences prefs;
    private Context mContext;
    private List<AssetsListener> assetListenerList = new ArrayList<>();

    protected Database() {}

    public static final synchronized Database getInstance() { return instance; }

    /**
     * This method should be called exactly once, from {@link BaseApplication}.
     */
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
