package com.beyondthehorizon.routeapp.adapters

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.models.Contact
import kotlinx.android.synthetic.main.row_contact.view.*

class ContactsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    /**
     * Set view with values available
     */
    fun setValues (context: Context, value: Contact) {
        itemView.username.text = value.name
        itemView.contact.text = value.contact
        //Picasso.with(context).load(value.avatar).into(itemView.avatar)
    }
}
