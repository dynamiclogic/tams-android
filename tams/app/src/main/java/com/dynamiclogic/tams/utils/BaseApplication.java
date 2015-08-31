package com.dynamiclogic.tams.utils;

import android.app.Application;

import com.dynamiclogic.tams.database.Database;

public class BaseApplication extends Application {

    private static final String TAG = BaseApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the Database
        Database.getInstance().initialize(getApplicationContext());
    }
}
