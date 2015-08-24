package com.dynamiclogic.tams.activity.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dynamiclogic.tams.R;
import com.dynamiclogic.tams.database.Database;
import com.dynamiclogic.tams.database.model.Asset;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PanelFragment extends Fragment {

    private static final String TAG = MyAdapter.class.getSimpleName();

    private OnPanelFragmentInteractionListener mListener;

    private ListView mListView;
    private ArrayList<Asset> mListAssets = new ArrayList<Asset>();
    private ListAdapter mListAdapter;
    private Database database;

    public PanelFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        database = Database.getInstance();

        mListAssets.addAll(database.getListOfAssets());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_panel, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPanelFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mListAdapter = new MyAdapter(getActivity(), mListAssets) {
        };
        mListView = (ListView) getView().findViewById(R.id.listView);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.format("at position (%d+1) : %s ", position,
                        String.valueOf(mListAdapter.getItem(position))));

            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    private class MyAdapter extends BaseAdapter {

        private final String TAG = MyAdapter.class.getSimpleName();
        private Context mContext;
        private List<Asset> mAssets;

        public MyAdapter(Context context, List<Asset> assets) {
            Log.d(TAG, "MyAdapter()");
            mContext = context;
            mAssets = assets;
        }

        @Override
        public int getCount() {
            return mAssets.size();
        }

        @Override
        public Object getItem(int position) {
            if (position >= mListAssets.size()) {
                Log.d(TAG, String.format("position = %d, array length = %d",
                        position, mListAssets.size()));
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
            Log.e(TAG, "getView() called");

            if (convertView == null) {
                LayoutInflater theInflater = LayoutInflater.from(mContext);

                View theView = theInflater.inflate(R.layout.fragment_cell_asset, parent, false);

                Asset asset = mAssets.get(position);

                TextView textViewTitle = (TextView) theView.findViewById(R.id.asset_title);
                TextView textViewBody = (TextView) theView.findViewById(R.id.asset_content);
                TextView textViewDistance = (TextView) theView.findViewById(R.id.asset_distance);

                textViewTitle.setText(asset.toString());
                textViewBody.setText(asset.getLatLng().toString());
                textViewDistance.setText(String.format("%d miles away", new Random().nextInt(100)));

                return theView;
            } else {
                return convertView;
            }
        }
    }



}
