package com.newthread.shiquan.dao;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class DoubleClickExit {
	private Context mContext;
	private boolean isExit = false;

	public DoubleClickExit(Context mContext) {
		this.mContext = mContext;
	}

	public void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(mContext, "再按一次退出视圈", Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			mContext.startActivity(intent);
			System.exit(0);
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			isExit = false;
		}

	};
}
