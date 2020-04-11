package com.beyondthehorizon.routeapp.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {NotificationCount.class}, exportSchema = false, version = 1)
public abstract class RouteDatabase extends RoomDatabase {

    private static final String DB_NAME = "notification_count_db";
    private static RouteDatabase instance;

    public abstract NotificationCountDao notificationCountDao();

    public static synchronized RouteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), RouteDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}