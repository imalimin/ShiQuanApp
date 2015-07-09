package com.newthread.shiquan.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.newthread.shiquan.R;
import com.newthread.shiquan.bean.FeedbackDate;
import com.newthread.shiquan.dao.MyRequest;
import com.newthread.shiquan.dao.NoticUtil;
import com.newthread.shiquan.utils.PhoneInfo;
import com.newthread.shiquan.utils.SharedPreferencesManager;

import org.apache.http.HttpStatus;

public class FeedBackActivity extends Activity {
    private final static String TAG = "FeedBackActivity";

    private Button exit;

    private EditText msgEdt;
    private EditText mailEdt;
    private EditText phoneEdt;
    private EditText qqEdt;
    private Button send;
    private SharedPreferencesManager SPManager;
    private MyRequest myRequest = MyRequest.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SPManager = new SharedPreferencesManager(this);
        setContentView(R.layout.activity_feedback);

        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
        exit = (Button) findViewById(R.id.feedback_back);
        msgEdt = (EditText) findViewById(R.id.feedback_message_edt);
        mailEdt = (EditText) findViewById(R.id.feedback_email_edt);
        phoneEdt = (EditText) findViewById(R.id.feedback_mobilephone_edt);
        qqEdt = (EditText) findViewById(R.id.feedback_qq_edt);
        send = (Button) findViewById(R.id.feedback_send);
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                sendMsg();
            }
        });
    }

    private void sendMsg() {
        if (msgEdt.getText().equals("")) {
            Toast.makeText(this, "请填写反馈信息", Toast.LENGTH_SHORT);
            return;
        }
        FeedbackDate feedbackDate = new FeedbackDate(msgEdt.getText().toString(), PhoneInfo.getTime("yyyy-MM-dd HH:mm:ss"),
                mailEdt.getText().toString(),phoneEdt.getText().toString(), qqEdt.getText().toString());
        NoticUtil.feedback(feedbackDate, new MyRequest.IRequestListener() {
            @Override
            public void onComplete(int result, String msg, Object obj) {
                if (result == HttpStatus.SC_OK) {
                    String content = new String((byte[]) obj);
                    Log.v(TAG, new String((byte[])obj));
                    Toast.makeText(FeedBackActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FeedBackActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
