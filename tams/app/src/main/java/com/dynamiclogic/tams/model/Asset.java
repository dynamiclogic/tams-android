package com.dynamiclogic.tams.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

public class Asset{

    //Not sure if we need mId as a string or a UUID???
    private UUID mId;
    private String mName, mDescription;
    private String mPictureLocation;
    private String mAudioLocation;
    private LatLng mLatLng;


    public Asset(LatLng latLng){
        mId = UUID.randomUUID();
        mLatLng = latLng;
    }

    public LatLng getLatLng(){
        return mLatLng;
    }

    public UUID getId() {
        return mId;
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

    public String getPictureLocation() {
        return mPictureLocation;
    }

    public void setPictureLocation(String pictureLocation) {
        mPictureLocation = pictureLocation;
    }

    public String getAudioLocation() {
        return mAudioLocation;
    }

    public void setAudioLocation(String audioLocation) {
        mAudioLocation = audioLocation;
    }

    @Override
    public boolean equals(Object o) {
        return this.mLatLng.latitude == ((Asset)o).mLatLng.latitude
                && this.mLatLng.longitude == ((Asset)o).mLatLng.longitude
                && this.mId == ((Asset)o).mId;
    }

    @Override
    public int hashCode() {
        return (int)(mLatLng.latitude * mLatLng.longitude * 1000000);
    }
}