package com.newthread.shiquan.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.newthread.shiquan.R;
import com.newthread.shiquan.adapter.SearchFriendsAdapter;
import com.newthread.shiquan.bean.UserData;
import com.newthread.shiquan.dao.DataParser;
import com.newthread.shiquan.dao.MyRequest;
import com.newthread.shiquan.dao.PersonalDataUtil;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchFriendsActivity extends Activity {
    private final static String TAG = "SearchFriendsActivity";

    private Button exit;
    private Button searchBtn;
    private EditText editText;
    private ListView listView;
    private SearchFriendsAdapter sFriendsAdapter;
    private ArrayList<UserData> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
        listView = (ListView) findViewById(R.id.search_listview);
        searchBtn = (Button) findViewById(R.id.search_btn);
        editText = (EditText) findViewById(R.id.search_edt);
        exit = (Button) findViewById(R.id.search_back);
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        searchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(SearchFriendsActivity.this, "请输入搜索条件", Toast.LENGTH_SHORT).show();
                    return;
                }
                searchFriends();
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(SearchFriendsActivity.this,
                        PersonalpageActivity.class);
                intent.putExtra("ID", userList.get(arg2).getUserID());
                startActivity(intent);
            }
        });
    }

    private void searchFriends() {
        PersonalDataUtil.queryUser(editText.getText().toString(), new MyRequest.IRequestListener() {
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
                    Toast.makeText(SearchFriendsActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showList() {
        sFriendsAdapter = new SearchFriendsAdapter(this, userList);
        listView.setAdapter(sFriendsAdapter);
    }
}
