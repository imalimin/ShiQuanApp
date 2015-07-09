package com.newthread.shiquan.dao;

import android.graphics.Bitmap;
import android.os.Message;
import android.provider.MediaStore;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.Handler;

import com.newthread.shiquan.utils.ImageUtil;

/**
 * Created by TOSHIBA on 13-11-1.
 */
public class AsyncFileImageLoader {
    private final static int WIDTH = 136;
    private final static int HEIGHT = 136;
    private HashMap<String, SoftReference<Bitmap>> imageCache;//图片缓存
    private HashMap<String, Integer> imageKey;
    private ArrayList<String> paths;

    public AsyncFileImageLoader() {
        //所有图片存在于此，这是图片的一个缓存，键值对构成，键是图片的下载地址，而值是储存drawable的softreference的对象
        imageCache = new HashMap<String, SoftReference<Bitmap>>();
        imageKey = new HashMap<String, Integer>();
        paths = new ArrayList<String>();
    }

    public Bitmap loadDrawable(final String imagePath, final ImageCallback imageCallback) {
        //判断缓存中是否存在该链接的图片，有则直接返回该图片的Drawable
        if (imageCache.containsKey(imagePath)) {
            if (imageCache.get(imagePath).get() != null) {
                imageCallback.imageLoaded(imageCache.get(imagePath).get(), imagePath);
                return imageCache.get(imagePath).get();
            }
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Bitmap) message.obj, imagePath);
            }
        };
        if (imageKey.get(imagePath) == null || !imageCache.containsKey(imagePath) || imageCache.get(imagePath).get() == null) {
            imageKey.put(imagePath, 1);
            //在HashMap中没有出现过，调用线程重新下载
            new Thread() {
                public void run() {
                    //根据图片的下载地址，创建drawable对象
                    Bitmap bitmap = ImageUtil.getVideoThumbnail(imagePath,
                            WIDTH, HEIGHT, MediaStore.Images.Thumbnails.MICRO_KIND);
                    //加入HashMap缓存，以备下次使用
                    imageCache.put(imagePath, new SoftReference<Bitmap>(bitmap));
                    //通知handler
                    Message message = handler.obtainMessage(0, bitmap);
                    handler.sendMessage(message);
                    imageKey.remove(imagePath);
                }
            }.start();
        }
        return null;
    }

    public interface ImageCallback {
        public void imageLoaded(Bitmap bitmap, String imageUrl);
//        public void refreshAdapter(Handler handler);
    }
}
