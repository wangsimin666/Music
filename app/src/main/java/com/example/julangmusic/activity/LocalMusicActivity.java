package com.example.julangmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.julangmusic.R;
import com.example.julangmusic.fragment.AlbumFragment;
import com.example.julangmusic.fragment.FolderFragment;
import com.example.julangmusic.fragment.SingerFragment;
import com.example.julangmusic.fragment.SingleFragment;
import com.example.julangmusic.util.Constant;
import com.example.julangmusic.view.MyViewPager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class LocalMusicActivity extends PlayBarBaseActivity{
    private static final String TAG = "LocalMusicActivity";
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private MyViewPager viewPager;
    private MyAdapter fragmentAdapter;
    private List<String> titleList = new ArrayList<>(4);
    private List<Fragment> fragments = new ArrayList<>(4);
    private SingleFragment singleFragment;
    private SingerFragment singerFragment;
    private AlbumFragment albumFragment;
    private FolderFragment folderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        toolbar = findViewById(R.id.local_music_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Constant.LABEL_LOCAL);
        }
        init();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " );
    }


    private void init(){
        addTapData();
        viewPager = findViewById(R.id.local_viewPager);
        tabLayout = findViewById(R.id.local_tab);
        //获取FragmentManager，管理一级子fragment
        fragmentAdapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOffscreenPageLimit(2); //预加载页面数
        //填满一个屏幕，不可左右滑动
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //让每个标签平分TabLayout的全部宽度
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //点击TabLayout时ViewPager相应变化
        tabLayout.setupWithViewPager(viewPager);

    }


    //滑动布局
    private void addTapData() {
        titleList.add("单曲");
        titleList.add("歌手");
        titleList.add("专辑");
        titleList.add("文件夹");

        if (singleFragment == null) {
            singleFragment = new SingleFragment();
            fragments.add(singleFragment);
        }
        if (singerFragment == null) {
            singerFragment = new SingerFragment();
            fragments.add(singerFragment);
        }
        if (albumFragment == null) {
            albumFragment = new AlbumFragment();
            fragments.add(albumFragment);
        }
        if (folderFragment == null) {
            folderFragment = new FolderFragment();
            fragments.add(folderFragment);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.local_music_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.scan_local_menu){
            Intent intent = new Intent(LocalMusicActivity.this,ScanActivity.class);
            startActivity(intent);
        }else if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return true;
    }

    //滑屏切换
    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * 用来显示tab上的名字
         * @param position 给定的位置
         * @return 显示的标题
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        //设置每一个的内容
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        //设置有多少内容
        @Override
        public int getCount() {
            return fragments.size();
        }

    }
}
