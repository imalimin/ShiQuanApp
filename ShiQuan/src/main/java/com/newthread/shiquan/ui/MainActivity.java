package com.newthread.shiquan.ui;

import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.newthread.shiquan.R;
import com.newthread.shiquan.dao.DoubleClickExit;
import com.newthread.shiquan.ui.fragment.ContentFragment;
import com.newthread.shiquan.ui.fragment.MenuFragment;
import com.newthread.shiquan.ui.fragment.MessageFragment;
import com.newthread.shiquan.utils.ImageUtil;
import com.newthread.shiquan.utils.MyFileManager;
import com.newthread.shiquan.utils.SharedPreferencesManager;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends SlidingFragmentActivity {
    private final static String TAG = "MainActivity";
    private SlidingMenu sm;
    private MenuFragment menuFragment;
    private ContentFragment contentFragment;
    private MessageFragment messageFragment;

    private SharedPreferencesManager SPManager;
    private TabPageIndicator tabPageIndicator;
    // private Button searchBtn;

    private ImageView userMenuBtn;
    private Button cameraBtn;
    private ImageView messageboxBtn;

    private DoubleClickExit dClickExit = new DoubleClickExit(MainActivity.this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        SPManager = new SharedPreferencesManager(MainActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initView() {
        tabPageIndicator = (TabPageIndicator) findViewById(R.id.main_titlepageindicator);
        // searchBtn = (Button)findViewById(R.id.main_search_btn);
        userMenuBtn = (ImageView) findViewById(R.id.main_bottom_title_user_btn);
        cameraBtn = (Button) findViewById(R.id.main_bottom_title_camera_btn);
        messageboxBtn = (ImageView) findViewById(R.id.main_bottom_title_messagebox_btn);

        // customize the SlidingMenu
        sm = getSlidingMenu();
        // 设置是左滑还是右滑，还是左右都可以滑
        sm.setMode(SlidingMenu.LEFT_RIGHT);
        // 设置菜单占屏幕的比例
        sm.setBehindOffset(getWindowManager().getDefaultDisplay().getWidth() / 4);
        // 设置滑动时菜单的是否淡入淡出
        sm.setFadeEnabled(true);
        // 设置淡入淡出的比例
        sm.setFadeDegree(0.5f);
        // 设置滑动时拖拽效果
        // sm.setBehindScrollScale(SlidingMenu.SLIDING_CONTENT);
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        // 设置slding menu的几种手势模式
        // TOUCHMODE_FULLSCREEN 全屏模式，在content页面中，滑动，可以打开sliding menu
        // TOUCHMODE_MARGIN 边缘模式，在content页面中，如果想打开slding ,你需要在屏幕边缘滑动才可以打开slding
        // menu
        // TOUCHMODE_NONE 自然是不能通过手势打开啦
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        setSlidingActionBarEnabled(true);

        // 使用左上方icon可点，这样在onOptionsItemSelected里面才可以监听到R.id.home
        // getActionBar().setDisplayHomeAsUpEnabled(true);

        // set the Behind View
        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction();
        menuFragment = new MenuFragment(MainActivity.this);
        contentFragment = new ContentFragment(MainActivity.this, sm,
                getSupportFragmentManager(), tabPageIndicator);
        messageFragment = new MessageFragment(MainActivity.this);

        fragmentTransaction.replace(R.id.menu, menuFragment);
        fragmentTransaction.replace(R.id.main_content, contentFragment);
        fragmentTransaction.replace(R.id.messagebox, messageFragment);
        fragmentTransaction.commit();

        setBehindContentView(R.layout.frame_menu);
        sm.setSecondaryMenu(R.layout.frame_messagebox);
        sm.showMenu();

        userMenuBtn.setOnClickListener(mListener);
        cameraBtn.setOnClickListener(mListener);
        messageboxBtn.setOnClickListener(mListener);
        menuFragment.setContactListener(new MenuFragment.IContactListener() {
            @Override
            public void contact(int result, Object obj) {
                if (result == MenuFragment.LOGOUT) {
                    setUserInfo();
                }
            }
        });
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.v(TAG, "onResume");
        setUserInfo();
    }

    private void setUserInfo() {
        if (SPManager.readQQExpiresIn() > 0
                && MyFileManager.readCurrentUserIcon(MainActivity.this)
                .exists()) {
            Bitmap img = BitmapFactory.decodeFile(MyFileManager
                    .readCurrentUserIcon(MainActivity.this).getAbsolutePath());
            userMenuBtn.setImageBitmap(ImageUtil.getRoundedCornerBitmap(img));
        } else {
            userMenuBtn.setImageResource(R.drawable.icon_user_circle_small);
        }
    }

    public View.OnClickListener mListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_bottom_title_user_btn:
                    sm.showMenu();
                    break;
                case R.id.main_bottom_title_camera_btn:
                    if (SPManager.readQQExpiresIn() <= 0) {
                        Toast.makeText(MainActivity.this, "请登录！", Toast.LENGTH_SHORT).show();
//                    return;
                    }
                    Intent intent = new Intent(MainActivity.this,
                            CameraActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_bottom_title_messagebox_btn:
                    sm.showSecondaryMenu();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!sm.isMenuShowing()) {
                dClickExit.exit();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
