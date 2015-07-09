package com.newthread.shiquan.ftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPFile;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * @author gaofeng 2014-6-18
 */
public class FTPManager {

    FTPClientProxy ftpProxy;
    IRetrieveListener listener;
    volatile boolean isLogin = false;
    volatile boolean stopDownload = false;

    protected FTPManager() {

    }

    public FTPManager(FTPCfg cfg) {
        ftpProxy = new FTPClientProxy(cfg);
    }

    /**
     * track listener for FTP downloading
     *
     * @param listener
     */
    public void setListener(IRetrieveListener listener) {
        this.listener = listener;
    }

    /**
     * stop download task if you set true
     *
     * @param stopDownload
     */
    public void setStopDownload(boolean stopDownload) {
        this.stopDownload = stopDownload;
    }

    public FTPFile[] showListFile(String remoteDir) {
        return ftpProxy.getFTPFiles(remoteDir);
    }

    public boolean connectLogin() {
        boolean ok = false;
        if (ftpProxy.connect()) {
            ok = ftpProxy.login();
        }
        isLogin = ok;
        return ok;
    }

    /**
     * @param remoteDir of FTP
     * @param name      of file name under FTP Server's remote DIR.
     * @return FTPFile
     */
    public FTPFile getFileByName(String remoteDir, String name) {
        FTPFile[] files = showListFile(remoteDir);
        if (files != null) {
            for (FTPFile f : files) {
                if (name.equalsIgnoreCase(f.getName())) {
                    return f;
                }
            }
        }
        return null;
    }

    public void download(final String remotePath, final String localPath, final long offset) {
        final Handler handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 0:
                        listener.onStart((Long) message.obj);
                        break;
                    case 1:
                        listener.onTrack((Long) message.obj);//监控当前下载了多少字节，可用于显示到UI进度条中
                        break;
                    case 2:
                        listener.onCancel("");
                        break;
                    case 3:
                        listener.onDone();
                        break;
//                    case 4:
//                        listener.onError("File Download Error", ERROR.FILE_DOWNLOAD_ERROR);
//                        break;
//                    case 5:
//                        listener.onError("File Download Error:", ERROR.FILE_DOWNLOAD_ERROR);
//                        break;
                }
            }
        };
        new Thread() {
            public void run() {
                super.run();
                connectLogin();
                File f = new File(localPath);
                byte[] buffer = new byte[ftpProxy.getConfig().bufferSize];
                int len = -1;
                long now = -1;
                boolean append = false;
                InputStream ins = null;
                OutputStream ous = null;
                Message message = null;
                try {
                    if (offset > 0) { //用于续传
                        ftpProxy.setRestartOffset(offset);
                        now = offset;
                        append = true;
                    }
                    String remotePathT = remotePath.substring(remotePath.indexOf("/") + 1);
                    remotePathT = remotePathT.substring(remotePathT.indexOf("/") + 1);
                    remotePathT = remotePathT.substring(remotePathT.indexOf("/"));
                    Log.d("", "downloadFile:" + now + ";" + remotePathT);

                    ins = ftpProxy.getRemoteFileStream(remotePathT);
                    ous = new FileOutputStream(f, append);
                    FTPFile ftpFile = ftpProxy.getFTPFile(remotePathT);
                    if (ftpFile != null) {
                        message = handler.obtainMessage(0, ftpFile.getSize());
                    } else {
                        message = handler.obtainMessage(0, 1024 * 1024 * 4);
                    }
                    handler.sendMessage(message);
                    Log.d("", "downloadFileRenew:" + ins);
                    while ((len = ins.read(buffer)) != -1) {
                        if (stopDownload) {
                            break;
                        }
                        ous.write(buffer, 0, len);
                        now = now + len;
                        message = handler.obtainMessage(1, now);
                        handler.sendMessage(message);
                    }
                    if (stopDownload) {
                        message = handler.obtainMessage(2, null);
                        handler.sendMessage(message);
                    } else {
                        if (ftpProxy.isDone()) {
                            message = handler.obtainMessage(3, null);
                            handler.sendMessage(message);
                        } else {
                            message = handler.obtainMessage(4, null);
                            handler.sendMessage(message);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    message = handler.obtainMessage(5, null);
                    handler.sendMessage(message);
                } finally {
                    try {
                        ous.close();
                        ins.close();
                    } catch (Exception e2) {
                    }
                }
            }
        }.start();
    }

    public void download(String remotePath, String localPath) {
        download(remotePath, localPath, -1);
    }

    public void close() {
        ftpProxy.close();
    }

    public static class ERROR { //自己定义的一些错误代码
        public static final int FILE_NO_FOUNT = 9001;
        public static final int FILE_DOWNLOAD_ERROR = 9002;
        public static final int LOGIN_ERROR = 9003;
        public static final int CONNECT_ERROR = 9004;
    }

    public boolean connect() {
        return ftpProxy.connect();
    }
}
