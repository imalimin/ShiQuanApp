package com.newthread.shiquan.bean;

/**
 * Created by lanqx on 2014/8/8.
 */
public class ReportDate {
    private String videoId = "";
    private String informerID = "";
    private String informerNickName = "";
    private String reportTime = "";

    public ReportDate(String videoId, String informerID, String informerNickName, String reportTime) {
        this.videoId = videoId;
        this.informerID = informerID;
        this.informerNickName = informerNickName;
        this.reportTime = reportTime;
    }

    @Override
    public String toString() {
        return "ReportDate{" +
                "videoId='" + videoId + '\'' +
                ", informerID='" + informerID + '\'' +
                ", informerNickName='" + informerNickName + '\'' +
                ", reportTime='" + reportTime + '\'' +
                '}';
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getInformerID() {
        return informerID;
    }

    public void setInformerID(String informerID) {
        this.informerID = informerID;
    }

    public String getInformerNickName() {
        return informerNickName;
    }

    public void setInformerNickName(String informerNickName) {
        this.informerNickName = informerNickName;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }
}
