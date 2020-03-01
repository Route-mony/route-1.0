package com.beyondthehorizon.routeapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.models.Notification

class NotificationsAdapter (var context: Context, var notifications: MutableList<Notification>): RecyclerView.Adapter<NotificationsHolder>(){

    override fun getItemCount(): Int{
        return notifications.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_notification, parent, false)
        return NotificationsHolder(context, view)
    }

    override fun onBindViewHolder(holder: NotificationsHolder, position: Int) {
        holder.setValues(notifications.get(holder.adapterPosition))
    }
}