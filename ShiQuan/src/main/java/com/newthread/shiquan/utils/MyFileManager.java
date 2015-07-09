package com.newthread.shiquan.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

public class MyFileManager {
    private static final String TAG = "MyFilePath_TAG";
    private static final String DRAGFTBOX = "dragftbox/";
    private static final String COMPLETEDBOX = "completedbox/";
    private static final String THUMBNAILS = ".thumbnails/";

    public static String getPublicCacheDir() {
        try {
            String cacheDir = android.os.Environment
                    .getExternalStorageDirectory().getCanonicalPath()
                    + "/ShiQuan/cache/";
            Log.v(TAG, cacheDir);
            makeDir(cacheDir);
            return cacheDir;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String getDataDir() {
        try {
            String cacheDir = android.os.Environment
                    .getExternalStorageDirectory().getCanonicalPath()
                    + "/ShiQuan/data/";
            Log.v(TAG, cacheDir);
            makeDir(cacheDir);
            return cacheDir;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static File getVideoCacheFile(Context context, String name) {
        String path = getUserCacheDir(context) + PhoneInfo.getDate() + "/";
        Log.v(TAG, "getVideoCacheFile:" + path);
        makeDir(path);
        try {
            File cacheFile = new File(path + name);
            if (!cacheFile.exists())
                cacheFile.createNewFile();
            return cacheFile;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static boolean cacheFilecanW(Context context) {
        String path = getUserCacheDir(context) + PhoneInfo.getDate() + "/";
        File cacheFile = new File(path);
        if (cacheFile.canWrite()) {
            return true;
        }
        return false;
    }

    public String findFileByRW(String path) {
        for (int i = 0; i < 3; i++) {
            File file = new File(path);
            if (file.canRead() && file.canWrite()) {
                return path;
            } else {

            }
        }
        return null;
    }

    public static String getUserCacheDir(Context context) {
        SharedPreferencesManager SPManager = new SharedPreferencesManager(
                context);
        try {
            String cacheDir = "";
            if (!SPManager.readQQOpenid().equals("")) {
                cacheDir = android.os.Environment
                        .getExternalStorageDirectory().getCanonicalPath()
                        + "/ShiQuan/" + SPManager.readQQOpenid() + "/";
            } else {
                cacheDir = android.os.Environment
                        .getExternalStorageDirectory().getCanonicalPath()
                        + "/ShiQuan/cache";
            }
            Log.v(TAG, cacheDir);
            makeDir(cacheDir);
            return cacheDir;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String getUserDragftbox(Context context) {
        String dragftBoxDir = getUserCacheDir(context) + DRAGFTBOX;
        Log.v(TAG, "getUserDragftbox:" + dragftBoxDir);
        makeDir(dragftBoxDir);
        return dragftBoxDir;
    }

    public static String getUserDragftboxThumbnails(Context context) {
        String dragftBoxDirThumbnails = getUserDragftbox(context) + THUMBNAILS;
//        Log.v(TAG, "getUserDragftbox:" + dragftBoxDirThumbnails);
        makeDir(dragftBoxDirThumbnails);
        return dragftBoxDirThumbnails;
    }

    public static File getUserDragftboxFile(Context context) {
        File file = new File(getUserDragftbox(context) + PhoneInfo.getTime() + ".mp4");
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return file;
    }

    public static File getUserDragftboxThumbnailsFile(Context context, String name) {
        File file = new File(getUserDragftboxThumbnails(context) + name + ".png");
        Log.v(TAG, "getUserDragftboxThumbnailsFile:" + file.getAbsolutePath());
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return file;
    }
    //用户已完成箱
    public static String getUserCompletedBox(Context context) {
        String completedBoxDir = getUserCacheDir(context) + COMPLETEDBOX;
        Log.v(TAG, "getUserCompletedBox:" + completedBoxDir);
        makeDir(completedBoxDir);
        return completedBoxDir;
    }
    //用户已完视频缩略图
    public static String getUserCompletedBoxThumbnails(Context context) {
        String completedBoxDirThumbnails = getUserCompletedBox(context) + THUMBNAILS;
        makeDir(completedBoxDirThumbnails);
        return completedBoxDirThumbnails;
    }
    //用户已完成箱文件
    public static File getUserCompletedBoxFile(Context context, String fileName) {
        File file = new File(getUserCompletedBox(context) + fileName);
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return file;
    }
    //用户已完成箱缩略图文件
    public static File getUserCompletedBoxThumbnailsFile(Context context, String name) {
        File file = new File(getUserCompletedBoxThumbnails(context) + name + ".png");
        Log.v(TAG, "getUserCompletedBoxThumbnailsFile:" + file.getAbsolutePath());
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return file;
    }
    public static String getFileName(File file) {
        String name = file.getName();
        return name.substring(0, name.lastIndexOf("."));
    }

    public static String getFileName(String fileName) {
        String name = fileName;
        return name.substring(0, name.lastIndexOf("."));
    }

    public static void deleteFiles(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();

            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFiles(f);
            }
            file.delete();
        }
    }

    public static void makeDir(String path) {
        if (path != null && !path.equals("")) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

//    public static File getCacheFile(Context context) {
//        try {
//            File cacheFile = new File(getUserCacheDir(context) + "cacheFile.mp4");
//            if (!cacheFile.exists())
//                cacheFile.createNewFile();
//            return cacheFile;
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static void writeFile(byte[] data, String path, String fileName) {
        File toFile = new File(path + fileName);
        deleteFile(toFile);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(toFile);
            fileOutputStream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 如果文件存在则删除文件
    public static void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    public static void saveCurrentUserIcon(byte[] data, Context context) {
        SharedPreferencesManager SPManager = new SharedPreferencesManager(
                context);
        if (!SPManager.readQQOpenid().equals("")) {
            MyFileManager.writeFile(data, MyFileManager.getUserCacheDir(context),
                    SPManager.readQQOpenid() + ".png");
        }
    }

    public static File readCurrentUserIcon(Context context) {
        SharedPreferencesManager SPManager = new SharedPreferencesManager(
                context);
        return new File(MyFileManager.getUserCacheDir(context) + SPManager.readQQOpenid()
                + ".png");
    }

    public static File copyAssets(Context mContext, String assetFile, String sdFile) {
        try {
            AssetFileDescriptor aFD = mContext.getAssets().openFd(assetFile);
            InputStream in = aFD.createInputStream();
            File file = new File(getPublicCacheDir() + sdFile);
            OutputStream out = new FileOutputStream(file);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
