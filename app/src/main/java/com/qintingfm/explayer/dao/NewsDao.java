package com.qintingfm.explayer.dao;

import android.arch.persistence.room.*;
import com.qintingfm.explayer.entity.News;

import java.util.List;

@Dao
public interface NewsDao {
    @Insert
    public void insertNew(News ... news);

    @Update
    public void updateNews(News... news);


    @Delete
    public void deleteNews(News... news);

    @Query("SELECT * FROM News where id=:id")
    public News findByid(int id);
}
