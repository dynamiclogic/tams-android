package com.dynamiclogic.tams.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Base64;
import android.widget.ImageView;

import com.dynamiclogic.tams.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.Random;
import java.util.UUID;

public class Asset{

    //Not sure if we need mId as a string or a UUID???
    private String mId;
    private String mName, mDescription, mCreatedAt, mUpdatedAt;
    private String mDeleted, mNeedsSync, mIsNew; // TODO why aren't these booleans (Nati)
    private String mPictureBase64;
    private Bitmap mPicture;
    private LatLng mLatLng;


    public Asset(LatLng latLng){
        // TODO Looks like the SQL DB wants a number for an id?
        // Ivan: Assigned the current unix time as id
        mId = getCurrentUnixTime();
        mLatLng = latLng;
    }

    public LatLng getLatLng(){
        return mLatLng;
    }

    public Double getLatitude() { return mLatLng.latitude;}

    public Double getLongitude() { return mLatLng.longitude;}

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getCreatedAt() { return mCreatedAt; }

    public void setCreatedAt(String createdAt) { this.mCreatedAt = createdAt;}

    public String getUpdatedAt() { return mUpdatedAt; }

    public void setUpdatedAt(String updatedAt) { this.mUpdatedAt = updatedAt;}

    public String getIsNew() { return mIsNew; }

    public void setIsNew(String isNew) { this.mIsNew = isNew; }

    public String getDeleted() { return mDeleted; }

    public void setDeleted(String deleted) { this.mDeleted = deleted; }

    public String getNeedsSync() { return mNeedsSync; }

    public void setNeedsSync(String needsSync) { this.mNeedsSync = needsSync; }

    public Bitmap getPicture() { return mPicture; }

    public void setPicture(Bitmap picture) {
        this.mPicture = picture;
        bitmapToBase64(picture);
    }

    public void setPictureBase64(String pictureBase64) {
        this.mPictureBase64 = pictureBase64;
        // TODO Fix this - getting an exception "bad base64"
        //base64ToBitmap(pictureBase64);
    }
    public String getPictureBase64() { return mPictureBase64; }

    public void bitmapToBase64(Bitmap picture) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArrayImage = stream.toByteArray();
        mPictureBase64 = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
    }

    public void base64ToBitmap(String pictureBase65) {
        byte[] imageAsBytes = Base64.decode(pictureBase65.getBytes(), Base64.DEFAULT);
        mPicture = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    @Override
    public boolean equals(Object o) {
        return this.mLatLng.latitude == ((Asset)o).mLatLng.latitude
                && this.mLatLng.longitude == ((Asset)o).mLatLng.longitude
                && this.mId == ((Asset)o).mId;
    }

    /**
     * Gets the current UNIX time and return it
     * @return currentTime
     */
    public String getCurrentUnixTime() {
        Long currentTime = System.currentTimeMillis() / 1000L;
        return currentTime.toString();
    }

    @Override
    public int hashCode() {
        return (int)(mLatLng.latitude * mLatLng.longitude * 1000000);
    }

    // Going to be used to determine the sorting for assets in the ListView
    public static class AssetDistanceComparator implements Comparator<Asset> {
        Location currentLocation;
        public AssetDistanceComparator(Location location) {
            currentLocation = location;
        }
        public int compare(Asset a1, Asset a2) {
            if (currentLocation == null) { return 0; }
            Location a1Loc = new Location("a1");
            a1Loc.setLatitude(a1.getLatLng().latitude);
            a1Loc.setLongitude(a1.getLatLng().longitude);

            Location a2Loc = new Location("a2");
            a2Loc.setLatitude(a2.getLatLng().latitude);
            a2Loc.setLongitude(a2.getLatLng().longitude);

            float a1Dist = currentLocation.distanceTo(a1Loc);
            float a2Dist = currentLocation.distanceTo(a2Loc);
            return (int)(a1Dist - a2Dist);
        }
    }
}