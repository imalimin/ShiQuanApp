package com.newthread.shiquan.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.newthread.shiquan.R;
import com.newthread.shiquan.bean.UserData;
import com.newthread.shiquan.widget.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class SearchFriendsAdapter extends BaseAdapter {

    private Context context;
    private ViewHolder holder;

    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ArrayList<UserData> list;

    public SearchFriendsAdapter(Context context, ArrayList<UserData> list) {
        this.context = context;
        this.list = list;
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
                    R.layout.item_searchfriends, null);
            holder.userIcon = (CircularImage) convertView
                    .findViewById(R.id.item_searchfriends_user_icon);
            holder.userName = (TextView) convertView
                    .findViewById(R.id.item_searchfriends_user_name);
            holder.userSex = (ImageView) convertView
                    .findViewById(R.id.item_searchfriends_user_sex);
            holder.userInterestedNum = (TextView) convertView
                    .findViewById(R.id.item_searchfriends_user_interset_num);
            holder.interestBtn = (CheckBox) convertView
                    .findViewById(R.id.item_searchfriends_interset_check);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.userName.setText(list.get(position).getNickName());
        holder.userInterestedNum.setText("" + list.get(position).getFansNumber());
        if (list.get(position).getGender().equals("女")) {
            holder.userSex.setBackgroundResource(R.drawable.icon_woman);
        } else {
            holder.userSex.setBackgroundResource(R.drawable.icon_man);
        }
        imageLoader.displayImage(list.get(position).getHeadPortraitAddress(), holder.userIcon, options);

        holder.interestBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(context, "关注成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "取消关注", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
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
        public ImageView userSex;
        public TextView userInterestedNum;
        public CheckBox interestBtn;

    }
}