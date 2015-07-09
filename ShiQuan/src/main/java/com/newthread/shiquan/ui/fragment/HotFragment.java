package com.newthread.shiquan.ui.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.newthread.shiquan.R;
import com.newthread.shiquan.adapter.HotListAdapter;
import com.newthread.shiquan.bean.VideoDataV2;
import com.newthread.shiquan.dao.DataParser;
import com.newthread.shiquan.dao.MyRequest;
import com.newthread.shiquan.dao.VideoManagerUtil;
import com.newthread.shiquan.ui.VideodetailActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("ValidFragment")
public class HotFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2 {
    private static final String TAG = "HotFragment";
    private Context context;

    private TextView tip;
    private PullToRefreshGridView videoGrid;
    private HotListAdapter hotListAdapter;

    private ArrayList<VideoDataV2> videoList;
    private IContactListener contactListener;

    private boolean isFirst = true;

    public HotFragment() {

    }

    public HotFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_hot, null);
        initView(view);

        return view;
    }

    private void initView(View view) {
        tip = (TextView)view.findViewById(R.id.hot_tip);
        videoGrid = (PullToRefreshGridView) view.findViewById(R.id.hot_gridview);
        videoGrid.setMode(PullToRefreshBase.Mode.BOTH);
        videoGrid.setOnRefreshListener(this);

        videoList = new ArrayList<VideoDataV2>();
        hotListAdapter = new HotListAdapter(context, videoList);
        videoGrid.setAdapter(hotListAdapter);
        videoGrid.setOnItemClickListener(new OnItemClickListener() {

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
                    tip.setVisibility(View.GONE);
//                    initList(videoList);
                } else {
                    Toast.makeText(context, TAG + ":" + msg, Toast.LENGTH_SHORT).show();
                    if(videoList.size() <= 0) {
                        tip.setVisibility(View.VISIBLE);
                    }
                    tip.setText("加载失败，请下拉刷新");
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

    public void setContactListener(IContactListener contactListener) {
        this.contactListener = contactListener;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        Log.v(TAG, "onRefresh");
        tip.setText("正在加载...");
        page = 1;
        onRefresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        Log.v(TAG, "onLoadMore");
        onLoadMore();
    }

    public interface IContactListener {
        public void contact(int result, Object obj);
    }
}
