package com.example.julangmusic.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.example.julangmusic.R;
import com.example.julangmusic.database.DBManager;
import com.example.julangmusic.entity.MusicInfo;
import com.example.julangmusic.service.MusicPlayerService;
import com.example.julangmusic.util.ChineseToEnglish;
import com.example.julangmusic.util.Constant;
import com.example.julangmusic.util.CustomAttrValueUtil;
import com.example.julangmusic.util.MyMusicUtil;
import com.example.julangmusic.util.SelectorUtil;
import com.example.julangmusic.view.ScanView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * created by 马宏彪
 * 创建日期：2020-5-14
 * 功能：扫描音乐
 */
public class ScanActivity extends BaseActivity{
    private static final String TAG = "ScanActivity";
    private DBManager dbManager;
    private Toolbar toolbar;
    private Button scanBtn;
    private TextView scanProgressTv;
    private TextView scanPathTv;
    private TextView scanCountTv;
    private CheckBox filterCb;
    private ScanView scanView;
    private Handler handler;
    private Message msg;
    private String scanPath;
    private int progress = 0;
    private int musicCount = 0;
    private boolean scanning = false;
    private int curMusicId;
    private String curMusicPath;
    private List<MusicInfo> musicInfoList;

    //功能：子类需重写的方法，一般做必要的初始化工作
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        dbManager = DBManager.getInstance(ScanActivity.this);
        scanBtn = findViewById(R.id.start_scan_btn);
        setScanBtnBg();
        toolbar = findViewById(R.id.scan_music_toolbar);
        scanProgressTv = findViewById(R.id.scan_progress);
        scanCountTv = findViewById(R.id.scan_count);
        scanPathTv = findViewById(R.id.scan_path);
        filterCb = findViewById(R.id.scan_filter_cb);
        scanView = findViewById(R.id.scan_view);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!scanning) {
                    scanPathTv.setVisibility(View.VISIBLE);
                    scanning = true;
                    startScanLocalMusic();
                    scanView.start();
                    scanBtn.setText("停止扫描");
                } else {
                    scanPathTv.setVisibility(View.GONE);
                    scanning = false;
                    scanView.stop();
                    scanCountTv.setText("");
                    scanBtn.setText("开始扫描");
                }
            }
        });


        handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case Constant.SCAN_NO_MUSIC:
                        Toast.makeText(ScanActivity.this,"本地没有歌曲，快去下载吧",Toast.LENGTH_SHORT).show();
                        scanComplete();
                        break;
                    case Constant.SCAN_ERROR:
                        Toast.makeText(ScanActivity.this, "数据库错误", Toast.LENGTH_LONG).show();
                        scanComplete();
                        break;
                    case Constant.SCAN_COMPLETE:
                        initCurPlaying();
                        scanComplete();
                        break;
                    case Constant.SCAN_UPDATE:
//                        int updateProgress = msg.getData().getInt("progress");
                        String path = msg.getData().getString("scanPath");
                        scanCountTv.setText("已扫描到" + progress + "首歌曲");
                        scanPathTv.setText(path);
                        break;
                }
            }
        };

    }

    //扫描完成，设置按钮为完成
    private void scanComplete(){
        scanBtn.setText("完成");
        scanning = false;
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!scanning){
                    ScanActivity.this.finish();
                }
            }
        });
        scanView.stop();
    }
    //扫描手机本地音乐
    public void startScanLocalMusic() {
        new Thread() {

            @Override
            public void run() {
                super.run();
                try {
                    String[] muiscInfoArray = new String[]{
                            MediaStore.Audio.Media.TITLE,               //歌曲名称
                            MediaStore.Audio.Media.ARTIST,              //歌曲歌手
                            MediaStore.Audio.Media.ALBUM,               //歌曲的专辑名
                            MediaStore.Audio.Media.DURATION,            //歌曲时长
                            MediaStore.Audio.Media.DATA};               //歌曲文件的全路径
                    Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            muiscInfoArray, null, null, null);
                    if (cursor!= null && cursor.getCount() != 0){
                        musicInfoList = new ArrayList<MusicInfo>();
                        Log.i(TAG, "run: cursor.getCount() = " + cursor.getCount());
                        while (cursor.moveToNext()) {
                            if (!scanning){
                                return;
                            }
                            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
                            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
                            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                            String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));

                            if (filterCb.isChecked() && duration != null && Long.valueOf(duration) < 1000 * 60){
                                Log.e(TAG, "run: name = "+name+" duration < 1000 * 60" );
                                continue;
                            }

                            File file = new File(path);
                            String parentPath = file.getParentFile().getPath();

                            name = replaseUnKnowe(name);
                            singer = replaseUnKnowe(singer);
                            album = replaseUnKnowe(album);
                            path = replaseUnKnowe(path);

                            MusicInfo musicInfo = new MusicInfo();

                            musicInfo.setName(name);
                            musicInfo.setSinger(singer);
                            musicInfo.setAlbum(album);
                            musicInfo.setPath(path);
                            Log.e(TAG, "run: parentPath = "+parentPath );
                            musicInfo.setParentPath(parentPath);
                            //设置首字母
                            musicInfo.setFirstLetter(ChineseToEnglish.StringToPinyinSpecial(name).toUpperCase().charAt(0)+"");

                            musicInfoList.add(musicInfo);
                            progress++;
                            scanPath = path;
                            musicCount = cursor.getCount();
                            msg = new Message();    //每次都必须new，必须发送新对象，不然会报错
                            msg.what = Constant.SCAN_UPDATE;
                            msg.arg1 = musicCount;
//                                Bundle data = new Bundle();
//                                data.putInt("progress", progress);
//                                data.putString("scanPath", scanPath);
//                                msg.setData(data);
                            handler.sendMessage(msg);  //更新UI界面
                            try {
                                sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        //扫描完成获取一下当前播放音乐及路径
                        curMusicId = MyMusicUtil.getIntShared(Constant.KEY_ID);
                        curMusicPath = dbManager.getMusicPath(curMusicId);

                        // 根据a-z进行排序源数据
                        Collections.sort(musicInfoList, new Comparator<MusicInfo>() {
                            @Override
                            public int compare(MusicInfo o1, MusicInfo o2) {
                                return o1.getFirstLetter().compareTo(o2.getFirstLetter());
                            }
                        });
                        dbManager.updateAllMusic(musicInfoList);

                        //扫描完成
                        msg = new Message();
                        msg.what = Constant.SCAN_COMPLETE;
                        handler.sendMessage(msg);  //更新UI界面

                    }else {
                        msg = new Message();
                        msg.what = Constant.SCAN_NO_MUSIC;
                        handler.sendMessage(msg);  //更新UI界面
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "run: error = ",e );
                    //扫描出错
                    msg = new Message();
                    msg.what = Constant.SCAN_ERROR;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    //设置没有找到的歌手、歌名、专辑、文件夹为未知
    public static String replaseUnKnowe(String oldStr){
        try {
            if (oldStr != null){
                if (oldStr.equals("<unknown>")){
                    oldStr = oldStr.replaceAll("<unknown>", "未知");
                }
            }
        }catch (Exception e){
            Log.e(TAG, "replaseUnKnowe: error = ",e );
        }
        return oldStr;
    }

    //设置开始扫描按钮颜色
    private void setScanBtnBg(){
        //获取当前主题颜色
        int defColor = CustomAttrValueUtil.getAttrColorValue(R.attr.colorAccent,R.color.colorAccent,this);
        int pressColor = CustomAttrValueUtil.getAttrColorValue(R.attr.press_color,R.color.colorAccent,this);
        Drawable backgroundDrawable =  scanBtn.getBackground();
        StateListDrawable sld = (StateListDrawable) backgroundDrawable;// 通过向下转型，转回原型，selector对应的Java类为：StateListDrawable
        SelectorUtil.changeViewColor(sld,new int[]{pressColor,defColor});
    }

    //初始化当前播放音乐，有可能当前正在播放音乐已经被过滤掉了
    private void initCurPlaying(){
        try {
            boolean contain = false;
            int id = 1;
            if (musicInfoList != null){
                for (MusicInfo info : musicInfoList){
                    Log.d(TAG, "initCurPlaying: info.getPath() = "+info.getPath());
                    Log.d(TAG, "initCurPlaying: curMusicPath = "+ curMusicPath);
                    if (info.getPath().equals(curMusicPath)){
                        contain = true;
                        Log.d(TAG, "initCurPlaying: musicInfoList.indexOf(info) = "+musicInfoList.indexOf(info));
                        id = musicInfoList.indexOf(info) + 1;
                    }

                }
            }
            if (contain){
                Log.d(TAG, "initCurPlaying: contains");
                Log.d(TAG, "initCurPlaying: id = "+id);
                MyMusicUtil.setShared(Constant.KEY_ID, id);
            }else {
                Log.d(TAG, "initCurPlaying: !!!contains");
                Intent intent = new Intent(MusicPlayerService.PLAYER_MANAGER_ACTION);
                intent.putExtra(Constant.COMMAND, Constant.COMMAND_STOP);
                sendBroadcast(intent);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
