package com.newthread.shiquan.tencent;

import com.tencent.tauth.Tencent;

public class MyTencent {
	
	private Tencent mTencent;
	private MyTencent() {
	}

	private static class MyTencentContainer {

		private static MyTencent instance = new MyTencent();
	}

	public static MyTencent getInstance() {
		return MyTencentContainer.instance;
	}
}
