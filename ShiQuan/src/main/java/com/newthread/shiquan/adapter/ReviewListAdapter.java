package com.newthread.shiquan.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.newthread.shiquan.R;
import com.newthread.shiquan.bean.UserData;
import com.newthread.shiquan.widget.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ReviewListAdapter extends BaseAdapter {

    private Context context;
    private ViewHolder holder;

    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ArrayList<UserData> list;
    private ArrayList<HashMap<String, String>> listT;
    private int p;

    public ReviewListAdapter(Context context, ArrayList<UserData> list, ArrayList<HashMap<String, String>> listT) {
        this.context = context;
        this.list = list;
        this.listT = listT;
        this.p = listT.size() / list.size();
        this.options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.icon_loading).cacheInMemory()
                .cacheOnDisc().build();
        this.imageLoader.init(ImageLoaderConfiguration.createDefault(context));
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
                    R.layout.item_review, null);
            holder.userIcon = (CircularImage) convertView
                    .findViewById(R.id.item_review_user_icon);
            holder.userName = (TextView) convertView
                    .findViewById(R.id.item_review_user_name);
            holder.userReviewText = (TextView) convertView
                    .findViewById(R.id.item_review_detailtext);
            holder.reviewTime = (TextView) convertView
                    .findViewById(R.id.item_review_detailtime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.userName.setText(list.get(position).getNickName());
        holder.userReviewText.setText(listT.get(position * p).get("TALK"));
        holder.reviewTime.setText(listT.get(position * p).get("TIME"));

        imageLoader.displayImage(list.get(position).getHeadPortraitAddress(), holder.userIcon, options);

        return convertView;
    }

    public ArrayList<UserData> getList() {
        return list;
    }

    public View.OnClickListener mListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                default:
                    break;
            }
        }
    };

    // 定义一个内部类来管理条目中的子组件​
    public static class ViewHolder {
        public CircularImage userIcon;
        public TextView userName;
        public TextView userReviewText;
        public TextView reviewTime;
    }
}