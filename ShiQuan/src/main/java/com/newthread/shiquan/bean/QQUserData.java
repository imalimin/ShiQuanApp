package com.newthread.shiquan.bean;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class QQUserData {
	public final static String TAG = "QQUserData";

	private String is_yellow_year_vip;//是不是黄钻年费
	private String is_yellow_vip;
	private int ret;
	private String nickname;//昵称
	private String yellow_vip_level;//黄钻等级
	private int is_lost;//是否有信息丢失
	private String msg;
	private String city;//城市
	private String province;//省份
	private String vip;
	private String level;
	private String gender;//性别
	private String figureurl_qq_1;//qq头像 40*40
	private String figureurl_qq_2;//qq头像 100*100
	private String figureurl;//qq空间头像30*30
	private String figureurl_1;//qq空间头像50*50
	private String figureurl_2;//qq空间头像100*100
	
	public QQUserData(JSONObject response) {
		this.ret = -1;
		this.is_lost = -1;
		parser(response);
	}
	private void parser(JSONObject response) {
		try {
			JSONObject jsonObject = response;
			ret = jsonObject.getInt("ret");
			is_lost = jsonObject.getInt("is_lost");
			msg = jsonObject.getString("msg");
			
			is_yellow_year_vip = jsonObject.getString("is_yellow_year_vip");
			is_yellow_vip = jsonObject.getString("is_yellow_vip");
			nickname = jsonObject.getString("nickname");
			yellow_vip_level = jsonObject.getString("yellow_vip_level");
			city = jsonObject.getString("city");
			province = jsonObject.getString("province");
			vip = jsonObject.getString("vip");
			level = jsonObject.getString("level");
			gender = jsonObject.getString("gender");
			figureurl_qq_1 = jsonObject.getString("figureurl_qq_1");
			figureurl_qq_2 = jsonObject.getString("figureurl_qq_2");
			figureurl = jsonObject.getString("figureurl");
			figureurl_1 = jsonObject.getString("figureurl_1");
			figureurl_2 = jsonObject.getString("figureurl_2");
		} catch (JSONException e) {
			Log.v(TAG, "QQLoginData.parser:" + e.toString());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public String toString() {
		return "QQUserData [is_yellow_year_vip=" + is_yellow_year_vip
				+ ", is_yellow_vip=" + is_yellow_vip + ", ret=" + ret
				+ ", nickname=" + nickname + ", yellow_vip_level="
				+ yellow_vip_level + ", is_lost=" + is_lost + ", msg=" + msg
				+ ", city=" + city + ", province=" + province + ", vip=" + vip
				+ ", level=" + level + ", gender=" + gender
				+ ", figureurl_qq_1=" + figureurl_qq_1 + ", figureurl_qq_2="
				+ figureurl_qq_2 + ", figureurl=" + figureurl
				+ ", figureurl_1=" + figureurl_1 + ", figureurl_2="
				+ figureurl_2 + "]";
	}
	public String getIs_yellow_year_vip() {
		return is_yellow_year_vip;
	}
	public void setIs_yellow_year_vip(String is_yellow_year_vip) {
		this.is_yellow_year_vip = is_yellow_year_vip;
	}
	public String getIs_yellow_vip() {
		return is_yellow_vip;
	}
	public void setIs_yellow_vip(String is_yellow_vip) {
		this.is_yellow_vip = is_yellow_vip;
	}
	public int getRet() {
		return ret;
	}
	public void setRet(int ret) {
		this.ret = ret;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getYellow_vip_level() {
		return yellow_vip_level;
	}
	public void setYellow_vip_level(String yellow_vip_level) {
		this.yellow_vip_level = yellow_vip_level;
	}
	public int getIs_lost() {
		return is_lost;
	}
	public void setIs_lost(int is_lost) {
		this.is_lost = is_lost;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getVip() {
		return vip;
	}
	public void setVip(String vip) {
		this.vip = vip;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getFigureurl_qq_1() {
		return figureurl_qq_1;
	}
	public void setFigureurl_qq_1(String figureurl_qq_1) {
		this.figureurl_qq_1 = figureurl_qq_1;
	}
	public String getFigureurl_qq_2() {
		return figureurl_qq_2;
	}
	public void setFigureurl_qq_2(String figureurl_qq_2) {
		this.figureurl_qq_2 = figureurl_qq_2;
	}
	public String getFigureurl() {
		return figureurl;
	}
	public void setFigureurl(String figureurl) {
		this.figureurl = figureurl;
	}
	public String getFigureurl_1() {
		return figureurl_1;
	}
	public void setFigureurl_1(String figureurl_1) {
		this.figureurl_1 = figureurl_1;
	}
	public String getFigureurl_2() {
		return figureurl_2;
	}
	public void setFigureurl_2(String figureurl_2) {
		this.figureurl_2 = figureurl_2;
	}
	public static String getTag() {
		return TAG;
	}
	
	
}
