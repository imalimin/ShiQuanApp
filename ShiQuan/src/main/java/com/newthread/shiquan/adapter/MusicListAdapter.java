package com.newthread.shiquan.adapter;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.newthread.shiquan.R;

import java.io.FileDescriptor;

/**
 * Created by lanqx on 2014/8/5.
 */
public class MusicListAdapter extends BaseAdapter {
    private Context context;
    private String[] files;
    private Button play;
    private MediaPlayer mediaPlayer;

    public MusicListAdapter(Context context, String[] files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return files.length;
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
        convertView = LayoutInflater.from(context).inflate(
                R.layout.item_music, null);
        TextView text = (TextView) convertView.findViewById(R.id.item_music_name);
        play = (Button)convertView.findViewById(R.id.item_music_play);
        final String name = files[position];
        text.setText(name.substring(0, name.lastIndexOf(".")));
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play.setBackgroundResource(R.drawable.pause);
                playMusic(name);
            }
        });
        return convertView;
    }

    private void playMusic(String audio) {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
        try {
            AssetFileDescriptor aFD = context.getAssets().openFd("music/" + audio);
            FileDescriptor fileDescriptor = aFD.getFileDescriptor();
            mediaPlayer.setDataSource(fileDescriptor);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
        }
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mediaPlayer.start();
//            }
//        });
    }
}
