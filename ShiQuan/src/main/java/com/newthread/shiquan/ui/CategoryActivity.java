package com.newthread.shiquan.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

public class CategoryActivity extends Activity implements PullToRefreshBase.OnRefreshListener2 {
    private static final String TAG = "CategoryActivity";
    private PullToRefreshGridView videoGrid;
	private HotListAdapter hotListAdapter;

    private  Button cameraBtn;
	private Button exit;
    private TextView title;
    private ArrayList<VideoDataV2> videoList;
    private String topic = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        topic = intent.getStringExtra("TOPIC");
		setContentView(R.layout.activity_category);

		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
        title = (TextView)findViewById(R.id.category_title);
        videoGrid = (PullToRefreshGridView) findViewById(R.id.category_gridview);
        cameraBtn = (Button)findViewById(R.id.category_takebtn);
		exit = (Button)findViewById(R.id.category_back);
        title.setText(topic);
        cameraBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent  =new Intent(CategoryActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

        videoGrid.setMode(PullToRefreshBase.Mode.BOTH);
        videoGrid.setOnRefreshListener(this);

        videoList = new ArrayList<VideoDataV2>();
        hotListAdapter = new HotListAdapter(this, videoList);
        videoGrid.setAdapter(hotListAdapter);
        videoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(CategoryActivity.this, VideodetailActivity.class);
                intent.putExtra("ID", videoList.get(position).getVideoID());
                intent.putExtra("TOPIC", topic);
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
                    Toast.makeText(CategoryActivity.this, TAG + ":" + msg, Toast.LENGTH_SHORT).show();
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
        hotListAdapter.setVedioList(videoList);
        hotListAdapter.updata();
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
