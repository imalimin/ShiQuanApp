package com.newthread.shiquan.tencent;

import android.util.Log;

import com.tencent.tauth.UiError;

/**
 * Created by 翌日黄昏 on 2014/8/6.
 */
public class UiErrorUtil {
    private final static String TAG = "UiErrorUtil";
    private final static int UIERROR_NO_NET = -10;

    public static String errorStatus(UiError e) {
        Log.v(TAG, "onError:" + e.errorCode);
        String msg = "";
        if (e.errorCode == UIERROR_NO_NET) {
            msg = "无网络";
        } else {
            msg = "未知错误:" + e.errorCode;
        }
        return msg;
    }
}
