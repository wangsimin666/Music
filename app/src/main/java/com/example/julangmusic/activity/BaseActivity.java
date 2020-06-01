package com.example.julangmusic.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.julangmusic.R;
import com.example.julangmusic.util.MyMusicUtil;

/**
 * created by 王思敏
 *
 * 音乐主题皮肤
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化主题
        initTheme();
    }

    private void initTheme() {
        int themeId = MyMusicUtil.getTheme(BaseActivity.this);
        switch (themeId){
            default:
            case 0:
                setTheme(R.style.SeaBlueTheme);
                break;
            case 1:
                setTheme(R.style.ZhiHuBlueTheme);
                break;
            case 2:
                setTheme(R.style.KuAnGreenTheme);
                break;
            case 3:
                setTheme(R.style.CloudRedTheme);
                break;
            case 4:
                setTheme(R.style.TengLuoPurpleTheme);
                break;
            case 5:
                setTheme(R.style.GrassGreenTheme);
                break;
            case 6:
                setTheme(R.style.BiLiPinkTheme);
                break;
            case 7:
                setTheme(R.style.CoffeeBrownTheme);
                break;
            case 8:
                setTheme(R.style.LemonOrangeTheme);
                break;
            case 9:
                setTheme(R.style.StartSkyGrayTheme);
                break;
        }
    }
}
