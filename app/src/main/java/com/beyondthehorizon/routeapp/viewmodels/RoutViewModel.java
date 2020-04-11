package com.beyondthehorizon.routeapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.beyondthehorizon.routeapp.databases.NotificationCount;
import com.beyondthehorizon.routeapp.repository.RouteRepository;

import java.util.List;

public class RoutViewModel extends AndroidViewModel {
    private RouteRepository chatsRepository;

    public RoutViewModel(@NonNull Application application) {
        super(application);
        chatsRepository = new RouteRepository(application);
    }

    public LiveData<List<NotificationCount>> getNotifiCount() {
        return chatsRepository.getNificCount();
    }

    public void insertChat(NotificationCount notificationCount) {
        chatsRepository.insertChat(notificationCount);
    }

    public void deleteNotifiCount() {
        chatsRepository.deleteNotificCount();
    }

}