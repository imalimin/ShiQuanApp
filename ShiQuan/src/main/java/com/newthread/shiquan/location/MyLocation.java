package com.newthread.shiquan.location;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.newthread.shiquan.R;

/**
 * Created by 翌日黄昏 on 2014/8/22.
 */
public class MyLocation {
    private final String TAG = "MyLocation";
    private int ScanSpan = 30 * 60 * 1000;
    private Context mContext;
    private ILocationListener locationListener;
    public LocationClient mLocationClient = null;
    //    public BDLocationListener bDLocationListener = null;
    public TextView locationBtn;

    public MyLocation(final Context mContext, ILocationListener locationListener) {
        this.mContext = mContext;
        this.locationListener = locationListener;
        this.mLocationClient = new LocationClient(mContext);     //声明LocationClient类
        mLocationClient.registerLocationListener(new MyLocationListener());    //注册监听函数

        init();
    }

    public MyLocation(final Context mContext, TextView locationBtn) {
        this.mContext = mContext;
        this.locationBtn = locationBtn;
        this.mLocationClient = new LocationClient(mContext);     //声明LocationClient类
        mLocationClient.registerLocationListener(new MyLocationListener());    //注册监听函数
        this.locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLocation.this.locationBtn.setText(mContext.getResources().getText(R.string.now_location));
                requestLocation();
            }
        });

        init();
    }

    private void init() {
        LocationClientOption mLocationClientOption = new LocationClientOption();
        mLocationClientOption.setLocationMode(LocationMode.Battery_Saving);
        mLocationClientOption.setNeedDeviceDirect(true);
        mLocationClientOption.setIsNeedAddress(true);
        mLocationClientOption.setProdName("newthread");
        mLocationClientOption.setScanSpan(ScanSpan);
        setLocOption(mLocationClientOption);
    }

    public void setLocOption(LocationClientOption mLocationClientOption) {
        mLocationClient.setLocOption(mLocationClientOption);
    }

    public void start() {
        mLocationClient.start();
    }

    public void stop() {
        mLocationClient.stop();
    }

    public void requestLocation() {
        mLocationClient.requestLocation();
    }

    public String getAccessKey() {
        return mLocationClient.getAccessKey();
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                Toast.makeText(mContext, "定位失败", Toast.LENGTH_SHORT).show();
                return;
            }
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(subCity(location.getAddrStr()));
            }
            if (locationBtn != null) {
                locationBtn.setText(subCity(location.getAddrStr()));
            }
            if (locationListener != null) {
                locationListener.doComplete(subCity(location.getAddrStr()), location.getAddrStr());
            }
        }
    }

    public String subCity(String location) {
        if(location == null || location.equals("")) return "定位失败";
        int start = -1;
        int end = -1;
        end = location.indexOf("市");
        if (location.contains("省")) {
            start = location.indexOf("省");
        } else if (location.contains("区")) {
            start = location.indexOf("区");
        } else {
            return location;
        }
        if (start >= end) {
            return location;
        }
        return location.substring(start + 1, end + 1);
    }

    public interface ILocationListener {
        public void doComplete(String city, String location);
    }
}
