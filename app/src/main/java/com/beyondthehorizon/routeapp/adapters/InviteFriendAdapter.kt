package com.beyondthehorizon.routeapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.models.InviteFriend
import kotlinx.android.synthetic.main.invite_friend_layout_item.view.*
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import android.R.attr.name


class InviteFriendAdapter(private val context: Context, private val theView: String) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

//        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.invite_friend_layout_item -> ViewHolderInvite(
                    LayoutInflater.from(context).inflate(R.layout.invite_friend_layout_item, parent, false)
            )

            R.layout.share_receipt -> ViewHolderShareReceipt(
                    LayoutInflater.from(context).inflate(R.layout.share_receipt, parent, false)
            )

            else -> throw IllegalArgumentException("Unsupported layout") // in case populated with a model we don't know how to display.
        }
    }

    override fun getItemCount(): Int {
        return if (listOfContacts.size == 0) {
            0
        } else {
            listOfContacts.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val inviteFriend: InviteFriend = listOfContacts[position]

        when (holder) {
            is ViewHolderInvite -> {
                holder.bind(inviteFriend)
            }

            is ViewHolderShareReceipt -> {
//                val adModel = element as AdModel
                // bind AdViewHolder
                holder.bind(inviteFriend)
            }

        }
    }

    private var listOfContacts = ArrayList<InviteFriend>()
    private var filterListOfContacts = ArrayList<InviteFriend>()

    inner class ViewHolderInvite(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to

        //        private val patientDoctor = view.p_doctor!!
        private val userName = view.userName!!
        private val phoneNumber = view.phoneNumber!!
        private val inviteBtn = view.inviteBtn!!

        init {
            inviteBtn.setOnClickListener {
                val shareBody = "Hello checkout this awesome app! Route App"
                val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Invite Friend")
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
                context.startActivity(Intent.createChooser(sharingIntent, "Invite Friend To Route App"))
            }
        }

        fun bind(invite: InviteFriend) {
            userName.text = invite.username
            phoneNumber.text = invite.phone

        }

    }

    inner class ViewHolderShareReceipt(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to

        //        private val patientDoctor = view.p_doctor!!
        private val userName = view.userName!!
        private val phoneNumber = view.phoneNumber!!
        private val inviteBtn = view.inviteBtn!!

        init {
            inviteBtn.setOnClickListener {

                Toast.makeText(context, "coming soon....", Toast.LENGTH_LONG).show()
//                val shareBody = "Hello checkout this awesome app! Route App"
//                val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
//                sharingIntent.type = "text/plain"
//                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Invite Friend")
//                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
//                context.startActivity(Intent.createChooser(sharingIntent, "Invite Friend To Route App"))
            }
        }

        fun bind(invite: InviteFriend) {
            userName.text = invite.username
            phoneNumber.text = invite.phone

        }

    }


    override fun getItemViewType(position: Int): Int {

        if (theView.contains("Invite")) {
            return R.layout.invite_friend_layout_item
        }
        return R.layout.share_receipt

    }

    fun setContact(patients: ArrayList<InviteFriend>) {
        listOfContacts = patients
        filterListOfContacts = patients
        Log.i("HospitalsAdapter", listOfContacts.size.toString())
    }

    fun clearList() {
        listOfContacts.clear()
    }

    override fun getFilter(): Filter {
        return filteredProvidersList
    }

    private val filteredProvidersList = object : Filter() {

        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            var filteredList = java.util.ArrayList<InviteFriend>()

            if (charSequence == null || charSequence.isEmpty()) {
                filteredList = filterListOfContacts
            } else {
                val filterPattern = charSequence.toString().toLowerCase().trim { it <= ' ' }
                for (item in filterListOfContacts) {
                    if (item.username.toLowerCase().contains(filterPattern) ||
                            item.phone.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            listOfContacts = filterResults.values as java.util.ArrayList<InviteFriend>
            notifyDataSetChanged()
        }
    }

}