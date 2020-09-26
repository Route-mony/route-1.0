package com.beyondthehorizon.route.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.models.Contact
import com.beyondthehorizon.route.R

class ContactsAdapater( var context: Context, var contacts: MutableList<Contact>): RecyclerView.Adapter<ContactsHolder>(){

    override fun getItemCount(): Int{
        return contacts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_contact, parent, false)
        return ContactsHolder(context, view)
    }

    override fun onBindViewHolder(holder: ContactsHolder, position: Int) {
        holder.setValues(contacts.get(holder.adapterPosition))
    }
}

