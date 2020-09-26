package com.beyondthehorizon.route.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.beyondthehorizon.route.databases.NotificationCount;
import com.beyondthehorizon.route.databases.NotificationCountDao;
import com.beyondthehorizon.route.databases.RouteDatabase;

import java.util.List;

public class RouteRepository {

    LiveData<List<NotificationCount>> notifCount;
    private NotificationCountDao notificationCountDao;

    public RouteRepository(Application application) {
        RouteDatabase residenceDatabase = RouteDatabase.getInstance(application);
        notificationCountDao = residenceDatabase.notificationCountDao();
        notifCount = notificationCountDao.allNotifications();
    }
    //Get all latest chats
    public LiveData<List<NotificationCount>> getNificCount() {
        return notifCount;
    }

    public void insertChat(NotificationCount notificationCount) {
        new InsertChatAsyncTask(notificationCountDao).execute(notificationCount);
    }
    //Delete all notification count
    public void deleteNotificCount() {
        new DeleteAllNotificationsAsyncTask(notificationCountDao).execute();
    }

    // INSERT CHAT
    private static class InsertChatAsyncTask extends AsyncTask<NotificationCount, Void, Void> {
        private NotificationCountDao chatsDao;

        private InsertChatAsyncTask(NotificationCountDao chatsDao) {
            this.chatsDao = chatsDao;
        }

        @Override
        protected Void doInBackground(NotificationCount... notificationCountDaos) {
            Log.d("TAG", "doInBackground: " + notificationCountDaos[0].toString());
            chatsDao.insertChat(notificationCountDaos[0]);

            return null;
        }
    }

    //DELETE ALL VISITORS
    private static class DeleteAllNotificationsAsyncTask extends AsyncTask<Void, Void, Void> {
        private NotificationCountDao chatsDao;

        private DeleteAllNotificationsAsyncTask(NotificationCountDao chatsDao) {
            this.chatsDao = chatsDao;
        }

        @Override
        protected Void doInBackground(Void... params) {
            chatsDao.deleteAllNotifications();
            return null;
        }
    }
}
