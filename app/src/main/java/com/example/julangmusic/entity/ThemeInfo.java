package com.example.julangmusic.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * @ClassName:     ThemeInfo
 * @Description:   主题实体类
 * @Author:        ydl
 * @date          2020/5/10
 *
 */
public class ThemeInfo implements Parcelable {
    public static final Creator<ThemeInfo> CREATOR = new Creator<ThemeInfo>() {
        @Override
        public ThemeInfo createFromParcel(Parcel source) {
            return new ThemeInfo(source);
        }

        @Override
        public ThemeInfo[] newArray(int size) {
            return new ThemeInfo[size];
        }
    };
    private String name;
    private int color;
    private boolean isSelect;

    public ThemeInfo() {
    }

    protected ThemeInfo(Parcel in) {
        this.name = in.readString();
        this.color = in.readInt();
        this.isSelect = in.readByte() != 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.color);
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
    }
}
