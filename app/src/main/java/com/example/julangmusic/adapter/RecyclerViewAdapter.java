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
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.julangmusic.R;
import com.example.julangmusic.database.DBManager;
import com.example.julangmusic.entity.MusicInfo;
import com.example.julangmusic.service.MusicPlayerService;
import com.example.julangmusic.util.Constant;
import com.example.julangmusic.util.CustomAttrValueUtil;
import com.example.julangmusic.util.MyMusicUtil;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements SectionIndexer {
    private static final String TAG = "RecyclerViewAdapter";
    private List<MusicInfo> musicInfoList;
    private Context context;
    private DBManager dbManager;
    private OnItemClickListener onItemClickListener ;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View swipeContent;
        LinearLayout contentLl;
        TextView musicIndex;
        TextView musicName;
        TextView musicSinger;
        TextView letterIndex;
        ImageView menuIv;
        Button deleteBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            this.swipeContent = itemView.findViewById(R.id.swipemenu_layout);
            this.contentLl = itemView.findViewById(R.id.local_music_item_ll);
            this.musicName = itemView.findViewById(R.id.local_music_name);
            this.musicIndex = itemView.findViewById(R.id.local_index);
            this.musicSinger = itemView.findViewById(R.id.local_music_singer);
            this.letterIndex = itemView.findViewById(R.id.indext_head_tv);
            this.menuIv = itemView.findViewById(R.id.local_music_item_never_menu);
            this.deleteBtn = itemView.findViewById(R.id.swip_delete_menu_btn);
        }

    }

    public RecyclerViewAdapter(Context context, List<MusicInfo> musicInfoList) {
        this.context = context;
        this.musicInfoList = musicInfoList;
        this.dbManager = DBManager.getInstance(context);
    }


    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的item的位置
     *
     * @param section 点击的首字母
     * @return
     */
    public int getPositionForSection(int section) {
        Log.i(TAG, "getPositionForSection: section = "+section);
        for (int i = 0; i < getItemCount(); i++) {
            char firstChar = musicInfoList.get(i).getFirstLetter().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    @Override
    public int getSectionForPosition(int position) {
        return musicInfoList.get(position).getFirstLetter().charAt(0);
    }

    @Override
    public int getItemCount() {
        return musicInfoList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.local_music_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        final MusicInfo musicInfo = musicInfoList.get(position);
        holder.musicName.setText(musicInfo.getName());
        holder.musicIndex.setText("" + (position + 1));
        holder.musicSinger.setText(musicInfo.getSinger());

        int appbg = CustomAttrValueUtil.getAttrColorValue(R.attr.colorAccent,0xFFFA7298,context);
        int defaultTvColor = CustomAttrValueUtil.getAttrColorValue(R.attr.text_color,R.color.grey700,context);

        //设置歌曲被点击后的颜色
        if (musicInfo.getId() == MyMusicUtil.getIntShared(Constant.KEY_ID)){
            holder.musicName.setTextColor(appbg);
            holder.musicIndex.setTextColor(appbg);
            holder.musicSinger.setTextColor(appbg);
        }else {
            holder.musicName.setTextColor(defaultTvColor);
            holder.musicIndex.setTextColor(context.getResources().getColor(R.color.grey700));
            holder.musicSinger.setTextColor(context.getResources().getColor(R.color.grey700));
        }
        //首字母
        int section = getSectionForPosition(position);
        //首字母对应的第一首歌的位置
        int firstPosition = getPositionForSection(section);
        Log.i(TAG, "onBindViewHolder: section = "+section + "  firstPosition = "+firstPosition);
        //如果是首字母的第一首歌，那么就在该歌曲上方设置首字母可见，下面相同的歌曲的首字母就不再设置了
        if (firstPosition == position){
            Log.d(TAG,firstPosition+"*****"+position);
            holder.letterIndex.setVisibility(View.VISIBLE);
            holder.letterIndex.setText(""+musicInfo.getFirstLetter());
        }else{
            Log.d(TAG,firstPosition+"######"+position);
            holder.letterIndex.setVisibility(View.GONE);
        }

        //播放歌曲
        holder.contentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: 播放 "+musicInfo.getName());
                String path = dbManager.getMusicPath(musicInfo.getId());
                //隐式意图
                Intent intent = new Intent(MusicPlayerService.PLAYER_MANAGER_ACTION);
                intent.putExtra(Constant.COMMAND, Constant.COMMAND_PLAY);
                intent.putExtra(Constant.KEY_PATH, path);
                context.sendBroadcast(intent);
                MyMusicUtil.setShared(Constant.KEY_ID,musicInfo.getId());
                notifyDataSetChanged();
                if (onItemClickListener != null)
                    onItemClickListener.onContentClick(position);
            }
        });

        holder.menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onOpenMenuClick(position);
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onDeleteMenuClick(holder.swipeContent,holder.getAdapterPosition());
            }
        });
    }

    public void updateMusicInfoList(List<MusicInfo> musicInfoList) {
        this.musicInfoList.clear();
        this.musicInfoList.addAll(musicInfoList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onOpenMenuClick(int position);
        void onDeleteMenuClick(View content,int position);
        void onContentClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener ;
    }
}
