package com.newthread.shiquan.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.newthread.shiquan.R;
import com.newthread.shiquan.adapter.RadarAdapter;
import com.newthread.shiquan.bean.RadarData;
import com.newthread.shiquan.location.MyLocation;

import java.util.ArrayList;

/**
 * Created by 翌日黄昏 on 2014/9/5.
 */
public class RadarActivity extends Activity {

    private Button exit;
    private ImageView map;
    private ListView listView;

    private ArrayList<RadarData> list;
    private RadarAdapter radarAdapter;
    private MyLocation myLocation;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

        initView();
    }

    private void initView() {
        View headerView = LayoutInflater.from(this).inflate(
                R.layout.header_image, null);
        map = (ImageView) headerView.findViewById(R.id.header_image);

        listView = (ListView) findViewById(R.id.radar_listview);
//        map = (ImageView)findViewById(R.id.radar_map);
        exit = (Button) findViewById(R.id.radar_back);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RadarActivity.this, VideodetailActivity.class);
                intent.putExtra("ID", list.get(position - 1).getVideoId());
                startActivity(intent);
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.getWindow().setTitleColor(getResources().getColor(R.color.black));
        progressDialog.setMessage("正在定位...");
        progressDialog.show();
        setDialogText(progressDialog.getWindow().getDecorView());
        listView.addHeaderView(headerView);

        list = new ArrayList<RadarData>();
        RadarData radarData = new RadarData();
        radarData.setIndex("1");
        radarData.setPublishUserNickName("小丑");
        radarData.setVideoTitle("猫咪很萌");
        radarData.setPublishTime("1分钟前");
        radarData.setVideoId("20140801-6F9619FF-8B86-D011-B42D-00C04FC968FF");
        list.add(radarData);
        radarData = new RadarData();
        radarData.setIndex("2");
        radarData.setPublishUserNickName("达生M");
        radarData.setVideoTitle("旅途风景很美好");
        radarData.setPublishTime("1分钟前");
        radarData.setVideoId("20140801-6F9619FF-8B86-D011-B42D-00C04FC969FF");
        list.add(radarData);
        radarData = new RadarData();
        radarData.setIndex("3");
        radarData.setPublishUserNickName("王图");
        radarData.setVideoTitle("我的女神");
        radarData.setPublishTime("1分钟前");
        radarData.setVideoId("20140801-6F9619FF-8B86-D011-B42D-00C04FC970FF");
        list.add(radarData);
        radarData = new RadarData();
        radarData.setIndex("4");
        radarData.setPublishUserNickName("生生相惜");
        radarData.setVideoTitle("萌宠大集合");
        radarData.setPublishTime("1分钟前");
        radarData.setVideoId("20140801-6F9619FF-8B86-D011-B42D-00C04FC971FF");
        list.add(radarData);
        radarData = new RadarData();
        radarData.setIndex("5");
        radarData.setPublishUserNickName("爱上佛啊");
        radarData.setVideoTitle("女汉子的世界我们不懂");
        radarData.setPublishTime("1分钟前");
        radarData.setVideoId("20140802-6F9619FF-8B86-D011-B42D-00C04FC967FF");
        list.add(radarData);
        radarData = new RadarData();
        radarData.setIndex("6");
        radarData.setPublishUserNickName("送的话V");
        radarData.setVideoTitle("神一般的世界");
        radarData.setPublishTime("1分钟前");
        radarData.setVideoId("20140802-6F9619FF-8B86-D011-B42D-00C04FC968FF");
        list.add(radarData);

//        setLocation();
        delayProgress();
    }

    private void setLocation() {
        myLocation = new MyLocation(this, new MyLocation.ILocationListener() {
            @Override
            public void doComplete(String city, String location) {
                if (city.equals("无锡市")) {
                    map.setBackgroundResource(R.drawable.wuxi);
                } else {
                    map.setBackgroundResource(R.drawable.wuhan);
                }
                setListView(list);
                progressDialog.dismiss();
            }
        });
        myLocation.start();
    }

    private void setListView(ArrayList<RadarData> list) {
        radarAdapter = new RadarAdapter(this, list);
        listView.setAdapter(radarAdapter);
    }

    private void delayProgress() {
        final Handler mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                map.setBackgroundResource(R.drawable.wuxi);
                setListView(list);
                progressDialog.dismiss();
            }
        };

        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = mHandler.obtainMessage(1, null);
                mHandler.sendMessage(message);
            }
        }.start();
    }

    private void setDialogText(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                setDialogText(child);
            }
        } else if (view instanceof TextView) {
            ((TextView) view).setTextColor(getResources().getColor(R.color.black));
        }
    }
}
