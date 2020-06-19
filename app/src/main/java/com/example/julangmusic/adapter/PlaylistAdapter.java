package com.example.julangmusic.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.julangmusic.R;
import com.example.julangmusic.database.DBManager;
import com.example.julangmusic.entity.MusicInfo;
import com.example.julangmusic.entity.PlayListInfo;
import com.example.julangmusic.service.MusicPlayerService;
import com.example.julangmusic.util.Constant;
import com.example.julangmusic.util.MyMusicUtil;

import java.util.List;

/**
 *
 * @ClassName:     PlaylistAdapter
 * @Description:   歌单资源的适配器
 * @Author:        ydl
 * @date          2020/5/15
 *
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private static final String TAG = "PlaylistAdapter";
    private List<MusicInfo> musicInfoList;
    private Context context;
    private DBManager dbManager;
    private PlayListInfo playListInfo;
    private PlaylistAdapter.OnItemClickListener onItemClickListener;

    public PlaylistAdapter(Context context, PlayListInfo playListInfo, List<MusicInfo> musicInfoList) {
        this.context = context;
        this.playListInfo = playListInfo;
        this.dbManager = DBManager.getInstance(context);
        this.musicInfoList = musicInfoList;
    }

    @Override
    public int getItemCount() {
        return musicInfoList.size();
    }

    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.local_music_item, parent, false);
        PlaylistAdapter.ViewHolder viewHolder = new PlaylistAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PlaylistAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: position = " + position);
        //加载歌曲信息
        final MusicInfo musicInfo = musicInfoList.get(position);
        holder.musicName.setText(musicInfo.getName());
        holder.musicIndex.setText("" + (position + 1));
        holder.musicSinger.setText(musicInfo.getSinger());
        if (musicInfo.getId() == MyMusicUtil.getIntShared(Constant.KEY_ID)) {
            holder.musicName.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.musicIndex.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.musicSinger.setTextColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.musicName.setTextColor(context.getResources().getColor(R.color.grey700));
            holder.musicIndex.setTextColor(context.getResources().getColor(R.color.grey700));
            holder.musicSinger.setTextColor(context.getResources().getColor(R.color.grey700));
        }

        //播放选中的音乐
        holder.contentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: 播放 " + musicInfo.getName());
                String path = dbManager.getMusicPath(musicInfo.getId());
                //开启意图
                Intent intent = new Intent(MusicPlayerService.PLAYER_MANAGER_ACTION);
                intent.putExtra(Constant.COMMAND, Constant.COMMAND_PLAY);
                intent.putExtra(Constant.KEY_PATH, path);
                context.sendBroadcast(intent);
                //存储列表信息
                MyMusicUtil.setShared(Constant.KEY_ID, musicInfo.getId());
                MyMusicUtil.setShared(Constant.KEY_LIST, Constant.LIST_PLAYLIST);
                MyMusicUtil.setShared(Constant.KEY_LIST_ID, playListInfo.getId());
                notifyDataSetChanged();
            }
        });

        holder.menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onOpenMenuClick(position);
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onDeleteMenuClick(holder.swipeContent, holder.getAdapterPosition());
            }
        });
    }

    /**
     * 更新歌曲列表
     * @param musicInfoList
     */
    public void updateMusicInfoList(List<MusicInfo> musicInfoList) {
        this.musicInfoList.clear();
        this.musicInfoList.addAll(musicInfoList);
        //通知
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(PlaylistAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 歌曲点击事件（菜单、删除）接口，在PlaylistActivity实现
     */
    public interface OnItemClickListener {
        void onOpenMenuClick(int position);

        void onDeleteMenuClick(View content, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View swipeContent;
        LinearLayout contentLl;
        TextView musicIndex;
        TextView musicName;
        TextView musicSinger;
        ImageView menuIv;
        Button deleteBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            this.swipeContent = itemView.findViewById(R.id.swipemenu_layout);
            this.contentLl = itemView.findViewById(R.id.local_music_item_ll);
            this.musicName = itemView.findViewById(R.id.local_music_name);
            this.musicIndex = itemView.findViewById(R.id.local_index);
            this.musicSinger = itemView.findViewById(R.id.local_music_singer);
            this.menuIv = itemView.findViewById(R.id.local_music_item_never_menu);
            this.deleteBtn = itemView.findViewById(R.id.swip_delete_menu_btn);
        }

    }
}
