package com.newthread.shiquan.dao;

import com.newthread.shiquan.bean.UserData;
import com.newthread.shiquan.bean.VideoDataV2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lanqx on 2014/9/4.
 */
public class DataParser {

    public static ArrayList<VideoDataV2> parseVideoList(JSONObject response) {
        if (response == null) return null;
        ArrayList<VideoDataV2> list = null;
        try {
            list = new ArrayList<VideoDataV2>();
            JSONObject jsonObjectT = response;
            JSONArray jsonArray = jsonObjectT.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                VideoDataV2 videoDataV2 = new VideoDataV2();
                videoDataV2.parser(jsonObject);
                list.add(videoDataV2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<UserData> parseUserList(JSONArray response) {
        if (response == null) return null;
        ArrayList<UserData> list = null;
        try {
            list = new ArrayList<UserData>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonObject = (JSONObject) response.get(i);
                UserData userData = new UserData(jsonObject);
                list.add(userData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
