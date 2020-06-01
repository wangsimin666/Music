package com.example.julangmusic.activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.julangmusic.R;
import com.example.julangmusic.database.DBManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 主程序入口
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private ImageView mImageView;
    private DBManager mDBManager;
    private SharedPreferences sharepreferences;
    private SharedPreferences.Editor mEditor;

    // Storage Permissions
    private static final int PERMISSON_REQUESTCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //找到控件
        mImageView = findViewById(R.id.welcome_iv);

        //获取DBManager对象
        mDBManager = DBManager.getInstance(getApplicationContext());

        initPermission();
    }

    private void initPermission() {
        //判断Andriod SDK版本号
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            checkSkip();
            Log.d(TAG,"version = "+Build.VERSION.SDK_INT);
            return;
        }

        /**
         * 检查用户是否授权,若没有获得，则请求权限
         * 授权：PackageManager.PERMISSION_GRANTED
         * 没有授权：PackageManager.PERMISSION_DENIED
         *
         * 传递的权限READ_EXTERNAL_STORAGE：读取外部存储
         */
        String[] READ_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED){
            //请求权限
            ActivityCompat.requestPermissions(MainActivity.this,READ_EXTERNAL_STORAGE,PERMISSON_REQUESTCODE);
        }else{
            checkSkip();
        }
    }

    /**
     * 定时器
     *
     * 设定停留在封面的时间，再跳转到下一个activity
     */
    private void checkSkip() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startMusicActivity();
            }
        };
        timer.schedule(task, 1000);
    }

    //显示意图，跳转到HomeActivity
    private void startMusicActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSON_REQUESTCODE:
                //同意授权
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    checkSkip();
                }else {
                    Toast.makeText(this,"必须同意所有权限才能使用本程序！",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
}
