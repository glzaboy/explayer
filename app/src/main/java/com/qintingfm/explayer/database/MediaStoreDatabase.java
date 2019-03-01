package com.qintingfm.explayer.database;

import com.qintingfm.explayer.dao.LocalMediaDao;
import com.qintingfm.explayer.entity.LocalMedia;
import com.qintingfm.explayer.entity.Media;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Media.class, LocalMedia.class},exportSchema = false,version = 2)
public abstract class MediaStoreDatabase extends RoomDatabase {
//     public abstract MediaDao  getMediaDao();
     public abstract LocalMediaDao getLocalMediaDao();
}
