package com.qintingfm.explayer.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Media {
    @PrimaryKey(autoGenerate=true)
    public int id;
    @ColumnInfo(name = "data")
    private String data;
    @ColumnInfo(name = "duration")
    private int duration;
    @ColumnInfo(name = "artist")
    private String artist;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "displayName")
    private String displayName;
    @ColumnInfo(name = "playCount")
    private int playCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }
}
