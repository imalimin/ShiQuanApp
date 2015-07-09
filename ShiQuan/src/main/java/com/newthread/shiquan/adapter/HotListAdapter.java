package com.newthread.shiquan.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.newthread.shiquan.R;
import com.newthread.shiquan.bean.VideoDataV2;
import com.newthread.shiquan.widget.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class HotListAdapter extends BaseAdapter {

    private Context context;
    private ViewHolder holder;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    private ArrayList<VideoDataV2> vedioList;

    public HotListAdapter(Context context, ArrayList<VideoDataV2> vedioList) {
        this.context = context;
        this.vedioList = vedioList;
        this.options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.icon_loading).cacheInMemory()
                .cacheOnDisc().build();
        this.imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return vedioList.size();
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
                    R.layout.item_videogrid, null);
            holder.userIcon = (CircularImage) convertView
                    .findViewById(R.id.item_video_user_icon);
            holder.userName = (TextView) convertView
                    .findViewById(R.id.item_video_user_name);
            holder.cacheImg = (ImageView) convertView
                    .findViewById(R.id.item_video_img);
            holder.titleText = (TextView) convertView
                    .findViewById(R.id.item_video_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(vedioList.get(position).getThum() != 0) {
            holder.cacheImg.setBackgroundResource(vedioList.get(position).getThum());
        }
        holder.titleText.setText(vedioList.get(position).getVideoTitle());
        holder.userName.setText(vedioList.get(position).getPublishUserNickName());
        imageLoader.displayImage(vedioList.get(position).getCoverAddress(), holder.cacheImg, options);
        imageLoader.displayImage(vedioList.get(position).getHeadPortraitAddress(), holder.userIcon, options);
        return convertView;
    }

    public void updata() {
        notifyDataSetChanged();
    }

    public void setVedioList(ArrayList<VideoDataV2> vedioList) {
        this.vedioList = vedioList;
    }

    // 定义一个内部类来管理条目中的子组件​
    public static class ViewHolder {
        public ImageView cacheImg;
        public TextView titleText;
        public CircularImage userIcon;
        public TextView userName;
    }

}
