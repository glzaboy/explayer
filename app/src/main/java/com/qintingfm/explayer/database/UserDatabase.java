package com.qintingfm.explayer.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import com.qintingfm.explayer.dao.UserDao;
import com.qintingfm.explayer.entity.User;

@Database(entities = User.class ,version = 1,exportSchema=false)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao userDao();

}
