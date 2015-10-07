package com.dynamiclogic.tams.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dynamiclogic.tams.model.Asset;
import com.dynamiclogic.tams.model.callback.AssetsListener;
import com.dynamiclogic.tams.utils.BaseApplication;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ntessema on 8/23/15.
 */
public final class Database {

    private static final String TAG = Database.class.getSimpleName();
    private static Database sDatabase = new Database();
    private SharedPreferences prefs;
    private List<AssetsListener> assetListenerList = new ArrayList<>();

    private Database() {}

    public static final synchronized Database getInstance() { return sDatabase; }

    /**
     * This method should be called exactly once, from {@link BaseApplication}.
     */
    public void initialize(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean addAssetListener(AssetsListener listener) {
        return assetListenerList.add(listener);
    }

    public boolean removeAssetListener(AssetsListener listener) {
        return assetListenerList.remove(listener);
    }

    public void addNewAsset(Asset asset) {

        List<Asset> list = getListOfAssets();
        list.add(asset);
        writeListOfAssetsToPrefs(list);

        for (AssetsListener listener : assetListenerList) {
            listener.onAssetsUpdated(list);
        }
    }

    public void removeAsset(Asset asset) {
        List<Asset> list = getListOfAssets();
        list.remove(asset);

        writeListOfAssetsToPrefs(list);

        for (AssetsListener listener : assetListenerList) {
            listener.onAssetsUpdated(list);
        }
    }

    public List<Asset> getListOfAssets() {
        String data = prefs.getString("asset_list", null);

        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        List<Asset> list = gson.fromJson(data, new TypeToken<ArrayList<Asset>>(){}.getType());

        if (list == null) {
            return new ArrayList<Asset>();
        }

        return list;
    }

    public List<LatLng> getListOfLatLngs() {
        String data = prefs.getString("asset_list", null);

        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        List<Asset> list = gson.fromJson(data, new TypeToken<ArrayList<Asset>>(){}.getType());

        if (list == null) {
            return new ArrayList<LatLng>();
        }

        List<LatLng> latLngs = new ArrayList<LatLng>();

        for (Asset a : list) {
            latLngs.add(a.getLatLng());
        }

        return latLngs;
    }

    private boolean writeListOfAssetsToPrefs(List<Asset> assets) {
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        String data = gson.toJson(assets);
        SharedPreferences.Editor e = prefs.edit();
        e.putString("asset_list", data);
        return e.commit();
    }


}
