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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ntessema on 8/23/15.
 */
public final class SharedPrefsDatabase extends Database {

    private static final String TAG = SharedPrefsDatabase.class.getSimpleName();
    private SharedPreferences prefs;
    private static final String ASSETS_KEY = "asset_map";

    /**
     * This method should be called exactly once, from {@link BaseApplication}.
     */
    public void initialize(Context context) {
        super.initialize(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public synchronized void addNewAsset(Asset asset) {
        Map<String,Asset> map = getMapOfAssets();
        map.put(asset.getId().toString(), asset);
        writeMapOfAssetsToPrefs(map);

        notifyListeners();
    }

    public synchronized void updateAsset(Asset asset){
        Log.d(TAG, "onUpdateAsset");

        Map<String,Asset> map = getMapOfAssets();

        Asset a = map.get(asset.getId().toString());
        a.setName(asset.getName());
        a.setDescription(asset.getDescription());

        writeMapOfAssetsToPrefs(map);

        notifyListeners();

    }

    public synchronized void removeAsset(String id) {
        Log.d(TAG, "removing asset " + id);

        Map<String, Asset> map = getMapOfAssets();
        map.remove(id);
        writeMapOfAssetsToPrefs(map);

        notifyListeners();
    }

    public List<Asset> getListOfAssets() {
        return new ArrayList<Asset>(getMapOfAssets().values());
    }

    private Map<String,Asset> getMapOfAssets() {
        String data = prefs.getString(ASSETS_KEY, null);

        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        Map<String,Asset> map = gson.fromJson(data, new TypeToken<HashMap<String,Asset>>(){}.getType());

        if (map == null) { return new HashMap<>(); }

        return map;
    }

    public List<LatLng> getListOfLatLngs() {
        List<LatLng> latLngs = new ArrayList<LatLng>();

        List<Asset> assets = getListOfAssets();
        for (Asset a : assets) {
            latLngs.add(a.getLatLng());
        }

        return latLngs;
    }

    private synchronized boolean writeMapOfAssetsToPrefs(Map<String,Asset> assets) {
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        String data = gson.toJson(assets);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(ASSETS_KEY, data);
        return e.commit();
    }

    public Asset getAssetFromID(String id){

        for(Asset a : this.getListOfAssets()){
            if(a.getId().equals(id)){
                return a;
            }
        }
        return null;
    }


}