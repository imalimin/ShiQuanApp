package com.newthread.shiquan.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.newthread.shiquan.R;

public class SquareAdapter extends BaseAdapter {

	private Context context;
	private ViewHolder holder;

	private ArrayList<HashMap<String, Integer>> list;

	public SquareAdapter(Context context, ArrayList<HashMap<String, Integer>> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_square, null);
			holder.cacheImg = (ImageView) convertView
					.findViewById(R.id.item_square_img);
			holder.titleText = (TextView) convertView
					.findViewById(R.id.item_square_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.cacheImg.setBackgroundResource(list.get(position).get("THUM"));
		holder.titleText.setText(context.getResources().getString(list.get(position).get("TOPIC")));
		return convertView;
	}

	// 定义一个内部类来管理条目中的子组件​
	public static class ViewHolder {
		public ImageView cacheImg;
		public TextView titleText;
	}

}
