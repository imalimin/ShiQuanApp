package com.newthread.shiquan.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.Toast;

import com.newthread.shiquan.R;

public class GestureActivity extends ActionBarActivity {

    private Button exit;
    private TableRow circle;
    private TableRow left;
    private TableRow right;
    private TableRow heart;
    private TableRow lightning;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture);

        initView();
    }

    private void initView() {
        circle = (TableRow)findViewById(R.id.gesture_circle_row);
        left = (TableRow)findViewById(R.id.gesture_left_arrow_row);
        right = (TableRow)findViewById(R.id.gesture_right_arrow_row);
        heart = (TableRow)findViewById(R.id.gesture_heart_row);
        lightning = (TableRow)findViewById(R.id.gesture_lightning_row);
        circle.setOnClickListener(mListener);
        left.setOnClickListener(mListener);
        right.setOnClickListener(mListener);
        heart.setOnClickListener(mListener);
        lightning.setOnClickListener(mListener);
        exit = (Button)findViewById(R.id.gesture_back);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public View.OnClickListener mListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.gesture_circle_row:
                    intent = new Intent(GestureActivity.this,
                            ShakeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.gesture_left_arrow_row:
                    intent = new Intent(GestureActivity.this,
                            ShakeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.gesture_right_arrow_row:
                    intent = new Intent(GestureActivity.this,
                            ShakeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.gesture_heart_row:
                    intent = new Intent(GestureActivity.this,
                            ShakeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.gesture_lightning_row:
                    intent = new Intent(GestureActivity.this,
                            ShakeActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };
}
