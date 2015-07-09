package com.newthread.shiquan.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.newthread.shiquan.R;
import com.newthread.shiquan.bean.VideoDate;
import com.newthread.shiquan.dao.MyRequest;
import com.newthread.shiquan.dao.VideoRecorderUtil;
import com.newthread.shiquan.location.MyLocation;
import com.newthread.shiquan.utils.ImageUtil;
import com.newthread.shiquan.utils.PhoneInfo;
import com.newthread.shiquan.utils.SharedPreferencesManager;

import org.apache.http.HttpStatus;

import java.io.File;

/**
 * Created by lanqx on 2014/8/1.
 */
public class PublicVideo extends FragmentActivity {
    private final static String TAG = "PublicVideo";
    private static final String THUMBNAILS = ".thumbnails/";
    private final static int WIDTH = 256;
    private final static int HEIGHT = 256;

    private Button exit;
    private ImageView videoImg;
    private EditText videoEdt;
    private TextView location;
    private Button shareToQQ;
    private Button shareToWeiXin;
    private Button share;
    private String filePath = "";
    private File file;

    private EditText api;
    private TextView log;

    private SharedPreferencesManager SPManager;
    private MyRequest myRequest = MyRequest.getInstance();

    private MyLocation myLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SPManager = new SharedPreferencesManager(this);
        setContentView(R.layout.activity_publicvideo);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            showMoreWindow("视频文件已丢失!");
        }
        filePath = bundle.getString("FILE");
        file = new File(filePath);
        Log.v(TAG, filePath);
        initView();
    }

    private void initView() {
        exit = (Button) findViewById(R.id.publicvideo_back);
        videoImg = (ImageView) findViewById(R.id.publicvideo_img);
        videoEdt = (EditText) findViewById(R.id.publicvideo_edt);
        location = (TextView)findViewById(R.id.publicvideo_location_text);
        shareToQQ = (Button) findViewById(R.id.publicvideo_share_qq);
        shareToWeiXin = (Button) findViewById(R.id.publicvideo_share_weixin);
        share = (Button) findViewById(R.id.publicvideo_share);
        api = (EditText) findViewById(R.id.publicvideo_api);
        log = (TextView) findViewById(R.id.publicvideo_text);

        videoImg.setImageBitmap(ImageUtil.getBitmap(getThumPath(file)));
        shareToQQ.setOnClickListener(mListener);
        shareToWeiXin.setOnClickListener(mListener);
        share.setOnClickListener(mListener);
        exit.setOnClickListener(mListener);

        myLocation = new MyLocation(getApplicationContext(), location);
        myLocation.start();
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.publicvideo_back:
                    finish();
                    break;
                case R.id.publicvideo_share_qq:
                    break;
                case R.id.publicvideo_share_weixin:
                    break;
                case R.id.publicvideo_share:
                    updataVideo();
                    break;
                default:
            }
        }
    };

    private void updataVideo() {
        VideoDate videoDate = new VideoDate(filePath, getThumPath(file), videoEdt.getText().toString(),
                SPManager.readQQOpenid(), PhoneInfo.getTime("yyyy-MM-dd HH:mm:ss"), location.getText().toString());
        VideoRecorderUtil.saveVideo(videoDate, new MyRequest.IRequestListener() {
            @Override
            public void onComplete(int result, String msg, Object obj) {
                if (result == HttpStatus.SC_OK) {
                    String content = new String((byte[]) obj);
                    Log.v(TAG, content);
                    Toast.makeText(PublicVideo.this, msg, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("result", result);
                    setResult(1001, intent);
                    finish();
                } else {
                    Toast.makeText(PublicVideo.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void showMoreWindow(String msg) {
        // TODO Auto-generated method stub
        Dialog dialog = new AlertDialog.Builder(PublicVideo.this)
                .setMessage(msg)
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        finish();
                    }

                }).create();
        dialog.show();
    }

    private String getThumPath(File file) {
        return file.getParent() + "/" + THUMBNAILS + file.getName() + ".png";
    }
}
