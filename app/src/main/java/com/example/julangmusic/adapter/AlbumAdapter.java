package com.example.julangmusic.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.julangmusic.R;
import com.example.julangmusic.database.DBManager;
import com.example.julangmusic.entity.AlbumInfo;

import java.util.List;

/**
 *
 * @ClassName:     AlbumAdapter
 * @Description:   专辑适配器
 * @Author:        王惠
 * @date          2020/5/16
 *
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private static final String TAG = "AlbumAdapter";
    private List<AlbumInfo> albumInfoList;
    private Context context;
    private DBManager dbManager;
    private AlbumAdapter.OnItemClickListener onItemClickListener;

    public AlbumAdapter(Context context, List<AlbumInfo> albumInfoList) {
        this.context = context;
        this.albumInfoList = albumInfoList;
        this.dbManager = DBManager.getInstance(context);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View swipeContent;
        LinearLayout contentLl;
        ImageView albumIv;
        TextView albumName;
        TextView count;
//        Button deleteBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            this.swipeContent = itemView.findViewById(R.id.model_swipemenu_layout);
            this.contentLl = itemView.findViewById(R.id.model_music_item_ll);
            this.albumIv = itemView.findViewById(R.id.model_head_iv);
            this.albumName = itemView.findViewById(R.id.model_item_name);
            this.count = itemView.findViewById(R.id.model_music_count);
        }

    }

    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.local_model_rv_item,parent,false);
        AlbumAdapter.ViewHolder viewHolder = new AlbumAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AlbumAdapter.ViewHolder holder, final int position) {
        Log.d("aaaa", "onBindViewHolder: position = "+position);
        AlbumInfo album = albumInfoList.get(position);
        holder.albumIv.setImageResource(R.drawable.album);
        holder.albumName.setText(album.getName());
        holder.count.setText(album.getCount()+"首 "+album.getSinger());
        holder.contentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onContentClick(holder.contentLl,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumInfoList.size();
    }

    public void update(List<AlbumInfo> albumInfoList) {
        this.albumInfoList.clear();
        this.albumInfoList.addAll(albumInfoList);
        notifyDataSetChanged();
    }

    /**
     * 实现方法在Fragment中
     */
    public interface OnItemClickListener{
        void onDeleteMenuClick(View content,int position);
        void onContentClick(View content,int position);
    }

    public void setOnItemClickListener(AlbumAdapter.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener ;
    }
}
