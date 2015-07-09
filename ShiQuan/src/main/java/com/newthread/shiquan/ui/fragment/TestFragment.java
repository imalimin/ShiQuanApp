package com.newthread.shiquan.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newthread.shiquan.R;

/**
 * Created by 翌日黄昏 on 2014/8/6.
 */
@SuppressLint("ValidFragment")
public class TestFragment extends Fragment {
    private Context context;
    public TestFragment() {

    }

    public TestFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_about, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
    }
}
