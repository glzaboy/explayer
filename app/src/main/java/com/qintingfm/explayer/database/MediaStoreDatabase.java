package com.qintingfm.explayer.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import com.qintingfm.explayer.dao.LocalMediaDao;
import com.qintingfm.explayer.entity.LocalMedia;
import com.qintingfm.explayer.entity.Media;

@Database(entities = {Media.class, LocalMedia.class},exportSchema = false,version = 2)
public abstract class MediaStoreDatabase extends RoomDatabase {
//     public abstract MediaDao  getMediaDao();
     public abstract LocalMediaDao getLocalMediaDao();
}
