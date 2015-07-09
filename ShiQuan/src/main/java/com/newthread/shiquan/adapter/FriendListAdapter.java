package com.newthread.shiquan.adapter;

import java.io.File;
import java.io.FileDescriptor;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.newthread.shiquan.R;
import com.newthread.shiquan.bean.VideoDataV2;
import com.newthread.shiquan.dao.MyRequest;
import com.newthread.shiquan.dao.VideoManagerUtil;
import com.newthread.shiquan.media.MyMediaPlayer;
import com.newthread.shiquan.ui.PersonalpageActivity;
import com.newthread.shiquan.ui.VideodetailActivity;
import com.newthread.shiquan.utils.PhoneInfo;
import com.newthread.shiquan.utils.SharedPreferencesManager;
import com.newthread.shiquan.widget.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendListAdapter extends BaseAdapter {
    private final static String TAG = "FriendListAdapter";
    private Context context;
    private ViewHolder holder;

    private PullToRefreshListView listView;
    private SharedPreferencesManager SPManager;

    private MyMediaPlayer mediaPlayer;
    public boolean canStart = false;

    private ArrayList<VideoDataV2> list;
    private ArrayList<VideoView> videoViewList;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public FriendListAdapter(Context context, ArrayList<VideoDataV2> list) {
        this.context = context;
        this.list = list;
        this.videoViewList = new ArrayList<VideoView>();
        this.options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.icon_loading).cacheInMemory()
                .cacheOnDisc().build();
        this.imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        SPManager = new SharedPreferencesManager(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        holder = null;
        Log.v(TAG, "getView:" + position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_friends, null);
            holder.userIcon = (CircularImage) convertView
                    .findViewById(R.id.item_friends_user_icon);
            holder.userName = (TextView) convertView
                    .findViewById(R.id.item_friends_user_name);
            holder.shareLocation = (TextView) convertView
                    .findViewById(R.id.item_friends_location);
            holder.shareTime = (TextView) convertView
                    .findViewById(R.id.item_friends_time);
            holder.shareTime = (TextView) convertView
                    .findViewById(R.id.item_friends_time);
            holder.text = (TextView) convertView
                    .findViewById(R.id.item_friends_text);
            holder.interestBtn = (TableRow) convertView
                    .findViewById(R.id.item_friends_interest_btn);
            holder.interestCheck = (CheckBox) convertView
                    .findViewById(R.id.item_friends_interest_check);
            holder.interestNum = (TextView) convertView
                    .findViewById(R.id.item_friends_interest_num);
            holder.reviewBtn = (TableRow) convertView
                    .findViewById(R.id.item_friends_review_btn);
            holder.reviewtNum = (TextView) convertView
                    .findViewById(R.id.item_friends_review_num);
            holder.shareBtn = (TableRow) convertView
                    .findViewById(R.id.item_friends_more_btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position >= videoViewList.size()) {
            ImageView cacheImg = (ImageView) convertView
                    .findViewById(R.id.item_friends_videocover);
            SurfaceView videoSurface = (SurfaceView) convertView
                    .findViewById(R.id.item_friends_video);
            Button videoBtn = (Button) convertView
                    .findViewById(R.id.item_friends_video_btn);
            videoBtn.setOnClickListener(mListener);
            ProgressBar progressBar = (ProgressBar) convertView
                    .findViewById(R.id.item_friends_progress);
            videoViewList.add(new VideoView(cacheImg, videoSurface, videoBtn, progressBar));
        }


        holder.userIcon.setBackgroundResource(R.drawable.icon_user_green);
        holder.userName.setText(list.get(position).getPublishUserNickName());
        holder.shareLocation.setText(list.get(position).getCurrentLocation());
        holder.shareTime.setText(list.get(position).getPublishTime());
        holder.text.setText(list.get(position).getVideoTitle());
        holder.interestNum.setText("" + list.get(position).getPraiseAmount());
        holder.reviewtNum.setText("" + list.get(position).getCommentAmount());
        imageLoader.displayImage(list.get(position).getCoverAddress(), videoViewList.get(position).cacheImg, options);
        imageLoader.displayImage(list.get(position).getHeadPortraitAddress(), holder.userIcon, options);

        holder.interestBtn.setOnClickListener(mListener);
        holder.reviewBtn.setOnClickListener(mListener);
        holder.shareBtn.setOnClickListener(mListener);
        holder.userIcon.setOnClickListener(mListener);
        return convertView;
    }

    private void reset(int pos) {
//        mediaPlayer
    }

    public void getVideo(final int position) {
        releaseMedia();
        if (list.size() == 0) return;
        if (list.get(position).getVideoID() == null || list.get(position).getVideoID().equals("")) {
            Toast.makeText(context, "无效的视频ID", Toast.LENGTH_SHORT).show();
            return;
        }
        VideoManagerUtil.getVideoFile(list.get(position).getStoragePath(), videoViewList.get(position).progressBar, new VideoManagerUtil.IRequestListener() {
            @Override
            public void onComplete(int result, String msg, File file) {
                videoViewList.get(position).progressBar.setVisibility(View.GONE);
                setMedia(position, file);
            }
        });
    }

    public void setMedia(final int position, File file) {
        Log.v(TAG, "setMediaRes");
        mediaPlayer = new MyMediaPlayer(context, videoViewList.get(position).videoSurface, videoViewList.get(position).videoBtn, true);
        mediaPlayer.reset();
        if (SPManager.readWifiAutoPlay() && PhoneInfo.isWifiConnected(context)) {
            mediaPlayer.setAutoPlay(true);
        }
        mediaPlayer.setPlayStateListener(new MyMediaPlayer.IPlayStateListener() {
            @Override
            public void pause() {

            }

            @Override
            public void start() {
                videoViewList.get(position).cacheImg.setVisibility(View.GONE);
            }
        });
        mediaPlayer.setLooping(true);
        try {
//            AssetFileDescriptor aFD = context.getAssets().openFd("1.3gp");
//            FileDescriptor fileDescriptor = aFD.getFileDescriptor();
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepareAsync();
//            aFD.close();
        } catch (Exception e) {
        }
        notifyDataSetChanged();
    }

    public View.OnClickListener mListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_friends_user_icon:
                    Intent intent = new Intent(context, PersonalpageActivity.class);
                    context.startActivity(intent);
                case R.id.item_friends_interest_btn:
                    break;
                case R.id.item_friends_review_btn:
                    break;
                case R.id.item_friends_more_btn:
                    setMoreWindow();
                    break;
                case R.id.item_friends_video_btn:
                    if(holder.progressBar == null) return;
                    holder.progressBar.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    // 定义一个内部类来管理条目中的子组件​
    public static class ViewHolder {
        public CircularImage userIcon;
        public TextView userName;
        public TextView shareLocation;
        public TextView shareTime;
        public ImageView cacheImg;
        public SurfaceView videoSurface;
        public Button videoBtn;
        public ProgressBar progressBar;
        public TextView text;
        public TableRow interestBtn;
        public CheckBox interestCheck;
        public TextView interestNum;
        public TableRow reviewBtn;
        public TextView reviewtNum;
        public TableRow shareBtn;
    }

    public static class VideoView {
        public ImageView cacheImg;
        public SurfaceView videoSurface;
        public Button videoBtn;
        public ProgressBar progressBar;

        public VideoView(ImageView cacheImg, SurfaceView videoSurface, Button videoBtn, ProgressBar progressBar) {
            this.cacheImg = cacheImg;
            this.videoSurface = videoSurface;
            this.videoBtn = videoBtn;
            this.progressBar = progressBar;
        }
    }

    public void releaseMedia() {
        Log.v(TAG, "releaseMedia");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void surfaceCreated() {
        if (mediaPlayer != null) {
            mediaPlayer.surfaceCreated(null);
        }
    }

    public void mediaPause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void mediaStart() {
        if (mediaPlayer != null && !mediaPlayer.isUserPause()) {
            mediaPlayer.start();
        }
    }

    public boolean isCanStart() {
        return canStart;
    }

    public void setCanStart(boolean canStart) {
        this.canStart = canStart;
    }

    protected void setMoreWindow() {
        // TODO Auto-generated method stub
        View convertView = LayoutInflater.from(context).inflate(
                R.layout.dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(
                convertView).create();
        dialog.getWindow().setDimAmount(0f);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.dialog_style); // 添加动画
        dialog.show();
    }

    public void setList(ArrayList<VideoDataV2> list) {
        this.list = list;
    }

    public void updata() {
        Log.v(TAG, "updata");
        notifyDataSetChanged();
    }
}