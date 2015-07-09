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
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.newthread.shiquan.R;
import com.newthread.shiquan.adapter.SearchFriendsAdapter;
import com.newthread.shiquan.adapter.UserattentionAdapter;
import com.newthread.shiquan.bean.UserData;
import com.newthread.shiquan.dao.DataParser;
import com.newthread.shiquan.dao.MyRequest;
import com.newthread.shiquan.dao.PersonalDataUtil;
import com.newthread.shiquan.ui.PersonalpageActivity;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by 翌日黄昏 on 2014/8/6.
 */
public class UserAttentionFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2 {
    private final static String TAG = "UserAttentionFragment";
    private Context context;
    private PullToRefreshListView listView;
    private UserattentionAdapter userattentionAdapter;
    private ArrayList<UserData> userList;

    public UserAttentionFragment() {

    }

    @SuppressLint("ValidFragment")
    public UserAttentionFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_userattention, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        listView = (PullToRefreshListView) view.findViewById(R.id.userattention_listview);
        listView.setOnRefreshListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(context,
                        PersonalpageActivity.class);
                intent.putExtra("ID", userList.get(arg2 - 1).getUserID());
                startActivity(intent);
            }
        });
        searchFriends();
    }

    private void searchFriends() {
        PersonalDataUtil.queryUser("", new MyRequest.IRequestListener() {
            @Override
            public void onComplete(int result, String msg, Object obj) {
                if (result == HttpStatus.SC_OK) {
                    String content = null;
                    content = new String((byte[]) obj);
                    Log.v(TAG, content);
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
        userattentionAdapter = new UserattentionAdapter(context, userList);
        listView.setAdapter(userattentionAdapter);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        searchFriends();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        searchFriends();

    }
}
