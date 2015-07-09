package com.newthread.shiquan.bean;

/**
 * Created by lanqx on 2014/8/8.
 */
public class FeedbackDate {
    private String feedbackText="";
    private String time="";
    private String mail="";
    private String phone="";
    private String qq="";

    public FeedbackDate(String feedbackText, String time, String mail, String phone, String qq) {
        this.feedbackText = feedbackText;
        this.time = time;
        this.mail = mail;
        this.phone = phone;
        this.qq = qq;
    }

    @Override
    public String toString() {
        return "FeedbackDate{" +
                "feedbackText='" + feedbackText + '\'' +
                ", time='" + time + '\'' +
                ", mail='" + mail + '\'' +
                ", phone='" + phone + '\'' +
                ", qq='" + qq + '\'' +
                '}';
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }
}
