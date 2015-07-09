package com.newthread.shiquan.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.newthread.shiquan.R;
import com.newthread.shiquan.adapter.DraftboxAdapter;
import com.newthread.shiquan.media.MyMediaPlayer;
import com.newthread.shiquan.utils.MyFileManager;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by lanqx on 2014/7/30.
 */
public class DraftboxActivity extends FragmentActivity {
    private Button exit;
    private Button switchBtn;
    private ListView listView;
    private SurfaceView videoSurface;
    private Button videoBtn;
    private DraftboxAdapter adapter;
    private MyMediaPlayer mediaPlayer;
    private File curFile;
    private boolean isDraft = true;

    private boolean isShowD = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draftbox);
        initView();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.draftbox_listview);
        exit = (Button) findViewById(R.id.draftbox_back);
        switchBtn = (Button) findViewById(R.id.draftbox_switch);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(isDraft) {
                    switchBtn.setText("未完成");
                    curFile = new File(MyFileManager.getUserCompletedBox(DraftboxActivity.this));
                    isDraft = false;
                }else {
                    switchBtn.setText("已完成");
                    curFile = new File(MyFileManager.getUserDragftbox(DraftboxActivity.this));
                    isDraft = true;
                }
                setListView(curFile);
            }
        });
        curFile = new File(MyFileManager.getUserDragftbox(DraftboxActivity.this));
        adapter = new DraftboxAdapter(DraftboxActivity.this, new File[0]);
        listView.setAdapter(adapter);
        setListView(curFile);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ((Vibrator) getApplication().getSystemService(
                        VIBRATOR_SERVICE)).vibrate(new long[]{0, 50}, -1);
                setMoreWindow(adapter.getFiles()[position]);
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isShowD) {
                    setVideoWindow(adapter.getFiles()[position]);
                }
            }
        });
    }

    private void setListView(File file) {
        File[] files = fitFile(file.listFiles());
        adapter.setFiles(files);
        adapter.updata();
//        adapter = new DraftboxAdapter(DraftboxActivity.this, files);
//        listView.setAdapter(adapter);
    }

    private void setVideoWindow(File file) {
        // TODO Auto-generated method stub
        if (!file.exists()) {
            Toast.makeText(DraftboxActivity.this, "文件不存在!", Toast.LENGTH_SHORT).show();
            return;
        }
        View convertView = LayoutInflater.from(this).inflate(R.layout.dialog_video,
                null);
        videoSurface = (SurfaceView) convertView
                .findViewById(R.id.dialog_video);
        videoBtn = (Button) convertView
                .findViewById(R.id.dialog_video_btn);
        Dialog dialog = new AlertDialog.Builder(DraftboxActivity.this)
                .setView(convertView)
                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                }).create();
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setDimAmount(0f);
        mediaPlayer = new MyMediaPlayer(this, videoSurface, videoBtn, true);
        mediaPlayer.setScalW(0.8);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setLooping(true);
        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
        }
        dialog.show();
    }

    private void setMoreWindow(final File file) {
        isShowD = true;
        Dialog dialog = new AlertDialog.Builder(DraftboxActivity.this)
                .setMessage(file.getName())
                .setPositiveButton("编辑", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(DraftboxActivity.this, VideoEditerActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("FILE", file.getAbsolutePath());
                        intent.putExtras(bundle);
                        startActivity(intent);
                        isShowD = false;
                    }
                }).setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if(isDraft) {
                            MyFileManager.getUserDragftboxThumbnailsFile(DraftboxActivity.this,
                                    file.getName()).delete();
                        }else {
                            MyFileManager.getUserCompletedBoxThumbnailsFile(DraftboxActivity.this,
                                    file.getName()).delete();
                        }
                        file.delete();
                        setListView(curFile);
                        isShowD = false;
                    }
                }).create();
        dialog.show();
    }

    private File[] fitFile(File[] files) {
        ArrayList<File> list = new ArrayList<File>();
        for (File file : files) {
            if (file.isFile())
                list.add(file);
        }
        File[] files1 = new File[list.size()];
        for (int i = 0; i < list.size(); i++) {
            files1[i] = list.get(i);
        }
        return files1;
    }
}
