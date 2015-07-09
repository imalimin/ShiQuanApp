package com.newthread.shiquan.ui;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.newthread.shiquan.R;
import com.newthread.shiquan.media.MyVideoRecorder;

/**
 * Created by 翌日黄昏 on 2014/9/5.
 */
public class ScanActivity extends Activity {

    private Button exit;
    private SurfaceView cameraSurface;
    private MyVideoRecorder mRecorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        initView();
    }

    private void initView() {
        cameraSurface = (SurfaceView)findViewById(R.id.scan_surface);
        exit = (Button) findViewById(R.id.scan_back);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        mRecorder = new MyVideoRecorder(this, cameraSurface, null, null);
        mRecorder.init(0, 1280, 720);
    }
}
