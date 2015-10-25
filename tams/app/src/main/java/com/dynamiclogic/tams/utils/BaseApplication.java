package com.dynamiclogic.tams.utils;

import android.app.Application;

import com.dynamiclogic.tams.database.SharedPrefsDatabase;

public class BaseApplication extends Application {

    private static final String TAG = BaseApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the Database
        SharedPrefsDatabase.getInstance().initialize(getApplicationContext());
    }
}
