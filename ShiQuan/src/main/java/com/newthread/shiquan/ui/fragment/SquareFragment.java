package com.newthread.shiquan.ui.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.newthread.shiquan.R;
import com.newthread.shiquan.adapter.SquareAdapter;
import com.newthread.shiquan.dao.MyRequest;
import com.newthread.shiquan.dao.VideoManagerUtil;
import com.newthread.shiquan.location.MyLocation;
import com.newthread.shiquan.ui.CategoryActivity;
import com.newthread.shiquan.ui.OneCityActivity;
import com.viewpagerindicator.TabPageIndicator;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("ValidFragment")
public class SquareFragment extends Fragment {
    private final static String TAG = "SquareFragment";
    private Context context;

    private TextView location;
    private ListView listView;
    private SquareAdapter squareAdapter;
    private IContactListener contactListener;

    private MyLocation myLocation;
    private ArrayList<HashMap<String, Integer>> list;

    private boolean isFirst = true;

    public SquareFragment() {

    }

    public SquareFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.view_square, null);
        initView(view);

        return view;
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.square_listview);

        final View convertView = LayoutInflater.from(context).inflate(
                R.layout.header_square, null);
        location = (TextView) convertView.findViewById(R.id.heade_square_location);
        myLocation = new MyLocation(context, location);
        myLocation.start();
        listView.addHeaderView(convertView);

        list = new ArrayList<HashMap<String, Integer>>();
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        hashMap.put("THUM", R.drawable.shangquan);
        hashMap.put("TOPIC", R.string.topic_5);
        list.add(hashMap);

        hashMap = new HashMap<String, Integer>();
        hashMap.put("THUM", R.drawable.pet);
        hashMap.put("TOPIC", R.string.topic_1);
        list.add(hashMap);

        hashMap = new HashMap<String, Integer>();
        hashMap.put("THUM", R.drawable.travall);
        hashMap.put("TOPIC", R.string.topic_2);
        list.add(hashMap);

        hashMap = new HashMap<String, Integer>();
        hashMap.put("THUM", R.drawable.nvshen);
        hashMap.put("TOPIC", R.string.topic_3);
        list.add(hashMap);

        hashMap = new HashMap<String, Integer>();
        hashMap.put("THUM", R.drawable.nvhanzi);
        hashMap.put("TOPIC", R.string.topic_4);
        list.add(hashMap);

        squareAdapter = new SquareAdapter(context, list);
        listView.setAdapter(squareAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Log.v(TAG, "onItemClick:" + position);
                Intent intent;
                if (position == 0) {
                    if (location.getText().equals(convertView.getResources().getString(R.string.now_location))) {
                        Toast.makeText(context, "正在定位...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    intent = new Intent(context, OneCityActivity.class);
                    intent.putExtra("CITY", location.getText());
                    startActivity(intent);
                } else {
                    intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra("TOPIC", context.getResources().getString(list.get(position - 1).get("TOPIC")));
                    startActivity(intent);
                }
            }
        });
    }

    public void init(int arg0) {
        Log.v(TAG, "init:" + isFirst);
            if (isFirst && arg0 == 2) {
                isFirst = false;
                showList();
            }
    }

    public void showList() {
        VideoManagerUtil.showSpecialList(new MyRequest.IRequestListener() {
            @Override
            public void onComplete(int result, String msg, Object obj) {
                if (result == HttpStatus.SC_OK) {
                    String content = null;
                    try {
                        content = new String((byte[]) obj, "utf-8");
                        Log.v(TAG, content);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, TAG + ":" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setContactListener(IContactListener contactListener) {
        this.contactListener = contactListener;
    }

    public interface IContactListener {
        public void contact(int result, Object obj);
    }
}
