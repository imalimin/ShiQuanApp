package com.newthread.shiquan.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.newthread.shiquan.R;
import com.newthread.shiquan.adapter.MessagesAdapter;
import com.newthread.shiquan.adapter.ReviewListAdapter;
import com.newthread.shiquan.bean.UserData;
import com.newthread.shiquan.dao.DataParser;
import com.newthread.shiquan.dao.MyRequest;
import com.newthread.shiquan.dao.PersonalDataUtil;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;

@SuppressLint("ValidFragment")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MessageFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2 {
    private Context context;

    private PullToRefreshListView listView;
    private MessagesAdapter messagesAdapter;
    private IContactListener contactListener;
    private ArrayList<UserData> userList;

    public MessageFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_messagebox, null);
        initView(view);

        return view;
    }

    private void initView(View view) {
        listView = (PullToRefreshListView) view.findViewById(R.id.messagebox_listview);
        listView.setOnRefreshListener(this);

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap hashMap = new HashMap<String, String>();
        hashMap.put("title", "通知");
        hashMap.put("text", "您的视频“献给武汉人”被小丑赞了一下");
        list.add(hashMap);
        hashMap = new HashMap<String, String>();
        hashMap.put("title", "通知");
        hashMap.put("text", "您的视频“献给武汉人”被达生M赞了一下");
        list.add(hashMap);
        hashMap = new HashMap<String, String>();
        hashMap.put("title", "通知");
        hashMap.put("text", "您的视频“献给武汉人”被王图赞了一下");
        list.add(hashMap);
        hashMap = new HashMap<String, String>();
        hashMap.put("title", "通知");
        hashMap.put("text", "您的视频“献给武汉人”被送的话V赞了一下");
        list.add(hashMap);
        hashMap = new HashMap<String, String>();
        hashMap.put("title", "通知");
        hashMap.put("text", "您的视频“献给武汉人”被爱上佛啊赞了一下");
        list.add(hashMap);
        searchFriends();
    }

    private void searchFriends() {
        PersonalDataUtil.queryUser("", new MyRequest.IRequestListener() {
            @Override
            public void onComplete(int result, String msg, Object obj) {
                if (result == HttpStatus.SC_OK) {
                    String content = null;
                    content = new String((byte[]) obj);
                    try {
                        userList = DataParser.parseUserList(new JSONArray(content));
                        showList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
                if (listView.isRefreshing())
                    listView.onRefreshComplete();
            }
        });
    }

    private void showList() {
        messagesAdapter = new MessagesAdapter(context, userList);
        listView.setAdapter(messagesAdapter);
    }

    public void setContactListener(IContactListener contactListener) {
        this.contactListener = contactListener;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        searchFriends();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        searchFriends();
    }

    public interface IContactListener {
        public void contact(int result, Object obj);
    }
}