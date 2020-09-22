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
import java.util.*


class NotificationsHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var context = context
    private lateinit var intent:Intent

    /**
     * Set view with values available
     */
    fun setValues(value: Notification) {
        var message = ""
        var id = value.id
        var username = value.username
        var firstName = value.firstName
        var lastName = value.lastName
        var amount = value.amount
        var reason = value.reason
        var statusIcon = value.statusIcon
        var status = value.status
        var avatarUrl = value.avatar
        var phone = value.phone
        var date = value.date
        var cancellationReason = value.cancellation_reason
        var type = value.type

        when(type.toLowerCase()){
            "sent" -> {
                message = "You've requested Ksh. $amount from $firstName $lastName for $reason"
            }
            "received" -> {
                message = "$firstName $lastName has requested you Ksh. $amount for $reason"
            }
        }
        intent = Intent(context, ApproveRequestActivity::class.java)
        itemView.message.text = message
        itemView.status_icon.setImageResource(statusIcon)
        itemView.tvStatus.text = status.capitalize()
        itemView.tvDate.text = date
        if(avatarUrl.isNotEmpty()) {
            Picasso.get().load(avatarUrl).into(itemView.notification_type_icon)
        }

        itemView.setOnClickListener {
            try {
                when(status.toLowerCase()){
                    "ok" -> status = "Approved"
                    "pending" -> status ="Pending"
                    "cancelled" -> status = "Rejected"
                }

                intent.putExtra("Id", id)
                intent.putExtra("Username", username)
                intent.putExtra("FirstName", firstName)
                intent.putExtra("LastName", lastName)
                intent.putExtra(PHONE_NUMBER, phone)
                intent.putExtra("Reason", reason)
                intent.putExtra("Amount", amount)
                intent.putExtra("Status", status)
                intent.putExtra("StatusIcon", statusIcon)
                intent.putExtra("Avatar", avatarUrl)
                intent.putExtra("Phone", phone)
                intent.putExtra("Date", date)
                intent.putExtra("CancellationReason", cancellationReason)
                intent.putExtra("RequestType", type)
                context.startActivity(intent)
            }
            catch (ex: Exception) {
                Timber.d(ex.message.toString())
            }
        }
    }
}
