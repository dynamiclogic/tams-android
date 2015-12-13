package com.dynamiclogic.tams.activity;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
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
    private boolean playIsPressed = true;
    private boolean recordIsPressed = true;
    private static String mAudioFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_asset, container, false);


        //Getting Database singleton reference.
        db = Database.getInstance();

        //Getting location from the intent coming from MainActivity
        mLocation = getActivity().getIntent().getParcelableExtra(EXTRA_ASSET_LOCATION);

        //Intent Service to get Address related to current location
        startIntentService();

        //Make a new asset from location latitude and longitude
        createAsset();

        //Hook up layout components
        setUpLayoutComponents(v);

        //Set text for layouts
        setTextMethods();

        //Set on click listeners
        setUpOnClickListeners();

        //Set up toolbar
        setUpToolbar();

        //Let Android know we have menu items we want for the toolbar
        setHasOptionsMenu(true);

        return v;
    }

    private void setTextMethods() {
        mLatitude.setText(String.valueOf(mAsset.getLatLng().latitude));
        mLongitude.setText(String.valueOf(mAsset.getLatLng().latitude));
        mNameEditField.setText(mAsset.getName());
        mDescriptionEditField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAsset.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });
    }

    private void setUpOnClickListeners() {
        mPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(recordIsPressed);
                if (recordIsPressed) {
                    mRecordButton.setImageResource(R.drawable.ic_mic_black_24dp);
                } else {
                    mRecordButton.setImageResource(R.drawable.ic_mic_off_black_24dp);
                }
                recordIsPressed = !recordIsPressed;
                //Intent intent = new Intent(getActivity(), AudioRecordTest.class);
                //startActivity(intent);
            }
        });
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlay(playIsPressed);
                if (playIsPressed) {
                    mPlayButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                } else {
                    mPlayButton.setImageResource(R.drawable.ic_stop_black_24dp);
                }
                playIsPressed = !playIsPressed;
            }
        });
    }

    private void setUpLayoutComponents(View v) {
        toolbar = (Toolbar) v.findViewById(R.id.tool_bar);
        mLatitude = (TextView) v.findViewById(R.id.latitudeTextView);
        mLongitude = (TextView) v.findViewById(R.id.longitudeTextView);
        mNameEditField = (EditText) v.findViewById(R.id.nameEditText);
        mAssetTypeSpinner = (Spinner) v.findViewById(R.id.assetTypesSpinner);
        mDescriptionEditField = (EditText) v.findViewById(R.id.descriptionEditText);
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        mPictureButton = (ImageButton) v.findViewById(R.id.pictureButton);
        mRecordButton = (ImageButton) v.findViewById(R.id.recordButton);
        mPlayButton = (ImageButton) v.findViewById(R.id.playButton);
    }

    private void setUpToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Add Asset");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Set up layout for toolbar
        inflater.inflate(R.menu.menu_addasset, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            db.addNewAsset(mAsset);
            getActivity().finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createAsset() {
        if (mLocation != null) {
            LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            mAsset = new Asset(latLng);
            String time = System.currentTimeMillis() / 1000L + "";
            mAsset.setCreatedAt(time);
            mAsset.setUpdatedAt(time);
        }
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

    private void createAudioFile() throws IOException {
        // Create an audio file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mAudioFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mAudioFileName += timeStamp;
        mAudioFileName += "/audiorecordtest.3gp";
    }

    String mCurrentPhotoPath;

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


    @Override
    public void onStop() {
        super.onStop();
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


            //Crop image and place in Image View
            Picasso.with(getActivity().getApplicationContext())
                    .load("file://" + mCurrentPhotoPath)
                    .placeholder(R.drawable.progress_animation)
                    .resize(mImageView.getWidth(), mImageView.getHeight())
                    .centerCrop()
                    .into(mImageView);

            //Shrink bitmap in background thread before converting to base74
            shrinkBitmap(mCurrentPhotoPath);

        }
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

    //Starts background thread to shrink bitmap before base64 conversion
    public void shrinkBitmap(String pictureLocation) {

        //Not entirely sure about the size values passed here. I tested these and they work
        // fine for now. Might need to adjust later
        BitmapShrinkTask task = new BitmapShrinkTask(300, 300);
        task.execute(pictureLocation);
    }

    //Decode bitmap from file to the reduced size
    public static Bitmap decodeSampledBitmapFromResource(String path,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
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


    //Async Task for bitmap shrinking
    class BitmapShrinkTask extends AsyncTask<String, Intent, Bitmap> {
        String location;
        int width, height;

        public BitmapShrinkTask(int width, int height) {
            this.width = width;
            this.height = height;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            location = params[0];
            return decodeSampledBitmapFromResource(location, width, height);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(TAG, "bitmap shrinking thread finished");
            mAsset.bitmapToBase64(bitmap);
        }
    }

}
