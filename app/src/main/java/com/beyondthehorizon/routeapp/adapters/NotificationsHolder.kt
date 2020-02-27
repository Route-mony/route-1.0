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
import kotlinx.android.synthetic.main.row_notification.view.*


class NotificationsHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    var intent = Intent(context, ApproveRequestActivity::class.java)
    var prefs: SharedPreferences.Editor = context.getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
    var context = context

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
                intent.putExtra("Id", value.id)
                intent.putExtra("Username", value.username)
                intent.putExtra("Phone", value.phone)
                intent.putExtra("Reason", value.reason)
                intent.putExtra("Amount", value.amount)
                intent.putExtra("Status", value.status)
                intent.putExtra("StatusIcon", value.statusIcon)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(intent)
            }
            catch (ex: Exception){
                Log.d("TAG", ex.message)
            }
        }
    }
}
