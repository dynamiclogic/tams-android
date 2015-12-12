package com.dynamiclogic.tams.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.dynamiclogic.tams.R;

/**
 * Created by Javier G on 12/12/2015.
 */
public class Settings extends AppCompatActivity{
    private Toolbar mToolbar;
    private EditText mServerEditText, mUserEditText, mPasswordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        mToolbar = (Toolbar) findViewById(R.id.settings_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitle("Settings");

        mServerEditText = (EditText) findViewById(R.id.serverEditText);
        mUserEditText = (EditText) findViewById(R.id.userEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);

        mServerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO: 12/12/2015 Ivan handle text
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mUserEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO: 12/12/2015 Ivan handle text
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO: 12/12/2015 Ivan handle text
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
