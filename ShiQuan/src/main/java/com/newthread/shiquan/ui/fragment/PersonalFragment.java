package com.newthread.shiquan.ui.fragment;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.newthread.shiquan.R;
import com.newthread.shiquan.bean.QQUserData;
import com.newthread.shiquan.bean.UserData;
import com.newthread.shiquan.dao.MyRequest;
import com.newthread.shiquan.dao.MyRequest.IRequestListener;
import com.newthread.shiquan.dao.PersonalDataUtil;
import com.newthread.shiquan.tencent.AppDate;
import com.newthread.shiquan.tencent.UiErrorUtil;
import com.newthread.shiquan.tencent.Util;
import com.newthread.shiquan.ui.DataEditActivity;
import com.newthread.shiquan.utils.ImageUtil;
import com.newthread.shiquan.utils.MyFileManager;
import com.newthread.shiquan.utils.SharedPreferencesManager;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.viewpagerindicator.TabPageIndicator;

@SuppressLint("ValidFragment")
public class PersonalFragment extends Fragment {
    private final static String TAG = "PersonalFragment";
    private static final String[] TITLE_CONTENT = new String[]{"我的视频", "我的关注", "我的收藏"};
    private static Context mContext;

    private TextView userName;
    private Button shareBtn;
    private Button dataEdit;
    private ImageView userIcon;
    private TextView userLocation;
    private ImageView userGender;
    private TextView userInterest;
    private TextView userFans;
    private TableRow userInterestBtn;
    private TableRow userFansBtn;
    private TableRow userExitBtn;
    private Button exit;

    private TabPageIndicator tabPageIndicator;
    private ViewPager mViewPager;
    private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    private SharedPreferencesManager SPManager;
    private Tencent mTencent;
    private QQUserData qqUserData;
    private UserData userData;

    private FragmentManager fragmentManager;
    private IUserInfoCompleted userInfoCompleted;

    private MyRequest mRequest = MyRequest.getInstance();

    public PersonalFragment() {
    }

    public PersonalFragment(Context mContext, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SPManager = new SharedPreferencesManager(mContext);
        View view = inflater.inflate(R.layout.view_personal, null);
        initView(view);

        return view;
    }

    private void initView(View view) {
        tabPageIndicator = (TabPageIndicator) view
                .findViewById(R.id.mypage_user_titlepageindicator);
        mViewPager = (ViewPager) view.findViewById(R.id.mypage_user_viewpager);
        userName = (TextView) view.findViewById(R.id.mypage_user_name);
        shareBtn = (Button) view.findViewById(R.id.mypage_user_share_btn);
        dataEdit = (Button) view.findViewById(R.id.mypage_user_dataedit);
        userIcon = (ImageView) view.findViewById(R.id.mypage_user_icon);
        userLocation = (TextView) view.findViewById(R.id.mypage_user_location);
        userGender = (ImageView) view.findViewById(R.id.mypage_user_gender);
        userInterest = (TextView) view
                .findViewById(R.id.mypage_user_interest_num);
        userFans = (TextView) view.findViewById(R.id.mypage_user_fans_num);
        userInterestBtn = (TableRow) view
                .findViewById(R.id.mypage_user_interest_btn);
        userFansBtn = (TableRow) view.findViewById(R.id.mypage_user_fans_btn);
        exit = (Button) view.findViewById(R.id.mypage_back);

        exit.setOnClickListener(mListener);
        shareBtn.setOnClickListener(mListener);
        dataEdit.setOnClickListener(mListener);
        userInterestBtn.setOnClickListener(mListener);
        userFansBtn.setOnClickListener(mListener);

        if (MyFileManager.readCurrentUserIcon(mContext).exists()) {
            Bitmap img = BitmapFactory.decodeFile(MyFileManager
                    .readCurrentUserIcon(mContext).getAbsolutePath());
            userIcon.setImageBitmap(ImageUtil.getRoundedCornerBitmap(img));
        }

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(fragmentManager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        tabPageIndicator.setViewPager(mViewPager);

        mTencent = Tencent.createInstance(AppDate.APP_ID, mContext);

        UserInfo info = new UserInfo(mContext, mTencent.getQQToken());
        info.getUserInfo(new IUserInfoListener());
    }

    private class IUserInfoListener implements IUiListener {

        @Override
        public void onComplete(Object arg0) {
            // TODO Auto-generated method stub
            JSONObject response = (JSONObject) arg0;
            qqUserData = new QQUserData(response);
            if (!MyFileManager.readCurrentUserIcon(mContext).exists()) {
                downloadUserIcon();
            }
            Log.v(TAG, "IUserInfoListener.onComplete:" + response.toString());
            dataEdit.setVisibility(Button.VISIBLE);
            SPManager.saveQQNick(qqUserData.getNickname());
            userName.setText(SPManager.readQQNick());
            PersonalDataUtil.addUser(qqUserData, SPManager, new IRequestListener() {
                @Override
                public void onComplete(int result, String msg, Object obj) {
                    Log.v(TAG, result + "");
                    if (result == HttpStatus.SC_OK) {
                        String content = new String((byte[]) obj);
                        try {
                            userData = new UserData(new JSONObject(content));
                            setUIInfo(userData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        protected void doComplete(JSONObject values) {
            Log.v(TAG, "doComplete:" + values.toString());
        }

        @Override
        public void onError(UiError e) {
            Toast.makeText(mContext, UiErrorUtil.errorStatus(e), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Log.v(TAG, "onCancel");
            Toast.makeText(mContext, "操作中断", Toast.LENGTH_SHORT).show();
        }

    }

    private void setUIInfo(UserData userData) {

        userLocation.setText(userData.getCity());
        userInterest.setText("" + userData.getAttentionNumber());
        userFans.setText("" + userData.getFansNumber());
        if (userData.getGender().equals("女")) {
            userGender.setBackgroundResource(R.drawable.icon_woman);
        } else {
            userGender.setBackgroundResource(R.drawable.icon_man);
        }
    }


    public OnClickListener mListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.mypage_user_share_btn:
                    break;
                case R.id.mypage_back:
                    ((Activity) mContext).finish();
                    break;
                case R.id.mypage_user_dataedit:
                    Intent intent = new Intent(mContext, DataEditActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("NICKNAME", qqUserData.getNickname());
                    bundle.putString("GENDER", qqUserData.getGender());
                    bundle.putString("AREA", qqUserData.getProvince() + " "
                            + qqUserData.getCity());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.mypage_user_interest_btn:
                    break;
                case R.id.mypage_user_fans_btn:
                    break;
                default:
                    break;
            }
        }
    };

    private void downloadUserIcon() {
        Log.v(TAG, "downloadUserIcon");
        MyRequest mRequest = MyRequest.getInstance();
        mRequest.get(qqUserData.getFigureurl_qq_2(),
                new MyRequest.IRequestListener() {
                    @Override
                    public void onComplete(int result, String msg, Object obj) {
                        // TODO Auto-generated method stub
                        if (result == HttpStatus.SC_OK) {
                            userIcon.setImageBitmap(ImageUtil
                                    .getRoundedCornerBitmap(Util
                                            .getbitmap((byte[]) obj)));
                            // doCompleted((byte[]) obj);
                            MyFileManager.saveCurrentUserIcon((byte[]) obj,
                                    mContext);
                        } else {
                            Toast.makeText(mContext, msg + ":" + result, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void doCompleted(byte[] data) {
        if (userInfoCompleted != null) {
            userInfoCompleted.doCompleted(data, qqUserData);
        }
    }

    private void doLogout() {
        if (userInfoCompleted != null) {
            userInfoCompleted.doLogout();
        }
    }

    public interface IUserInfoCompleted {
        public void doCompleted(byte[] data, QQUserData qqUserData);

        public void doLogout();
    }

    public void setLoginCompleted(IUserInfoCompleted userInfoCompleted) {
        this.userInfoCompleted = userInfoCompleted;
    }

    public void setUserInfoCompleted(IUserInfoCompleted userInfoCompleted) {
        this.userInfoCompleted = userInfoCompleted;
    }

    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public android.support.v4.app.Fragment getItem(int arg0) {
            // TODO Auto-generated method stub
            switch (arg0) {
                case 0:
                    UserVideoFragment userVideoFragment = new UserVideoFragment(mContext);
                    return userVideoFragment;
//                case 1:
//                    UserDynamicFragment userDynamicFragment = new UserDynamicFragment(mContext);
//                    return userDynamicFragment;
                case 1:
                    UserAttentionFragment userAttentionFragment = new UserAttentionFragment(mContext);
                    return userAttentionFragment;
                case 2:
                    UserCollectionFragment userCollectionFragment = new UserCollectionFragment(mContext);
                    return userCollectionFragment;
                default:
                    return null;
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLE_CONTENT[position % TITLE_CONTENT.length];
        }

        @Override
        public int getCount() {
            return TITLE_CONTENT.length;
        }
    }
}
