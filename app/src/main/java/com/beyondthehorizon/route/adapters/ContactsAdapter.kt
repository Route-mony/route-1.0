package com.beyondthehorizon.route.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.interfaces.ISelectedContact
import com.beyondthehorizon.route.models.Contact
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.hdodenhof.circleimageview.CircleImageView

class ContactsAdapter(
    var context: Context,
    private val iSelectedContact: ISelectedContact
) : RecyclerView.Adapter<ContactsAdapter.ContactHolder>(), Filterable {


    class ContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: CircleImageView = itemView.findViewById(R.id.profile_image)
        val tvUsername: TextView = itemView.findViewById(R.id.username)
        val tvPhone: TextView = itemView.findViewById(R.id.contact)
    }

    private var contactsList: MutableList<Contact> = mutableListOf()
    private var filteredContacts: MutableList<Contact> = mutableListOf()
    override fun getItemCount(): Int {
        return filteredContacts.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_contact, parent, false)
        return ContactHolder(view)
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        val contact = filteredContacts[position]
        holder.tvUsername.text = contact.name
        holder.tvPhone.text = when {
            contact.username.isNotEmpty() -> contact.username
            else -> contact.contact
        }
        Glide.with(context)
            .load(contact.avatar)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .error(R.drawable.ic_user_home_page)
            .placeholder(R.drawable.ic_user_home_page)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            iSelectedContact.selectedContact(contact)
        }
    }

    fun updateContacts(contacts: MutableList<Contact>) {
        this.contactsList = contacts
        this.filteredContacts = contacts
        notifyItemRangeInserted(itemCount - contacts.size, contacts.size)
        notifyItemRangeChanged(itemCount - contacts.size, contacts.size)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var query = constraint.toString()
                filteredContacts = if (query.isNullOrEmpty()) {
                    contactsList
                } else {
                    val resultList = mutableListOf<Contact>()
                    contactsList.forEach { contact ->
                        if (contact.contact.contains(
                                query,
                                ignoreCase = true
                            ) || contact.username.contains(
                                query,
                                ignoreCase = true
                            ) || contact.name.contains(query, ignoreCase = true)
                        ) {
                            resultList.add(contact)
                        }
                    }
                    resultList
                }
                val filteredResults = FilterResults()
                filteredResults.values = filteredContacts
                return filteredResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredContacts = results?.values as MutableList<Contact>
                notifyDataSetChanged()
            }
        }
    }
}

