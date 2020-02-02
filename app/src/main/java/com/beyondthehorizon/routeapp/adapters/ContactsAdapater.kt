package com.beyondthehorizon.routeapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.models.Contact
import com.beyondthehorizon.routeapp.R

class ContactsAdapater( var context: Context, var contacts: MutableList<Contact>): RecyclerView.Adapter<ContactsHolder>(){

    override fun getItemCount(): Int{
        return contacts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        return ContactsHolder(LayoutInflater.from(context).inflate(R.layout.row_contact, parent, false))
    }

    override fun onBindViewHolder(holder: ContactsHolder, position: Int) {
        holder.setValues(contacts.get(holder.adapterPosition))
    }
}

