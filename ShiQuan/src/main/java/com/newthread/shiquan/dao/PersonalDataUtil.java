package com.newthread.shiquan.dao;

import android.util.Log;

import com.newthread.shiquan.bean.QQUserData;
import com.newthread.shiquan.bean.UpdateUserDate;
import com.newthread.shiquan.utils.SharedPreferencesManager;
import com.newthread.shiquan.utils.UrlUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lanqx on 2014/8/8.
 */
public class PersonalDataUtil {
    private final static String TAG = "PersonalDataUtil";

    public static void addUser(QQUserData qqUserData, SharedPreferencesManager sharedPreferencesManager, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userID", sharedPreferencesManager.readQQOpenid());
            jsonObject.put("nickName", sharedPreferencesManager.readQQNick());
            jsonObject.put("city", qqUserData.getCity());
            jsonObject.put("gender", qqUserData.getGender());
            jsonObject.put("headPortraitAddress", qqUserData.getFigureurl_qq_2());
//            Log.v(TAG, jsonObject.toString());
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("user", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));

            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.ADD_USER, params, requestListener);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void updateUserInfo(UpdateUserDate updateUserDate, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userID", updateUserDate.getUserId());
            jsonObject.put("nickName", updateUserDate.getNickName());
            jsonObject.put("city", updateUserDate.getCity());
            jsonObject.put("gender", updateUserDate.getGender());
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("user", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));

            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.UPDATE_USER_INFO, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void attendUser(String userId1, String userId2, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId1", userId1);
            jsonObject.put("userId2", userId2);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));

            Log.v(TAG, UrlUtil.ATTEND_USER);
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.ATTEND_USER, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void cancelAttend(String userId1, String userId2, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId1", userId1);
            jsonObject.put("userId2", userId2);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));

            Log.v(TAG, UrlUtil.CANCEL_ATTENTION);
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.CANCEL_ATTENTION, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void getUserInfo(String userId, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userId);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));

            Log.v(TAG, UrlUtil.GET_USER_INFO);
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.GET_USER_INFO, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void queryUser(String nickName, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nickName", nickName);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));

            Log.v(TAG, UrlUtil.QUERY_USER);
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.QUERY_USER, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void showUserInfo(String userId, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userId);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));

            Log.v(TAG, UrlUtil.SHOW_USER_INFO);
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.SHOW_USER_INFO, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
