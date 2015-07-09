package com.newthread.shiquan.ui;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.newthread.shiquan.R;
import com.newthread.shiquan.bean.QQUserData;
import com.newthread.shiquan.ui.fragment.LoginFragment;
import com.newthread.shiquan.ui.fragment.LoginFragment.ILoginCompleted;
import com.newthread.shiquan.ui.fragment.PersonalFragment;
import com.newthread.shiquan.ui.fragment.PersonalFragment.IUserInfoCompleted;
import com.newthread.shiquan.utils.SharedPreferencesManager;
import com.tencent.tauth.Tencent;

public class LoginActivity extends FragmentActivity {
	public final static int LOGIN_SUCCESS = 0;
	public final static int LOGIN_FAIL = -1;
	private final static String SCOPE = "all";
	private final static String TAG = "LoginActivity";
	private int loginCode = -1;

	private SharedPreferencesManager SPManager;
//	private QQUserData qqUserData;

	private Tencent mTencent;

	private LoginFragment lFragment;
	private PersonalFragment pFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SPManager = new SharedPreferencesManager(LoginActivity.this);
		setContentView(R.layout.activity_login);
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		lFragment = new LoginFragment(LoginActivity.this);
		pFragment = new PersonalFragment(LoginActivity.this, getSupportFragmentManager());
		
		if (SPManager.readQQExpiresIn() <= 0) {
			loginCode = LOGIN_FAIL;
			fragmentTransaction.replace(R.id.login_content, lFragment);
			fragmentTransaction.commit();
		} else {
			loginCode = LOGIN_FAIL;
			fragmentTransaction.replace(R.id.login_content, pFragment);
			fragmentTransaction.commit();
		}

		lFragment.setLoginCompleted(new ILoginCompleted() {
			@Override
			public void doCompleted() {
				// TODO Auto-generated method stub
				loginCode = LOGIN_SUCCESS;
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				fragmentTransaction.replace(R.id.login_content, pFragment);
				fragmentTransaction.commit();
			}
		});
		pFragment.setUserInfoCompleted(new IUserInfoCompleted() {
			@Override
			public void doCompleted(byte[] data, QQUserData qqUserData) {
				// TODO Auto-generated method stub
//				LoginActivity.this.qqUserData = qqUserData;
			}
			@Override
			public void doLogout() {
				Log.v(TAG, "doLogout");
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				fragmentTransaction.replace(R.id.login_content, lFragment);
				fragmentTransaction.commit();
			}
		});
	}
}
