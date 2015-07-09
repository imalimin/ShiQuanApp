package com.newthread.shiquan.bean;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class QQLoginData {
	public final static String TAG = "QQLoginData";

	private String access_token;
	private String openid;
	private long expires_in;
	private int ret;
	private String pay_token;
	private String pf;
	private int query_authority_cost;
	private int authority_cost;
	private String pfkey;
	private String msg;
	private int login_cost;

	public QQLoginData(JSONObject response) {
		this.access_token = "";
		this.openid = "";
		this.expires_in = 0;
		this.ret = -1;
		this.pay_token = "";
		this.pf = "";
		this.query_authority_cost = 0;
		this.authority_cost = 0;
		this.pfkey = "";
		this.msg = "";
		this.login_cost = 0;
		parser(response);
	}

	private void parser(JSONObject response) {
		try {
			JSONObject jsonObject = response;
			ret = jsonObject.getInt("ret");
			msg = jsonObject.getString("msg");

			if (jsonObject.has("access_token")) {
				access_token = jsonObject.getString("access_token");
			}
			if (jsonObject.has("openid")) {
				openid = jsonObject.getString("openid");
			}
			if (jsonObject.has("expires_in")) {
				expires_in = jsonObject.getLong("expires_in");
			}
			if (jsonObject.has("pay_token")) {
				pay_token = jsonObject.getString("pay_token");
			}
			if (jsonObject.has("pf")) {
				pf = jsonObject.getString("pf");
			}
			if (jsonObject.has("query_authority_cost")) {
				query_authority_cost = jsonObject
						.getInt("query_authority_cost");
			}
			if (jsonObject.has("authority_cost")) {
				authority_cost = jsonObject.getInt("authority_cost");
			}
			if (jsonObject.has("pfkey")) {
				pfkey = jsonObject.getString("pfkey");
			}
			if (jsonObject.has("login_cost")) {
				login_cost = jsonObject.getInt("login_cost");
			}
		} catch (JSONException e) {
			Log.v(TAG, "QQLoginData.parser:" + e.toString());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String toString() {
		return "QQLoginData [access_token=" + access_token + ", openid="
				+ openid + ", expires_in=" + expires_in + ", ret=" + ret
				+ ", pay_token=" + pay_token + ", pf=" + pf
				+ ", query_authority_cost=" + query_authority_cost
				+ ", authority_cost=" + authority_cost + ", pfkey=" + pfkey
				+ ", msg=" + msg + ", login_cost=" + login_cost + "]";
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public long getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(long expires_in) {
		this.expires_in = expires_in;
	}

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	public String getPay_token() {
		return pay_token;
	}

	public void setPay_token(String pay_token) {
		this.pay_token = pay_token;
	}

	public String getPf() {
		return pf;
	}

	public void setPf(String pf) {
		this.pf = pf;
	}

	public int getQuery_authority_cost() {
		return query_authority_cost;
	}

	public void setQuery_authority_cost(int query_authority_cost) {
		this.query_authority_cost = query_authority_cost;
	}

	public int getAuthority_cost() {
		return authority_cost;
	}

	public void setAuthority_cost(int authority_cost) {
		this.authority_cost = authority_cost;
	}

	public String getPfkey() {
		return pfkey;
	}

	public void setPfkey(String pfkey) {
		this.pfkey = pfkey;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getLogin_cost() {
		return login_cost;
	}

	public void setLogin_cost(int login_cost) {
		this.login_cost = login_cost;
	}

}
