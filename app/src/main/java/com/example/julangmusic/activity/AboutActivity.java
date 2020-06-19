package com.example.julangmusic.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.example.julangmusic.R;


/**
 * created by 马宏彪
 * 创建日期：2020-5-10
 * 功能：关于信息
 */
public class AboutActivity extends BaseActivity{
    private Toolbar toolbar;

    //功能：子类需重写的方法，一般做必要的初始化工作
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        init();
    }

    //初始化控件
    private void init(){
        toolbar = findViewById(R.id.about_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //重写的菜单响应事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }
}
