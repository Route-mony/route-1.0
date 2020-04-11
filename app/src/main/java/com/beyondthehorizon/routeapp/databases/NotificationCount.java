package com.beyondthehorizon.routeapp.databases;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "notif_count_table")
public class NotificationCount {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String notif_count;

    @Ignore
    public NotificationCount() {
    }

    public NotificationCount(String notif_count) {
        this.notif_count = notif_count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotif_count() {
        return notif_count;
    }

    public void setNotif_count(String notif_count) {
        this.notif_count = notif_count;
    }
}
