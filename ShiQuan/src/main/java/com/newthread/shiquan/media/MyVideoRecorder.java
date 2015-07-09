package com.newthread.shiquan.media;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.newthread.shiquan.utils.ImageUtil;
import com.newthread.shiquan.utils.MyFileManager;

public class MyVideoRecorder implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private final static String TAG = "MyVideoRecorder";

    private final int maxDurationInMs = 15 * 1000;
    private final long maxFileSizeInBytes = 30 * 1024 * 1024;
    private final int videoFramesPerSecond = 24;
    private final int videoBitRate = 512 * 1024 * 8;
    private final int audioBitRate = 384 * 1024 * 8;

    public static final int LARGEST_WIDTH = 300;
    public static final int LARGEST_HEIGHT = 300;
    private int delayedTimeForStop = 500;
    private int delayedTimeForStart = delayedTimeForStop + 1000;

    private Context mContext;
    private MediaRecorder mediaRecorder;
    private Camera mCamera;
    private SurfaceView cameraSurface;
    private SurfaceHolder surfaceHolder;
    private Parameters parameters;

    private Button button;
    private ProgressBar seekBar;

    private double focusSensitivity = 7;
    private boolean isStart = false;
    private boolean isFinish = false;
    private int videoNum = 1;
    private ArrayList<Long> times = new ArrayList<Long>();
    private long startTime = 0;
    //    private long totalMilli = 0;
    private long curTotal = 0;
    private boolean onTorch = false;
    private int cameraCount = 1;
    private boolean isBack = true;
    private boolean canStop = false;
    private boolean canStart = true;

    public MyVideoRecorder(Context mContext, SurfaceView cameraSurface, ProgressBar seekBar,
                           Button button) {
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
        this.cameraSurface = cameraSurface;
        this.seekBar = seekBar;
        this.button = button;
        this.cameraCount = Camera.getNumberOfCameras();

    }

    private OnTouchListener mListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.v(TAG, "ACTION_DOWN");
                if (!isFinish && canStart) {
                    canStart = false;
                    startRecording();
                }
                if (seekBar != null) {
                    startProgressThread();
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.v(TAG, "ACTION_UP");
                if (!isFinish) {
                    if (!canStop) {
                        stopHandler.sendEmptyMessageDelayed(1, delayedTimeForStop);
                    } else {
                        stopRecording();
                    }
                }
            }
            return false;
        }
    };

    private void setProgress() {
        if (getTotalMilli() >= maxDurationInMs) {
            isFinish = true;
            Log.v(TAG, "setProgress:stop");
//            stopRecording();
//            button.setClickable(false);
            return;
        } else {
            curTotal += 100;
            Log.v(TAG, "setProgress:" + curTotal + "|||" + getTotalMilli());
            seekBar.setProgress((int) (curTotal + getTotalMilli()) / 100);
        }
    }

    private Handler progressHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            setProgress();
        }
    };

    private Handler stopHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            canStop = true;
            stopRecording();
        }
    };

    private Handler startHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            canStart = true;
        }
    };

    private Thread mThread;

    private void startProgressThread() {
        mThread = null;
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isStart) {
                    progressHandler.sendEmptyMessage(0);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mThread.start();
    }

    private long getTotalMilli() {
        long totalMilli = 0;
        for (int i = 0; i < times.size(); i++) {
            totalMilli = totalMilli + times.get(i);
        }
        return totalMilli;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if (null == mCamera) {
            if (cameraCount >= 2) {
                if (isBack) {
                    mCamera = Camera.open();
                } else {
                    mCamera = Camera.open(1);
                }
            } else {
                mCamera = Camera.open();
            }
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                initCamera();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        mCamera.autoFocus(new AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
//                    initCamera();
//                mCamera.cancelAutoFocus();
                }
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        // 停止预览
        if (mCamera != null) {
            mCamera.setPreviewCallback(null); //！！这个必须在前，不然退出出错
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private int displayOrientation;
    private int width;
    private int height;

    public void init(int displayOrientation, int width, int height) {
        this.displayOrientation = displayOrientation;
        this.width = width;
        this.height = height;

        if (this.seekBar != null) {
            this.seekBar.setMax(maxDurationInMs / 100);
        }
        surfaceHolder = cameraSurface.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        if (this.button != null) {
            this.button.setOnTouchListener(mListener);
        }
        focusByLight();
        cameraSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCamera();
                mCamera.setOneShotPreviewCallback(MyVideoRecorder.this);
            }
        });
    }

    // 初始化摄像头
    public void initCamera() {
        parameters = mCamera.getParameters();
        if (onTorch) {
            parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
        }
        setOrientation(parameters);
        bestSize(parameters);
//        mCamera.setDisplayOrientation(displayOrientation);
//        parameters.setPreviewSize(width, height);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
        mCamera.getParameters().setFocusMode(Parameters.FOCUS_MODE_AUTO);
        mCamera.autoFocus(new AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
//                    initCamera();
                }
            }
        });
//        mCamera.cancelAutoFocus();// 要自动对焦必须执行这个
    }

    private void setOrientation(Parameters parameters) {
        if (mContext.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            parameters.set("orientation", "portrait");
            mCamera.setDisplayOrientation(90);//针对android2.2和之前的版本
            parameters.setRotation(90);//去掉android2.0和之前的版本
        } else {
            parameters.set("orientation", "landscape");
            mCamera.setDisplayOrientation(0);
            parameters.setRotation(0);
        }
    }

    private void bestSize(Parameters parameters) {
        int bestWidth = 0;
        int bestHeight = 0;
        List<Camera.Size> previewSizes = parameters.getSupportedPictureSizes();
        if (previewSizes.size() > 1) {
            Iterator<Camera.Size> cei = previewSizes.iterator();
            while (cei.hasNext()) {
                Camera.Size aSize = cei.next();
                System.out.println(aSize.width + "," + aSize.height);
                if (aSize.width > bestWidth && aSize.width <= width
                        && aSize.height > bestHeight && aSize.height <= height) {
                    bestWidth = aSize.width;
                    bestHeight = aSize.height;
                }
                if (bestHeight != 0 && bestWidth != 0) {
                    Log.v("SNAPSHOT", "Using" + bestWidth + "x" + bestHeight);
                    parameters.setPreviewSize(bestWidth, bestHeight);
//                    cameraSurface.setLayoutParams(new LinearLayout.LayoutParams(bestWidth, bestHeight));
                }
            }
        }
    }

    public boolean startRecording() {
        try {
//            if (!MyFileManager.cacheFilecanW(mContext)) {
//                Toast.makeText(mContext, "文件不可读写!", Toast.LENGTH_SHORT).show();
//                return false;
//            }
            mCamera.unlock();
            mediaRecorder = new MediaRecorder();
            mediaRecorder.reset();
            mediaRecorder.setCamera(mCamera);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            mediaRecorder.setOrientationHint(0);// 视频旋转90度
            mediaRecorder.setVideoFrameRate(videoFramesPerSecond);
            mediaRecorder.setVideoSize(640, 480);

            mediaRecorder.setMaxDuration(maxDurationInMs);
            mediaRecorder.setMaxFileSize(maxFileSizeInBytes);

            // CamcorderProfile camcorderProfile =
            // CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
            // mediaRecorder.setProfile(camcorderProfile);//构造CamcorderProfile，使用高质量视频录制
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
            mediaRecorder.setVideoEncodingBitRate(videoBitRate);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setAudioEncodingBitRate(audioBitRate);

            mediaRecorder.setOutputFile(MyFileManager.getVideoCacheFile(mContext,
                    videoNum + ".mp4").getAbsolutePath());
            mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

            mediaRecorder.prepare();

            mediaRecorder.start();
            isStart = true;
            return true;
        } catch (IllegalStateException e) {
            // Log.v(TAG, e.getMessage());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // Log.v(TAG, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void stopRecording() {
        Log.v(TAG, "isStart:" + isStart);
        startHandler.sendEmptyMessageDelayed(1, delayedTimeForStart);
        if (!canStop) return;
        if (isStart) {
            videoNum++;
            times.add(curTotal);
            curTotal = 0;
            try {
                mediaRecorder.stop();
            } catch (RuntimeException e) {

            }
            mCamera.lock();
            isStart = false;
        }
    }

    public void switchCamera() {
        Log.v(TAG, "cameraCount:" + cameraCount + ";" + isBack);
        if (cameraCount >= 2) {
            if (isBack) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(1, cameraInfo);//得到每一个摄像头的信息
                //现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    Log.v(TAG, "CAMERA_FACING_FRONT");
                    mCamera.stopPreview();//停掉原来摄像头的预览
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头
                    mCamera = Camera.open(1);//打开当前选中的摄像头

                    try {
                        mCamera.setPreviewDisplay(surfaceHolder);//通过surfaceview显示取景画面
                        initCamera();
                        isBack = false;
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } else {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(0, cameraInfo);//得到每一个摄像头的信息
                //现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    Log.v(TAG, "CAMERA_FACING_BACK");
                    mCamera.stopPreview();//停掉原来摄像头的预览
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头
                    mCamera = Camera.open(0);//打开当前选中的摄像头
                    try {
                        mCamera.setPreviewDisplay(surfaceHolder);//通过surfaceview显示取景画面
                        initCamera();
                        isBack = true;
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public void cancel() {
        if (videoNum > 1) {
            videoNum = videoNum - 1;
            times.remove(videoNum - 1);
            File file = MyFileManager.getVideoCacheFile(mContext, videoNum + ".mp4");
            if (file.exists()) {
                file.delete();
            }
            if (seekBar != null)
                seekBar.setProgress((int) (getTotalMilli()) / 100);
            isFinish = false;
            curTotal = 0;
            setProgress();
//            seekBar.setProgress((int) getTotalMilli() / 100);
        }
    }

    public void cancelAll() {
        if (videoNum > 1) {
            File file = MyFileManager.getVideoCacheFile(mContext, videoNum + ".mp4");
            MyFileManager.deleteFiles(file.getParentFile());
        }
    }

    public void onTorch() {
        setOnTorch(true);
        Parameters parameter = mCamera.getParameters();

        parameter.setFlashMode(Parameters.FLASH_MODE_TORCH);

        mCamera.setParameters(parameter);
    }

    public void offTorch() {
        setOnTorch(false);
        Parameters parameter = mCamera.getParameters();

        parameter.setFlashMode(Parameters.FLASH_MODE_OFF);

        mCamera.setParameters(parameter);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.v(TAG, "onPreviewFrame");
        final Handler focusHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                double curLigth = (Double) message.obj;
                if (null != mCamera) {

                    Log.v(TAG, "focusHandler:" + Math.abs(curLigth - historyLight));
                    if (Math.abs(curLigth - historyLight) > focusSensitivity) {
                        Log.v(TAG, "Focus!");
                        initCamera();
                    }
                    historyLight = curLigth;
                }
            }
        };
        final byte[] newData = data;
        new Thread() {
            public void run() {
                if (null != mCamera) {
                    double curLigth = ImageUtil.getLight(newData, mCamera.getParameters().getPreviewSize().width, mCamera.getParameters().getPreviewSize().height / 2);
                    Message message = focusHandler.obtainMessage(1, curLigth);
                    focusHandler.sendMessage(message);
                }
            }
        }.start();
    }

    double historyLight = 0;

    private void focusByLight() {
        new Thread() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    if (null != mCamera)
                        mCamera.setOneShotPreviewCallback(MyVideoRecorder.this);
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public boolean isOnTorch() {
        return onTorch;
    }

    public void setOnTorch(boolean onTorch) {
        this.onTorch = onTorch;
    }

    public int getCameraCount() {
        return cameraCount;
    }

    public int getVideoNum() {
        return videoNum - 1;
    }

    public double getFocusSensitivity() {
        return focusSensitivity;
    }

    public void setFocusSensitivity(double focusSensitivity) {
        this.focusSensitivity = focusSensitivity;
    }
}
