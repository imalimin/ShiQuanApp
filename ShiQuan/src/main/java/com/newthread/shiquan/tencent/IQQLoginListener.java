package com.newthread.shiquan.tencent;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.newthread.shiquan.bean.QQLoginData;
import com.newthread.shiquan.utils.SharedPreferencesManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class IQQLoginListener implements IUiListener {
	private final static String TAG = "IQQLoginListener";
	
	private Context mContext;
	private SharedPreferencesManager SPManager;
	
	public IQQLoginListener(Context mContext) {
		this.mContext = mContext;
		SPManager = new SharedPreferencesManager(mContext);
	}

	@Override
	public void onComplete(Object arg0) {
		// TODO Auto-generated method stub
		JSONObject response = (JSONObject) arg0;
		Log.v(TAG, "BaseUiListener.onComplete:" + response.toString());
		QQLoginData qqLoginData = new QQLoginData(response);
		Log.v(TAG, "qqLoginData:" + qqLoginData.toString());
		SPManager.saveQQAccessToken(qqLoginData.getAccess_token());
		SPManager.saveQQOpenid(qqLoginData.getOpenid());
		SPManager.saveQQExpiresIn(qqLoginData.getExpires_in());
	}

	protected void doComplete(JSONObject values) {
		Log.v(TAG, "doComplete:" + values.toString());
	}

	@Override
	public void onError(UiError e) {
		Log.v(TAG, "onError:" + e.toString());
		// showResult("onError:", "code:" + e.errorCode + ", msg:"
		// + e.errorMessage + ", detail:" + e.errorDetail);
	}

	@Override
	public void onCancel() {
		Log.v(TAG, "onCancel");
		// showResult("onCancel", "");
	}

}