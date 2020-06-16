package com.example.julangmusic.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.julangmusic.R;
import com.example.julangmusic.database.DBManager;
import com.example.julangmusic.entity.MusicInfo;
import com.example.julangmusic.entity.PlayListInfo;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.List;

/**
 *
 * @ClassName:     AddPlaylistWindow
 * @Description:   歌曲添加歌单的窗口
 * @Author:        ydl
 * @date          2020/5/12
 */
public class AddPlaylistWindow extends PopupWindow {

    private View view;
    private Activity activity;
    private MusicInfo musicInfo;
    private LinearLayout addLl;
    private ListView listView;
    private Adapter adapter;
    private List<PlayListInfo> dataList;
    private DBManager dbManager;

    public AddPlaylistWindow(Activity activity, MusicInfo musicInfo) {
        super(activity);
        this.activity = activity;
        this.musicInfo = musicInfo;
        dbManager = DBManager.getInstance(activity);
        dataList = dbManager.getMyPlayList();
        initView();
    }

    private void initView() {
        this.view = LayoutInflater.from(activity).inflate(R.layout.pop_add_playlist, null);
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高,不设置显示不出来
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        int height = (int) (size.y * 0.6);
        this.setHeight(height);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        this.setFocusable(true);
        // 设置外部可点击
        this.setOutsideTouchable(true);

        // 设置弹出窗体的背景
        this.setBackgroundDrawable(activity.getResources().getDrawable(R.color.colorWhite));

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.pop_window_animation);

        // 添加OnTouchListener监听判断获取触屏位置，如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = view.getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        listView = view.findViewById(R.id.pop_add_pl_lv);
        adapter = new Adapter();
        listView.setAdapter(adapter);

        addLl = view.findViewById(R.id.pop_add_playlist_ll);
        addLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加歌单
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                View view = LayoutInflater.from(activity).inflate(R.layout.dialog_create_playlist, null);
                final EditText playlistEt = (EditText) view.findViewById(R.id.dialog_playlist_name_et);
                builder.setView(view);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = playlistEt.getText().toString();
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
    }

    /**
     *
     * @ClassName:     AddPlaylistWindow
     * @Description:   适配器
     * @Author:        ydl
     *
     */
    private class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(activity).inflate(R.layout.play_list_view_item, null, false);
                holder.swipView = convertView.findViewById(R.id.play_list_content_swip_view);
                holder.contentLl = convertView.findViewById(R.id.play_list_content_ll);
                holder.coverIv = convertView.findViewById(R.id.play_list_cover_iv);
                holder.listName = convertView.findViewById(R.id.play_list_name_tv);
                holder.listCount = convertView.findViewById(R.id.play_list_music_count_tv);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.swipView.setSwipeEnable(false);

            //展现已有的歌单列表
            final PlayListInfo playListInfo = dataList.get(position);
            holder.listName.setText(playListInfo.getName());
            holder.listCount.setText(playListInfo.getCount() + "首");

            holder.contentLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断歌曲是否在歌单内
                    if (dbManager.isExistPlaylist(playListInfo.getId(), musicInfo.getId())) {
                        Toast.makeText(activity, "该歌单已存在该歌曲", Toast.LENGTH_SHORT).show();
                    } else {
                        dbManager.addToPlaylist(playListInfo.getId(), musicInfo.getId());
                        Toast.makeText(activity, "添加到歌单成功", Toast.LENGTH_SHORT).show();
                    }
                    dismiss();
                }
            });


            return convertView;
        }

        //更新列表
        public void updateDataList() {
            List<PlayListInfo> playListInfos;
            playListInfos = dbManager.getMyPlayList();
            dataList.clear();
            dataList.addAll(playListInfos);
            notifyDataSetChanged();
        }

        class Holder {
            SwipeMenuLayout swipView;
            LinearLayout contentLl;
            ImageView coverIv;
            TextView listName;
            TextView listCount;
        }
    }
}
