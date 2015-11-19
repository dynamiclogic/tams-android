package com.dynamiclogic.tams.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dynamiclogic.tams.R;

/**
 * Created by Andreas on 10/4/2015.
 */
public class ManageAsset extends AppCompatActivity {
    private static final String TAG = ManageAsset.class.getSimpleName();

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.d(TAG, "onAttachFragment() called with: " + "fragment = [" + fragment + "]");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.manage_asset);
    }

}