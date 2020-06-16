package com.example.julangmusic.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.julangmusic.R;
import com.example.julangmusic.entity.ThemeInfo;
import com.example.julangmusic.util.MyMusicUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @ClassName:     ThemeActivity
 * @Description:   主题设置的Activity
 * @Author:        ydl
 * @date          2020/5/13
 *
 */
public class ThemeActivity extends BaseActivity {

    private static final String TAG = "ThemeActivity";
    public static int THEME_SIZE = 11;
    private String[] themeType = {"碧海蓝", "知乎蓝", "酷安绿", "网易红", "藤萝紫", "樱草绿", "哔哩粉", "咖啡棕", "柠檬橙", "星空灰"};
    private int[] colors = {R.color.seaBlue, R.color.zhihuBlue, R.color.kuanGreen, R.color.cloudRed,
            R.color.tengluoPurple, R.color.grassGreen, R.color.biliPink, R.color.coffeeBrown,
            R.color.lemonOrange, R.color.startSkyGray};
    private RecyclerView recyclerView;
    private ThemeAdapter adapter;
    private Toolbar toolbar;
    private int selectTheme = 0;
    private List<ThemeInfo> themeInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        selectTheme = MyMusicUtil.getTheme(ThemeActivity.this);
        toolbar = findViewById(R.id.theme_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    private void init() {
        for (int i = 0; i < themeType.length; i++) {
            ThemeInfo info = new ThemeInfo();
            info.setName(themeType[i]);
            info.setColor(colors[i]);
            info.setSelect((selectTheme == i) ? true : false);
            themeInfoList.add(info);
        }
        recyclerView = findViewById(R.id.theme_rv);
        adapter = new ThemeAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ThemeActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    //刷新主题
    private void refreshTheme(ThemeInfo themeInfo, int position) {
        selectTheme = position;
        MyMusicUtil.setTheme(ThemeActivity.this, position);
        toolbar.setBackgroundColor(getResources().getColor(themeInfo.getColor()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(themeInfo.getColor()));
        }
        for (ThemeInfo info : themeInfoList) {
            if (info.getName().equals(themeInfo.getName())) {
                info.setSelect(true);
            } else {
                info.setSelect(false);
            }
        }
        //修改适配器绑定的数组，不用重新刷新Activity
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ThemeActivity.this.finish();
    }

    private class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ViewHolder> {

        public ThemeAdapter() {
        }

        //负责承载每个子项的布局
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ThemeActivity.this).inflate(R.layout.change_theme_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        //负责将每个子项holder绑定数据
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final ThemeInfo themeInfo = themeInfoList.get(position);
            holder.selectBtn.setPadding(0, 0, 0, 0);
            if (themeInfo.isSelect()) {
                holder.circleIv.setImageResource(R.drawable.tick);
                holder.selectBtn.setText("使用中");
                holder.selectBtn.setTextColor(getResources().getColor(themeInfo.getColor()));
            } else {
                holder.circleIv.setImageBitmap(null);
                holder.selectBtn.setText("使用");
                holder.selectBtn.setTextColor(getResources().getColor(R.color.grey500));
            }
            holder.circleIv.setBackgroundResource(themeInfo.getColor());
            holder.nameTv.setTextColor(getResources().getColor(themeInfo.getColor()));
            holder.nameTv.setText(themeInfo.getName());
            holder.selectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshTheme(themeInfo, position);
                }
            });
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshTheme(themeInfo, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return themeInfoList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            RelativeLayout relativeLayout;
            ImageView circleIv;
            TextView nameTv;
            Button selectBtn;

            public ViewHolder(View itemView) {
                super(itemView);
                this.relativeLayout = itemView.findViewById(R.id.theme_item_rl);
                this.circleIv = itemView.findViewById(R.id.theme_iv);
                this.nameTv = itemView.findViewById(R.id.theme_name_tv);
                this.selectBtn = itemView.findViewById(R.id.theme_select_tv);
            }
        }
    }
}
