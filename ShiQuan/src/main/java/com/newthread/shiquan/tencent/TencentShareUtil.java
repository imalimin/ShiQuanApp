package com.newthread.shiquan.tencent;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * Created by lanqx on 2014/8/8.
 */
public class TencentShareUtil {
    private final static String TAG = "TencentShareUtil";

    public static void shareToQzone(Context context, Tencent mTencent) {
        final Bundle params = new Bundle();
        // 分享类型
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
                QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "视圈QQ分享测试标题");// 必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "视圈QQ分享测试摘要");// 选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "视圈QQ分享测试跳转URL");// 必填
        mTencent.shareToQzone((Activity) context, params, new IUiListener() {

            @Override
            public void onError(UiError arg0) {
                // TODO Auto-generated method stub
                Log.v(TAG, "onError");
            }

            @Override
            public void onComplete(Object arg0) {
                // TODO Auto-generated method stub
                Log.v(TAG, "onComplete");
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub
                Log.v(TAG, "onCancel");
            }
        });
    }
}
