package com.example.ashutoshchaubey.getbin;

/**
 * Created by ashutoshchaubey on 03/02/18.
 */

public class BinInfo {

    public String latitude;
    public String longitude;
    public String imageUrl;
    public String isVerified;

    public BinInfo(){}

    public BinInfo(String latitude,String longitude, String imageUrl, String isVerified){

        this.imageUrl=imageUrl;
        this.latitude=latitude;
        this.isVerified=isVerified;
        this.longitude=longitude;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    public String getIsVerified() {
        return isVerified;
    }
}
