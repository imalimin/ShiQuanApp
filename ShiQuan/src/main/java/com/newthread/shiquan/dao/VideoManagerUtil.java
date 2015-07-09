package com.newthread.shiquan.dao;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.newthread.shiquan.ftp.FTPCfg;
import com.newthread.shiquan.ftp.FTPManager;
import com.newthread.shiquan.ftp.IRetrieveListener;
import com.newthread.shiquan.utils.MyFileManager;
import com.newthread.shiquan.utils.UrlUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lanqx on 2014/9/4.
 */
public class VideoManagerUtil {
    private final static String TAG = "VideoManagerUtil";

    public static void shake(String userId, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userId);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("form", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));

            Log.v(TAG, UrlUtil.SHAKE);
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.SHAKE, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void showListInHot(String page, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("page", page);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("page", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));

            Log.v(TAG, UrlUtil.SHOW_LIST_IN_HOT);
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.SHOW_LIST_IN_HOT, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void showVideoInFriend(String page, String userId, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("page", page);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("page", jsonObject.toString()));
            jsonObject = new JSONObject();
            jsonObject.put("userId", userId);
            params.add(new BasicNameValuePair("userId", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));

            Log.v(TAG, UrlUtil.SHOW_VIDEO_IN_FRIEND);
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.SHOW_VIDEO_IN_FRIEND, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getCityVideo(String page, String userId, MyRequest.IRequestListener requestListener) {
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("page", page);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("page", page));
        params.add(new BasicNameValuePair("userId", userId));
        params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));

        Log.v(TAG, UrlUtil.SHOW_VIDEO_IN_FRIEND);
        MyRequest mRequest = MyRequest.getInstance();
        mRequest.post(UrlUtil.SHOW_VIDEO_IN_FRIEND, params, requestListener);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public static void showSpecialList(MyRequest.IRequestListener requestListener) {
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("page", page);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair("page", jsonObject.toString()));
        params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));

        Log.v(TAG, UrlUtil.SHOW_SPECIAL_LIST);
        MyRequest mRequest = MyRequest.getInstance();
        mRequest.post(UrlUtil.SHOW_SPECIAL_LIST, params, requestListener);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public static void showSpecialVideoList(String page, String categoryId, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("page", page);
            jsonObject.put("categoryId", categoryId);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("form", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));

            Log.v(TAG, UrlUtil.SHOW_SPECIAL_LIST);
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.SHOW_SPECIAL_LIST, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getVideo(String videoId, MyRequest.IRequestListener requestListener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("videoId", videoId);
            Log.v(TAG, jsonObject.toString());
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("videoId", jsonObject.toString()));
            params.add(new BasicNameValuePair("appKey", UrlUtil.APP_KEY));

            Log.v(TAG, UrlUtil.GET_VIDEO);
            MyRequest mRequest = MyRequest.getInstance();
            mRequest.post(UrlUtil.GET_VIDEO, params, requestListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getVideoFile(final String videoPath, final ProgressBar progressBar, final IRequestListener requestListener) {
        Log.v(TAG, videoPath);
        final String publicPath = MyFileManager.getPublicCacheDir();
        final String filename = getFileName(videoPath);
        Log.v(TAG, publicPath + filename);
        final File file = new File(publicPath + filename);
        if (file.exists()) {
            requestListener.onComplete(1, "成功", file);
            return;
        }
        FTPCfg ftpCfg = new FTPCfg();
        ftpCfg.address = UrlUtil.FTP_ADDRESS;
        ftpCfg.port = 21;
        ftpCfg.user = "anonymous";
        ftpCfg.pass = "0000";
        FTPManager ftpManager = new FTPManager(ftpCfg);
//        ftpManager.connectLogin();
        ftpManager.setListener(new IRetrieveListener() {
            @Override
            public void onStart(long size) {
                Log.v(TAG, "onStart");
                progressBar.setMax((int) size);
            }

            @Override
            public void onTrack(long nowOffset) {
                Log.v(TAG, "onTrack:" + nowOffset);
                progressBar.setProgress((int) nowOffset);
            }

            @Override
            public void onError(Object obj) {
                Log.v(TAG, "onError");
            }

            @Override
            public void onCancel(Object obj) {
                Log.v(TAG, "onCancel");
            }

            @Override
            public void onDone() {
                Log.v(TAG, "onDone");
                requestListener.onComplete(1, "成功", file);
            }
        });
        ftpManager.download(videoPath, publicPath + filename);
    }

    private static String getFileName(String videoPath) {
        return videoPath.substring(videoPath.lastIndexOf("/") + 1, videoPath.length());
    }

    private static String getRemotePath(String videoPath) {
        String remotePath = videoPath.substring(0, videoPath.lastIndexOf("/") + 1);
        remotePath = remotePath.substring(remotePath.indexOf("/") + 1);
        Log.v(TAG, "remotePath:" + remotePath);
        remotePath = remotePath.substring(remotePath.indexOf("/") + 1);
        Log.v(TAG, "remotePath:" + remotePath);
        remotePath = remotePath.substring(remotePath.indexOf("/"));
        Log.v(TAG, "remotePath:" + remotePath);
        return remotePath;
    }

    public interface IRequestListener {
        public void onComplete(int result, String msg, File file);
    }
}
