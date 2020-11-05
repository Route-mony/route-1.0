package com.beyondthehorizon.route.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.models.Contact

class ContactsAdapater(var context: Context, contacts: MutableList<Contact>) : RecyclerView.Adapter<ContactsHolder>() {
    private var contactsList: MutableList<Contact> = contacts
    override fun getItemCount(): Int {
        return contactsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_contact, parent, false)
        return ContactsHolder(context, view)
    }

    override fun onBindViewHolder(holder: ContactsHolder, position: Int) {
        holder.setValues(contactsList.get(holder.adapterPosition))
    }

    fun updateContacts(contacts: MutableList<Contact>) {
        this.contactsList = contacts
        notifyDataSetChanged()
    }
}

