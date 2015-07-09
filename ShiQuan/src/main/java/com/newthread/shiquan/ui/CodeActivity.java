package com.newthread.shiquan.ui;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.newthread.shiquan.R;

/**
 * Created by 翌日黄昏 on 2014/9/4.
 */
public class CodeActivity  extends Activity {

    private Button exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        initView();
    }

    private void initView() {
        exit = (Button)findViewById(R.id.code_back);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }
}
