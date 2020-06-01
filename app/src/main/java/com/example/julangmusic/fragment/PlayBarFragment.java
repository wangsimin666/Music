package com.example.julangmusic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.julangmusic.R;
import com.example.julangmusic.activity.PlayActivity;
import com.example.julangmusic.database.DBManager;
import com.example.julangmusic.receiver.PlayerManagerReceiver;
import com.example.julangmusic.service.MusicPlayerService;
import com.example.julangmusic.util.Constant;
import com.example.julangmusic.util.MyMusicUtil;
import com.example.julangmusic.view.PlayingPopWindow;

/**
 * 播放显示条
 */
public class PlayBarFragment extends Fragment {
    private static final String TAG = "PlayBarFragment";
    public static final String ACTION_UPDATE_UI_PlayBar = "com.example.julangmusic.fragment.PlayBarFragment:action_update_ui_broad_cast";

    private HomeReceiver mReceiver;

    private LinearLayout playBarLl;
    //播放暂停键
    private ImageView playIv;
    private SeekBar seekBar;
    private ImageView nextIv;
    private ImageView menuIv;
    private TextView musicNameTv;
    private TextView singerNameTv;
    private DBManager dbManager;
    private View view;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = DBManager.getInstance(getActivity());
        Log.d(TAG,"*****"+getActivity());
        register();
    }

    /**
     * 重写onCreateView方法
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView: ");

        //调用:inflater.inflate()方法加载Fragment的布局文件
        //获取加载的view对象
        view = inflater.inflate(R.layout.fragment_playbar,container,false);

        playBarLl = view.findViewById(R.id.home_activity_playbar_ll);
        seekBar = view.findViewById(R.id.home_seekbar);
        playIv = view.findViewById(R.id.play_iv);
        menuIv = view.findViewById(R.id.play_menu_iv);
        nextIv = view.findViewById(R.id.next_iv);
        musicNameTv = view.findViewById(R.id.home_music_name_tv);
        singerNameTv = view.findViewById(R.id.home_singer_name_tv);

        setMusicName();
        initPlayIv();
        setFragmentBb();
        playBarLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlayActivity.class);
                startActivity(intent);
            }
        });

        playIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int musicId = MyMusicUtil.getIntShared(Constant.KEY_ID);
                if (musicId == -1 || musicId == 0) {
                    Intent intent = new Intent(Constant.MP_FILTER);
                    intent.putExtra(Constant.COMMAND, Constant.COMMAND_STOP);
                    getActivity().sendBroadcast(intent);
                    Toast.makeText(getActivity(), "歌曲不存在",Toast.LENGTH_SHORT).show();
                    return;
                }
                //如果当前媒体在播放音乐状态，则图片显示暂停图片，按下播放键，则发送暂停媒体命令，图片显示播放图片。以此类推。
                if (PlayerManagerReceiver.status == Constant.STATUS_PAUSE) {
                    Intent intent = new Intent(MusicPlayerService.PLAYER_MANAGER_ACTION);
                    intent.putExtra(Constant.COMMAND,Constant.COMMAND_PLAY);
                    getActivity().sendBroadcast(intent);
                }else if (PlayerManagerReceiver.status == Constant.STATUS_PLAY) {
                    Intent intent = new Intent(MusicPlayerService.PLAYER_MANAGER_ACTION);
                    intent.putExtra(Constant.COMMAND, Constant.COMMAND_PAUSE);
                    getActivity().sendBroadcast(intent);
                }else {
                    //为停止状态时发送播放命令，并发送将要播放歌曲的路径
                    String path = dbManager.getMusicPath(musicId);
                    Intent intent = new Intent(MusicPlayerService.PLAYER_MANAGER_ACTION);
                    intent.putExtra(Constant.COMMAND, Constant.COMMAND_PLAY);
                    intent.putExtra(Constant.KEY_PATH, path);
                    Log.i(TAG, "onClick: path = "+path);
                    getActivity().sendBroadcast(intent);
                }
            }
        });

        nextIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MyMusicUtil.playNextMusic(getActivity());
            }
        });

        menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopFormBottom();
            }
        });
        return view;
    }

    public static synchronized PlayBarFragment newInstance(){
        return new PlayBarFragment();
    }


    /**
     * 创建广播接收者，继承自BroadcastReceiver
     */
    class HomeReceiver extends BroadcastReceiver {

        int status;
        int duration;
        int current;

        //接收到广播，就会回调这个方法
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            setMusicName();
            status = intent.getIntExtra(Constant.STATUS,0);
            current = intent.getIntExtra(Constant.KEY_CURRENT,0);
            duration = intent.getIntExtra(Constant.KEY_DURATION,100);
            //根据歌曲播放状态选择
            switch (status){
                //停止状态
                case Constant.STATUS_STOP:
                    playIv.setSelected(false);
                    //设置进度为0
                    seekBar.setProgress(0);
                    break;
                //播放状态
                case Constant.STATUS_PLAY:
                    playIv.setSelected(true);
                    break;
                //暂停状态
                case Constant.STATUS_PAUSE:
                    playIv.setSelected(false);
                    break;
                case Constant.STATUS_RUN:
                    playIv.setSelected(true);
                    seekBar.setMax(duration);
                    seekBar.setProgress(current);
                    break;
                default:
                    break;
            }

        }
    }

    private void setMusicName(){
        int musicId = MyMusicUtil.getIntShared(Constant.KEY_ID);
        if (musicId == -1){
            musicNameTv.setText("巨浪音乐");
            singerNameTv.setText("好音质");
        }else{
            musicNameTv.setText(dbManager.getMusicInfo(musicId).get(1));
            singerNameTv.setText(dbManager.getMusicInfo(musicId).get(2));
        }
    }

    public void showPopFormBottom() {
        PlayingPopWindow playingPopWindow = new PlayingPopWindow(getActivity());
//      设置Popupwindow显示位置（从底部弹出）
        playingPopWindow.showAtLocation(view, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        //当弹出Popupwindow时，背景变半透明
        params.alpha=0.7f;
        getActivity().getWindow().setAttributes(params);

        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        playingPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                params.alpha=1f;
                getActivity().getWindow().setAttributes(params);
            }
        });

    }



    private void register() {
        IntentFilter intentFilter = new IntentFilter();
        //设置频道
        intentFilter.addAction(ACTION_UPDATE_UI_PlayBar);

        mReceiver = new HomeReceiver();

        //注册广播
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    private void unRegister() {
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    public void setFragmentBb(){
        //获取播放控制栏颜色
        int defaultColor = 0xFFFFFF;
        int[] attrsArray = {R.attr.play_bar_color };
        TypedArray typedArray = context.obtainStyledAttributes(attrsArray);
        int color = typedArray.getColor(0, defaultColor);
        typedArray.recycle();
        playBarLl.setBackgroundColor(color);
    }

    private void initPlayIv(){
        int status = PlayerManagerReceiver.status;
        switch (status) {
            case Constant.STATUS_STOP:
                playIv.setSelected(false);
                break;
            case Constant.STATUS_PLAY:
                playIv.setSelected(true);
                break;
            case Constant.STATUS_PAUSE:
                playIv.setSelected(false);
                break;
            case Constant.STATUS_RUN:
                playIv.setSelected(true);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegister();
    }
}
