package com.example.julangmusic.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatDelegate;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.julangmusic.service.MusicPlayerService;

/**
 * Application类是在APP启动的时候就启动，启动在所有Activity之前
 *
 * 资源初始化
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        //获取应用的上下文
        context = getApplicationContext();

        //显式意图
        Intent startIntent = new Intent(MyApplication.this, MusicPlayerService.class);
        //开启服务
        startService(startIntent);
        //initNightMode();
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);


    }


    //返回应用的上下文
    public static Context getContext() {
        return context;
    }
}
