package com.example.ashutoshchaubey.getbin;

/**
 * Created by ashutoshchaubey on 03/02/18.
 */

public class BinInfo {

    public String latitude;
    public String longitude;
    public String imageUrl;
    public String upVotes;
    public String downVotes;
    public String isVerified;

    public BinInfo(){}

    public BinInfo(String latitude,String longitude, String imageUrl, String upVotes, String downVotes, String isVerified){

        this.imageUrl=imageUrl;
        this.latitude=latitude;
        this.upVotes=upVotes;
        this.downVotes=downVotes;
        this.longitude=longitude;
        this.isVerified=isVerified;

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

    public String getDownVotes() {
        return downVotes;
    }

    public String getUpVotes() {
        return upVotes;
    }

    public void setDownVotes(String downVotes) {
        this.downVotes = downVotes;
    }

    public void setUpVotes(String upVotes) {
        this.upVotes = upVotes;
    }
}
