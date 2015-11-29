package com.dynamiclogic.tams.database;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.view.Display;
import android.widget.Toast;

import com.dynamiclogic.tams.model.Asset;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;


import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Observable;

import com.dynamiclogic.tams.utils.SQLVariables;

/**
 * Created by Ivan Mihov on 10/1/15.
 */
public class DBSync extends Observable {
    //ProgressDialog prgDialog; //crashes
    Context mContext;
    private DBController mDBController;
    private Asset mAsset;

    public DBSync(Context applicationContext, DBController dbController) {
        this.mContext = applicationContext;
        mDBController = dbController;

        //prgDialog = new ProgressDialog(mContext); //crashes
    }

    protected void sync() {
        pull();
        //deleteAssets();
        //updateAssets();
        //createAssets();
    }

    protected void createAssets() {
        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        //prgDialog.show(); //crashes

        String updatedAssets = mDBController.newAssetsToJSON();

        if(updatedAssets != "") {
            params.put(SQLVariables._ASSETS_JSON_POST, updatedAssets);
            params.put(SQLVariables._API_AUTH_POST, SQLVariables._API_PASSWORD);

            // Make Http call to push.php
            client.post(SQLVariables._IPADDRESS + SQLVariables._CREATE_URL, params, new TextHttpResponseHandler() {

                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                    //prgDialog.hide(); //crashes
                    if (statusCode == 404) {
                        Toast.makeText(mContext.getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    } else if (statusCode == 500) {
                        Toast.makeText(mContext.getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext.getApplicationContext(), "Unexpected Error occurred! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    String isNew, needsSync, purgeAsset, assetId;
                    isNew = needsSync = purgeAsset = assetId = "";
                    HashMap<String, String> queryValues = new HashMap<>();

                    try {
                        JSONArray arr = new JSONArray(response);
                        //System.out.println(arr.length());
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            //System.out.println("RESPONSE:");
                            //System.out.println(obj);

                            //get the returned values
                            isNew = obj.get(SQLVariables._ASSETS_COLUMN_ISNEW).toString().trim();
                            assetId = obj.get(SQLVariables._ASSETS_COLUMN_ASSET_ID).toString().trim();
                            needsSync = obj.get(SQLVariables._ASSETS_COLUMN_NEEDSSYNC).toString().trim();

                            //if asset was successfully deleted on server, purge it from sqllite
                            purgeAsset = obj.get("purgeAsset").toString().trim();
                            if (purgeAsset.equals("1")) {
                                mDBController.purgeAsset(assetId);
                            } else {
                                queryValues.put(SQLVariables._ASSETS_COLUMN_ASSET_ID, assetId);
                                queryValues.put(SQLVariables._ASSETS_COLUMN_ISNEW, isNew);
                                queryValues.put(SQLVariables._ASSETS_COLUMN_NEEDSSYNC, needsSync);
                                mDBController.updateAssetFlags(queryValues);
                            }
                            Toast.makeText(mContext.getApplicationContext(), "Asset create completed!", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(mContext.getApplicationContext(), "Error occurred [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    protected void updateAssets() {
        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        //prgDialog.show(); //crashes

        String updatedAssets = mDBController.updatedAssetsToJSON();

        if(updatedAssets != "") {
            params.put(SQLVariables._ASSETS_JSON_POST, updatedAssets);
            params.put(SQLVariables._API_AUTH_POST, SQLVariables._API_PASSWORD);

            // Make Http call to push.php
            client.post(SQLVariables._IPADDRESS + SQLVariables._UPDATE_URL, params, new TextHttpResponseHandler() {

                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                    //prgDialog.hide(); //crashes
                    if (statusCode == 404) {
                        Toast.makeText(mContext.getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    } else if (statusCode == 500) {
                        Toast.makeText(mContext.getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext.getApplicationContext(), "Unexpected Error occurred! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    String isNew, needsSync, purgeAsset, assetId;
                    isNew = needsSync = purgeAsset = assetId = "";
                    HashMap<String, String> queryValues = new HashMap<>();

                    try {
                        JSONArray arr = new JSONArray(response);
                        //System.out.println(arr.length());
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            //System.out.println("RESPONSE:");
                            //System.out.println(obj);

                            //get the returned values
                            assetId = obj.get(SQLVariables._ASSETS_COLUMN_ASSET_ID).toString().trim();
                            needsSync = obj.get(SQLVariables._ASSETS_COLUMN_NEEDSSYNC).toString().trim();

                            //if asset was successfully deleted on server, purge it from sqllite
                            purgeAsset = obj.get("purgeAsset").toString().trim();
                            if (purgeAsset.equals("1")) {
                                mDBController.purgeAsset(assetId);
                            } else {
                                queryValues.put(SQLVariables._ASSETS_COLUMN_ASSET_ID, assetId);
                                queryValues.put(SQLVariables._ASSETS_COLUMN_ISNEW, isNew);
                                queryValues.put(SQLVariables._ASSETS_COLUMN_NEEDSSYNC, needsSync);
                                mDBController.updateAssetFlags(queryValues);
                            }
                            createAssets();
                            Toast.makeText(mContext.getApplicationContext(), "Asset update completed!", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(mContext.getApplicationContext(), "Error occurred [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });
        }
        else {
            createAssets();
        }
    }

    protected void deleteAssets() {
        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        //prgDialog.show(); //crashes

        final String updatedAssets = mDBController.deletedAssetsToJSON();

        if(updatedAssets != "") {
            params.put(SQLVariables._ASSETS_JSON_POST, updatedAssets);
            params.put(SQLVariables._API_AUTH_POST, SQLVariables._API_PASSWORD);

            // Make Http call to push.php
            client.put(SQLVariables._IPADDRESS + SQLVariables._DELETE_URL, params, new TextHttpResponseHandler() {

                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                    //prgDialog.hide(); //crashes
                    if (statusCode == 404) {
                        Toast.makeText(mContext.getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    } else if (statusCode == 500) {
                        Toast.makeText(mContext.getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext.getApplicationContext(), "Unexpected Error occurred! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    String isNew, needsSync, purgeAsset, assetId;
                    isNew = needsSync = purgeAsset = assetId = "";
                    HashMap<String, String> queryValues = new HashMap<>();

                    try {
                        JSONArray arr = new JSONArray(response);
                        //System.out.println(arr.length());
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            //System.out.println("RESPONSE:");
                            //System.out.println(obj);

                            //get the returned values
                            //if (obj.get(SQLVariables._ASSETS_COLUMN_ISNEW).toString().trim() != "")
                            //  isNew = obj.get(SQLVariables._ASSETS_COLUMN_ISNEW).toString().trim();
                            assetId = obj.get(SQLVariables._ASSETS_COLUMN_ASSET_ID).toString().trim();
                            needsSync = obj.get(SQLVariables._ASSETS_COLUMN_NEEDSSYNC).toString().trim();

                            //if asset was successfully deleted on server, purge it from sqllite
                            purgeAsset = obj.get("purgeAsset").toString().trim();
                            if (purgeAsset.equals("1")) {
                                mDBController.purgeAsset(assetId);
                            } else {
                                queryValues.put(SQLVariables._ASSETS_COLUMN_ASSET_ID, assetId);
                                queryValues.put(SQLVariables._ASSETS_COLUMN_ISNEW, isNew);
                                queryValues.put(SQLVariables._ASSETS_COLUMN_NEEDSSYNC, needsSync);
                                mDBController.updateAssetFlags(queryValues);
                            }
                            updateAssets();
                            Toast.makeText(mContext.getApplicationContext(), "Asset delete completed!", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(mContext.getApplicationContext(), "Error occurred [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });
        }
        else {
            updateAssets();
        }
    }

    protected void pull() {
        // Create AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        //prgDialog.show(); //crashes

        //params.put("assetsJSON", allAssets);
        params.put(SQLVariables._API_AUTH_POST, SQLVariables._API_PASSWORD);

        // Make Http call to pull.php
        client.get(SQLVariables._IPADDRESS + SQLVariables._PULL_URL, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                //prgDialog.hide(); //crashes
                if (statusCode == 404) {
                    Toast.makeText(mContext.getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(mContext.getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext.getApplicationContext(), "Unexpected Error occurred! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                // Create GSON object
                Gson gson = new GsonBuilder().create();

                try {
                    // Extract JSON array from the response
                    JSONArray arr = new JSONArray(response);
                    // If no of array elements is not zero
                    if (arr.length() != 0) {
                        // Loop through each array element, get JSON object which has assetId and username
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            //System.out.println("RESPONSE:");
                            //System.out.println(obj);
                            //System.out.println("LOCAL:");
                            //System.out.println(mDBController.getAllAssets());

                            String purgeAllAssets;
                            purgeAllAssets = "0";

                            purgeAllAssets = obj.get("purgeAllAssets").toString().trim();
                            if (purgeAllAssets.equals("1")) {
                                mDBController.purgeAllAssets();
                                break;
                            }

                            LatLng latLng = new LatLng(Double.parseDouble(obj.get(SQLVariables._LOCATIONS_COLUMN_LATITUDE).toString()),
                                    Double.parseDouble(obj.get(SQLVariables._LOCATIONS_COLUMN_LONGITUDE).toString()));
                            mAsset = new Asset(latLng);

                            // get the json objects and set the asset values
                            mAsset.setId(obj.get(SQLVariables._ASSETS_COLUMN_ASSET_ID).toString());
                            mAsset.setName(obj.get(SQLVariables._ASSETS_COLUMN_ASSET_NAME).toString());
                            mAsset.setCreatedAt(obj.get(SQLVariables._ASSETS_COLUMN_CREATED_AT).toString());
                            mAsset.setUpdatedAt(obj.get(SQLVariables._ASSETS_COLUMN_UPDATED_AT).toString());
                            mAsset.setNeedsSync(obj.get(SQLVariables._ASSETS_COLUMN_NEEDSSYNC).toString());
                            mAsset.setDeleted(obj.get(SQLVariables._ASSETS_COLUMN_DELETED).toString());
                            mAsset.setIsNew(obj.get(SQLVariables._ASSETS_COLUMN_ISNEW).toString());
                            mAsset.setDescription(obj.get(SQLVariables._ASSETS_COLUMN_ASSET_DESCRIPTION).toString());

                            if (obj.get(SQLVariables._MEDIA_COLUMN_IMAGES).toString() != "")
                                mAsset.setPictureBase64(obj.get(SQLVariables._MEDIA_COLUMN_IMAGES).toString());

                            /**
                             * Server returns assets
                             * if asset is not present locally create it
                             * If asset exist locally and the server timestamp is newer, update it
                             * Or if the asset has been market as deleted locally and the server sends it again, update it **commented out
                             */
                            if (!mDBController.hasAsset(obj.get(SQLVariables._ASSETS_COLUMN_ASSET_ID).toString())) {
                                // Insert asset into SQLite DB
                                mDBController.insertAsset(mAsset);
                            } else if ((mDBController.hasAsset(obj.get(SQLVariables._ASSETS_COLUMN_ASSET_ID).toString()) &&
                                    mDBController.getAssetUpdatedTimestamp(obj.get(SQLVariables._ASSETS_COLUMN_ASSET_ID).toString()) < Integer.parseInt(obj.get(SQLVariables._ASSETS_COLUMN_UPDATED_AT).toString()))
                                    /*|| mDBController.isAssetDeleted(obj.get("assetId").toString())*/) {

                                // update local asset
                                mDBController.updateAsset(mAsset);
                            }
                        }
                        //notify the listeners
                        setChanged();
                        notifyObservers();
                    }
                    deleteAssets();
                    Toast.makeText(mContext.getApplicationContext(), "Asset get completed!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(mContext.getApplicationContext(), "Error occurred [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

}
