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
import com.example.julangmusic.entity.SingerInfo;

import java.util.List;

/**
 * created by 马宏彪
 * 创建日期：2020-5-15
 * 功能：歌手适配器
 */
public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.ViewHolder>{
    private static final String TAG = "SingerAdapter";
    private List<SingerInfo> singerInfoList;
    private Context context;
    private DBManager dbManager;
    private OnItemClickListener onItemClickListener;

    public SingerAdapter(Context context, List<SingerInfo> singerInfoList) {
        this.context = context;
        this.singerInfoList = singerInfoList;
        this.dbManager = DBManager.getInstance(context);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View swipeContent;
        LinearLayout contentLl;
        ImageView singerIv;
        TextView singelName;
        TextView count;

        public ViewHolder(View itemView) {
            super(itemView);
            this.swipeContent = itemView.findViewById(R.id.model_swipemenu_layout);
            this.contentLl = itemView.findViewById(R.id.model_music_item_ll);
            this.singerIv = itemView.findViewById(R.id.model_head_iv);
            this.singelName = itemView.findViewById(R.id.model_item_name);
            this.count = itemView.findViewById(R.id.model_music_count);
        }

    }

    /**
     * 加载布局资源
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //加载布局资源
        View view = LayoutInflater.from(context).inflate(R.layout.local_model_rv_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /**
     *将每个子项holder绑定数据
     *
     * @param holder 相当于列表中的每一行
     * @param position 列表中的位置，从0开始
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d("aaaa", "onBindViewHolder: position = "+position);
        SingerInfo singer = singerInfoList.get(position);
        holder.singelName.setText(singer.getName());
        holder.singerIv.setImageResource(R.drawable.singer);
        holder.count.setText(singer.getCount()+"首");
        holder.contentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入歌单页面
                onItemClickListener.onContentClick(holder.contentLl,position);
                Log.d(TAG,"onContentClick的方法"+position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return singerInfoList.size();
    }

    //单击每一项，接口
    public interface OnItemClickListener{
        void onDeleteMenuClick(View content,int position);
        void onContentClick(View content,int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener ;
    }
}
