package com.dynamiclogic.tams.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.dynamiclogic.tams.utils.SQLVariables;
import com.dynamiclogic.tams.model.Asset;

/**
 * Created by Ivan Mihov on 8/26/15.
 */
public class DBController extends SQLiteOpenHelper {

    private Context mCxt;

    /**
     * constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */
    public DBController(Context ctx) {
        super(ctx, SQLVariables.DATABASE_NAME, null, SQLVariables.DATABASE_VERSION);
        this.mCxt = ctx;
    }

    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(SQLVariables.CREATE_TABLE_ASSETS);
        database.execSQL(SQLVariables.CREATE_TABLE_LOCATIONS);
        database.execSQL(SQLVariables.CREATE_TABLE_ASSET_TYPES);
        database.execSQL(SQLVariables.CREATE_TABLE_MEDIA);
        database.execSQL(SQLVariables.CREATE_TABLE_ATTRIBUTES);
        database.execSQL(SQLVariables.CREATE_TABLE_ATTRIBUTES_INDEXES);
        database.execSQL(SQLVariables.CREATE_TABLE_ATTRIBUTES_VALUES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        database.execSQL("DROP TABLE IF EXISTS " + SQLVariables._ASSETS_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + SQLVariables._LOCATIONS_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + SQLVariables._ASSET_TYPES_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + SQLVariables._MEDIA_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + SQLVariables._ATTRIBUTES_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + SQLVariables._ATTRIBUTES_INDEXES_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + SQLVariables._ATTRIBUTES_VALUES_TABLE);

        onCreate(database);
    }

    /**
     * Inserts Asset into SQLite DB
     * @param asset
     */
    public void insertAsset(Asset asset) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues assetValues = new ContentValues();
        ContentValues locationValues = new ContentValues();

        assetValues.put(SQLVariables._ASSETS_COLUMN_ASSET_ID, asset.getId());
        if (asset.getCreatedAt() != null) {
            assetValues.put(SQLVariables._ASSETS_COLUMN_CREATED_AT, asset.getCreatedAt());
        } else {
            assetValues.put(SQLVariables._ASSETS_COLUMN_CREATED_AT, "0");
        }
        if (asset.getUpdatedAt() != null) {
            assetValues.put(SQLVariables._ASSETS_COLUMN_UPDATED_AT, asset.getUpdatedAt());
        } else {
            assetValues.put(SQLVariables._ASSETS_COLUMN_UPDATED_AT, "0");
        }

        if (asset.getNeedsSync() != null) {
            assetValues.put(SQLVariables._ASSETS_COLUMN_NEEDSSYNC, asset.getNeedsSync());
        } else {
            assetValues.put(SQLVariables._ASSETS_COLUMN_NEEDSSYNC, "1");
        }

        if (asset.getDeleted() != null) {
            assetValues.put(SQLVariables._ASSETS_COLUMN_DELETED, asset.getDeleted());
        } else {
            assetValues.put(SQLVariables._ASSETS_COLUMN_DELETED, "0");
        }
        if (asset.getIsNew() != null) {
            assetValues.put(SQLVariables._ASSETS_COLUMN_ISNEW, asset.getIsNew());
        } else {
            assetValues.put(SQLVariables._ASSETS_COLUMN_ISNEW, "1");
        }

        if (asset.getName() != null) {
            assetValues.put(SQLVariables._ASSETS_COLUMN_ASSET_NAME, asset.getName());
        } else {
            assetValues.put(SQLVariables._ASSETS_COLUMN_ASSET_NAME, "Unnamed");
        }

        if (asset.getDescription() != null) {
            assetValues.put(SQLVariables._ASSETS_COLUMN_ASSET_DESCRIPTION, asset.getDescription());
        } else {
            assetValues.put(SQLVariables._ASSETS_COLUMN_ASSET_DESCRIPTION, "");
        }

        locationValues.put(SQLVariables._LOCATIONS_COLUMN_ASSET_ID, asset.getId());
        locationValues.put(SQLVariables._LOCATIONS_COLUMN_LATITUDE, asset.getLatitude());
        locationValues.put(SQLVariables._LOCATIONS_COLUMN_LONGITUDE, asset.getLongitude());

        database.insert(SQLVariables._ASSETS_TABLE, null, assetValues);
        database.insert(SQLVariables._LOCATIONS_TABLE, null, locationValues);
        database.close();
    }

    /**
     * Updates User into SQLite DB
     * @param asset
     */
    public void updateAsset(Asset asset) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues assetValues = new ContentValues();
        ContentValues locationValues = new ContentValues();

        assetValues.put(SQLVariables._ASSETS_COLUMN_UPDATED_AT, asset.getUpdatedAt());
        assetValues.put(SQLVariables._ASSETS_COLUMN_DELETED, asset.getDeleted());
        assetValues.put(SQLVariables._ASSETS_COLUMN_NEEDSSYNC, asset.getNeedsSync());
        assetValues.put(SQLVariables._ASSETS_COLUMN_ISNEW, asset.getIsNew());
        if (asset.getName() != null)
            assetValues.put(SQLVariables._ASSETS_COLUMN_ASSET_NAME, asset.getName());
        if (asset.getDescription() != null)
            assetValues.put(SQLVariables._ASSETS_COLUMN_ASSET_DESCRIPTION, asset.getDescription());

        locationValues.put(SQLVariables._LOCATIONS_COLUMN_LATITUDE, asset.getLatitude());
        locationValues.put(SQLVariables._LOCATIONS_COLUMN_LONGITUDE, asset.getLongitude());

        database.update(SQLVariables._ASSETS_TABLE, assetValues, SQLVariables._ASSETS_COLUMN_ASSET_ID + "=" + asset.getId(), null);
        database.update(SQLVariables._LOCATIONS_TABLE, locationValues, SQLVariables._ASSETS_COLUMN_ASSET_ID + "=" + asset.getId(), null);
        database.close();
    }

    /**
     * Updates the asset flags when server returns them
     */
    protected void updateAssetFlags(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (queryValues.get(SQLVariables._ASSETS_COLUMN_DELETED) != null )
            values.put(SQLVariables._ASSETS_COLUMN_DELETED, queryValues.get(SQLVariables._ASSETS_COLUMN_DELETED));
        if (queryValues.get(SQLVariables._ASSETS_COLUMN_ISNEW) != null )
            values.put(SQLVariables._ASSETS_COLUMN_ISNEW, queryValues.get(SQLVariables._ASSETS_COLUMN_ISNEW));
        if (queryValues.get(SQLVariables._ASSETS_COLUMN_NEEDSSYNC) != null )
            values.put(SQLVariables._ASSETS_COLUMN_NEEDSSYNC, queryValues.get(SQLVariables._ASSETS_COLUMN_NEEDSSYNC));

        database.update(SQLVariables._ASSETS_TABLE, values, SQLVariables._ASSETS_COLUMN_ASSET_ID + "=" + queryValues.get(SQLVariables._ASSETS_COLUMN_ASSET_ID), null);
        database.close();
    }

    /**
     * Marks Asset as deleted
     * @param id
     *
     */
    public void deleteAsset(String id) {
        String updated_at = toString().valueOf(System.currentTimeMillis() / 1000L);
        String deleted = "1";
        String needsSync = "1";

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SQLVariables._ASSETS_COLUMN_DELETED, deleted);
        values.put(SQLVariables._ASSETS_COLUMN_NEEDSSYNC, needsSync);
        values.put(SQLVariables._ASSETS_COLUMN_UPDATED_AT, updated_at);
        database.update(SQLVariables._ASSETS_TABLE, values, SQLVariables._ASSETS_COLUMN_ASSET_ID + "=" + id, null);
        database.close();
        //System.out.println("Asset Deleted: " + assetId);
    }

    /**
     * Purge Asset from SQLlite
     * @param assetId
     *
     * TODO: delte the location of the asset
     */
    protected void purgeAsset(String assetId) {
        if(hasAsset(assetId)) {
            SQLiteDatabase database = this.getWritableDatabase();
            database.delete(SQLVariables._ASSETS_TABLE, SQLVariables._ASSETS_COLUMN_ASSET_ID + "=" + assetId, null);
            database.close();
        }
    }

    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllAssets() {
        ArrayList<HashMap<String, String>> assetsList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + SQLVariables._ASSETS_TABLE + " WHERE " + SQLVariables._ASSETS_COLUMN_DELETED + " =0";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                for(int i=0; i<cursor.getColumnCount();i++) {
                    map.put(cursor.getColumnName(i), cursor.getString(i));
                }
                assetsList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return assetsList;
    }

    public ArrayList<Asset> getListOfAssets()/* throws Exception */{
        // DB query for all assets (with locations)
        String selectQuery = "SELECT "+ SQLVariables._ASSETS_TABLE+".*, " +
                SQLVariables._LOCATIONS_TABLE+"."+ SQLVariables._LOCATIONS_COLUMN_LONGITUDE+", " +
                SQLVariables._LOCATIONS_TABLE+"."+ SQLVariables._LOCATIONS_COLUMN_LATITUDE +
                " FROM " + SQLVariables._ASSETS_TABLE +
                " LEFT JOIN " + SQLVariables._LOCATIONS_TABLE+ " ON "+ SQLVariables._ASSETS_TABLE+"."+ SQLVariables._ASSETS_COLUMN_ASSET_ID +" = " + SQLVariables._LOCATIONS_TABLE+"."+ SQLVariables._LOCATIONS_COLUMN_ASSET_ID+
                " WHERE " + SQLVariables._ASSETS_COLUMN_DELETED + " = '0'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        ArrayList<Asset> assetList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                for(int i=0; i<cursor.getColumnCount();i++) {
                    map.put(cursor.getColumnName(i), cursor.getString(i));
                }

                double lat = Double.valueOf(map.get(SQLVariables._LOCATIONS_COLUMN_LATITUDE));
                double lng = Double.valueOf(map.get(SQLVariables._LOCATIONS_COLUMN_LONGITUDE));
                Asset asset = new Asset(new LatLng(lat, lng));

                for (String key : map.keySet()) {
                    String value = map.get(key);
                    if (value == null) { value = ""; }
                    switch (key) {
                        case SQLVariables._ASSETS_COLUMN_ISNEW:
                            asset.setIsNew(value);
                            break;
                        case SQLVariables._ASSETS_COLUMN_UPDATED_AT:
                            asset.setUpdatedAt(value);
                            break;
                        case SQLVariables._ASSETS_COLUMN_NEEDSSYNC:
                            asset.setNeedsSync(value);
                            break;
                        case SQLVariables._ASSETS_COLUMN_ASSET_DESCRIPTION:
                            asset.setDescription(value);
                            break;
                        case SQLVariables._ASSETS_COLUMN_ASSET_ID:
                            asset.setId(value);
                            break;
                        case SQLVariables._ASSETS_COLUMN_ASSET_NAME:
                            asset.setName(value);
                            break;
                        case SQLVariables._ASSETS_COLUMN_DELETED:
                            asset.setDeleted(value);
                            break;
                        case SQLVariables._ASSETS_COLUMN_CREATED_AT:
                            asset.setCreatedAt(value);
                            break;
                        case SQLVariables._LOCATIONS_COLUMN_LATITUDE:
                        case SQLVariables._LOCATIONS_COLUMN_LONGITUDE:
                            break;
                        default:
                            Log.w("warning", "Unrecognized attribute " + key);
                            //throw new Exception("Unrecognized attribute " + key);
                    }
                }

                assetList.add(asset);

            } while (cursor.moveToNext());
        }
        database.close();

        return assetList;
    }

    /**
     * Compose JSON out of SQLite records
     * @return
     */
    protected String assetsToJSON(boolean needSyncOnly){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<>();
        String selectQuery = "";
        if (needSyncOnly) {
            selectQuery = "SELECT "+ SQLVariables._ASSETS_TABLE+".*, " +
                    SQLVariables._LOCATIONS_TABLE+"."+ SQLVariables._LOCATIONS_COLUMN_LONGITUDE+", " +
                    SQLVariables._LOCATIONS_TABLE+"."+ SQLVariables._LOCATIONS_COLUMN_LATITUDE +
                    " FROM " + SQLVariables._ASSETS_TABLE +
                    " LEFT JOIN " + SQLVariables._LOCATIONS_TABLE+ " ON "+ SQLVariables._ASSETS_TABLE+"."+ SQLVariables._ASSETS_COLUMN_ASSET_ID +" = " + SQLVariables._LOCATIONS_TABLE+"."+ SQLVariables._LOCATIONS_COLUMN_ASSET_ID+
                    " WHERE " + SQLVariables._ASSETS_COLUMN_NEEDSSYNC + " = '1'";
        } else {
            //selectQuery = "SELECT " + Variables._ASSETS_COLUMN_ASSET_ID + " FROM " + Variables._ASSETS_TABLE;
            selectQuery = "SELECT "+ SQLVariables._ASSETS_TABLE+".*, " +
                    SQLVariables._LOCATIONS_TABLE+"."+ SQLVariables._LOCATIONS_COLUMN_LONGITUDE+", " +
                    SQLVariables._LOCATIONS_TABLE+"."+ SQLVariables._LOCATIONS_COLUMN_LATITUDE +
                    " FROM " + SQLVariables._ASSETS_TABLE +
                    " LEFT JOIN " + SQLVariables._LOCATIONS_TABLE+ " ON "+ SQLVariables._ASSETS_TABLE+"."+ SQLVariables._ASSETS_COLUMN_ASSET_ID +" = " + SQLVariables._LOCATIONS_TABLE+"."+ SQLVariables._LOCATIONS_COLUMN_ASSET_ID+
                    " WHERE " + SQLVariables._ASSETS_COLUMN_DELETED + " = '0'";
        }
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                for(int i=0; i<cursor.getColumnCount();i++) {
                    map.put(cursor.getColumnName(i), cursor.getString(i));
                }
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
//        System.out.println("ALLASSETS");
//        System.out.println(gson.toJson(wordList));
        return gson.toJson(wordList);
    }

    public List<Asset> assetsList() {
        String data = assetsToJSON(false);
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        List<Asset> list = gson.fromJson(data, new TypeToken<ArrayList<Asset>>(){}.getType());

        if (list == null) {
            return new ArrayList<Asset>();
        }
        return list;
    }

    /**
     * Get Sync status of SQLite
     * @return
     */
    protected String getSyncStatus(){
        String msg = null;
        if(this.dbSyncCount() == 0){
            msg = "SQLite and Remote MySQL DBs are in Sync!";
        }else{
            msg = "DB Sync needed\n";
        }
        return msg;
    }

    /**
     * Get SQLite records that need syncing
     * @return
     */
    protected int dbSyncCount(){
        int count = 0;
        String selectQuery = "SELECT * FROM " + SQLVariables._ASSETS_TABLE + " WHERE " + SQLVariables._ASSETS_COLUMN_NEEDSSYNC + " = '1'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        count = cursor.getCount();
        database.close();
        return count;
    }

    /**
     * Update Sync status against each User ID
     * @param assetId
     * @param needsSync
     */
    protected void updateSyncStatus(String assetId, String needsSync){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLVariables._ASSETS_COLUMN_NEEDSSYNC, needsSync);
        database.update(SQLVariables._ASSETS_TABLE, values, SQLVariables._ASSETS_COLUMN_ASSET_ID + "=" + assetId, null);
        database.close();
        //System.out.println("Asset Sync Status Updated: " + assetId);
    }

    protected boolean isAssetDeleted(String id) {
        SQLiteDatabase db = getWritableDatabase();
        String selectString = "SELECT * FROM " + SQLVariables._ASSETS_TABLE + " WHERE " + SQLVariables._ASSETS_COLUMN_ASSET_ID + " =? AND "+ SQLVariables._ASSETS_COLUMN_DELETED + "=1";

        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
        Cursor cursor = db.rawQuery(selectString, new String[]{id});

        boolean isAssetDeleted = false;
        if(cursor.moveToFirst()){
            isAssetDeleted = true;
        }

        cursor.close();          // Dont forget to close your cursor
        db.close();              //AND your Database!
        return isAssetDeleted;
    }

    protected boolean hasAsset(String id) {
        SQLiteDatabase db = getWritableDatabase();
        String selectString = "SELECT * FROM " + SQLVariables._ASSETS_TABLE + " WHERE " + SQLVariables._ASSETS_COLUMN_ASSET_ID + " =?";

        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
        Cursor cursor = db.rawQuery(selectString, new String[]{id});

        boolean hasAsset = false;
        if(cursor.moveToFirst()){
            hasAsset = true;
        }

        cursor.close();          // Dont forget to close your cursor
        db.close();              //AND your Database!
        return hasAsset;
    }

    protected int getAssetUpdatedTimestamp(String id) {
        SQLiteDatabase db = getWritableDatabase();
        String selectString = "SELECT " + SQLVariables._ASSETS_COLUMN_UPDATED_AT + " FROM " + SQLVariables._ASSETS_TABLE + " WHERE " + SQLVariables._ASSETS_COLUMN_ASSET_ID + " =?";

        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
        Cursor cursor = db.rawQuery(selectString, new String[]{id});

        int lastTimeStamp = 0;
        if(cursor.moveToFirst()){

            lastTimeStamp = cursor.getInt(0);
        }

        cursor.close();          // Dont forget to close your cursor
        db.close();              //AND your Database!
        return lastTimeStamp;
    }


}