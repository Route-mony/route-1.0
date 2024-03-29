package com.beyondthehorizon.route.adapters

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.models.Notification
import com.beyondthehorizon.route.utils.Constants.PHONE_NUMBER
import com.beyondthehorizon.route.views.ApproveRequestActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_notification.view.*
import timber.log.Timber
import java.util.*


class NotificationsHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var context = context
    private lateinit var intent: Intent

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

        when (type.toLowerCase(Locale.ROOT)) {
            "sent" -> {
                message = "You've requested Ksh. $amount from $firstName $lastName for $reason"
            }
            "received" -> {
                message = "$firstName $lastName has requested you Ksh. $amount for $reason"
            }
        }

        if (avatarUrl.isNotEmpty()) {
            Picasso.get().load(avatarUrl).into(itemView.notification_type_icon)
        } else {
            Picasso.get().load(R.drawable.ic_user).into(itemView.notification_type_icon)
        }
        when (status.toLowerCase(Locale.ROOT)) {
            "ok" -> {
                status = "Done"
                itemView.tvStatus.setBackgroundResource(R.drawable.round_button_green)
            }
            "cancelled" -> {
                status = "Rejected"
                itemView.tvStatus.setBackgroundResource(R.drawable.round_button_danger)
            }
            else -> {
                status = "Pending"
                itemView.tvStatus.setBackgroundResource(R.drawable.round_button_pending)
            }
        }

        intent = Intent(context, ApproveRequestActivity::class.java)
        itemView.message.text = message
        itemView.status_icon.setImageResource(statusIcon)
        itemView.tvStatus.text = status.capitalize()
        itemView.tvDate.text = date

        itemView.setOnClickListener {
            try {
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
            } catch (ex: Exception) {
                Timber.d(ex.message.toString())
                Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
