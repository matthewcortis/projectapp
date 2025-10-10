package com.example.project.Model;

import java.util.Date;

public class NewFeedModel {
    private String idAuthor, postMedia, postText, imageStatus, videoStatus, location;
    private int likeCount;
    private Date postTimestamp;
    private double rating;
    private double latitude;
    private double longitude;

    public NewFeedModel(double longitude, double latitude, double rating, Date postTimestamp, int likeCount, String location, String videoStatus, String imageStatus, String postText, String postMedia, String idAuthor) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.rating = rating;
        this.postTimestamp = postTimestamp;
        this.likeCount = likeCount;
        this.location = location;
        this.videoStatus = videoStatus;
        this.imageStatus = imageStatus;
        this.postText = postText;
        this.postMedia = postMedia;
        this.idAuthor = idAuthor;
    }

    public NewFeedModel() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(String idAuthor) {
        this.idAuthor = idAuthor;
    }

    public String getPostMedia() {
        return postMedia;
    }

    public void setPostMedia(String postMedia) {
        this.postMedia = postMedia;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getImageStatus() {
        return imageStatus;
    }

    public void setImageStatus(String imageStatus) {
        this.imageStatus = imageStatus;
    }

    public String getVideoStatus() {
        return videoStatus;
    }

    public void setVideoStatus(String videoStatus) {
        this.videoStatus = videoStatus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public Date getPostTimestamp() {
        return postTimestamp;
    }

    public void setPostTimestamp(Date postTimestamp) {
        this.postTimestamp = postTimestamp;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
