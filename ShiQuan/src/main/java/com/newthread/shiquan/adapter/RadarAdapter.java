package com.newthread.shiquan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.newthread.shiquan.R;
import com.newthread.shiquan.bean.RadarData;
import com.newthread.shiquan.widget.CircularImage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lanqx on 2014/9/5.
 */
public class RadarAdapter extends BaseAdapter {

    private Context context;
    private ViewHolder holder;

    private ArrayList<RadarData> list;

    public RadarAdapter(Context context, ArrayList<RadarData> list) {
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
        convertView = LayoutInflater.from(context).inflate(
                R.layout.item_radar, null);

        TextView index = (TextView)convertView.findViewById(R.id.item_radar_index);
        CircularImage icon = (CircularImage)convertView.findViewById(R.id.item_radar_user_icon);
        TextView nickName = (TextView)convertView.findViewById(R.id.item_radar_user_name);
        TextView title = (TextView)convertView.findViewById(R.id.item_radar_video_title);
        TextView time = (TextView)convertView.findViewById(R.id.item_radar_time);

        index.setText(list.get(position).getIndex());
        nickName.setText(list.get(position).getPublishUserNickName());
        title.setText(list.get(position).getVideoTitle());
        time.setText(list.get(position).getPublishTime());

        if(list.get(position).getHeadPortraitAddress().equals("")) {
            icon.setBackgroundResource(R.drawable.icon_user_green);
        }

        return convertView;
    }

    // 定义一个内部类来管理条目中的子组件​
    public static class ViewHolder {
        public ImageView cacheImg;
        public TextView titleText;
        public ImageView userIcon;
        public TextView userName;
        public TextView location;
    }
}
