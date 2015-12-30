package com.dynamiclogic.tams.activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

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
    private ImageButton mPictureButton, mRecordButton, mPlayButton;
    private Asset mAsset;
    private Location mLocation;
    private String mCurrentPhotoPath;
    public static final String EXTRA_ASSET_LOCATION =
            "com.dynamiclogic.tams.activity.asset_location";
    private Database db;
    private List<Asset> list;
    private Toolbar toolbar;


    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manage_asset, container, false);
        
        db = Database.getInstance();

        toolbar = (Toolbar)v.findViewById(R.id.manage_asset_tool_bar);

        //Set up toolbar
        setUpToolbar();

        //Let Android know we have menu items we want for the toolbar
        setHasOptionsMenu(true);

        Intent intent = getActivity().getIntent();
        String value = intent.getStringExtra("asset_pass");
        mAsset = db.getAssetFromID(value);

        if(mAsset != null) {
            mLatiture = (TextView) v.findViewById(R.id.latitudeTextView);
            mLatiture.setText(String.valueOf(mAsset.getLatLng().latitude));

            mLongitude = (TextView) v.findViewById(R.id.longitudeTextView);
            mLongitude.setText(String.valueOf(mAsset.getLatLng().longitude));
        }

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

        /*Button pictureButton = (Button)v.findViewById(R.id.pictureButton);
        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });*/

        mPictureButton = (ImageButton) v.findViewById(R.id.pictureButton);
        mRecordButton = (ImageButton) v.findViewById(R.id.recordButton);
        mPlayButton = (ImageButton) v.findViewById(R.id.playButton);

        Bitmap bitmap = mAsset.getPicture();
        // TODO: 12/29/2015 Change the hardcoded values for width and height so we get them from the imageView
        Bitmap croppedBitmap = scaleCenterCrop(bitmap, 1050, 1404);
        mImageView.setImageBitmap(croppedBitmap);

        Log.d(TAG, "onCreateView() in ManageAssetFragment");

        return v;
    }

    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    private void setUpToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Manage Asset");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Set up layout for toolbar
        inflater.inflate(R.menu.menu_manage_asset, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_delete_black_24dp)
                    .setTitle(R.string.deleteAsset)
                    .setMessage(R.string.deleteAssetPrompt)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "Deleted Asset", Toast.LENGTH_SHORT).show();
                            String uuid = mAsset.getId();
                            db.removeAsset(uuid);
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
            return true;
        }
        if (id == R.id.action_save) {
            Toast.makeText(getActivity(), "Saved Asset", Toast.LENGTH_SHORT).show();
            db.updateAsset(mAsset);
            getActivity().finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();



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