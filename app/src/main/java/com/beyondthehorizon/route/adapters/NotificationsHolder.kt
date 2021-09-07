package com.beyondthehorizon.route.adapters

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.interfaces.ISelectedNotification
import com.beyondthehorizon.route.models.notification.Recipient
import com.beyondthehorizon.route.models.notification.SelectedNotification
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.row_notification.view.*


class NotificationsHolder(
    context: Context,
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {
    private val context = context

    /**
     * Set view with values available
     */
    fun setValues(
        selectedNotification: SelectedNotification,
        isSent: Boolean? = true,
        isSelectedNotification: ISelectedNotification
    ) {
        val details: Recipient?
        when {
            isSent!! -> {
                itemView.message.text = String.format(
                    "You've requested Ksh. %s from %s %s for %s",
                    selectedNotification.amount,
                    selectedNotification.recipient!!.firstName,
                    selectedNotification.recipient.lastName,
                    selectedNotification.reason
                )
                details = selectedNotification.recipient
            }
            else -> {
                itemView.message.text = String.format(
                    "%s %s has requested you Ksh. %s for %s",
                    selectedNotification.requester!!.firstName,
                    selectedNotification.requester.lastName,
                    selectedNotification.amount,
                    selectedNotification.reason
                )
                details = selectedNotification.requester
            }
        }
        val requestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(16))
        Glide.with(context)
            .load(details.image)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .error(R.drawable.ic_user_home_page)
            .placeholder(R.drawable.ic_user_home_page)
            .apply(requestOptions)
            .into(itemView.notification_type_icon)

        when (selectedNotification.status!!.lowercase()) {
            "ok" -> {
                itemView.tvStatus.text = "Done"
                selectedNotification.status = "Done"
                selectedNotification.statusIcon = R.drawable.round_button_green
            }
            "cancelled" -> {
                itemView.tvStatus.text = "Rejected"
                selectedNotification.status = "Rejected"
                selectedNotification.statusIcon = R.drawable.round_button_danger
            }
            else -> {
                itemView.tvStatus.text = "Pending"
                selectedNotification.status = "Pending"
                selectedNotification.statusIcon = R.drawable.round_button_pending
            }
        }
        itemView.tvStatus.setBackgroundResource(selectedNotification.statusIcon!!)
        itemView.tvStatus.setBackgroundResource(selectedNotification.statusIcon!!)
        itemView.tvDate.text = selectedNotification.createdAt

        itemView.setOnClickListener {
            selectedNotification.sentNotification = isSent
            isSelectedNotification.selectedNotification(selectedNotification)
        }
    }
}
