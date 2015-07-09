package com.newthread.shiquan.ui;

import java.io.File;
import java.io.FileDescriptor;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.newthread.shiquan.R;
import com.newthread.shiquan.adapter.ReviewListAdapter;
import com.newthread.shiquan.adapter.SearchFriendsAdapter;
import com.newthread.shiquan.bean.ReportDate;
import com.newthread.shiquan.bean.UserData;
import com.newthread.shiquan.bean.VideoDataV2;
import com.newthread.shiquan.dao.DataParser;
import com.newthread.shiquan.dao.MyRequest;
import com.newthread.shiquan.dao.NoticUtil;
import com.newthread.shiquan.dao.PersonalDataUtil;
import com.newthread.shiquan.dao.VideoManagerUtil;
import com.newthread.shiquan.media.MyMediaPlayer;
import com.newthread.shiquan.utils.PhoneInfo;
import com.newthread.shiquan.utils.SharedPreferencesManager;
import com.newthread.shiquan.widget.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.viewpagerindicator.TabPageIndicator;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class VideodetailActivity extends Activity {
    private final static String TAG = "VideodetailActivity";

    private ListView reviewList;
    private EditText reviewEdt;

    private SharedPreferencesManager SPManager;

    private MyMediaPlayer mediaPlayer;
    private VideoView videoView = new VideoView();
    private TextView titleText;
    private Button viewCode;
    private Button reviewBtn;
    private Button exit;
    private ReviewListAdapter rAdapter;
    private String strVideoPath = "";
    private String topic = "";
    private String id = "";
    private String title = "";
    private VideoDataV2 videoDataV2;
    private ArrayList<UserData> userList;
    private ArrayList<HashMap<String, String>> list;

    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        topic = intent.getStringExtra("TOPIC");
        id = intent.getStringExtra("ID");
        SPManager = new SharedPreferencesManager(VideodetailActivity.this);
        setContentView(R.layout.activity_videodetail);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        strVideoPath = "/storage/sdcard0/视频/1.mp4";
        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
        reviewList = (ListView) findViewById(R.id.videodetail_review_listview);
        reviewEdt = (EditText) findViewById(R.id.videodetail_review_edt);

        View convertView = LayoutInflater.from(this).inflate(
                R.layout.header_videodetail, null);
        videoView.userIcon = (CircularImage) convertView
                .findViewById(R.id.header_videodetail_user_icon);
        videoView.userName = (TextView) convertView
                .findViewById(R.id.header_videodetail_user_name);
        videoView.time = (TextView) convertView
                .findViewById(R.id.header_videodetail_time);
        videoView.location = (TextView) convertView
                .findViewById(R.id.header_videodetail_location);
        videoView.progressBar = (ProgressBar) convertView.findViewById(R.id.header_videodetail_progress);
        videoView.videoSurface = (SurfaceView) convertView
                .findViewById(R.id.header_videodetail_video);
        videoView.videoCover = (ImageView) convertView
                .findViewById(R.id.header_videodetail_videocover);
        videoView.videoBtn = (Button) convertView
                .findViewById(R.id.header_videodetail_video_btn);
        videoView.videoTitle = (TextView) convertView
                .findViewById(R.id.header_videodetail_text);
        videoView.interestNum = (TextView) convertView
                .findViewById(R.id.header_videodetail_interest_num);
        videoView.reviewNum = (TextView) convertView
                .findViewById(R.id.header_videodetail_review_num);
        videoView.moreBtn = (TableRow) convertView
                .findViewById(R.id.header_videodetail_more_btn);
        titleText = (TextView) findViewById(R.id.videodetail_title);
        viewCode = (Button) findViewById(R.id.videodetail_view_code);
        reviewBtn = (Button) findViewById(R.id.videodetail_review_btn);
        exit = (Button) findViewById(R.id.videodetail_back);
        reviewBtn.setOnClickListener(mListener);
        exit.setOnClickListener(mListener);
        if (topic != null && topic.equals(getResources().getString(R.string.topic_5))) {
            viewCode.setVisibility(View.VISIBLE);
        }
        viewCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideodetailActivity.this, CodeActivity.class);
                startActivity(intent);
            }
        });

        videoView.moreBtn.setOnClickListener(mListener);
        videoView.userIcon.setOnClickListener(mListener);
        reviewList.addHeaderView(convertView);

        list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("TIME", "1分钟前");
        hashMap.put("TALK", "歌很好听，棒棒哒");
        list.add(hashMap);

        hashMap = new HashMap<String, String>();
        hashMap.put("TIME", "1分钟前");
        hashMap.put("TALK", "一个人在武汉");
        list.add(hashMap);

        hashMap = new HashMap<String, String>();
        hashMap.put("TIME", "2分钟前");
        hashMap.put("TALK", "好听，是什么歌");
        list.add(hashMap);

        hashMap = new HashMap<String, String>();
        hashMap.put("TIME", "3分钟前");
        hashMap.put("TALK", "为梦想加油");
        list.add(hashMap);

        hashMap = new HashMap<String, String>();
        hashMap.put("TIME", "5分钟前");
        hashMap.put("TALK", "感触很深");
        list.add(hashMap);
        hashMap = new HashMap<String, String>();
        hashMap.put("TIME", "7分钟前");
        hashMap.put("TALK", "回忆");
        list.add(hashMap);
        hashMap = new HashMap<String, String>();
        hashMap.put("TIME", "14分钟前");
        hashMap.put("TALK", "求互粉");
        list.add(hashMap);

        hashMap = new HashMap<String, String>();
        hashMap.put("TIME", "20分钟前");
        hashMap.put("TALK", "为了生活，大家都不容易");
        list.add(hashMap);


        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.icon_loading).cacheInMemory()
                .cacheOnDisc().build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        reviewList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String name = rAdapter.getList().get(position + 1).getNickName();
                reviewEdt.setText("回复" + name + "：");
                reviewEdt.setSelection(reviewEdt.getText().length());

                reviewEdt.setFocusable(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        reviewList.setOnScrollListener(new MyOnScrollListener());
        getVideo();
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
                    Toast.makeText(VideodetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showList() {
        rAdapter = new ReviewListAdapter(this, userList, list);
        reviewList.setAdapter(rAdapter);
    }

    private void getVideo() {
        if (id == null || id.equals("")) {
            Toast.makeText(this, "无效的视频ID", Toast.LENGTH_SHORT).show();
            return;
        }
        VideoManagerUtil.getVideo(id, new MyRequest.IRequestListener() {
            @Override
            public void onComplete(int result, String msg, Object obj) {
                if (result == HttpStatus.SC_OK) {
                    String content = null;
                    try {
                        content = URLDecoder.decode(new String((byte[]) obj, "UTF-8"));
                        Log.v(TAG, content);
                        videoDataV2 = new VideoDataV2();
                        videoDataV2.parser(new JSONObject(content));
                        setView(videoDataV2);
                        VideoManagerUtil.getVideoFile(videoDataV2.getStoragePath(), videoView.progressBar, new VideoManagerUtil.IRequestListener() {
                            @Override
                            public void onComplete(int result, String msg, File file) {
                                videoView.progressBar.setVisibility(View.GONE);
                                setMedia(file);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(VideodetailActivity.this, TAG + ":" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setView(VideoDataV2 videoDataV2) {
        titleText.setText(videoDataV2.getPublishUserNickName() + "的视频");
        videoView.userName.setText(videoDataV2.getPublishUserNickName());
        videoView.time.setText(videoDataV2.getPublishTime());
        videoView.location.setText(videoDataV2.getCurrentLocation());
        videoView.videoTitle.setText(videoDataV2.getVideoTitle());
        videoView.interestNum.setText("" + videoDataV2.getPraiseAmount());
        videoView.reviewNum.setText("" + videoDataV2.getCommentAmount());
        imageLoader.displayImage(videoDataV2.getHeadPortraitAddress(), videoView.userIcon, options);
        imageLoader.displayImage(videoDataV2.getCoverAddress(), videoView.videoCover, options);
    }

    private void setMedia(File file) {
        mediaPlayer = new MyMediaPlayer(this, videoView.videoSurface, videoView.videoBtn, true);
        mediaPlayer.setPlayStateListener(new MyMediaPlayer.IPlayStateListener() {
            @Override
            public void pause() {

            }

            @Override
            public void start() {
                videoView.videoCover.setVisibility(View.GONE);
            }
        });
        if (SPManager.readWifiAutoPlay() && PhoneInfo.isWifiConnected(this)) {
            mediaPlayer.setAutoPlay(true);
        }
        mediaPlayer.setLooping(true);
        try {
//            AssetFileDescriptor aFD = getAssets().openFd("1.3gp");
//            FileDescriptor fileDescriptor = aFD.getFileDescriptor();
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepareAsync();
//            aFD.close();
        } catch (Exception e) {
        }
    }

    private class MyOnScrollListener implements OnScrollListener {

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            // TODO Auto-generated method stub
            Log.v(TAG, "firstVisibleItem:" + firstVisibleItem + ";"
                    + "visibleItemCount:" + visibleItemCount + ";"
                    + "totalItemCount:" + totalItemCount + ";");
            if (mediaPlayer != null && firstVisibleItem > 0) {
                mediaPlayer.pause();
            } else if (mediaPlayer != null && !mediaPlayer.isUserPause()
                    && firstVisibleItem <= 0) {
                mediaPlayer.userStart();
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // TODO Auto-generated method stub
            Log.v(TAG, "scrollState:" + scrollState + ";");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void setMoreWindow() {
        // TODO Auto-generated method stub
        View convertView = LayoutInflater.from(this).inflate(R.layout.dialog,
                null);
        AlertDialog dialog = new AlertDialog.Builder(VideodetailActivity.this)
                .setView(convertView).create();
        Button shareFriends = (Button) convertView.findViewById(R.id.dialog_share_friend);
        Button shareQzone = (Button) convertView.findViewById(R.id.dialog_share_qzone);
        Button shareWeixin = (Button) convertView.findViewById(R.id.dialog_share_weixin);
        Button shareReport = (Button) convertView.findViewById(R.id.dialog_share_report);
        dialog.getWindow().setDimAmount(0f);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.dialog_style); // 添加动画
        dialog.show();
        shareFriends.setOnClickListener(mListener);
        shareQzone.setOnClickListener(mListener);
        shareWeixin.setOnClickListener(mListener);
        shareReport.setOnClickListener(mListener);
    }

    private OnClickListener mListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.videodetail_back:
                    finish();
                case R.id.header_videodetail_more_btn:
                    setMoreWindow();
                    break;
                case R.id.header_videodetail_user_icon:
                    Intent intent = new Intent(VideodetailActivity.this,
                            PersonalpageActivity.class);
                    startActivity(intent);
                    break;
                case R.id.videodetail_review_btn:
                    Toast.makeText(VideodetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                    reviewEdt.setText("");
                case R.id.dialog_share_friend:
                    break;
                case R.id.dialog_share_qzone:
                    break;
                case R.id.dialog_share_weixin:
                    break;
                case R.id.dialog_share_report:
                    break;
                default:
            }
        }
    };

    private void report() {
        ReportDate reportDate = new ReportDate("1010101010", "333333333", "汉子", PhoneInfo.getTime("yyyy-MM-dd HH:mm:ss"));
        NoticUtil.report(reportDate, new MyRequest.IRequestListener() {
            @Override
            public void onComplete(int result, String msg, Object obj) {
                if (result == HttpStatus.SC_OK) {
                    String content = new String((byte[]) obj);
                    Log.v(TAG, new String((byte[]) obj));
                    Toast.makeText(VideodetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VideodetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class VideoView {
        public CircularImage userIcon;
        public TextView userName;
        public TextView time;
        public TextView location;
        public TextView videoTitle;
        public ImageView videoCover;
        public SurfaceView videoSurface;
        public ProgressBar progressBar;
        public Button videoBtn;
        public TextView interestNum;
        public TextView reviewNum;
        public TableRow moreBtn;
    }
}
