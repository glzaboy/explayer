package com.qintingfm.explayer.dao;

import android.arch.persistence.room.*;
import com.qintingfm.explayer.entity.LocalMedia;

import java.util.List;

@Dao
public interface LocalMediaDao {
    @Insert
    void insertMedia(LocalMedia... media);

    @Update
    void updateMedia(LocalMedia... media);


    @Delete
    void deleteMedia(LocalMedia... media);

    @Query("SELECT * FROM LocalMedia where id=:id")
    LocalMedia findById(int id);
    @Query("SELECT * FROM LocalMedia")
    List<LocalMedia> findAll();

    @Query("SELECT * FROM LocalMedia where id>:currentId limit 1")
    LocalMedia findNext(int currentId);

    @Query("SELECT * FROM LocalMedia where id<:currentId limit 1")
    LocalMedia findPrev(int currentId);
    @Query("SELECT * FROM LocalMedia ORDER BY RANDOM() limit 1 ")
    LocalMedia findRand();
}
