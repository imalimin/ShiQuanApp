package com.newthread.shiquan.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.newthread.shiquan.R;
import com.newthread.shiquan.adapter.HotListAdapter;
import com.newthread.shiquan.adapter.OneCityAdapter;
import com.newthread.shiquan.bean.VideoDataV2;
import com.newthread.shiquan.dao.DataParser;
import com.newthread.shiquan.dao.MyRequest;
import com.newthread.shiquan.dao.VideoManagerUtil;
import com.newthread.shiquan.widget.MyGridView;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lanqx on 2014/8/1.
 */
public class OneCityActivity extends Activity implements PullToRefreshBase.OnRefreshListener2 {
    private static final String TAG = "OneCityActivity";

    private OneCityAdapter oneCityAdapter;

    private  Button cameraBtn;
    private Button exit;
    private TextView title;
    private String cityStr = "";

    private PullToRefreshGridView videoGrid;
    private VideoDataV2 videoDataV2;
    private ArrayList<VideoDataV2> videoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        cityStr = intent.getStringExtra("CITY");
        setContentView(R.layout.activity_category);

        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
        title = (TextView)findViewById(R.id.category_title);
        videoGrid = (PullToRefreshGridView) findViewById(R.id.category_gridview);
        cameraBtn = (Button)findViewById(R.id.category_takebtn);
        exit = (Button)findViewById(R.id.category_back);
        title.setText(cityStr);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent  =new Intent(OneCityActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        videoGrid.setMode(PullToRefreshBase.Mode.BOTH);
        videoGrid.setOnRefreshListener(this);

        videoList = new ArrayList<VideoDataV2>();
        oneCityAdapter = new OneCityAdapter(this, videoList);
        videoGrid.setAdapter(oneCityAdapter);
        videoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(OneCityActivity.this, VideodetailActivity.class);
                intent.putExtra("ID", videoList.get(position).getVideoID());
                Log.v(TAG, videoList.get(position).getVideoID());
                startActivity(intent);
            }
        });
        showList(1);
    }

    private void showList(final int page) {
        VideoManagerUtil.showListInHot(page + "", new MyRequest.IRequestListener() {
            @Override
            public void onComplete(int result, String msg, Object obj) {
                if (result == HttpStatus.SC_OK) {
                    String content = null;
                    try {
                        content = URLDecoder.decode(new String((byte[]) obj, "UTF-8"));
                        Log.v(TAG, content);
                        ArrayList<VideoDataV2> list = new ArrayList<VideoDataV2>();
                        if (page == 1) {
                            videoList = DataParser.parseVideoList(new JSONObject(content));
                        } else {
                            list = DataParser.parseVideoList(new JSONObject(content));
                        }
                        addData(list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
//                    initList(videoList);
                } else {
                    Toast.makeText(OneCityActivity.this, TAG + ":" + msg, Toast.LENGTH_SHORT).show();
                }
                if (videoGrid.isRefreshing())
                    videoGrid.onRefreshComplete();
            }
        });
    }

    private int page = 1;

    private void onLoadMore() {
        page += 1;
        showList(page);
    }

    private void onRefresh() {
        showList(1);
    }

    private void initList(ArrayList<VideoDataV2> videoList) {

    }

    private void addData(ArrayList<VideoDataV2> list) {
        if (list != null) {
            for (VideoDataV2 videoDataV21 : list) {
                videoList.add(videoDataV21);
            }
        }
        oneCityAdapter.setVedioList(videoList);
        oneCityAdapter.updata();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        Log.v(TAG, "onRefresh");
        page = 1;
        onRefresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        Log.v(TAG, "onLoadMore");
        onLoadMore();
    }
}
