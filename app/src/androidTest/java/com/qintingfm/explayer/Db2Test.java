package com.qintingfm.explayer;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.qintingfm.explayer.dao.UserDao;
import com.qintingfm.explayer.database.UserDatabase;
import com.qintingfm.explayer.entity.User;
import io.reactivex.internal.util.SuppressAnimalSniffer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class Db2Test {
    UserDatabase userDatabase;
    @Before
    void setup(){
        Context context = InstrumentationRegistry.getContext();
        userDatabase = Room.inMemoryDatabaseBuilder(context, UserDatabase.class).build();
    }

    @Test
    void addUser(){
        UserDao userDao = userDatabase.userDao();
        userDao.insertUsers(new User());
    }
}
