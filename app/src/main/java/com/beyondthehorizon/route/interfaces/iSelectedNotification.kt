package com.beyondthehorizon.route.interfaces

import com.beyondthehorizon.route.models.notification.SelectedNotification

interface ISelectedNotification {
    fun selectedNotification(notification: SelectedNotification)
}