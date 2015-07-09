package com.newthread.shiquan.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.newthread.shiquan.R;
import com.newthread.shiquan.tencent.AppDate;
import com.newthread.shiquan.utils.SharedPreferencesManager;
import com.tencent.tauth.Tencent;

public class SplashActivity extends Activity {
	private static final int DALAYTIME = 2000;

	private SharedPreferencesManager SPManager;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initAplication();
		setContentView(R.layout.activity_splash);
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent _intent = new Intent(getApplication(),
						MainActivity.class);
				startActivity(_intent);
				SplashActivity.this.finish();
			}
		}, DALAYTIME);
	}
	
	private void initAplication() {
		// TODO Auto-generated method stub
		SPManager = new SharedPreferencesManager(SplashActivity.this);
		if(SPManager.readFirstRun()) {
			SPManager.initSetting();
		}
		Tencent mTencent = Tencent.createInstance(AppDate.APP_ID,SplashActivity.this);
		if(SPManager.readQQExpiresIn()>0) {
			mTencent.setAccessToken(SPManager.readQQAccessToken(),
					SPManager.readQQExpiresIn() + "");
			mTencent.setOpenId(SPManager.readQQOpenid());
		}else {
            SPManager.initTencent();
        }
	}
}
