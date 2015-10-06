package com.dynamiclogic.tams.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.dynamiclogic.tams.database.Database;
import com.dynamiclogic.tams.model.Asset;
import com.dynamiclogic.tams.R;

import java.util.List;
import java.util.UUID;

/**
 * Created by Andreas on 10/4/2015.
 */
public class ManageAsset extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.manage_asset);
    }


}
