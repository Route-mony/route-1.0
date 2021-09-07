package com.beyondthehorizon.route.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.interfaces.IOnGroupSelected
import com.beyondthehorizon.route.models.SavedGroupItem
import com.beyondthehorizon.route.utils.Constants.RECYCLER_HEADER
import com.beyondthehorizon.route.utils.Constants.RECYCLER_SECTION
import kotlinx.android.synthetic.main.recycvler_header.view.*
import kotlinx.android.synthetic.main.saved_group_item.view.*

class SavedGroupsAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var listOfSentTransactions = ArrayList<SavedGroupItem>()
    private var filterListOfSentTransactions = ArrayList<SavedGroupItem>()
    private lateinit var iOnGroupSelected: IOnGroupSelected

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
//            recyclerHeader.text = invite.recipients.capitalize()
            recyclerHeader.text = ""
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val group_name = view.group_name!!
        private val group_status = view.group_status!!
        private val group_amount = view.group_amount!!
        private val group_date = view.group_date!!

        private var savedGroupItem: SavedGroupItem? = null

        init {
            view.setOnClickListener {
                iOnGroupSelected.onGroupSelectedListener(savedGroupItem!!)
            }
        }

        fun bind(groupItem: SavedGroupItem) {
            group_name.text = groupItem.group_name
            group_status.text = groupItem.status
            group_amount.text = "Ksh ${groupItem.total_amount}"
            group_date.visibility = View.GONE
            savedGroupItem = groupItem

        }

    }

    fun setContact(patients: ArrayList<SavedGroupItem>) {
        listOfSentTransactions = patients
        filterListOfSentTransactions = patients
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