package com.dynamiclogic.tams.activity;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dynamiclogic.tams.R;
import com.dynamiclogic.tams.database.Database;
import com.dynamiclogic.tams.model.Asset;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Javier G on 8/17/2015.
 */
public class AddAssetFragment extends Fragment {

    private static final String TAG = AddAssetFragment.class.getSimpleName();
    private TextView mLatitude, mLongitude;
    private ImageView mImageView;
    private EditText mNameEditField, mDescriptionEditField;
    private Spinner mAssetTypeSpinner;
    private ImageButton mPictureButton, mRecordButton, mPlayButton;
    private Asset mAsset;
    private Location mLocation;
    public static final String EXTRA_ASSET_LOCATION =
            "com.dynamiclogic.tams.activity.asset_location";
    private Database db;
    private String mAddressOutput;
    private AddressResultReceiver mResultReceiver;
<<<<<<< HEAD
    private int externalStorageWrittingLocationPermissionCheck;
    private int cameraPermissionCheck;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE_WRITTING = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    private boolean playIsPressed = true;
    private boolean recordIsPressed = true;
    private static String mAudioFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
=======

    /*Trying to save asset on state change
    public static final String ASSET =
            "com.dynamiclogic.tams.activity.asset";*/
>>>>>>> ddbe67dda4148c445caab476069c8b29ceef0be8

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_asset, container, false);

        //Getting Database singleton reference.
        db = Database.getInstance();

        mLocation = (Location) getActivity().getIntent().getParcelableExtra(EXTRA_ASSET_LOCATION);

        //Start worker thread to get address from location
        startIntentService();

        //Make a new asset from location latitude and longitude
        if(mLocation != null) {
            LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            mAsset = new Asset(latLng);
        }

        mLatitude = (TextView)v.findViewById(R.id.latitudeTextView);
        mLatitude.setText(String.valueOf(mAsset.getLatLng().latitude));

        mLongitude = (TextView)v.findViewById(R.id.longitudeTextView);
        mLongitude.setText(String.valueOf(mAsset.getLatLng().latitude));

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
        mDescriptionEditField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAsset.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        mImageView = (ImageView)v.findViewById(R.id.imageView);

        /*mPictureButton = (Button)v.findViewById(R.id.pictureButton);
        mPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });*/

        mPictureButton = (ImageButton)v.findViewById(R.id.pictureButton);
        mPictureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        mRecordButton = (ImageButton)v.findViewById(R.id.recordButton);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(recordIsPressed);
                if (recordIsPressed){
                    mRecordButton.setImageResource(R.drawable.ic_mic_black_24dp);
                } else {
                    mRecordButton.setImageResource(R.drawable.ic_mic_off_black_24dp);
                }
                recordIsPressed = !recordIsPressed;
                //Intent intent = new Intent(getActivity(), AudioRecordTest.class);
                //startActivity(intent);
            }
        });



        mPlayButton = (ImageButton)v.findViewById(R.id.playButton);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlay(playIsPressed);
                if (playIsPressed){
                    mPlayButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                } else {
                    mPlayButton.setImageResource(R.drawable.ic_stop_black_24dp);
                }
                playIsPressed = !playIsPressed;
            }
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        try {
            createAudioFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.setOutputFile(mAudioFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mAudioFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }



    /*Not doing 6.0 permissions

    private void cameraPermissionChecks() {

        // TODO: 11/6/2015 Have 2 ways of checking permission's before requesting, not sure which is best
        *//*if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE_WRITTING);

        }*//*

        if ( externalStorageWrittingLocationPermissionCheck == PackageManager.PERMISSION_DENIED){
            //Request access to write to external storage
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE_WRITTING);
        }

        // TODO: 11/6/2015 Need to uncomment camera permission once I have them fixed
        if ( cameraPermissionCheck == PackageManager.PERMISSION_DENIED){
            //Request access to write to external storage
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }


    }*/

    String mCurrentPhotoPath;

    private void createAudioFile() throws IOException {
        // Create an audio file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mAudioFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mAudioFileName += timeStamp;
        mAudioFileName += "/audiorecordtest.3gp";
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        mAsset.setPictureLocation(mCurrentPhotoPath);
        return image;
    }

    /* Not doing 6.0 permissions

    // TODO: 11/6/2015 Never getting the call back after permission's are requested, need to fix
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult() called with: " + "requestCode = [" + requestCode + "], permissions = [" + permissions + "], grantResults = [" + grantResults + "]");

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE_WRITTING: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    Log.d(TAG, "Write External Storage permission granted");
                    externalStorageWrittingLocationPermissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (cameraPermissionCheck == PackageManager.PERMISSION_GRANTED){
                        dispatchTakePictureIntent();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "Camera permission granted");
                    cameraPermissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.CAMERA);

                    if (externalStorageWrittingLocationPermissionCheck == PackageManager.PERMISSION_GRANTED){
                        dispatchTakePictureIntent();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;

                // other 'case' lines to check for other
                // permissions this app might request
            }
        }
    }*/

    @Override
    public void onStop() {
        super.onStop();
        //Add Asset to the database
        db.addNewAsset(mAsset);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    //Respond to calls to StartActivityForResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Responding to the Camera activity call for result
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            //setPic();
            loadBitmap(999, mImageView);
        }
    }

    public void loadBitmap(int resId, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(resId);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        mImageView.setImageBitmap(
                decodeSampledBitmapFromFile(mCurrentPhotoPath, targetW, targetH));


        /*// Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        calculateInSampleSize(bmOptions, targetW, targetH);

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);*/
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String picturePath,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(picturePath, options);
    }

    //Start worker thread to get address from lat, long
    protected void startIntentService() {
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        mResultReceiver = new AddressResultReceiver(new Handler());
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLocation);
        getActivity().startService(intent);
    }

    //Custom Receiver to get back results from worker thread
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
                updateUI();
            }

        }
    }

    private void updateUI() {
        mAsset.setName(mAddressOutput.toString());
        mNameEditField.setText(mAsset.getName());

    }

    //Background worker thread to process images from camera
    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;
        int targetW;
        int targetH;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
            targetW = imageView.getWidth();
            targetH = imageView.getHeight();

        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return decodeSampledBitmapFromFile(mCurrentPhotoPath, targetW, targetH);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

}
