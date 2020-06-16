package com.example.julangmusic.util;

import android.content.Context;
import android.content.res.TypedArray;

/**
 *
 * @ClassName:     CustomAttrValueUtil
 * @Description:   自定义属性值工具
 * @Author:        ydl
 * @date          2020/5/18
 *
 */
public class CustomAttrValueUtil {
    public static int getAttrColorValue(int attr, int defaultColor, Context context) {

        int[] attrsArray = {attr};
        TypedArray typedArray = context.obtainStyledAttributes(attrsArray);
        int value = typedArray.getColor(0, defaultColor);
        typedArray.recycle();
        return value;
    }
}
