package com.qintingfm.explayer.dao;

import android.arch.persistence.room.*;
import com.qintingfm.explayer.entity.User;

@Dao
public interface UserDao {
    @Insert
    public void insertUsers(User ... users);

    @Update
    public void updateUsers(User... users);


    @Delete
    public void deleteUsers(User... users);


    @Query("SELECT * FROM user")
    public User[] loadAllUsers();


}
