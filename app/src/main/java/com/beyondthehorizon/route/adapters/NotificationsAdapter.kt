package com.beyondthehorizon.route.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.interfaces.ISelectedNotification
import com.beyondthehorizon.route.models.notification.SelectedNotification

class NotificationsAdapter(
    var context: Context,
    private val isSelectedNotification: ISelectedNotification
) : RecyclerView.Adapter<NotificationsHolder>() {
    private var notifications: List<SelectedNotification> = mutableListOf()
    private var isSentNotification:Boolean? = true
    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_notification, parent, false)
        return NotificationsHolder(context, view)
    }

    override fun onBindViewHolder(holder: NotificationsHolder, position: Int) {
        holder.setValues(notifications[position], isSentNotification!!, isSelectedNotification)
    }

    fun updateNotifications(notificatonList: List<SelectedNotification>, sent: Boolean? = true) {
        this.notifications = notificatonList
        this.isSentNotification = sent!!
        notifyItemRangeInserted(itemCount - notificatonList.size, notificatonList.size)
        notifyItemRangeChanged(itemCount - notificatonList.size, notificatonList.size)
    }
}