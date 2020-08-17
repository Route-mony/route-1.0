package com.beyondthehorizon.routeapp.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.models.Notification
import com.beyondthehorizon.routeapp.utils.Constants.PHONE_NUMBER
import com.beyondthehorizon.routeapp.views.ApproveRequestActivity
import com.beyondthehorizon.routeapp.views.RequestReminderActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_notification.view.*
import timber.log.Timber


class NotificationsHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var context = context
    private lateinit var intent:Intent

    /**
     * Set view with values available
     */
    fun setValues(value: Notification) {
        val maxLen = 45
        var message = ""
        var id = value.id
        var username = value.username
        var amount = value.amount
        var reason = value.reason
        var statusIcon = value.statusIcon
        var status = value.status
        var avatarUrl = value.avatar
        var phone = value.phone

        when(value.type){
            "sent" -> {
                intent = Intent(context, RequestReminderActivity::class.java)
                message = "You have requested Ksh. $amount from $username for $reason"
            }
            "received" -> {
                intent = Intent(context, ApproveRequestActivity::class.java)
                message = username.split(" ")[0] + " has requested you Ksh. $amount for $reason"
            }
        }
        if (message.length > maxLen) {
            message = message.subSequence(0, maxLen).toString() + "..."
        }
        itemView.message.text = message
        itemView.status_icon.setImageResource(statusIcon)
        Picasso.get().load(avatarUrl).into(itemView.notification_type_icon)

        itemView.setOnClickListener {
            try {
                when(status){
                    "ok" -> status = "Approved"
                    "pending" -> status ="Pending"
                    "cancelled" -> status = "Rejected"
                }

                intent.putExtra("Id", id)
                intent.putExtra("Username", username)
                intent.putExtra(PHONE_NUMBER, phone)
                intent.putExtra("Reason", reason)
                intent.putExtra("Amount", amount)
                intent.putExtra("Status", status)
                intent.putExtra("StatusIcon", statusIcon)
                intent.putExtra("Phone", phone)
                context.startActivity(intent)
            }
            catch (ex: Exception) {
                Timber.d(ex.message.toString())
            }
        }
    }
}
