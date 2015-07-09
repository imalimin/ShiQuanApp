package com.newthread.shiquan.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newthread.shiquan.R;
import com.newthread.shiquan.bean.VideoDataV2;
import com.newthread.shiquan.dao.MyRequest;
import com.newthread.shiquan.dao.ShakeListener;
import com.newthread.shiquan.dao.ShakeListener.OnShakeListener;
import com.newthread.shiquan.dao.VideoManagerUtil;
import com.newthread.shiquan.utils.SharedPreferencesManager;
import com.newthread.shiquan.widget.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

public class ShakeActivity extends Activity {
    private static final String TAG = "ShakeActivity";

    private ShakeListener shakeListener;
    private Vibrator mVibrator;

    private ImageView cacheImg;
    private TextView titleText;
    private CircularImage userIcon;
    private TextView userName;

    private Button exit;
    private LinearLayout shakeLayout;
    private LinearLayout resultLayout;
    private ProgressDialog progressDialog;
    private SharedPreferencesManager SPManager;
    private VideoDataV2 videoDataV2;

    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SPManager = new SharedPreferencesManager(ShakeActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);

        initView();
    }

    private void initView() {
        cacheImg = (ImageView) findViewById(R.id.shake_video_img);
        userIcon = (CircularImage) findViewById(R.id.shake_video_user_icon);
        userName = (TextView) findViewById(R.id.shake_video_user_name);
        titleText = (TextView) findViewById(R.id.shake_video_text);
        shakeLayout = (LinearLayout) findViewById(R.id.shake_img_layout);
        resultLayout = (LinearLayout) findViewById(R.id.shake_result_layout);
        shakeLayout.setVisibility(View.VISIBLE);
        resultLayout.setVisibility(View.GONE);
        exit = (Button) findViewById(R.id.shake_back);
        cacheImg.setOnClickListener(mListener);
        userIcon.setOnClickListener(mListener);
        exit.setOnClickListener(mListener);
        shakeListener = new ShakeListener(this);
        shakeListener.setOnShakeListener(new OnShakeListener() {

            @Override
            public void onShake() {
                // TODO Auto-generated method stub
//				Toast.makeText(ShakeActivity.this, "检测到摇晃，执行操作！",
//						Toast.LENGTH_SHORT).show();
//                Log.v(TAG, "isGetting:" + isGetting);
//                if (isGetting) return;
//                isGetting = true;
                startVibrato();
                shakeLayout.setVisibility(View.VISIBLE);
                resultLayout.setVisibility(View.GONE);
                progressDialog.show();
                setDialogText(progressDialog.getWindow().getDecorView());
                getVideo();
            }
        });
        init();
    }

    private void init() {
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.icon_loading).cacheInMemory()
                .cacheOnDisc().build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        progressDialog = new ProgressDialog(ShakeActivity.this);
        progressDialog.setMessage("正在处理...");

        mVibrator = (Vibrator) getApplication().getSystemService(
                VIBRATOR_SERVICE);

        // sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private void setDialogText(View view) {
        if(view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                setDialogText(child);
            }
        }else if(view instanceof TextView) {
            ((TextView)view).setTextColor(getResources().getColor(R.color.black));
        }
    }

//    private boolean isGetting = false;

    private void getVideo() {
        VideoManagerUtil.shake(SPManager.readQQOpenid(), new MyRequest.IRequestListener() {
            @Override
            public void onComplete(int result, String msg, Object obj) {
                progressDialog.dismiss();
                if (result == HttpStatus.SC_OK) {
                    String content = new String((byte[]) obj);
                    Log.v(TAG, content);
                    videoDataV2 = new VideoDataV2();
                    try {
                        videoDataV2.parser(new JSONObject(content));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    isGetting = false;
                    viewVideo(videoDataV2);
                } else {
                    Toast.makeText(ShakeActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void viewVideo(VideoDataV2 videoDataV2) {
        Log.v(TAG, videoDataV2.toString());
        userName.setText(videoDataV2.getPublishUserNickName());
        titleText.setText(videoDataV2.getVideoTitle());
        imageLoader.displayImage(videoDataV2.getHeadPortraitAddress(), userIcon, options);
        imageLoader.displayImage(videoDataV2.getCoverAddress(), cacheImg, options);
        shakeLayout.setVisibility(View.GONE);
        resultLayout.setVisibility(View.VISIBLE);
    }

    public View.OnClickListener mListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.shake_back:
                    finish();
                    break;
                case R.id.shake_video_img:
                    intent = new Intent(ShakeActivity.this, VideodetailActivity.class);
                    intent.putExtra("ID", videoDataV2.getVideoID());
                    Log.v(TAG, videoDataV2.getVideoID());
                    startActivityForResult(intent, 1);
                    break;
                case R.id.shake_video_user_icon:
                    intent = new Intent(ShakeActivity.this,
                            PersonalpageActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    public void startVibrato() {
        // 定义震动
        mVibrator.vibrate(new long[]{100, 150, 300, 150}, -1); // 第一个｛｝里面是节奏数组，第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
    }

    @Override
    protected void onStop() {
        super.onStop();
        shakeListener.stop();
        // if (sensorManager != null) {// 取消监听器
        // sensorManager.unregisterListener(sensorEventListener);
        // }
        // vibrator.cancel();
    }
}
