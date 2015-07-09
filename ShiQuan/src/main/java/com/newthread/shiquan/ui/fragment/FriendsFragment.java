package com.newthread.shiquan.ui.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.newthread.shiquan.R;
import com.newthread.shiquan.adapter.FriendListAdapter;
import com.newthread.shiquan.bean.VideoDataV2;
import com.newthread.shiquan.dao.DataParser;
import com.newthread.shiquan.dao.MyRequest;
import com.newthread.shiquan.dao.VideoManagerUtil;
import com.newthread.shiquan.ui.VideodetailActivity;
import com.newthread.shiquan.utils.SharedPreferencesManager;
import com.newthread.shiquan.utils.UrlUtil;
import com.viewpagerindicator.TabPageIndicator;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("ValidFragment")
public class FriendsFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2 {
    private final static String TAG = "FriendsFragment";
    private Context context;

    private TextView tip;
    private PullToRefreshListView listView;
    private FriendListAdapter friendListAdapter;

    private ArrayList<VideoDataV2> videoList;
    private SharedPreferencesManager SPManager;
    private TabPageIndicator tabPageIndicator;
    private IContactListener contactListener;

    private boolean isFirst = true;

    public FriendsFragment() {

    }

    public FriendsFragment(Context context, TabPageIndicator tabPageIndicator) {
        this.context = context;
        this.tabPageIndicator = tabPageIndicator;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.view_friends, null);
        SPManager = new SharedPreferencesManager(context);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tip = (TextView)view.findViewById(R.id.friends_tip);
        listView = (PullToRefreshListView) view.findViewById(R.id.friends_listview);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);

        videoList = new ArrayList<VideoDataV2>();
        friendListAdapter = new FriendListAdapter(context, videoList);
        listView.setAdapter(friendListAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(context, VideodetailActivity.class);
                intent.putExtra("ID", videoList.get(position - 1).getVideoID());
                Log.v(TAG, videoList.get(position).getVideoID());
                startActivity(intent);
            }
        });

//		tabPageIndicator.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    public void init(int page) {
        Message message = handler.obtainMessage(page, null);
        handler.sendMessageDelayed(message, 150);
    }

    private android.os.Handler handler = new android.os.Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            if (isFirst && message.what == 1) {
                isFirst = false;
                showList(1);
            }
            setMediaStatus(message.what);
        }
    };

    public void showList(final int page) {
        VideoManagerUtil.showVideoInFriend(page + "", SPManager.readQQOpenid(), new MyRequest.IRequestListener() {
            @Override
            public void onComplete(int result, String msg, Object obj) {
                if (result == HttpStatus.SC_OK) {
                    String content = null;
                    try {
                        content = new String((byte[]) obj, "utf-8");
                        Log.v(TAG, content);
                        ArrayList<VideoDataV2> list = new ArrayList<VideoDataV2>();
                        if (page == 1) {
                            videoList = DataParser.parseVideoList(new JSONObject(content));
                        } else {
                            list = DataParser.parseVideoList(new JSONObject(content));
                            if (list != null) {
                                for (VideoDataV2 videoDataV21 : list) {
                                    videoList.add(videoDataV21);
                                }
                            }
                        }
                        addData(list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    tip.setVisibility(View.GONE);
                } else {
                    Toast.makeText(context, TAG + ":" + msg, Toast.LENGTH_SHORT).show();
                    if(videoList.size() <= 0) {
                        tip.setVisibility(View.VISIBLE);
                    }
                    tip.setText("加载失败，请下拉刷新");
                }
                if (listView.isRefreshing())
                    listView.onRefreshComplete();
            }
        });
    }

    //    private void initList(ArrayList<VideoDataV2> videoList) {
//        hotListAdapter = new HotListAdapter(context, videoList);
//        listView.setAdapter(hotListAdapter);
//    }
    private int page = 1;

    private void onLoadMore() {
        page += 1;
        showList(page);
    }

    private void onRefresh() {
        showList(1);
    }

    private void addData(ArrayList<VideoDataV2> list) {
        Log.v(TAG, "addData:" + videoList.size());
        friendListAdapter.setList(videoList);
        friendListAdapter.updata();
        listView.setOnScrollListener(new MyOnScrollListener());
    }

    private void setMediaStatus(int page) {
        if (page == 1 && isFirst) {
        } else if (!isFirst && page == 1) {
            friendListAdapter.mediaStart();
        } else if (!isFirst && (page == 0 || page == 2)) {
            friendListAdapter.mediaPause();
        }
    }

    private class MyOnScrollListener implements AbsListView.OnScrollListener {
        private int firstVisibleItem = -1;

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            Log.v(TAG, "onScroll:" + firstVisibleItem + ";");
            int firstVisibleItemT = firstVisibleItem;
            if (this.firstVisibleItem != firstVisibleItemT)
                friendListAdapter.getVideo(firstVisibleItemT);
            this.firstVisibleItem = firstVisibleItemT;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // TODO Auto-generated method stub
            Log.v(TAG, "scrollState:" + scrollState + ";");
        }

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        friendListAdapter.releaseMedia();
        Log.v(TAG, "onDestroy");
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.v(TAG, "onResume");
    }

    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
        Log.v(TAG, "onDetach");
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.v(TAG, "onStart");
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
