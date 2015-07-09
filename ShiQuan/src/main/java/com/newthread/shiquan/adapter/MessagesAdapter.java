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
import com.newthread.shiquan.bean.UserData;
import com.newthread.shiquan.widget.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MessagesAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder;

    private ArrayList<UserData> list;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public MessagesAdapter(Context context, ArrayList<UserData> list) {
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
                    R.layout.item_mesageslist, null);
            holder.icon = (CircularImage) convertView
                    .findViewById(R.id.item_messagelist_icon);
            holder.title = (TextView) convertView
                    .findViewById(R.id.item_messagelist_title);
            holder.text = (TextView) convertView
                    .findViewById(R.id.item_messagelist_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText("通知");
        holder.text.setText("您的视频\"献给武汉的人\"被" + list.get(position).getNickName() + "赞了一下");
        imageLoader.displayImage(list.get(position).getHeadPortraitAddress(), holder.icon, options);
        return convertView;
    }

    // 定义一个内部类来管理条目中的子组件​
    public static class ViewHolder {
        public CircularImage icon;
        public TextView title;
        public TextView text;
    }
}
