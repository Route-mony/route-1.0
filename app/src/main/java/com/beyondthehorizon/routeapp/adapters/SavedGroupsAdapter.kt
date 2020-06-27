package com.beyondthehorizon.routeapp.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.models.SavedGroupItem
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.views.multicontactschoice.ui.main.SendToManyActivity
import kotlinx.android.synthetic.main.recycvler_header.view.*
import kotlinx.android.synthetic.main.saved_group_item.view.*

class SavedGroupsAdapter(private val context: Context) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var listOfSentTransactions = ArrayList<SavedGroupItem>()
    private var filterListOfSentTransactions = ArrayList<SavedGroupItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var layout = 0
        val viewHolder: RecyclerView.ViewHolder?
        when (viewType) {
            RECYCLER_SECTION -> {
                layout = R.layout.saved_group_item
                val chatView = LayoutInflater.from(parent.context).inflate(layout, parent, false)
                viewHolder = ViewHolder(chatView)
            }
            RECYCLER_HEADER -> {
                layout = R.layout.recycvler_header
                val videoView = LayoutInflater.from(parent.context).inflate(layout, parent, false)
                viewHolder = ShowHeaderHolder(videoView)
            }
            else -> viewHolder = null
        }
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return if (listOfSentTransactions.size == 0) {
            0
        } else {
            listOfSentTransactions.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val transactionModel: SavedGroupItem = listOfSentTransactions[position]

        val viewType = holder.itemViewType
        when (viewType) {
            RECYCLER_SECTION -> {
                val section = transactionModel
                (holder as ViewHolder).bind(section)
            }
            RECYCLER_HEADER -> {
                val header = transactionModel
                (holder as ShowHeaderHolder).bind(header)
            }
        }
    }

    inner class ShowHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val recyclerHeader = itemView.recyclerHeader!!

        init {
        }

        fun bind(invite: SavedGroupItem) {
            // Attach values for each item
            recyclerHeader.text = invite.recipients
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to

        //        private val patientDoctor = view.p_doctor!!
        private val group_name = view.group_name!!
        private val group_status = view.group_status!!
        private val group_amount = view.group_amount!!
        private val group_date = view.group_date!!
        private val imag1 = view.imag1!!
        private val imag2 = view.imag2!!

        private var receiptModel: SavedGroupItem? = null

        var sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)

        init {
            view.setOnClickListener {
                val editor = sharedPref.edit()
                editor.putString(GROUP_IS_SAVED, "YES").apply()
                editor.putString(MY_MULTI_CHOICE_SELECTED_CONTACTS, receiptModel!!.recipients)
                editor.apply()
                val intent = Intent(context, SendToManyActivity::class.java)
                context.startActivity(intent)
//                listener = context as ReceiptActivity
//                listener.onReceiptClicked(personString, receipt_type)

            }
        }

        fun bind(invite: SavedGroupItem) {
            group_name.text = invite.group_name
            group_status.text = invite.status
//            group_amount.text = invite.total_amount
            group_amount.text = "Ksh ${invite.total_amount}"
            group_date.visibility = View.GONE
//            if (invite.status.contains("ok")) {
//                receipt_status.text = "Status: Approved"
//            } else {
//                receipt_status.text = "Status: ${invite.status}"
//            }
            receiptModel = invite
//            val img = invite.image

//            Glide.with(context)
//                    .load(invite.image)
//                    .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
//                    .placeholder(R.color.input_back)
//                    .into(receipt_image)

        }

    }

    fun setContact(patients: ArrayList<SavedGroupItem>) {
        listOfSentTransactions = patients
        filterListOfSentTransactions = patients
//        Log.i("HospitalsAdapter", listOfSentTransactions.size.toString())
    }

    fun clearList() {
        listOfSentTransactions.clear()
    }

    override fun getItemViewType(position: Int): Int {
        if (listOfSentTransactions[position].view_type.compareTo(RECYCLER_SECTION) == 0) {
            return RECYCLER_SECTION
        } else if (listOfSentTransactions[position].view_type.compareTo(RECYCLER_HEADER) == 0) {
            return RECYCLER_HEADER
        }
        return -1
    }

    override fun getFilter(): Filter {
        return filteredProvidersList
    }

    private val filteredProvidersList = object : Filter() {

        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            var filteredList = java.util.ArrayList<SavedGroupItem>()

            if (charSequence == null || charSequence.isEmpty()) {
                filteredList = filterListOfSentTransactions
            } else {
                val filterPattern = charSequence.toString().toLowerCase().trim { it <= ' ' }
                for (item in filterListOfSentTransactions) {
                    if (item.group_name.toLowerCase().contains(filterPattern) ||
                            item.total_amount.toLowerCase().contains(filterPattern)
                    ) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            listOfSentTransactions = filterResults.values as java.util.ArrayList<SavedGroupItem>
            notifyDataSetChanged()
        }
    }

}