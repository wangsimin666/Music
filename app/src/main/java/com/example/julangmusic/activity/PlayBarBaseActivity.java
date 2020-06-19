package com.example.julangmusic.activity;

import android.os.Bundle;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.example.julangmusic.fragment.PlayBarFragment;
import com.example.julangmusic.R;

/**
 *
 * @ClassName:     PlayBarBaseActivity
 * @Description:   播放显示条Activity
 * @Author:        王思敏
 * @date          2020/5/22
 *
 */
public abstract class PlayBarBaseActivity extends BaseActivity{

    private PlayBarFragment playBarFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        show();
    }

    /**
     * Fragment和activity的绑定
     */
    private void show(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (playBarFragment == null) {
            playBarFragment = PlayBarFragment.newInstance();
            ft.add(R.id.fragment_playbar, playBarFragment).commit();
        }else {
            ft.show(playBarFragment).commit();
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
