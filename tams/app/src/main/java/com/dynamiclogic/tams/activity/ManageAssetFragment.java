package com.dynamiclogic.tams.activity;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dynamiclogic.tams.R;
import com.dynamiclogic.tams.database.Database;
import com.dynamiclogic.tams.model.Asset;

import java.util.List;

/**
 *Andreas
 */
public class ManageAssetFragment extends Fragment {
    private static final String TAG = ManageAssetFragment.class.getSimpleName();
    private static final int CAMERA_REQUEST = 1888;
    private TextView mLatiture, mLongitude;
    private ImageView mImageView;
    private EditText mNameEditField, mDescriptionEditField;
    private Asset mAsset;
    private Location mLocation;
    private String mCurrentPhotoPath;
    public static final String EXTRA_ASSET_LOCATION =
            "com.dynamiclogic.tams.activity.asset_location";
    private Database db;
    private List<Asset> list;


    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manage_asset, container, false);
        
        db = Database.getInstance();

    //    list = db.getListOfAssets();
        Intent intent = getActivity().getIntent();
        String value = intent.getStringExtra("asset_pass");
        mAsset = db.getAssetFromID(value);
        //Intent intent = getIntent();
        //mAsset = (Asset)getIntent().getExtras().getSerializable("asset_pass");


        // Bundle bundle = getIntent.getExtra();

        /*Trying to save asset on state change
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ASSET)) {
                mAsset = (Asset) savedInstanceState.getSerializable(ASSET);
            }
        } else {
            *//*mLocation = (Location) getActivity().getIntent().getParcelableExtra(EXTRA_ASSET_LOCATION);
            if (mAsset == null){
                LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                mAsset = new Asset(latLng);
            }*//*
        }*/

        //mLocation = (Location) getActivity().getIntent().getParcelableExtra(EXTRA_ASSET_LOCATION);

        // LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        // mAsset = new Asset(latLng);


        if(mAsset != null) {
            mLatiture = (TextView) v.findViewById(R.id.latitudeTextView);
            mLatiture.setText(String.valueOf(mAsset.getLatLng().latitude));

            mLongitude = (TextView) v.findViewById(R.id.longitudeTextView);
            mLongitude.setText(String.valueOf(mAsset.getLatLng().longitude));
        }
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAsset.setName(s.toString());
                Log.d(TAG, "Name: " + mAsset.getName());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mDescriptionEditField = (EditText)v.findViewById(R.id.descriptionEditText);
        mDescriptionEditField.setText(mAsset.getDescription());
        mDescriptionEditField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAsset.setDescription(s.toString());
                Log.d(TAG, "Description: " + mAsset.getDescription());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCurrentPhotoPath = mAsset.getPictureLocation();


        mImageView = (ImageView)v.findViewById(R.id.imageView);

        //Need onGlobalLayoutListener to get size of imageView after it has been inflated
        // so we can set the picture in it
        ViewTreeObserver ivvto = mImageView.getViewTreeObserver();
        ivvto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setPic();
            }
        });


        Button pictureButton = (Button)v.findViewById(R.id.pictureButton);
        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });


        Log.d(TAG, "onCreateView() in ManageAssetFragment");

        return v;
    }


    private void setPic() {
        Log.d(TAG, "setPic() from ManageAssetFragment");

        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onStop() {
        super.onStop();
        db.updateAsset(mAsset);


    }


    //Respond to calls to StartActivityForResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {



        //Responding to the Camera activity call for result
//        if (requestCode == CAMERA_REQUEST) {
//            //mAsset.setPictureLocation((Bitmap) data.getExtras().get("data"));
//            //mImageView.setImageBitmap(mAsset.getPictureLocation());
//        }
    }

}