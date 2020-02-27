package com.beyondthehorizon.routeapp.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.models.Contact
import com.beyondthehorizon.routeapp.models.Notification
import com.beyondthehorizon.routeapp.views.FundAmountActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_contact.view.*
import com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES
import com.beyondthehorizon.routeapp.views.ApproveRequestActivity
import com.beyondthehorizon.routeapp.views.RequestReminderActivity
import kotlinx.android.synthetic.main.row_notification.view.*


class NotificationsHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var prefs: SharedPreferences.Editor = context.getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
    private var context = context
    private lateinit var intent:Intent

    /**
     * Set view with values available
     */
    fun setValues(value: Notification) {
        val maxLen = 46
        var message = value.message
        if (value.message.length > maxLen) {
            message = message.subSequence(0, maxLen).toString() + "..."
        }
        itemView.message.text = message
        itemView.status_icon.setImageResource(value.statusIcon)
        Picasso.get().load(value.avatar).into(itemView.notification_type_icon)

        itemView.setOnClickListener {
            try {
                var status = value.status
                when(status){
                    "ok" -> status = "Approved"
                    "pending" -> status ="Pending"
                    "cancelled" -> status = "Rejected"
                }

                when(value.type){
                    "sent" -> intent = Intent(context, RequestReminderActivity::class.java)
                    "received" -> intent = Intent(context, ApproveRequestActivity::class.java)
                }

                intent.putExtra("Id", value.id)
                intent.putExtra("Username", value.username)
                intent.putExtra("Phone", value.phone)
                intent.putExtra("Reason", value.reason)
                intent.putExtra("Amount", value.amount)
                intent.putExtra("Status", status)
                intent.putExtra("StatusIcon", value.statusIcon)
                context.startActivity(intent)
            }
            catch (ex: Exception){
                Log.d("TAG", ex.message)
            }
        }
    }
}
