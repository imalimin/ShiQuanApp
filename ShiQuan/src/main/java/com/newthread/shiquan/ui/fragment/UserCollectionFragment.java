package com.newthread.shiquan.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.newthread.shiquan.R;
import com.newthread.shiquan.adapter.UservideoListAdapter;
import com.newthread.shiquan.bean.VideoDataV2;
import com.newthread.shiquan.dao.DataParser;
import com.newthread.shiquan.dao.MyRequest;
import com.newthread.shiquan.dao.VideoManagerUtil;
import com.newthread.shiquan.ui.VideodetailActivity;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * Created by 翌日黄昏 on 2014/8/6.
 */
public class UserCollectionFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2 {
    private final static String TAG = "UserCollectionFragment";
    private Context context;

    private PullToRefreshGridView videoGrid;

    private ArrayList<VideoDataV2> videoList;
    private UservideoListAdapter uservideoListAdapter;
    private boolean isFirst = true;
    public UserCollectionFragment() {

    }

    @SuppressLint("ValidFragment")
    public UserCollectionFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_uservideo, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        videoGrid = (PullToRefreshGridView)view.findViewById(R.id.uservideo_gridview);
        videoGrid.setMode(PullToRefreshBase.Mode.BOTH);
        videoGrid.setOnRefreshListener(this);

        videoList = new ArrayList<VideoDataV2>();
        uservideoListAdapter = new UservideoListAdapter(context, videoList);
        videoGrid.setAdapter(uservideoListAdapter);
        videoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(context, VideodetailActivity.class);
                intent.putExtra("ID", videoList.get(position).getVideoID());
                Log.v(TAG, videoList.get(position).getVideoID());
                startActivity(intent);
            }
        });
        init(0);
    }
    public void init(int arg0) {
        if (isFirst && arg0 == 0) {
            isFirst = false;
            showList(1);
        }
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
                    Toast.makeText(context, TAG + ":" + msg, Toast.LENGTH_SHORT).show();
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
        uservideoListAdapter.setVideoList(videoList);
        uservideoListAdapter.updata();
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
