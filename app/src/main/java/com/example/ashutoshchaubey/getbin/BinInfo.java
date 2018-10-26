package com.example.ashutoshchaubey.getbin;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ashutoshchaubey on 03/02/18.
 */

public class BinInfo implements Serializable {

    public String binId;
    public String latitude;
    public String longitude;
    public String imageUrl;
    public String upVotes;
    public String downVotes;
    public String isVerified;
    public ArrayList<String> upVotedUsers;
    public ArrayList<String> downVotedUsers;
    public String uploader;

    public BinInfo() {
    }

    public BinInfo(String uploader, String binId, String latitude, String longitude, String imageUrl, String upVotes, String downVotes, String isVerified, ArrayList<String> upVotedUsers, ArrayList<String> downVotedUsers) {
        this.binId = binId;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.longitude = longitude;
        this.isVerified = isVerified;
        this.downVotedUsers = downVotedUsers;
        this.upVotedUsers = upVotedUsers;
        this.uploader = uploader;

    }

    public String getBinId() {
        return binId;
    }

    public void setBinId(String binId) {
        this.binId = binId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    public String getDownVotes() {
        return downVotes;
    }

    public void setDownVotes(String downVotes) {
        this.downVotes = downVotes;
    }

    public String getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(String upVotes) {
        this.upVotes = upVotes;
    }

    public ArrayList<String> getDownVotedUsers() {
        return downVotedUsers;
    }

    public void setDownVotedUsers(ArrayList<String> downVotedUsers) {
        this.downVotedUsers = downVotedUsers;
    }

    public ArrayList<String> getUpVotedUsers() {
        return upVotedUsers;
    }

    public void setUpVotedUsers(ArrayList<String> upVotedUsers) {
        this.upVotedUsers = upVotedUsers;
    }
}
