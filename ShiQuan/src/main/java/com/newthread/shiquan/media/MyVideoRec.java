package com.newthread.shiquan.media;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.newthread.shiquan.utils.ImageUtil;
import com.newthread.shiquan.utils.MyFileManager;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 翌日黄昏 on 2014/8/16.
 */
public class MyVideoRec implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private final static String TAG = "MyVideoRec";

    private final int maxDurationInMs = 15 * 1000;
    private final long maxFileSizeInBytes = 30 * 1024 * 1024;
    private final int videoFramesPerSecond = 24;
    private final int videoBitRate = 512 * 1024 * 8;
    private final int audioBitRate = 384 * 1024 * 8;
    private final int videoWidth = 640;
    private final int videoHeight = 480;

    private Context mContext;
    private AvcEncoder avcEncoder;
    private Camera mCamera;
    private SurfaceView cameraSurface;
    private SurfaceHolder surfaceHolder;
    private Camera.Parameters parameters;

    private Button button;
    private ProgressBar seekBar;

    private double focusSensitivity = 7;
    private boolean isStart = false;
    private boolean isFinish = false;
    //    private int videoNum = 1;
    private ArrayList<byte[]> videoList;
    private ArrayList<Long> times = new ArrayList<Long>();
    private long curTotal = 0;
    private boolean onTorch = false;
    private int cameraCount = 1;
    private boolean isBack = true;

    private byte[] videoBuffer;
    private boolean canPutFrame = false;

    public MyVideoRec(Context mContext, SurfaceView cameraSurface, ProgressBar seekBar, Button button) {
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
        this.cameraSurface = cameraSurface;
        this.button = button;
        this.seekBar = seekBar;
        this.cameraCount = Camera.getNumberOfCameras();
        this.videoList = new ArrayList<byte[]>();
        surfaceHolder = cameraSurface.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.button.setOnTouchListener(mListener);
        cameraSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focus();
            }
        });
    }

    // 初始化摄像头
    public void initCamera() {
        parameters = mCamera.getParameters();
        choosePreviewSize(parameters, videoWidth, videoHeight);
        parameters.setPreviewFormat(ImageFormat.YV12);
        if (onTorch) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        }
        mCamera.setDisplayOrientation(0);
        mCamera.setParameters(parameters);
        videoBuffer = new byte[videoWidth * videoHeight * 3 / 2];
        mCamera.addCallbackBuffer(videoBuffer);
        mCamera.setPreviewCallbackWithBuffer(this);
        mCamera.startPreview();
        mCamera.getParameters().setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
    }

    private static void choosePreviewSize(Camera.Parameters parms, int width, int height) {
        // We should make sure that the requested MPEG size is less than the preferred
        // size, and has the same aspect ratio.
        Camera.Size ppsfv = parms.getPreferredPreviewSizeForVideo();

        for (Camera.Size size : parms.getSupportedPreviewSizes()) {
            if (size.width == width && size.height == height) {
                parms.setPreviewSize(width, height);
                return;
            }
        }

        Log.w(TAG, "Unable to set preview size to " + width + "x" + height);
        if (ppsfv != null) {
            parms.setPreviewSize(ppsfv.width, ppsfv.height);
        }
    }

    public boolean startRecording() {
        if (!MyFileManager.cacheFilecanW(mContext)) {
            Toast.makeText(mContext, "文件不可读写!", Toast.LENGTH_SHORT).show();
//                return false;
        }
        isStart = true;
        return true;
    }

    public void stopRecording() {
        Log.v(TAG, "isStart:" + isStart);
        if (isStart) {

            times.add(curTotal);
            curTotal = 0;
            isStart = false;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (canPutFrame) {
            Log.v(TAG, "putFrame");
            videoList.add(data);
        }
        autoFocusByLight(data);
        mCamera.addCallbackBuffer(videoBuffer);
    }

    public void creatVideo() {
        if (videoList.size() <= 0) {
            Toast.makeText(mContext, "请至少拍摄一段视频！", Toast.LENGTH_SHORT);
            return;
        }
        Log.v(TAG, "creatVideo");
//        avcEncoder = new AvcEncoder(videoWidth, videoHeight, MyFileManager.getUserDragftboxFile(mContext), videoList);

        new Thread() {
            public void run() {
                VideoEncoderFromBuffer videoEncoderFromBuffer = new VideoEncoderFromBuffer(videoWidth, videoHeight, MyFileManager.getUserDragftboxFile(mContext));
//                for (byte[] inputFrame: videoList) {
                    videoEncoderFromBuffer.encodeFrame(videoList.get(5));
//                }
//                avcEncoder.startVideoToMp4();
//                avcEncoder.finish();
            }
        }.start();
    }

    private View.OnTouchListener mListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.v(TAG, "ACTION_DOWN");
                canPutFrame = true;
                if (!isFinish) {
                    startRecording();
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.v(TAG, "ACTION_UP");
                canPutFrame = false;
                if (!isFinish) {
                    stopRecording();
                }
            }
            return false;
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
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
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
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
        // 停止预览
        if (mCamera != null) {
            mCamera.setPreviewCallback(null); //！！这个必须在前，不然退出出错
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void focus() {
        if (mCamera == null) return;
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
            }
        });
    }

    double historyLight = 0;
    boolean isFocusing = false;

    public void autoFocusByLight(byte[] data) {
        if (isFocusing) return;
        final Handler focusHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                double curLigth = (Double) message.obj;
                if (null != mCamera) {
//                    Log.v(TAG, "focusHandler:" + Math.abs(curLigth - historyLight));
                    if (Math.abs(curLigth - historyLight) > focusSensitivity) {
//                        Log.v(TAG, "Focus!");
                        focus();
                    }
                    historyLight = curLigth;
                    isFocusing = false;
                }
            }
        };
        final byte[] newData = data;
        new Thread() {
            public void run() {
                if (null != mCamera) {
                    isFocusing = true;
                    double curLigth = ImageUtil.getLight(newData, mCamera.getParameters().getPreviewSize().width, mCamera.getParameters().getPreviewSize().height / 2);
                    Message message = focusHandler.obtainMessage(1, curLigth);
                    focusHandler.sendMessage(message);
                }
            }
        }.start();
    }

    public void onTorch() {
        setOnTorch(true);
        Camera.Parameters parameter = mCamera.getParameters();

        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

        mCamera.setParameters(parameter);
    }

    public void offTorch() {
        setOnTorch(false);
        Camera.Parameters parameter = mCamera.getParameters();

        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

        mCamera.setParameters(parameter);
    }

    public boolean isOnTorch() {
        return onTorch;
    }

    public void setOnTorch(boolean onTorch) {
        this.onTorch = onTorch;
    }


    public double getFocusSensitivity() {
        return focusSensitivity;
    }

    public void setFocusSensitivity(double focusSensitivity) {
        this.focusSensitivity = focusSensitivity;
    }
}
