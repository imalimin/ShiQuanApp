package com.newthread.shiquan.bean;

/**
 * Created by lanqx on 2014/8/8.
 */
public class VideoDate {
    private String videoPath = "";
    private String coverPath = "";
    private String title = "";
    private String userId = "";
    private String time = "";
    private String location = "";

    public VideoDate(String videoPath, String coverPath, String title, String userId, String time, String location) {
        this.videoPath = videoPath;
        this.coverPath = coverPath;
        this.title = title;
        this.userId = userId;
        this.time = time;
        this.location = location;
    }

    @Override
    public String toString() {
        return "VideoDate{" +
                "videoPath='" + videoPath + '\'' +
                ", coverPath='" + coverPath + '\'' +
                ", title='" + title + '\'' +
                ", userId='" + userId + '\'' +
                ", time='" + time + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
