package com.beyondthehorizon.routeapp.adapters

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.models.Contact
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_contact.view.*

class ContactsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    /**
     * Set view with values available
     */
    fun setValues (value: Contact) {
        itemView.username.text = value.name
        itemView.contact.text = value.contact
        Picasso.get().load(value.avatar).into(itemView.profile_image)
    }
}
