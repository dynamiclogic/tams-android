package com.dynamiclogic.tams.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.dynamiclogic.tams.R;

/**
 * Created by Javier G on 11/18/2015.
 */
public class AudioRecording extends AppCompatActivity {

    private Button mPlayStopButton, mRecordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_recording);

        mPlayStopButton = (Button) findViewById(R.id.playPauseButton);
        mPlayStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayStopButton.getText() == "Play"){
                    mPlayStopButton.setText("Stop");
                } else {
                    mPlayStopButton.setText("Play");
                }
            }
        });

        mRecordButton = (Button) findViewById(R.id.recordButton);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
