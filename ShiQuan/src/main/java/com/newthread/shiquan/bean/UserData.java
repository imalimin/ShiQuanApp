package com.newthread.shiquan.bean;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lanqx on 2014/8/5.
 */
public class UserData {
    public final static String TAG = "UserData";
    private String userID = "";
    private String nickName = "";
    private String city = "";
    private String gender = "";
    private String headPortraitAddress = "";
    private int attentionNumber = 0;
    private int collectionNumber = 0;
    private int fansNumber = 0;
    private String userActivity = "";
    public UserData(JSONObject response) {
        parser(response);
    }
    private void parser(JSONObject response) {
        try {
            JSONObject jsonObject = response;
            if(response == null) return;
            userID = jsonObject.getString("userID");
            nickName = jsonObject.getString("nickName");
            city = jsonObject.getString("city");
            gender = jsonObject.getString("gender");
            headPortraitAddress = jsonObject.getString("headPortraitAddress");
            attentionNumber = jsonObject.getInt("attentionNumber");
            collectionNumber = jsonObject.getInt("collectionNumber");
            fansNumber = jsonObject.getInt("fansNumber");
            userActivity = jsonObject.getString("userActivity");
        } catch (JSONException e) {
            Log.v(TAG, "UserData.parser:" + e.toString());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return "UserData{" +
                "userId='" + userID + '\'' +
                ", nickName='" + nickName + '\'' +
                ", city='" + city + '\'' +
                ", gender='" + gender + '\'' +
                ", headPortraitAddress='" + headPortraitAddress + '\'' +
                ", attentionNumber='" + attentionNumber + '\'' +
                ", collectionNumber='" + collectionNumber + '\'' +
                ", fansNumber='" + fansNumber + '\'' +
                ", userActivity='" + userActivity + '\'' +
                '}';
    }

    public static String getTag() {
        return TAG;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeadPortraitAddress() {
        return headPortraitAddress;
    }

    public void setHeadPortraitAddress(String headPortraitAddress) {
        this.headPortraitAddress = headPortraitAddress;
    }

    public int getAttentionNumber() {
        return attentionNumber;
    }

    public void setAttentionNumber(int attentionNumber) {
        this.attentionNumber = attentionNumber;
    }

    public int getCollectionNumber() {
        return collectionNumber;
    }

    public void setCollectionNumber(int collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    public int getFansNumber() {
        return fansNumber;
    }

    public void setFansNumber(int fansNumber) {
        this.fansNumber = fansNumber;
    }

    public String getUserActivity() {
        return userActivity;
    }

    public void setUserActivity(String userActivity) {
        this.userActivity = userActivity;
    }
}
