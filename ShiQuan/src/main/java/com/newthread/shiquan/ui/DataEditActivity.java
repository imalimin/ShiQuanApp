package com.newthread.shiquan.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.newthread.shiquan.R;
import com.newthread.shiquan.bean.UpdateUserDate;
import com.newthread.shiquan.dao.MyRequest;
import com.newthread.shiquan.dao.PersonalDataUtil;
import com.newthread.shiquan.utils.SharedPreferencesManager;

import org.apache.http.HttpStatus;

public class DataEditActivity extends FragmentActivity {

    private TableRow nickName;
    private TableRow gender;
    private TableRow area;

    private TextView nickNameText;
    private TextView genderText;
    private TextView areaText;

    private Button savaBtn;
    private Button cancelBtn;
    private Button exit;

    private EditText editText;

    private String nickNameStr = "";
    private String genderStr = "";
    private String areaStr = "";
    private MyRequest myRequest = MyRequest.getInstance();
    private SharedPreferencesManager SPManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SPManager = new SharedPreferencesManager(this);
        setContentView(R.layout.activity_dataedit);

        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
        nickName = (TableRow) findViewById(R.id.dataedit_nickname);
        gender = (TableRow) findViewById(R.id.dataedit_gender);
        area = (TableRow) findViewById(R.id.dataedit_area);
        nickNameText = (TextView) findViewById(R.id.dataedit_nickname_text);
        genderText = (TextView) findViewById(R.id.dataedit_gender_text);
        areaText = (TextView) findViewById(R.id.dataedit_area_text);
        savaBtn = (Button) findViewById(R.id.dataedit_save);
        cancelBtn = (Button) findViewById(R.id.dataedit_cancel);
        exit = (Button) findViewById(R.id.dataedit_back);
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            nickNameStr = bundle.getString("NICKNAME");
            genderStr = bundle.getString("GENDER");
            areaStr = bundle.getString("AREA");

            nickNameText.setText(nickNameStr);
            genderText.setText(genderStr);
            areaText.setText(areaStr);

            nickName.setOnClickListener(mListener);
            gender.setOnClickListener(mListener);
            area.setOnClickListener(mListener);
            setRes();
        }
        savaBtn.setOnClickListener(mListener);
        cancelBtn.setOnClickListener(mListener);
    }

    private void setRes() {

    }

    private OnClickListener mListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.dataedit_nickname:
                    setMoreWindow(R.id.dataedit_nickname);
                    break;
                case R.id.dataedit_gender:
                    setGenderWindow();
                    break;
                case R.id.dataedit_area:
                    setMoreWindow(R.id.dataedit_area);
                    break;
                case R.id.dataedit_cancel:
                    finish();
                    break;
                case R.id.dataedit_save:
                    update();
                    break;
                default:
                    break;
            }
        }

    };

    private void update() {
        if (nickNameText.getText().equals("") || genderText.getText().equals("") || areaText.getText().equals("")) {
            Toast.makeText(DataEditActivity.this, "请填写完整信息！", Toast.LENGTH_SHORT).show();
            return;
        }
        UpdateUserDate updateUserDate = new UpdateUserDate(SPManager.readQQOpenid(),
                nickNameText.getText().toString(), genderText.getText().toString(), areaText.getText().toString());
        PersonalDataUtil.updateUserInfo(updateUserDate, new MyRequest.IRequestListener() {
            @Override
            public void onComplete(int result, String msg, Object obj) {
                if (result == HttpStatus.SC_OK) {
                    String content = new String((byte[]) obj);
                    Log.v("000000", content);
                    Toast.makeText(DataEditActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DataEditActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void setGenderWindow() {
        View convertView = LayoutInflater.from(this).inflate(
                R.layout.dialog_gender, null);
        RadioGroup radioGroup = (RadioGroup) convertView.findViewById(R.id.dialog_gender_group);
        RadioButton men = (RadioButton) convertView.findViewById(R.id.dialog_gender_men);
        RadioButton women = (RadioButton) convertView.findViewById(R.id.dialog_gender_women);
        if (genderText.getText().equals("男")) {
            men.setChecked(true);
            women.setChecked(false);
        } else {
            men.setChecked(false);
            women.setChecked(true);

        }
        final AlertDialog dialog = new AlertDialog.Builder(DataEditActivity.this)
                .setView(convertView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                }).setNegativeButton("取消", null).create();
        dialog.show();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.dialog_gender_men:
                        genderText.setText("男");
                        break;
                    case R.id.dialog_gender_women:
                        genderText.setText("女");
                        break;
                    default:
                }
                dialog.cancel();
            }
        });
    }

    protected void setMoreWindow(final int id) {
        // TODO Auto-generated method stub
        View convertView = LayoutInflater.from(this).inflate(
                R.layout.dialog_dataedit, null);
        editText = (EditText) convertView
                .findViewById(R.id.dialog_dataedit_edit);
        if (id == R.id.dataedit_nickname) {
            editText.setText(nickNameText.getText().toString());
        } else if (id == R.id.dataedit_gender) {
            editText.setText(genderText.getText().toString());
        } else if (id == R.id.dataedit_area) {
            editText.setText(areaText.getText().toString());
        }
        AlertDialog dialog = new AlertDialog.Builder(DataEditActivity.this)
                .setView(convertView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (id == R.id.dataedit_nickname) {
                            nickNameText.setText(editText.getText().toString());
                        } else if (id == R.id.dataedit_gender) {
                            genderText.setText(editText.getText().toString());
                        } else if (id == R.id.dataedit_area) {
                            areaText.setText(editText.getText().toString());
                        }
                    }
                }).setNegativeButton("取消", null).create();
        dialog.show();
    }
}
