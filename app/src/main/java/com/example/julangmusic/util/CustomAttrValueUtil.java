package com.example.julangmusic.util;

import android.content.Context;
import android.content.res.TypedArray;

public class CustomAttrValueUtil {
    public static int getAttrColorValue(int attr, int defaultColor, Context context) {

        int[] attrsArray = {attr};
        TypedArray typedArray = context.obtainStyledAttributes(attrsArray);
        int value = typedArray.getColor(0, defaultColor);
        typedArray.recycle();
        return value;
    }
}
