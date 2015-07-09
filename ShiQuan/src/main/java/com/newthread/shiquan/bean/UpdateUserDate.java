package com.newthread.shiquan.bean;

/**
 * Created by lanqx on 2014/8/8.
 */
public class UpdateUserDate {
    private String userId = "";
    private String nickName = "";
    private String gender = "";
    private String city = "";

    public UpdateUserDate(String userId, String nickName, String gender, String city) {
        this.userId = userId;
        this.nickName = nickName;
        this.gender = gender;
        this.city = city;
    }

    @Override
    public String toString() {
        return "UpdateUserDate{" +
                "userId='" + userId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", gender='" + gender + '\'' +
                ", city='" + city + '\'' +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
