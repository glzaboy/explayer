package com.qintingfm.explayer.dao;

import android.arch.persistence.room.*;
import com.qintingfm.explayer.entity.Media;
@Dao
public interface MediaDao {
    @Insert
    public void insertMedia(Media... news);

    @Update
    public void updateMedia(Media... news);


    @Delete
    public void deleteMedia(Media... news);

    @Query("SELECT * FROM Media where id=:id")
    public Media findByid(int id);
}
