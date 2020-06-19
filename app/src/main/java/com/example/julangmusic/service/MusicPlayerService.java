package com.example.julangmusic.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.julangmusic.receiver.PlayerManagerReceiver;

/**
 *
 * @ClassName:     MusicPlayerService
 * @Description:   常驻在后台的播放服务
 * @Author:        王思敏
 * @date          2020/5/18
 *
 */
public class MusicPlayerService extends Service {
    private static final String TAG = "MusicPlayerService";

    public static final String PLAYER_MANAGER_ACTION = "com.example.julangmusic.service.MusicPlayerService.player.action";

    private PlayerManagerReceiver mReceiver;

    public MusicPlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        register();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
        unRegister();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }


    private void register() {
        mReceiver = new PlayerManagerReceiver(MusicPlayerService.this);
        //创建意图过滤器
        IntentFilter intentFilter = new IntentFilter();
        //设置频道
        intentFilter.addAction(PLAYER_MANAGER_ACTION);
        //注册广播
        registerReceiver(mReceiver, intentFilter);
    }

    //注销广播
    private void unRegister() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }


}
