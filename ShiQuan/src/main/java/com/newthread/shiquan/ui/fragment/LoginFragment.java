package com.newthread.shiquan.ui.fragment;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.newthread.shiquan.R;
import com.newthread.shiquan.bean.QQLoginData;
import com.newthread.shiquan.bean.QQUserData;
import com.newthread.shiquan.tencent.AppDate;
import com.newthread.shiquan.utils.SharedPreferencesManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

@SuppressLint("ValidFragment")
public class LoginFragment extends Fragment {
	private final static String SCOPE = "all";
	private final static String TAG = "LoginFragment";
	private static Context mContext;
	private Activity mActivity;

	private Button loginBtn;
	private Button qqLoginBtn;
	private ImageView userIcon;
	private Button exit;

	private SharedPreferencesManager SPManager;
	private QQUserData qqUserData;

	private Tencent mTencent;

	private ILoginCompleted loginCompleted;

	public LoginFragment() {
	}

	public LoginFragment(Context mContext) {
		this.mContext = mContext;
		this.mActivity = (Activity) mContext;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		SPManager = new SharedPreferencesManager(mContext);
		View view = inflater.inflate(R.layout.view_login, null);
		initView(view);

		return view;
	}

	private void initView(View view) {

		// loginBtn = (Button) view.findViewById(R.id.login_enter);
		qqLoginBtn = (Button) view.findViewById(R.id.login_qq_enter);
		exit = (Button) view.findViewById(R.id.login_qq_back);
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((Activity) mContext).finish();
			}
		});
		// Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
		// 其中APP_ID是分配给第三方应用的appid，类型为String。
		mTencent = Tencent.createInstance(AppDate.APP_ID, mActivity);
		qqLoginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mTencent.login(mActivity, SCOPE, new IQQLoginListener());
			}
		});
	}

	private class IQQLoginListener implements IUiListener {

		@Override
		public void onComplete(Object arg0) {
			// TODO Auto-generated method stub
			JSONObject response = (JSONObject) arg0;
			Log.v(TAG, "IQQLoginListener.onComplete:" + response.toString());
			QQLoginData qqLoginData = new QQLoginData(response);
			Log.v(TAG, "qqLoginData:" + qqLoginData.toString());
			SPManager.saveQQAccessToken(qqLoginData.getAccess_token());
			SPManager.saveQQOpenid(qqLoginData.getOpenid());
			SPManager.saveQQExpiresIn(qqLoginData.getExpires_in());
			doCompleted();
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

	private void doCompleted() {
		if (loginCompleted != null) {
			loginCompleted.doCompleted();
		}
	}

	public interface ILoginCompleted {
		public void doCompleted();
	}

	public void setLoginCompleted(ILoginCompleted loginCompleted) {
		this.loginCompleted = loginCompleted;
	}

}
