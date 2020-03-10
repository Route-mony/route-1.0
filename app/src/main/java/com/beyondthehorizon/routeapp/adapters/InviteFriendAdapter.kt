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

class InviteFriendAdapter(private val context: Context) :
        RecyclerView.Adapter<InviteFriendAdapter.ViewHolder>(), Filterable {

    private var listOfContacts = ArrayList<InviteFriend>()
    private var filterListOfContacts = ArrayList<InviteFriend>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.invite_friend_layout_item,
                        parent,
                        false
                )
        )
    }

    override fun getItemCount(): Int {
        return if (listOfContacts.size == 0) {
            0
        } else {
            listOfContacts.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val inviteFriend: InviteFriend = listOfContacts[position]
        holder.bind(inviteFriend)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to

        //        private val patientDoctor = view.p_doctor!!
        private val userName = view.userName!!
        private val phoneNumber = view.phoneNumber!!
        private val inviteBtn = view.inviteBtn!!

        init {
            inviteBtn.setOnClickListener {
                //                val editor = sharedPref.edit()
//                val gson = Gson()
//                val personString = gson.toJson(Patient)
//                editor.putString(VISITING_HISTORY_PROFILE, personString)
//                editor.apply()
//                context.startActivity(Intent(context, HealthRecordsActivity::class.java))
                Toast.makeText(context, "Coming soon", Toast.LENGTH_LONG).show()
            }
        }

        fun bind(invite: InviteFriend) {
            userName.text = invite.username
            phoneNumber.text = invite.phone

        }

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