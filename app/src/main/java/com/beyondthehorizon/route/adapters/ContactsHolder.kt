package com.beyondthehorizon.route.adapters

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.interfaces.ISelectedContact
import com.beyondthehorizon.route.models.Contact
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.row_contact.view.*

class ContactsHolder(
    private val context: Context,
    itemView: View,
    private val iSelectedContact: ISelectedContact
) : RecyclerView.ViewHolder(itemView) {

    /**
     * Set view with values available
     */
    fun setValues(value: Contact) {
        itemView.username.text = value.name
        itemView.contact.text = when {value.username.isNotEmpty() -> value.username else -> value.contact}
        Glide.with(context)
            .load(value.avatar)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .error(R.drawable.ic_user_home_page)
            .placeholder(R.drawable.ic_user_home_page)
            .into(itemView.profile_image)

        itemView.setOnClickListener {
            iSelectedContact.selectedContact(value)
        }
    }
}
