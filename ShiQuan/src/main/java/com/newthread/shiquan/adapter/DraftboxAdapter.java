package com.newthread.shiquan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.newthread.shiquan.R;
import com.newthread.shiquan.dao.AsyncFileImageLoader;
import com.newthread.shiquan.utils.ImageUtil;

import java.io.File;

/**
 * Created by 翌日黄昏 on 2014/7/31.
 */
public class DraftboxAdapter extends BaseAdapter {
    private final static String TAG = "DraftboxAdapter";
    private static final String THUMBNAILS = ".thumbnails/";
    private final static int WIDTH = 256;
    private final static int HEIGHT = 256;
    private Context context;

    private File[] files;
    private AsyncFileImageLoader asyncImage;

    public DraftboxAdapter(Context context, File[] files) {
        this.context = context;
        this.files = files;
        asyncImage = new AsyncFileImageLoader();
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
                R.layout.item_draftbox, null);

        final ImageView img = (ImageView) convertView.findViewById(R.id.item_draftbox_img);
        TextView text = (TextView) convertView.findViewById(R.id.item_draftbox_text);
        String name = files[position].getName();
        text.setText(name.substring(0, name.lastIndexOf(".")));
        img.setImageBitmap(ImageUtil.getBitmap(files[position].getParent() + "/" + THUMBNAILS + files[position].getName() + ".png"));
//        AsynImageLoader asynImageLoader = new AsynImageLoader();
//        asynImageLoader.showImageAsyn(img, files[position].getAbsolutePath(), R.drawable.icon_user);
//        asyncImage.loadDrawable(files[position].getAbsolutePath(), new AsyncFileImageLoader.ImageCallback() {
//            @Override
//            public void imageLoaded(Bitmap bitmap, String imageUrl) {
//                Log.v(TAG, imageUrl);
//                img.setImageBitmap(bitmap);
//            }
//        });
        return convertView;
    }


    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public void updata() {
        notifyDataSetChanged();
    }
}
