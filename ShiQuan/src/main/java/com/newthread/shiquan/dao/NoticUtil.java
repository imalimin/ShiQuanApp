package com.newthread.shiquan.dao;

import android.util.Log;

import com.newthread.shiquan.bean.FeedbackDate;
import com.newthread.shiquan.bean.ReportDate;
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
public class NoticUtil {
    private final static String TAG = "NoticUtil";
    public static void feedback(FeedbackDate feedbackDate, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("feedback", feedbackDate.getFeedbackText());
            jsonObject.put("feedbackTime", feedbackDate.getTime());
            jsonObject.put("mail", feedbackDate.getMail());
            jsonObject.put("phone", feedbackDate.getPhone());
            jsonObject.put("qq", feedbackDate.getQq());
            Log.v(TAG, UrlUtil.ADD_USER_FB);
            Log.v(TAG, jsonObject.toString());
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("form", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.ADD_USER_FB, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void report(ReportDate reportDate, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("videoId", reportDate.getVideoId());
            jsonObject.put("informerId", reportDate.getInformerID());
            jsonObject.put("informerNickName", reportDate.getInformerNickName());
            jsonObject.put("reportTime", reportDate.getReportTime());
            Log.v(TAG, UrlUtil.ADD_USER_REPORT);
            Log.v(TAG, jsonObject.toString());
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("form", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.ADD_USER_REPORT, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void deleteAllNotic(String userId, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userId);
            Log.v(TAG, UrlUtil.DELETE_ALL_NOTICE_INFO);
            Log.v(TAG, jsonObject.toString());
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("form", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.DELETE_ALL_NOTICE_INFO, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void deleteNotic(String userId, String userNoticeId, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userId);
            jsonObject.put("userNoticeId", userNoticeId);
            Log.v(TAG, UrlUtil.DELETE_NOTICE_INFO);
            Log.v(TAG, jsonObject.toString());
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("form", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.DELETE_NOTICE_INFO, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void queryNotic(String userId, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userId);
            Log.v(TAG, UrlUtil.QUERY_NOTICE_INFO);
            Log.v(TAG, jsonObject.toString());
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("form", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.QUERY_NOTICE_INFO, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void querySYSNotic(boolean sysNoticeState, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sysNoticeState", sysNoticeState);
            Log.v(TAG, UrlUtil.QUERY_SYS_INFO);
            Log.v(TAG, jsonObject.toString());
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("form", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.QUERY_SYS_INFO, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void showNotic(String userId, String userNoticeId, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userId);
            jsonObject.put("userNoticeId", userNoticeId);
            Log.v(TAG, UrlUtil.SHOW_NOTICE_INFO);
            Log.v(TAG, jsonObject.toString());
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("form", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.SHOW_NOTICE_INFO, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
