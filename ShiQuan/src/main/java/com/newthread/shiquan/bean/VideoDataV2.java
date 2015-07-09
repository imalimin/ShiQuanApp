package com.newthread.shiquan.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lanqx on 2014/9/4.
 */
public class VideoDataV2 {
    private int commentAmount = 0;
    private String coverAddress = "";
    private String currentLocation = "";
    private int praiseAmount = 0;
    private String publishTime = "";
    private String publishUserID = "";
    private String publishUserNickName = "";
    private String videoID = "";
    private String videoTitle = "";
    private String headPortraitAddress = "";

    private String categoryID = "";
    private String categoryName = "";
    private String storagePath = "";
    private int wathAmount = 0;
    private int shareAmount = 0;
    private int collectionAmount = 0;
    private boolean auditState = false;


    private int thum = 0;

    public VideoDataV2() {
//        parser(response);
    }

    public void parser(JSONObject response) {
        try {
            JSONObject jsonObject = response;
            if (response == null) return;
            commentAmount = jsonObject.getInt("commentAmount");
            coverAddress = jsonObject.getString("coverAddress");
            currentLocation = jsonObject.getString("currentLocation");
            praiseAmount = jsonObject.getInt("praiseAmount");
            publishTime = jsonObject.getString("publishTime");
            publishUserID = jsonObject.getString("publishUserID");
            publishUserNickName = jsonObject.getString("publishUserNickName");
            videoID = jsonObject.getString("videoID");
            videoTitle = jsonObject.getString("videoTitle");
            if (jsonObject.has("headPortraitAddress"))
                headPortraitAddress = jsonObject.getString("headPortraitAddress");

            if (jsonObject.has("categoryID"))
            categoryID = jsonObject.getString("categoryID");
            if (jsonObject.has("categoryName"))
            categoryName = jsonObject.getString("categoryName");
            if (jsonObject.has("storagePath"))
            storagePath = jsonObject.getString("storagePath");
            if (jsonObject.has("wathAmount"))
            wathAmount = jsonObject.getInt("wathAmount");
            if (jsonObject.has("shareAmount"))
            shareAmount = jsonObject.getInt("shareAmount");
            if (jsonObject.has("collectionAmount"))
            collectionAmount = jsonObject.getInt("collectionAmount");
            if (jsonObject.has("auditState"))
            auditState = jsonObject.getBoolean("auditState");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "VideoDataV2{" +
                "commentAmount=" + commentAmount +
                ", coverAddress='" + coverAddress + '\'' +
                ", currentLocation='" + currentLocation + '\'' +
                ", praiseAmount=" + praiseAmount +
                ", publishTime='" + publishTime + '\'' +
                ", publishUserID='" + publishUserID + '\'' +
                ", publishUserNickName='" + publishUserNickName + '\'' +
                ", videoID='" + videoID + '\'' +
                ", videoTitle='" + videoTitle + '\'' +
                ", headPortraitAddress='" + headPortraitAddress + '\'' +
                ", categoryID='" + categoryID + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", storagePath='" + storagePath + '\'' +
                ", wathAmount=" + wathAmount +
                ", shareAmount=" + shareAmount +
                ", collectionAmount=" + collectionAmount +
                ", auditState=" + auditState +
                '}';
    }

    public int getCommentAmount() {
        return commentAmount;
    }

    public void setCommentAmount(int commentAmount) {
        this.commentAmount = commentAmount;
    }

    public String getCoverAddress() {
        return coverAddress;
    }

    public void setCoverAddress(String coverAddress) {
        this.coverAddress = coverAddress;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public int getPraiseAmount() {
        return praiseAmount;
    }

    public void setPraiseAmount(int praiseAmount) {
        this.praiseAmount = praiseAmount;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublishUserID() {
        return publishUserID;
    }

    public void setPublishUserID(String publishUserID) {
        this.publishUserID = publishUserID;
    }

    public String getPublishUserNickName() {
        return publishUserNickName;
    }

    public void setPublishUserNickName(String publishUserNickName) {
        this.publishUserNickName = publishUserNickName;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getHeadPortraitAddress() {
        return headPortraitAddress;
    }

    public void setHeadPortraitAddress(String headPortraitAddress) {
        this.headPortraitAddress = headPortraitAddress;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public int getWathAmount() {
        return wathAmount;
    }

    public void setWathAmount(int wathAmount) {
        this.wathAmount = wathAmount;
    }

    public int getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(int shareAmount) {
        this.shareAmount = shareAmount;
    }

    public int getCollectionAmount() {
        return collectionAmount;
    }

    public void setCollectionAmount(int collectionAmount) {
        this.collectionAmount = collectionAmount;
    }

    public boolean isAuditState() {
        return auditState;
    }

    public void setAuditState(boolean auditState) {
        this.auditState = auditState;
    }

    public int getThum() {
        return thum;
    }

    public void setThum(int thum) {
        this.thum = thum;
    }
}
