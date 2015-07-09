package com.newthread.shiquan.ui.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.newthread.shiquan.R;
import com.newthread.shiquan.tencent.AppDate;
import com.newthread.shiquan.ui.DraftboxActivity;
import com.newthread.shiquan.ui.LoginActivity;
import com.newthread.shiquan.ui.RadarActivity;
import com.newthread.shiquan.ui.ScanActivity;
import com.newthread.shiquan.ui.SearchFriendsActivity;
import com.newthread.shiquan.ui.SettingActivity;
import com.newthread.shiquan.ui.ShakeActivity;
import com.newthread.shiquan.utils.ImageUtil;
import com.newthread.shiquan.utils.MyFileManager;
import com.newthread.shiquan.utils.SharedPreferencesManager;
import com.tencent.tauth.Tencent;

/**
 * Created by lanqx on 2014/5/3.
 */
@SuppressLint("ValidFragment")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MenuFragment extends Fragment {
    private final static String TAG = "MenuFragment";
    public final static int LOGOUT = 0;
    private Context context;
    private SharedPreferencesManager SPManager;

    private ImageView loginBtn;
    private TextView userName;
    private TableRow draftBox;
    private TableRow searchfriends;
    private TableRow radar;
    private TableRow shake;
    private TableRow scan;
    private TableRow setting;
    private TableRow logout;
    private IContactListener contactListener;
    private Tencent mTencent;

    public MenuFragment() {

    }

    public MenuFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SPManager = new SharedPreferencesManager(context);
        mTencent = Tencent.createInstance(AppDate.APP_ID, context);
        View view = inflater.inflate(R.layout.view_menu, null);
        initView(view);

        return view;
    }

    private void initView(View view) {
        loginBtn = (ImageView) view.findViewById(R.id.main_user_icon);
        userName = (TextView) view.findViewById(R.id.main_user_text);
        draftBox = (TableRow) view.findViewById(R.id.main_draftbox);
        searchfriends = (TableRow) view.findViewById(R.id.main_searchfriends);
        radar = (TableRow) view.findViewById(R.id.main_user_radar);
        shake = (TableRow) view.findViewById(R.id.main_shake);
        scan = (TableRow) view.findViewById(R.id.main_user_scan);
        setting = (TableRow) view.findViewById(R.id.main_setting);
        logout = (TableRow) view.findViewById(R.id.main_logout);

        loginBtn.setOnClickListener(mListener);
        draftBox.setOnClickListener(mListener);
        searchfriends.setOnClickListener(mListener);
        radar.setOnClickListener(mListener);
        shake.setOnClickListener(mListener);
        scan.setOnClickListener(mListener);
        setting.setOnClickListener(mListener);
        logout.setOnClickListener(mListener);

        if (SPManager.readQQExpiresIn() > 0) {
            setUserInfo();
        }

    }

    public View.OnClickListener mListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.main_user_icon:
                    intent = new Intent(context, LoginActivity.class);
                    int requestCode = 1;
                    startActivity(intent);
                    break;
                case R.id.main_draftbox:
                    intent = new Intent(context, DraftboxActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_searchfriends:
                    intent = new Intent(context, SearchFriendsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_user_radar:
                    intent = new Intent(context, RadarActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_shake:
                    intent = new Intent(context, ShakeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_user_scan:
                    intent = new Intent(context, ScanActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_setting:
                    intent = new Intent(context, SettingActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_logout:
                    doLogout();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.v(TAG, "onResume");
        setUserInfo();
    }

    private void doLogout() {
        SPManager.initTencent();
        mTencent.logout(context);
        setUserInfo();
        if(contactListener != null) {
            contactListener.contact(LOGOUT, null);
        }
    }
    public void setContactListener(IContactListener contactListener) {
        this.contactListener = contactListener;
    }

    public interface IContactListener {
        public void contact(int result, Object obj);
    }

    private void setUserInfo() {
        if (SPManager.readQQExpiresIn() > 0
                && !SPManager.readQQNick().equals("")
                && MyFileManager.readCurrentUserIcon(context).exists()) {
            userName.setText(SPManager.readQQNick());
            Bitmap img = BitmapFactory.decodeFile(MyFileManager
                    .readCurrentUserIcon(context).getAbsolutePath());
            loginBtn.setImageBitmap(ImageUtil.getRoundedCornerBitmap(img));
            logout.setVisibility(View.VISIBLE);
        } else {
            userName.setText(R.string.login);
            logout.setVisibility(View.GONE);
            loginBtn.setImageResource(R.drawable.icon_user);
        }
    }
}
