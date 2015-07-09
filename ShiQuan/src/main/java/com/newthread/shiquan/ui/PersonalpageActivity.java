package com.newthread.shiquan.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.newthread.shiquan.R;
import com.newthread.shiquan.bean.UserData;
import com.newthread.shiquan.dao.DataParser;
import com.newthread.shiquan.dao.MyRequest;
import com.newthread.shiquan.dao.PersonalDataUtil;
import com.newthread.shiquan.ui.fragment.UserAttentionFragment;
import com.newthread.shiquan.ui.fragment.UserDynamicFragment;
import com.newthread.shiquan.ui.fragment.UserVideoFragment;
import com.newthread.shiquan.widget.CircularImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.viewpagerindicator.TabPageIndicator;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PersonalpageActivity extends FragmentActivity {
    private final static String TAG = "PersonalpageActivity";
    private static final String[] TITLE_CONTENT = new String[]{"Ta的视频", "Ta的关注"};

    private Button exit;
    private TextView userName;
    private CheckBox interestBtn;
    private CircularImage userIcon;
    private TextView userLocation;
    private ImageView userGender;
    private TextView userInterest;
    private TextView userFans;
    private TableRow userInterestBtn;
    private TableRow userFansBtn;

    private TabPageIndicator tabPageIndicator;
    private ViewPager mViewPager;
    private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    private String userId = "";
    private UserData userData;


    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
        tabPageIndicator = (TabPageIndicator) findViewById(R.id.personal_user_titlepageindicator);
        mViewPager = (ViewPager) findViewById(R.id.personal_user_viewpager);
        userName = (TextView)findViewById(R.id.personal_user_name);
        interestBtn = (CheckBox)findViewById(R.id.personal_interset_check);
        userIcon = (CircularImage)findViewById(R.id.personal_user_icon);
        userLocation = (TextView)findViewById(R.id.personal_location);
        userGender = (ImageView)findViewById(R.id.personal_user_gender);
        userInterest = (TextView)findViewById(R.id.personal_user_interest_num);
        userFans = (TextView)findViewById(R.id.personal_user_fans_num);
        exit = (Button) findViewById(R.id.personal_back);

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        tabPageIndicator.setViewPager(mViewPager);


        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.icon_loading).cacheInMemory()
                .cacheOnDisc().build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        interestBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(PersonalpageActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PersonalpageActivity.this, "取消关注", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Intent intent = getIntent();
        userId = intent.getStringExtra("ID");
        if(userId == null || userId.equals(""))
            Toast.makeText(PersonalpageActivity.this, "无效的用户ID", Toast.LENGTH_SHORT).show();
        Log.v(TAG, userId);
        showUserInfo();
    }

    private void showUserInfo() {
        PersonalDataUtil.showUserInfo(userId, new MyRequest.IRequestListener() {
            @Override
            public void onComplete(int result, String msg, Object obj) {
                if (result == HttpStatus.SC_OK) {
                    String content = null;
                    content = new String((byte[]) obj);
                    Log.v(TAG, content);
                    try {
                        userData = new UserData(new JSONObject(content));
                        showInfo();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(PersonalpageActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showInfo() {
        userName.setText(userData.getNickName());
        userLocation.setText(userData.getCity());
        userInterest.setText("" + userData.getAttentionNumber());
        userFans.setText("" + userData.getFansNumber());
        if (userData.getGender().equals("女")) {
            userGender.setBackgroundResource(R.drawable.icon_woman);
        } else {
            userGender.setBackgroundResource(R.drawable.icon_man);
        }
        imageLoader.displayImage(userData.getHeadPortraitAddress(), userIcon, options);
    }

    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {
        private Context context;

        public AppSectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.context = context;
            // TODO Auto-generated constructor stub
        }

        @Override
        public android.support.v4.app.Fragment getItem(int arg0) {
            // TODO Auto-generated method stub
            switch (arg0) {
                case 0:
                    UserVideoFragment userVideoFragment = new UserVideoFragment(context);
                    return userVideoFragment;
//                case 1:
//                    UserDynamicFragment userDynamicFragment = new UserDynamicFragment(context);
//                    return userDynamicFragment;
                case 1:
                    UserAttentionFragment userAttentionFragment = new UserAttentionFragment(context);
                    return userAttentionFragment;
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
