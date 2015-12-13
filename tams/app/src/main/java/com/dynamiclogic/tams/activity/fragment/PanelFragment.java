package com.dynamiclogic.tams.activity.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dynamiclogic.tams.R;
import com.dynamiclogic.tams.activity.MainActivity;
import com.dynamiclogic.tams.activity.ManageAsset;
import com.dynamiclogic.tams.database.DBController;
import com.dynamiclogic.tams.database.DBSync;
import com.dynamiclogic.tams.database.Database;
import com.dynamiclogic.tams.model.Asset;
import com.dynamiclogic.tams.model.callback.AssetsListener;
import com.dynamiclogic.tams.model.callback.TAMSLocationListener;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PanelFragment extends Fragment implements AssetsListener {

    private static final String TAG = PanelFragment.class.getSimpleName();
    private OnPanelFragmentInteractionListener mPanelListener;

    private ListView mListView;
    private ArrayList<Asset> mListAssets = new ArrayList<Asset>();
    private ListAdapter mListAdapter;
    private Database database;
    private DBSync dbSync;
    private DBController dbController;

    public PanelFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

        database = Database.getInstance();
        dbController = new DBController(getActivity());
        dbSync = new DBSync(getActivity(), dbController);

        mListAssets.addAll(database.getListOfAssets());
//        Log.d(TAG, "onCreate() called, adding listener");
//        database.addAssetListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_panel, container, false);

        // Get a reference to the floating button's to start appropriate activities
        final FloatingActionButton newNode = (FloatingActionButton) v.findViewById(R.id.refresh);
        newNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 11/18/2015 Add refresh code
                dbSync.sync();

                mListAssets.clear();
                mListAssets.addAll(dbController.getListOfAssets());
                ((MyAdapter) mListAdapter).sortAssets();
                ((BaseAdapter) mListAdapter).notifyDataSetChanged();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mPanelListener = (OnPanelFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.d(TAG, "onStart() called, adding listener");
//        database.addAssetListener(this);
    }



    @Override
    public void onResume() {
        super.onResume();

        mListAdapter = new MyAdapter(getActivity(), mListAssets);
        mListView = (ListView) getView().findViewById(R.id.list);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.format("at position (%d+1) : %s ", position,
                        String.valueOf(mListAdapter.getItem(position))));
            }
        });
//        Log.d(TAG, "onResume() called, adding listener");
//        database.addAssetListener(this);
        ((MainActivity)getActivity()).addTAMSLocationListener((TAMSLocationListener) mListAdapter);


    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.d(TAG, "onPause() called, removing listener");
//        database.removeAssetListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.d(TAG, "onStop() called, removing listener");
//        database.removeAssetListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPanelListener = null;
    }

    public void onAssetsUpdated(List<Asset> assets) {
        mListAssets.clear();
        mListAssets.addAll(assets);
        ((MyAdapter)mListAdapter).sortAssets();
        ((BaseAdapter) mListAdapter).notifyDataSetChanged();
        Log.d(TAG, "onAssetsUpdated: ");
        Log.d(TAG, "onAssetsUpdated() called with: list = [" + assets + "]");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPanelFragmentInteractionListener {
        void onPanelFragmentInteraction();
    }

    private class MyAdapter extends BaseAdapter implements TAMSLocationListener {

        private final String TAG = MyAdapter.class.getSimpleName();
        private Context mContext;
        private List<Asset> mAssetList;
        private Location mLastLocation;

        public MyAdapter(Context context, List<Asset> assets) {
            mContext = context;
            mAssetList = assets;
            sortAssets();
        }

        @Override
        public int getCount() {
            return mAssetList.size();
        }

        @Override
        public Object getItem(int position) {
            if (position >= mListAssets.size()) {
                return null;
            }
            return mListAssets.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // TODO NEED TO CONSOLIDATE DUPLICATED CODE
            // had issues with onclick events when moving code out
            if (convertView == null) {
                LayoutInflater theInflater = LayoutInflater.from(mContext);

                convertView = theInflater.inflate(R.layout.fragment_cell_asset, parent, false);

                Asset asset = mAssetList.get(position);

                ImageView imageViewPreview = (ImageView) convertView.findViewById(R.id.image);
                TextView textViewTitle = (TextView) convertView.findViewById(R.id.asset_title);
                TextView textViewBody = (TextView) convertView.findViewById(R.id.asset_description);
                TextView textViewDistance = (TextView) convertView.findViewById(R.id.asset_distance);

                //Trying from db base64
                Bitmap bitmap = asset.getPicture();
                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 500, 500);
                imageViewPreview.setImageBitmap(thumbImage);
                textViewTitle.setText(asset.getName());
                textViewBody.setText(asset.getDescription());

                // set distance away
                Location loc = new Location("existing_location");
                loc.setLatitude(asset.getLatLng().latitude);
                loc.setLongitude(asset.getLatLng().longitude);

                final float metersPerMile = 1609.34f;
                if (mLastLocation != null) {
                    float milesAway = mLastLocation.distanceTo(loc) / metersPerMile;
                    textViewDistance.setText(String.format("%.2f miles away", milesAway));
                } else {
                    Log.d(TAG, "mLastLocation is still null");
                    textViewDistance.setText("? miles away");
                }

                convertView.setTag(asset);

                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Asset asset = null;
                        try {
                            asset = (Asset) v.getTag();
                        } catch (ClassCastException e) {
                            Log.e(TAG, "error on OnLongClick: " + e);
                            return false;
                        }

                        if (asset != null) {
                            String uuid = asset.getId();
                            database.removeAsset(uuid);
                            Toast.makeText(getActivity(), "Removing asset", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }
                });

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Asset asset = null;
                        Intent intent = new Intent(getActivity(), ManageAsset.class);
                        try {
                            asset = (Asset) v.getTag();
                        } catch (ClassCastException e) {
                            Log.e(TAG, "error on OnClick: " + e);
                            return;
                        }

                        if (asset != null) {
                            String dis = asset.getId();
                            Bundle bundle = new Bundle();
                            // bundle.putSerializable("asset_pass",asset);
                            intent.putExtra("asset_pass", dis);
                            //intent.putExtra("asset_pass",(Serializable)asset);
                            // intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                });

                return convertView;
            } else {
                Asset asset = mAssetList.get(position);
                ImageView imageViewPreview = (ImageView) convertView.findViewById(R.id.image);
                TextView textViewTitle = (TextView) convertView.findViewById(R.id.asset_title);
                TextView textViewBody = (TextView) convertView.findViewById(R.id.asset_description);
                TextView textViewDistance = (TextView) convertView.findViewById(R.id.asset_distance);

                Bitmap bitmap = asset.getPicture();
                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 500, 500);
                imageViewPreview.setImageBitmap(thumbImage);

                textViewTitle.setText(asset.getName());
                textViewBody.setText(asset.getDescription());

                // set distance away
                Location loc = new Location("existing_location");
                loc.setLatitude(asset.getLatLng().latitude);
                loc.setLongitude(asset.getLatLng().longitude);

                final float metersPerMile = 1609.34f;
                if (mLastLocation != null) {
                    float milesAway = mLastLocation.distanceTo(loc) / metersPerMile;
                    textViewDistance.setText(String.format("%.2f miles away", milesAway));
                } else {
                    textViewDistance.setText("? miles away");
                }

                convertView.setTag(asset);

                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Asset asset = null;
                        try {
                            asset = (Asset) v.getTag();
                        } catch (ClassCastException e) {
                            Log.e(TAG, "error on OnLongClick: " + e);
                            return false;
                        }

                        if (asset != null) {
                            String id = asset.getId();
                            database.removeAsset(id);
                            Toast.makeText(getActivity(), "Removing asset", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }
                });

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Asset asset = null;
                        Intent intent = new Intent(getActivity(), ManageAsset.class);
                        try {
                            asset = (Asset) v.getTag();
                        } catch (ClassCastException e) {
                            Log.e(TAG, "error on OnClick: " + e);
                            return;
                        }

                        if (asset != null) {
                            String dis = asset.getId();
                            Bundle bundle = new Bundle();
                            // bundle.putSerializable("asset_pass",asset);
                            intent.putExtra("asset_pass", dis);
                            //intent.putExtra("asset_pass",(Serializable)asset);
                            // intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                });

                return convertView;
            }
        }

        @Override
        public synchronized void onLocationChanged(Location location) {

            // TODO want to get the locations in the ListView to update
            if (location == null) {
                Log.e(TAG, "location in the panel set TO NULL");
                return;
            }
        //    Log.d(TAG, "location changed in panel");
            mLastLocation = location;
            sortAssets();
            notifyDataSetChanged();

        }

        public void sortAssets() {
            if (mLastLocation != null) {
                Collections.sort(mAssetList, new Asset.AssetDistanceComparator(mLastLocation));
            }
        }
    }


}