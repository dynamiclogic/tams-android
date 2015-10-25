package com.dynamiclogic.tams.activity;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dynamiclogic.tams.R;
import com.dynamiclogic.tams.database.Database;
import com.dynamiclogic.tams.database.SharedPrefsDatabase;
import com.dynamiclogic.tams.model.Asset;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Javier G on 8/17/2015.
 */
public class AddAssetFragment extends Fragment {

    private static final String TAG = AddAssetFragment.class.getSimpleName();
    private static final int CAMERA_REQUEST = 1888;
    private TextView mLatitude, mLongitude;
    private ImageView mImageView;
    private EditText mNameEditField, mDescriptionEditField;
    private Spinner mAssetTypeSpinner;
    private Asset mAsset;
    private Location mLocation;
    public static final String EXTRA_ASSET_LOCATION =
            "com.dynamiclogic.tams.activity.asset_location";
    private Database db;
    private String mAddressOutput;
    private AddressResultReceiver mResultReceiver;

    /*Trying to save asset on state change
    public static final String ASSET =
            "com.dynamiclogic.tams.activity.asset";*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_asset, container, false);

        db = SharedPrefsDatabase.getInstance();
        mLocation = (Location) getActivity().getIntent().getParcelableExtra(EXTRA_ASSET_LOCATION);

        startIntentService();

        if(mLocation != null) {
            LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            mAsset = new Asset(latLng);
        }

        mLatitude = (TextView)v.findViewById(R.id.latitudeTextView);
        mLatitude.setText(String.valueOf(mAsset.getLatLng().latitude));

        mLongitude = (TextView)v.findViewById(R.id.longitudeTextView);
        mLongitude.setText(String.valueOf(mAsset.getLatLng().latitude));

        Button mRecordButton = (Button)v.findViewById(R.id.recordButton);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AudioRecordTest.class);
                startActivity(intent);
            }
        });

        mNameEditField = (EditText)v.findViewById(R.id.nameEditText);
        mNameEditField.setText(mAsset.getName());
        mNameEditField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAsset.setName(s.toString());
                Log.d(TAG, "Name: " + mAsset.getName());
            }

            @Override
            public void afterTextChanged(Editable s) { }

        });

        mAssetTypeSpinner = (Spinner)v.findViewById(R.id.assetTypesSpinner);
        mDescriptionEditField = (EditText)v.findViewById(R.id.descriptionEditText);
        mDescriptionEditField.setHintTextColor(getResources().getColor(R.color.material_blue_grey_800));
        mDescriptionEditField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAsset.setDescription(s.toString());
                Log.d(TAG, "Description: " + mAsset.getDescription());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        this.mImageView = (ImageView)v.findViewById(R.id.imageView);

        Button pictureButton = (Button)v.findViewById(R.id.pictureButton);
        pictureButton.setEnabled(false);
        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();

        //Add Asset to the database
        db.addNewAsset(mAsset);
    }

    //Respond to calls to StartActivityForResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Responding to the Camera activity call for result
        if (requestCode == CAMERA_REQUEST) {
            mAsset.setPicture((Bitmap) data.getExtras().get("data"));
            mImageView.setImageBitmap(mAsset.getPicture());
        }
    }

    protected void startIntentService() {
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        mResultReceiver = new AddressResultReceiver(new Handler());
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLocation);
        getActivity().startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //showToast(getString(R.string.address_found));
                Log.d(TAG, "address: " + mAddressOutput.toString());
                updateUI();
            }

        }
    }

    private void updateUI() {

        mAsset.setName(mAddressOutput.toString());
        mNameEditField.setText(mAsset.getName());

    }

}
