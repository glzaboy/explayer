package com.qintingfm.explayer.dao;

import android.arch.persistence.room.*;
import com.qintingfm.explayer.entity.Media;
@Dao
public interface MediaDao {
    @Insert
    void insertMedia(Media... media);

    @Update
    void updateMedia(Media... media);


    @Delete
    void deleteMedia(Media... media);

    @Query("SELECT * FROM Media where id=:id")
    Media findById(int id);
}
