package com.newthread.shiquan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TableRow;

import com.newthread.shiquan.R;
import com.newthread.shiquan.utils.SharedPreferencesManager;

public class SettingActivity extends FragmentActivity {
    private final static String TAG="SettingActivity";
    private static final String[] TITLE_CONTENT = new String[]{"我的视频", "我的动态"};
	private Switch newMessageNoticeSwitch;
	private Switch wifiAutoPlaySwitch;
	private Button exit;
    private TableRow shake;
    private TableRow feedback;
    private TableRow about;
	private SharedPreferencesManager SPManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SPManager = new SharedPreferencesManager(SettingActivity.this);
		setContentView(R.layout.activity_setting);

		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		newMessageNoticeSwitch = (Switch) findViewById(R.id.setting_newmessage_notice_switch);
		wifiAutoPlaySwitch = (Switch) findViewById(R.id.setting_wifiautoplay_switch);
        feedback = (TableRow) findViewById(R.id.setting_feedbck);
        shake = (TableRow) findViewById(R.id.setting_shake);
        about = (TableRow) findViewById(R.id.setting_about);
		exit = (Button)findViewById(R.id.setting_back);
		exit.setOnClickListener(mListener);
        shake.setOnClickListener(mListener);
        feedback.setOnClickListener(mListener);
        about.setOnClickListener(mListener);
		initSetting();
		newMessageNoticeSwitch
				.setOnCheckedChangeListener(mOnCheckedChangeListener);
		wifiAutoPlaySwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
	}

    public View.OnClickListener mListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.setting_back:
                    finish();
                    break;
                case R.id.setting_shake:
                    intent = new Intent(SettingActivity.this, GestureActivity.class);
                    startActivity(intent);
                    break;
                case R.id.setting_feedbck:
                    intent = new Intent(SettingActivity.this, FeedBackActivity.class);
                    startActivity(intent);
                    break;
                case R.id.setting_about:
                    intent = new Intent(SettingActivity.this, AboutActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };
	private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			switch (buttonView.getId()) {
			case R.id.setting_newmessage_notice_switch:
				break;
			case R.id.setting_wifiautoplay_switch:
				SPManager.saveWifiAutoPlay(isChecked);
				break;
			default:
				break;
			}
		}

	};

	private void initSetting() {
		// TODO Auto-generated method stub
		wifiAutoPlaySwitch.setChecked(SPManager.readWifiAutoPlay());
	}
}
