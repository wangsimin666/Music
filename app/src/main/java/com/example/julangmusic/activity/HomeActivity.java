package com.example.julangmusic.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.example.julangmusic.R;
import com.example.julangmusic.adapter.HomeListViewAdapter;
import com.example.julangmusic.database.DBManager;
import com.example.julangmusic.entity.PlayListInfo;
import com.example.julangmusic.service.MusicPlayerService;
import com.example.julangmusic.util.Constant;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.List;

/**
 * created by 王思敏
 *
 * 主界面
 */
public class HomeActivity extends PlayBarBaseActivity {
    private static final String TAG = "HomeActivity";

    private DBManager dbManager;
    private DrawerLayout mDrawerLayout;
    private NavigationView navView;
    private ImageView navHeadIv;
    private LinearLayout localMusicLl;
    private LinearLayout lastPlayLl;
    private LinearLayout myLoveLl;
    private LinearLayout myListTitleLl;
    private Toolbar toolbar;
    private TextView localMusicCountTv;
    private TextView lastPlayCountTv;
    private TextView myLoveCountTv;
    private TextView myPLCountTv;
    private ImageView myPLArrowIv;
    private ImageView myPLAddIv;
    private ListView listView;
    private HomeListViewAdapter adapter;
    private List<PlayListInfo> playListInfos;
    private int count;
    private boolean isOpenMyPL = false; //标识我的歌单列表打开状态
    private long exitTime = 0;
    private boolean isStartTheme = false;

    private ImageView locationImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dbManager = DBManager.getInstance(HomeActivity.this);
        toolbar = findViewById(R.id.home_activity_toolbar);

        //toolbar代替actionbar
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //给左上角图标的左边加上一个返回的图标
            actionBar.setDisplayHomeAsUpEnabled(true);
            //自定义图标
            actionBar.setHomeAsUpIndicator(R.drawable.drawer_menu);
        }
        //设置选择Item监听
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //关闭所有出现的抽屉
                mDrawerLayout.closeDrawers();
                switch (item.getItemId()){
                    case R.id.nav_theme:
                        isStartTheme = true;
                        Intent intentTheme = new Intent(HomeActivity.this,ThemeActivity.class);
                        startActivity(intentTheme);
                        break;
                    case R.id.nav_about_me:
                        Intent aboutTheme = new Intent(HomeActivity.this,AboutActivity.class);
                        startActivity(aboutTheme);
                        break;
                    case R.id.nav_logout:
                        finish();
                        //隐式意图
                        Intent intentBroadcast = new Intent(MusicPlayerService.PLAYER_MANAGER_ACTION);
                        intentBroadcast.putExtra(Constant.COMMAND, Constant.COMMAND_RELEASE);
                        sendBroadcast(intentBroadcast);
                        Intent stopIntent = new Intent(HomeActivity.this,MusicPlayerService.class);
                        //关闭程序
                        stopService(stopIntent);
                        break;
                }
                return true;
            }
        });
        init();

        Intent startIntent = new Intent(HomeActivity.this,MusicPlayerService.class);
        startService(startIntent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        count = dbManager.getMusicCount(Constant.LIST_ALLMUSIC);
        localMusicCountTv.setText(count + "");
        count = dbManager.getMusicCount(Constant.LIST_MYLOVE);
        myLoveCountTv.setText(count + "");
        count = dbManager.getMusicCount(Constant.LIST_MYPLAY);
        myPLCountTv.setText("(" + count + ")");
        adapter.updateDataList();
    }

    private void init(){
        localMusicLl = findViewById(R.id.home_local_music_ll);
        myLoveLl = findViewById(R.id.home_my_love_music_ll);
        myListTitleLl = findViewById(R.id.home_my_list_title_ll);
        listView = findViewById(R.id.home_my_list_lv);
        localMusicCountTv = findViewById(R.id.home_local_music_count_tv);
        myLoveCountTv = findViewById(R.id.home_my_love_music_count_tv);
        myPLCountTv = findViewById(R.id.home_my_list_count_tv);
        myPLArrowIv = findViewById(R.id.home_my_pl_arror_iv);
        myPLAddIv = findViewById(R.id.home_my_pl_add_iv);
        //定位
        locationImg = findViewById(R.id.loc_img);
        locationImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,LocationActivity.class);
                startActivity(intent);
            }
        });


        //本地音乐点击事件
        localMusicLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,LocalMusicActivity.class);
                startActivity(intent);
            }
        });

        //我的喜爱点击事件
        myLoveLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,LastMyloveActivity.class);
                intent.putExtra(Constant.LABEL,Constant.LABEL_MYLOVE);
                startActivity(intent);
            }
        });

        playListInfos = dbManager.getMyPlayList();
        adapter = new HomeListViewAdapter(playListInfos,this,dbManager);
        listView.setAdapter(adapter);
        myPLAddIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加歌单
                final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.dialog_create_playlist,null);
                final EditText playlistEt = (EditText)view.findViewById(R.id.dialog_playlist_name_et);
                builder.setView(view);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = playlistEt.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(HomeActivity.this,"请输入歌单名",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dbManager.createPlaylist(name);
                        dialog.dismiss();
                        adapter.updateDataList();
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();//配置好后再builder show
            }
        });
        myListTitleLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //展现我的歌单
                if (isOpenMyPL){
                    isOpenMyPL = false;
                    myPLArrowIv.setImageResource(R.drawable.arrow_right);
                    listView.setVisibility(View.GONE);
                }else {
                    isOpenMyPL = true;
                    myPLArrowIv.setImageResource(R.drawable.arrow_down);
                    listView.setVisibility(View.VISIBLE);
                    playListInfos = dbManager.getMyPlayList();
                    adapter = new HomeListViewAdapter(playListInfos,HomeActivity.this,dbManager);
                    listView.setAdapter(adapter);
                }
            }
        });
    }

    public void updatePlaylistCount(){
        count = dbManager.getMusicCount(Constant.LIST_MYPLAY);
        myPLCountTv.setText("(" + count + ")");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isStartTheme){
            HomeActivity.this.finish();
        }
        isStartTheme = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次切换到桌面", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                moveTaskToBack(true);
            }
            return true;
        }
        finish();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
