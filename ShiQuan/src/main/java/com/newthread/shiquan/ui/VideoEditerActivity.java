package com.newthread.shiquan.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.newthread.shiquan.R;
import com.newthread.shiquan.adapter.MusicListAdapter;
import com.newthread.shiquan.media.MyMediaPlayer;
import com.newthread.shiquan.utils.ImageUtil;
import com.newthread.shiquan.utils.MyFileManager;
import com.newthread.shiquan.utils.VideoUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by LMY on 2014/7/30.
 */
public class VideoEditerActivity extends FragmentActivity {
    private final static String TAG = "VideoEditerActivity";
    private final static int WIDTH = 256;
    private final static int HEIGHT = 256;

    private SurfaceView videoSurface;
    private Button videoBtn;
    private Button exit;
    private TableRow next;
    private Button selectMusic;
    private CheckBox soundCheck;
    private ListView musicListView;
    private MusicListAdapter musicListAdapter;

    private RadioGroup editGroup;
    private RadioButton lvjingRadioButton;
    private RadioButton mvRadioButton;

    private RadioGroup lvjingRadioGroup;
    private RadioGroup mvRadioGroup;

    private ProgressDialog progressDialog;
    private File curFile;
    private String curFilePath = "";
    private File completedFile;

    private MyMediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoediter);
        Intent intent = getIntent();
        curFilePath = intent.getStringExtra("FILE");
        getWindow().setFormat(PixelFormat.UNKNOWN);
        initView();
    }

    private void initView() {
        videoSurface = (SurfaceView) findViewById(R.id.videoediter_video);
        videoBtn = (Button) findViewById(R.id.videoediter_video_btn);
        next = (TableRow) findViewById(R.id.videoediter_next);
        exit = (Button) findViewById(R.id.videoediter_back);
        selectMusic = (Button) findViewById(R.id.videoediter_select_music);
        soundCheck = (CheckBox) findViewById(R.id.videoediter_sound_checkbox);

        editGroup = (RadioGroup) findViewById(R.id.videoediter_radiogroup);
        lvjingRadioButton = (RadioButton) findViewById(R.id.videoediter_lvjing);
        mvRadioButton = (RadioButton) findViewById(R.id.videoediter_mv);

        lvjingRadioGroup = (RadioGroup) findViewById(R.id.videoediter_lvjing_radiogroup);
        mvRadioGroup = (RadioGroup) findViewById(R.id.videoediter_mv_radiogroup);
        editGroup.setOnCheckedChangeListener(mCheckedChangeListener);
        lvjingRadioGroup.setOnCheckedChangeListener(mCheckedChangeListener);
        mvRadioGroup.setOnCheckedChangeListener(mCheckedChangeListener);

        exit.setOnClickListener(mListener);
        next.setOnClickListener(mListener);
        selectMusic.setOnClickListener(mListener);
        progressDialog = new ProgressDialog(VideoEditerActivity.this);
        progressDialog.setMessage("正在生成视频...");
        if (curFilePath != null && !curFilePath.equals("")) {
            curFile = new File(curFilePath);
            completedFile = curFile;
            playVideo(curFile);
        } else {
            appendVideos();
        }
    }

    private void addAudio(String audioName) {
        progressDialog.show();
        setDialogText(progressDialog.getWindow().getDecorView());
        final File audio = MyFileManager.copyAssets(VideoEditerActivity.this, audioName, "cache.mp4");
        if (audio == null) {
            Toast.makeText(VideoEditerActivity.this, "copy失败!" + audioName, Toast.LENGTH_SHORT).show();
            return;
        }
        completedFile = MyFileManager.getUserCompletedBoxFile(VideoEditerActivity.this, curFile.getName().substring(0, curFile.getName().lastIndexOf(".")) + "_finish.mp4");
        VideoUtil.addAudio(VideoEditerActivity.this, curFile.getAbsolutePath(),
                audio.getAbsolutePath(), completedFile.getAbsolutePath(), true,
                new VideoUtil.IVideoListener() {
                    @Override
                    public void onComplete(int result) {
                        Toast.makeText(VideoEditerActivity.this, "add成功!", Toast.LENGTH_SHORT).show();
                        ImageUtil.saveBitmap(ImageUtil.getVideoThumbnail(curFile.getAbsolutePath(),
                                        WIDTH, HEIGHT, MediaStore.Images.Thumbnails.MINI_KIND),
                                MyFileManager.getUserCompletedBoxThumbnailsFile(VideoEditerActivity.this, completedFile.getName()).getAbsolutePath()
                        );
                        stopVideo();
                        audio.delete();
                        progressDialog.dismiss();
                        playVideo(completedFile);
                    }
                }
        );
    }

    private void appendVideos() {
        File[] files = MyFileManager.getVideoCacheFile(VideoEditerActivity.this, "1.mp4").getParentFile().listFiles();
        Log.v(TAG, "appendVideos:" + files.length);
        if (files.length < 1) {
//            Toast.makeText(VideoEditerActivity.this, "没有找到可处理的视频文件!", Toast.LENGTH_SHORT).show();
            setMoreWindow();
            return;
        }
        progressDialog.show();
        setDialogText(progressDialog.getWindow().getDecorView());
        String[] filePaths = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            filePaths[i] = files[i].getAbsolutePath();
            Log.v(TAG, "appendVideos:" + filePaths[i]);
        }
        curFile = MyFileManager.getUserDragftboxFile(VideoEditerActivity.this);
        completedFile = curFile;
        VideoUtil.appendVideo(VideoEditerActivity.this, filePaths, curFile.getAbsolutePath(), new VideoUtil.IVideoListener() {
            @Override
            public void onComplete(int result) {
                ImageUtil.saveBitmap(ImageUtil.getVideoThumbnail(curFile.getAbsolutePath(),
                                WIDTH, HEIGHT, MediaStore.Images.Thumbnails.MINI_KIND),
                        MyFileManager.getUserDragftboxThumbnailsFile(VideoEditerActivity.this, curFile.getName()).getAbsolutePath()
                );
                progressDialog.dismiss();
                Toast.makeText(VideoEditerActivity.this, "处理成功!", Toast.LENGTH_SHORT).show();
                playVideo(curFile);
                deleteCache();
            }
        });
    }

    private void playVideo(File file) {
        mediaPlayer = new MyMediaPlayer(this, videoSurface, videoBtn, false);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setLooping(true);
        try {
            Log.v(TAG, curFile.getAbsolutePath());
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepareAsync();
            mediaPlayer.surfaceCreated(videoSurface.getHolder());
        } catch (Exception e) {
        }
    }

    private boolean isSelected = false;

    protected void showMusicWindow() throws IOException {
        // TODO Auto-generated method stub
        mediaPlayer.pause();
        final String[] files = getAssets().list("music");
        View convertView = LayoutInflater.from(this).inflate(R.layout.dialog_music,
                null);
        musicListView = (ListView) convertView.findViewById(R.id.dialog_music_listview);
        musicListAdapter = new MusicListAdapter(VideoEditerActivity.this, files);
        musicListView.setAdapter(musicListAdapter);
        final AlertDialog dialog = new AlertDialog.Builder(VideoEditerActivity.this)
                .setView(convertView).create();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.dialog_style); // 添加动画
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!isSelected) {
                    mediaPlayer.start();
                }
                isSelected = false;
            }
        });
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addAudio("music/" + files[position]);
                isSelected = true;
                dialog.cancel();
            }
        });
    }

    protected void setMoreWindow() {
        // TODO Auto-generated method stub
        Dialog dialog = new AlertDialog.Builder(VideoEditerActivity.this)
                .setMessage("没有找到可处理的视频文件!")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        finish();
                    }

                }).create();
        dialog.show();
    }

    protected void showExitWindow() {
        // TODO Auto-generated method stub
        if(curFilePath != null) {
            stopVideo();
            finish();
            return;
        }
        Dialog dialog = new AlertDialog.Builder(VideoEditerActivity.this)
                .setMessage("是否把视频放进草稿箱？")
                .setPositiveButton("保留", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Toast.makeText(VideoEditerActivity.this, "视频已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        stopVideo();
                        finish();
                    }

                }).setNegativeButton("放弃", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        curFile.delete();
                        MyFileManager.getUserDragftboxThumbnailsFile(VideoEditerActivity.this, curFile.getName()).delete();
                        completedFile.delete();
                        MyFileManager.getUserDragftboxThumbnailsFile(VideoEditerActivity.this, completedFile.getName()).delete();
                        Toast.makeText(VideoEditerActivity.this, "视频已丢弃", Toast.LENGTH_SHORT).show();
                        stopVideo();
                        finish();
                    }

                }).create();
        dialog.show();
    }

    private void stopVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void deleteCache() {
        File file = MyFileManager.getVideoCacheFile(VideoEditerActivity.this, "1.mp4").getParentFile();
        File renameFile = new File(file.getParent() + "/" + System.currentTimeMillis());
        file.renameTo(renameFile);
        MyFileManager.deleteFiles(renameFile);
    }

    private RadioGroup.OnCheckedChangeListener mCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (group.getId()) {
                case R.id.videoediter_radiogroup:
                    if (checkedId == R.id.videoediter_lvjing) {
                        lvjingRadioGroup.setVisibility(View.VISIBLE);
                        mvRadioGroup.setVisibility(View.GONE);
                    } else if (checkedId == R.id.videoediter_mv) {
                        lvjingRadioGroup.setVisibility(View.GONE);
                        mvRadioGroup.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.videoediter_lvjing_radiogroup:
                    break;
                case R.id.videoediter_mv_radiogroup:
                    break;
            }
        }
    };

    public View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.videoediter_back:
                    showExitWindow();
                    break;
                case R.id.videoediter_next:
                    Intent intent = new Intent(VideoEditerActivity.this, PublicVideo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("FILE", completedFile.getAbsolutePath());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1000);
                    break;
                case R.id.videoediter_select_music:
                    try {
                        showMusicWindow();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitWindow();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 1001) {
            finish();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
