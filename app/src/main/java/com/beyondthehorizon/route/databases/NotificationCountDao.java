package com.beyondthehorizon.route.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificationCountDao {

    //GET Specific chats by senderUID
    @Query("SELECT * FROM notif_count_table")
    LiveData<List<NotificationCount>> allNotifications();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChat(NotificationCount... chatModels);

    @Query("DELETE FROM notif_count_table")
    void deleteAllNotifications();
}