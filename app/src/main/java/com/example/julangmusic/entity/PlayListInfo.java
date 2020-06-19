package com.example.julangmusic.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * @ClassName:     PlayListInfo
 * @Description:   歌单实体类
 * @Author:        ydl
 * @date          2020/5/10
 *
 */
public class PlayListInfo implements Parcelable {

    public static final Creator<PlayListInfo> CREATOR = new Creator<PlayListInfo>() {
        @Override
        public PlayListInfo createFromParcel(Parcel source) {
            return new PlayListInfo(source);
        }

        @Override
        public PlayListInfo[] newArray(int size) {
            return new PlayListInfo[size];
        }
    };
    private int id;
    private String name;
    private int count;

    public PlayListInfo() {
    }

    protected PlayListInfo(Parcel in) {
        this.id = in.readInt();
        this.count = in.readInt();
        this.name = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.count);
        dest.writeString(this.name);
    }

}
