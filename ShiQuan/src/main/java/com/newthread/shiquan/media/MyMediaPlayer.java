package com.newthread.shiquan.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

import com.newthread.shiquan.R;

public class MyMediaPlayer extends MediaPlayer implements
        SurfaceHolder.Callback, OnPreparedListener {
    private Context context;
    private SurfaceView videoSurface;
    private Button videoBtn;
    private DisplayMetrics displayMetrics;

    private SurfaceHolder mSurfaceHolder;
    private int mediaPosition;
    private int screenWidthPixels, screenHightPixels, videoWidth, videoHeight;
    private LayoutParams lp;
    private double scalW = 1;

    private boolean autoPlay = false;
    // private boolean prepared = false;
    private boolean isFirst = true;
    private boolean userPause = true;
    private boolean justBack = false;
    private boolean isRelease = false;

    private IPlayStateListener playStateListener;

    private int palyScan = 100000;
    private long historyTimeMillis = 0;
    private long currentTimeMillis = 0;

    public MyMediaPlayer(final Context context, SurfaceView videoSurface,
                         Button videoBtn, boolean canTouch) {
        reset();
        this.context = context;
        this.videoSurface = videoSurface;
        this.videoBtn = videoBtn;
        this.displayMetrics = context.getApplicationContext().getResources()
                .getDisplayMetrics();
        if (canTouch) {
            videoSurface.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        if (isPlaying()) {
                            userPause();
                        } else {
                            start();
                        }
                    } catch (IllegalStateException e) {
                        currentTimeMillis = System.currentTimeMillis();
                        if (currentTimeMillis - historyTimeMillis > 1000) {
                            Toast.makeText(context, "视频未准备完毕...", Toast.LENGTH_SHORT).show();
                        } else if (currentTimeMillis - historyTimeMillis > 700) {
                            Toast.makeText(context, "再点就要崩了！", Toast.LENGTH_SHORT).show();
                        } else if (currentTimeMillis - historyTimeMillis > 500) {
                            Toast.makeText(context, "我要崩了！", Toast.LENGTH_SHORT).show();
                        }
                        historyTimeMillis = currentTimeMillis;
                    }
                }
            });
        }
        if (this.videoBtn != null) this.videoBtn.setVisibility(View.VISIBLE);
    }

    private void setResources() {
        // TODO Auto-generated method stub

        lp = videoSurface.getLayoutParams();
        lp.width = LayoutParams.MATCH_PARENT;
        screenHightPixels = displayMetrics.heightPixels;
        screenWidthPixels = displayMetrics.widthPixels;
        lp.height = (int) (screenWidthPixels * 480 / 640 * scalW);
        videoSurface.setLayoutParams(lp);
        videoSurface.getHolder().setKeepScreenOn(true);

        mSurfaceHolder = videoSurface.getHolder();

        mSurfaceHolder.addCallback(this);
        setOnPreparedListener(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setAudioStreamType(AudioManager.STREAM_MUSIC);
        setDisplay(videoSurface.getHolder());
        setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
            }
        });
    }

    private void setVideoSize() {
        lp = videoSurface.getLayoutParams();
        lp.height = lp.width * 480 / 640;
        videoSurface.setLayoutParams(lp);
    }

    private void prepareMediaRes() {
        try {
            prepareAsync();
            Log.v("000000", "mMediaPlayer.start()");
        } catch (Exception e) {
        }
    }

    @Override
    public void start() throws IllegalStateException {
        // TODO Auto-generated method stub
        Log.v("000000", "start:" + userPause);
        if (!isPlaying()) {
            userPause = false;
            if (justBack) {
                justBack = false;
            }
            super.start();
            playState(true);
        }
    }

    public void autoStart() {
        if (autoPlay) {
            start();
        }
    }

    public void userStart() {
        if (!userPause) {
            start();
        }
    }

    @Override
    public void pause() throws IllegalStateException {
        // TODO Auto-generated method stub
        if (isPlaying()) {
            super.pause();
            playState(false);
        }
    }

    public void userPause() {
        if (isPlaying()) {
            userPause = true;
        }
        pause();
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        // TODO Auto-generated method stub
        Log.v("000000", "prepareAsync");
        setResources();
        super.prepareAsync();
    }

    @Override
    public void release() {
        isRelease = true;
        super.release();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Log.v("000000", "surfaceCreated");
        prepareMediaRes();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Log.v("000000", "surfaceDestroyed");
        Log.v("000000", "userStop:" + userPause);
        if (!isRelease) {
            if (isPlaying()) {
                justBack = true;
                // userStop = false;
                stop();
            } else {
                // userStop = true;
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // TODO Auto-generated method stub
        Log.v("000000", "onPrepared||" + "autoPlay:" + autoPlay + ";userStop:"
                + userPause);
        autoStart();
        // if (!userStop) {
        // start();
        // }
    }

    private void playState(boolean isStart) {

        if (isStart) {
            videoBtn.setBackgroundResource(R.drawable.pause);
            if (playStateListener != null) {
                playStateListener.start();
            }
        } else {
            videoBtn.setBackgroundResource(R.drawable.play);
            if (playStateListener != null) {
                playStateListener.pause();
            }
        }
    }

    public interface IPlayStateListener {
        public void pause();

        public void start();
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public void setPlayStateListener(IPlayStateListener playStateListener) {
        this.playStateListener = playStateListener;
    }

    public boolean isUserPause() {
        return userPause;
    }

    public void setUserPause(boolean userStop) {
        this.userPause = userStop;
    }

    public void setScalW(double scalW) {
        this.scalW = scalW;
    }
}
