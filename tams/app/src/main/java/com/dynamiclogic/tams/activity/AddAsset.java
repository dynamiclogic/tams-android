package com.dynamiclogic.tams.activity;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dynamiclogic.tams.R;

/**
 * Created by Javier G on 8/17/2015.
 */
public class AddAsset extends AppCompatActivity {
    //private AddressResultReceiver mResultReceiver;
    public String mAddressOutput;
    public Location mLocation;
    private static final String TAG = AddAssetFragment.class.getSimpleName();
    public static final String EXTRA_ASSET_LOCATION =
            "com.dynamiclogic.tams.activity.asset_location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_asset);

        mLocation = (Location) getIntent().getParcelableExtra(EXTRA_ASSET_LOCATION);



        //startIntentService();
    }

//    protected void startIntentService() {
//        Intent intent = new Intent(this, FetchAddressIntentService.class);
//        mResultReceiver = new AddressResultReceiver(new Handler());
//        intent.putExtra(Constants.RECEIVER, mResultReceiver);
//        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLocation);
//        startService(intent);
//    }
//
//    class AddressResultReceiver extends ResultReceiver {
//        public AddressResultReceiver(Handler handler) {
//            super(handler);
//        }
//
//        @Override
//        protected void onReceiveResult(int resultCode, Bundle resultData) {
//
//            // Display the address string
//            // or an error message sent from the intent service.
//            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
//
//            // Show a toast message if an address was found.
//            if (resultCode == Constants.SUCCESS_RESULT) {
//                //showToast(getString(R.string.address_found));
//                Log.d(TAG, "address: " + mAddressOutput.toString());
//            }
//
//        }
//    }

}
