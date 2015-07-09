package com.newthread.shiquan.ui.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.newthread.shiquan.R;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

/**
 * Created by lanqx on 2014/5/3.
 */
@SuppressLint({"ValidFragment", "NewApi"})
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ContentFragment extends Fragment {
    private static final String[] TITLE_CONTENT = new String[]{"热门", "好友",
            "广场"};
    private static Context context;
    private FragmentManager fragmentManager;
    private SlidingMenu slidingMenu;
    private IContactListener contactListener;

    private static ViewPager mViewPager;// viewpager
    private static TabPageIndicator tabPageIndicator;
    private AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    public ContentFragment() {
    }

    public ContentFragment(Context context, SlidingMenu slidingMenu,
                           FragmentManager fragmentManager, TabPageIndicator tabPageIndicator) {
        // this.activity = activity;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.tabPageIndicator = tabPageIndicator;
        this.slidingMenu = slidingMenu;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflater the layout
        View view = inflater.inflate(R.layout.view_content, null);
        initView(view);
        return view;
    }

    private void initView(View view) {

        mViewPager = (ViewPager) view.findViewById(R.id.content_viewpager);
        mViewPager.setOffscreenPageLimit(6);
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(fragmentManager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        tabPageIndicator.setViewPager(mViewPager);
        tabPageIndicator.setOnPageChangeListener(new MyOnPageChangeListener());

    }

    public void setContactListener(IContactListener contactListener) {
        this.contactListener = contactListener;
    }

    public interface IContactListener {
        public void contact(int result, Object obj);
    }

    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {
        public HotFragment hotFragment;
        public FriendsFragment friendsFragment;
        public SquareFragment squareFragment;

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
            hotFragment = new HotFragment(context);
            friendsFragment = new FriendsFragment(context, tabPageIndicator);
            squareFragment = new SquareFragment(context);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int arg0) {
            // TODO Auto-generated method stub
            switch (arg0) {
                case 0:
                    return hotFragment;
                case 1:
                    return friendsFragment;
                case 2:
                    return squareFragment;
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

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
//			tabPageIndicator.setCurrentItem(arg0);
            Log.v("MyOnPageChangeListener", "onPageSelected:" + arg0);
            mAppSectionsPagerAdapter.hotFragment.init(arg0);
            mAppSectionsPagerAdapter.squareFragment.init(arg0);
            mAppSectionsPagerAdapter.friendsFragment.init(arg0);
        }

    }

}
