package com.beyondthehorizon.routeapp.Adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.Models.Contacts
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.row_contact.view.*

class ContactsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    /**
     * Set view with values available
     */
    fun setValues (value: Contacts) {
        itemView.user_name.text = value.name
        itemView.contact.text = value.contact
    }
}
