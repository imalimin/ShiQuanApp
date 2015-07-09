package com.newthread.shiquan.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.newthread.shiquan.R;
import com.newthread.shiquan.media.MyVideoRecorder;
import com.newthread.shiquan.utils.MyFileManager;

import java.io.File;

public class CameraActivity extends Activity {
    private static final String TAG = "CAMERA_TUTORIAL";

    private SurfaceView cameraSurface;
    private ProgressBar seekBar;
    private CheckBox onTorch;
    private Button cancelBtn;
    private Button cameraBtn;
    private Button nextBtn;
    private Button exit;
    private Button cameraSwitch;
    private MyVideoRecorder mRecorder;
//    private MyVideoRec mRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
        cameraSurface = (SurfaceView) findViewById(R.id.camera_surface);
        seekBar = (ProgressBar) findViewById(R.id.camera_seekbar);
        cameraBtn = (Button) findViewById(R.id.camera_start);
        mRecorder = new MyVideoRecorder(CameraActivity.this, cameraSurface, seekBar, cameraBtn);
        mRecorder.init(0, 640, 480);
//        mRec = new MyVideoRec(CameraActivity.this, cameraSurface, seekBar, cameraBtn);

        onTorch = (CheckBox) findViewById(R.id.camera_torch_check);
        cancelBtn = (Button) findViewById(R.id.camera_cancel);
        nextBtn = (Button) findViewById(R.id.camera_next);
        exit = (Button) findViewById(R.id.camera_back);
        cameraSwitch = (Button) findViewById(R.id.camera_switch);
        exit.setOnClickListener(mListener);
        cameraSwitch.setOnClickListener(mListener);
        cancelBtn.setOnClickListener(mListener);
        nextBtn.setOnClickListener(mListener);
        onTorch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                Log.v(TAG, "onCheckedChanged");
                    mRecorder.setOnTorch(isChecked);
                    mRecorder.surfaceCreated(null);
            }
        });
    }

    private OnClickListener mListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.camera_cancel:
                    mRecorder.cancel();
                    break;
                case R.id.camera_next:
//                    mRec.creatVideo();
                    if(mRecorder == null || mRecorder.getVideoNum()< 1) {
                        Toast.makeText(CameraActivity.this, "请先录制一段视频!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(CameraActivity.this, VideoEditerActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.camera_switch:
                    mRecorder.switchCamera();
                    break;
                case R.id.camera_back:
                    setMoreWindow();
                    break;
                default:
                    break;
            }
        }
    };

//    @Override
//    protected void onDestroy() {
//        // TODO Auto-generated method stub
//        super.onDestroy();
//        if(mRecorder != null) {
//            mRecorder.cancelAll();
//        }
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setMoreWindow();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    protected void setMoreWindow() {
        // TODO Auto-generated method stub
        View convertView = LayoutInflater.from(this).inflate(R.layout.dialog,
        null);
        if(mRecorder == null || mRecorder.getVideoNum()< 1) {
            finish();
            return;
        }
        Dialog dialog = new AlertDialog.Builder(CameraActivity.this)
                .setMessage("确定要丢弃当前视频?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        deleteCache();
                        finish();
                    }

                }).setNegativeButton("取消", null).create();
        dialog.show();
    }

    private void deleteCache() {
        File file = MyFileManager.getVideoCacheFile(CameraActivity.this, "1.mp4");
        MyFileManager.deleteFiles(file.getParentFile());
    }
}
