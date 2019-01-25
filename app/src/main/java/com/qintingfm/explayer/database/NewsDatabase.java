package com.qintingfm.explayer.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import com.qintingfm.explayer.dao.NewsDao;
import com.qintingfm.explayer.entity.News;

@Database(entities = News.class ,version = 1,exportSchema=false)
public abstract class NewsDatabase extends RoomDatabase {
    public abstract NewsDao getnewsDao();

}
