package com.newthread.shiquan.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lanqx on 2014/5/8.
 */
public class SharedPreferencesManager {
	public static int MODE = Context.MODE_WORLD_READABLE
			+ Context.MODE_WORLD_WRITEABLE;
	public static String PREFERENCE_NAME;

	public final static String FIRST_RUN = "FIRST_RUN";
	public final static String WIFI_AUTOPLAY = "WIFI_AUTOPLAY";
	public final static String QQ_OPENID = "QQ_OPENID";
	public final static String QQ_ACCESS_TOKEN = "QQ_ACCESS_TOKEN";
	public final static String QQ_EXPIRES_IN = "QQ_EXPIRES_IN";
	public final static String QQ_NICK = "QQ_NICK";

	private Context context;

	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;

	public SharedPreferencesManager(Context context) {
		this.context = context;
		this.MODE = Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE;
		this.PREFERENCE_NAME = "SaveSetting";
		this.sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,
				MODE);
		this.editor = sharedPreferences.edit();
	}

	public void saveFirstRun(boolean isFirstRun) {
		editor.putBoolean(FIRST_RUN, isFirstRun);
		editor.commit();
	}

	public boolean readFirstRun() {
		return sharedPreferences.getBoolean(FIRST_RUN, true);
	}

	public void saveWifiAutoPlay(boolean isWifiAutoPlay) {
		editor.putBoolean(WIFI_AUTOPLAY, isWifiAutoPlay);
		editor.commit();
	}

	public boolean readWifiAutoPlay() {
		return sharedPreferences.getBoolean(WIFI_AUTOPLAY, true);
	}

	public void saveQQOpenid(String QQOpenid) {
		editor.putString(QQ_OPENID, QQOpenid);
		editor.commit();
	}

	public String readQQOpenid() {
		return sharedPreferences.getString(QQ_OPENID, "");
	}

	public void saveQQAccessToken(String QQAccessToken) {
		editor.putString(QQ_ACCESS_TOKEN, QQAccessToken);
		editor.commit();
	}

	public String readQQAccessToken() {
		return sharedPreferences.getString(QQ_ACCESS_TOKEN, "");
	}

	public void saveQQExpiresIn(long QQExpiresIn) {
		editor.putLong(QQ_EXPIRES_IN, System.currentTimeMillis() + QQExpiresIn
				* 1000);
		editor.commit();
	}

	public long readQQExpiresIn() {
		return (sharedPreferences.getLong(QQ_EXPIRES_IN, 0) - System
				.currentTimeMillis()) / 100;
	}
	
	public void saveQQNick(String QQNick) {
		editor.putString(QQ_NICK, QQNick);
		editor.commit();
	}

	public String readQQNick() {
		return sharedPreferences.getString(QQ_NICK, "");
	}

	public void initSetting() {
		saveFirstRun(false);
		saveWifiAutoPlay(false);
	}
    public void initTencent() {
        saveQQOpenid("");
        saveQQAccessToken("");
        saveQQExpiresIn(0);
        saveQQNick("");
    }
}
