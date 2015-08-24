package com.dynamiclogic.tams.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dynamiclogic.tams.database.model.Asset;
import com.dynamiclogic.tams.database.model.callback.AssetListener;
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
    private static Database instance = new Database();
    private SharedPreferences prefs;
    private List<AssetListener> assetListenerList = new ArrayList<>();

    private Database() { }

    public static final synchronized Database getInstance() { return instance; }

    /**
     * This method should be called exactly once, from {@link BaseApplication}.
     */
    public void initialize(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean addAssetListener(AssetListener listener) {
        return assetListenerList.add(listener);
    }

    public boolean removeAssetListener(AssetListener listener) {
        return assetListenerList.remove(listener);
    }

    // TODO find out why this isn't working
    public void addNewAsset(Asset asset) {

        // read current list of assets
        String data = prefs.getString("asset_list", null);

        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        List<Asset> list = gson.fromJson(data, ArrayList.class);

        if (list == null) {
            list = new ArrayList<Asset>();
        }

        list.add(asset);
        data = gson.toJson(list);
        SharedPreferences.Editor e = prefs.edit();
        e.putString("asset_list", data);
        e.commit();

        for (AssetListener listener : assetListenerList) {
            listener.onAssetUpdate(asset);
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



}
