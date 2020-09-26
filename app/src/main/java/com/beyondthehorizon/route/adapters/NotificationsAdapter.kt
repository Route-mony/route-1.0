package com.beyondthehorizon.route.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.models.Notification

class NotificationsAdapter (var context: Context, var notifications: MutableList<Notification>): RecyclerView.Adapter<NotificationsHolder>(){

    override fun getItemCount(): Int{
        return notifications.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_notification, parent, false)
        return NotificationsHolder(context, view)
    }

    override fun onBindViewHolder(holder: NotificationsHolder, position: Int) {
        holder.setValues(notifications[holder.adapterPosition])
    }
}