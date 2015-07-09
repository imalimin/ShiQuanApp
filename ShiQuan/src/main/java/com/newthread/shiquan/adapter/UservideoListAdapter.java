package com.newthread.shiquan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.newthread.shiquan.R;
import com.newthread.shiquan.bean.VideoDataV2;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

/**
 * Created by lanqx on 2014/8/7.
 */
public class UservideoListAdapter  extends BaseAdapter {

    private Context context;
    private ViewHolder holder;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    private ArrayList<VideoDataV2> videoLidt;

    public UservideoListAdapter(Context context, ArrayList<VideoDataV2> videoLidt) {
        this.context = context;
        this.videoLidt = videoLidt;
        this.options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.icon_loading).cacheInMemory()
                .cacheOnDisc().build();
        this.imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return videoLidt.size();
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
                    R.layout.item_uservideo, null);
            holder.cacheImg = (ImageView) convertView
                    .findViewById(R.id.item_uservideo_img);
            holder.titleText = (TextView) convertView
                    .findViewById(R.id.item_uservideo_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        imageLoader.displayImage(videoLidt.get(position).getCoverAddress(), holder.cacheImg, options);
        holder.titleText.setText(videoLidt.get(position).getVideoTitle());
        return convertView;
    }

    // 定义一个内部类来管理条目中的子组件​
    public static class ViewHolder {
        public ImageView cacheImg;
        public TextView titleText;
    }
    public void updata() {
        notifyDataSetChanged();
    }
    public void setVideoList(ArrayList<VideoDataV2> videoLidt) {
        this.videoLidt = videoLidt;
    }

}
