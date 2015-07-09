package com.newthread.shiquan.bean;

/**
 * Created by lanqx on 2014/9/5.
 */
public class RadarData {
    private String index = "";
    private String headPortraitAddress = "";
    private String publishUserNickName = "";
    private String videoTitle = "";
    private String publishTime = "";
    private String videoId = "";

    @Override
    public String toString() {
        return "RadarData{" +
                "index='" + index + '\'' +
                ", headPortraitAddress='" + headPortraitAddress + '\'' +
                ", publishUserNickName='" + publishUserNickName + '\'' +
                ", videoTitle='" + videoTitle + '\'' +
                ", publishTime='" + publishTime + '\'' +
                '}';
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getHeadPortraitAddress() {
        return headPortraitAddress;
    }

    public void setHeadPortraitAddress(String headPortraitAddress) {
        this.headPortraitAddress = headPortraitAddress;
    }

    public String getPublishUserNickName() {
        return publishUserNickName;
    }

    public void setPublishUserNickName(String publishUserNickName) {
        this.publishUserNickName = publishUserNickName;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
