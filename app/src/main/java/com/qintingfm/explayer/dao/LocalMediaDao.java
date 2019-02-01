package com.qintingfm.explayer.dao;

import android.arch.persistence.room.*;
import com.qintingfm.explayer.entity.LocalMedia;
import com.qintingfm.explayer.entity.Media;

@Dao
public interface LocalMediaDao {
    @Insert
    public void insertMedia(LocalMedia... media);

    @Update
    public void updateMedia(LocalMedia... media);


    @Delete
    public void deleteMedia(LocalMedia... media);

    @Query("SELECT * FROM LocalMedia where id=:id")
    public LocalMedia findByid(int id);
}
