package com.newthread.shiquan.dao;

import android.util.Log;

import com.newthread.shiquan.bean.VideoDate;
import com.newthread.shiquan.utils.UrlUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lanqx on 2014/8/8.
 */
public class VideoRecorderUtil {
    private final static String TAG = "VideoRecorderUtil";
    public static void saveVideo(VideoDate videoDate, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("publishUserID", videoDate.getUserId());
            jsonObject.put("videoTitle", videoDate.getTitle());
            jsonObject.put("publishTime", videoDate.getTime());
            jsonObject.put("currentLocation", videoDate.getLocation());
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("form", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));

            File[] files = new File[2];
            files[0] = new File(videoDate.getVideoPath());
            files[1] = new File(videoDate.getCoverPath());

            Log.v(TAG, UrlUtil.SAVE_VIDEO);
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.postMultipart(UrlUtil.SAVE_VIDEO, params, files, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
