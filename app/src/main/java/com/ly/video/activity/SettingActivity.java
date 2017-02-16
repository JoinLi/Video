package com.ly.video.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ly.video.R;

/**
 * @author: cpacm
 * @date: 2016/10/21
 * @desciption: 设置界面
 */

public class SettingActivity extends AppCompatActivity {
    private CheckBox notify_checkbox;
    private TextView text_update;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            //设置返回按钮
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        text_update = (TextView) findViewById(R.id.text_update);
        notify_checkbox = (CheckBox) findViewById(R.id.notify_checkbox);
        notify_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    text_update.setText(getString(R.string.setting_open_update));
                } else {
                    text_update.setText(getString(R.string.setting_close_update));
                }
            }
        });
        SharedPreferences share = getSharedPreferences("setting", MODE_PRIVATE);
        boolean isRem = share.getBoolean("checked", false);
        notify_checkbox.setChecked(isRem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

//    private void checkedState(boolean b) {
//        if (b) {
//            text_update.setText(getString(R.string.setting_open_update));
//        } else {
//            text_update.setText(getString(R.string.setting_close_update));
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences user_share = getSharedPreferences("setting", MODE_PRIVATE);
        SharedPreferences.Editor setting_edit = user_share.edit();
        setting_edit.putBoolean("checked", notify_checkbox.isChecked());
        setting_edit.commit();
    }
}
