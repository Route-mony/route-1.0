package com.beyondthehorizon.route.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.bottomsheets.ReceiptDetailsBottomModel
import com.beyondthehorizon.route.models.ReceiptModel
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.Constants.*
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionDetailsActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import kotlinx.android.synthetic.main.invite_friend_layout_item.view.*
import kotlinx.android.synthetic.main.invite_friend_layout_item.view.userName
import kotlinx.android.synthetic.main.receipt_item.view.*
import kotlinx.android.synthetic.main.recycvler_header.view.*
import kotlinx.android.synthetic.main.sent_transactions.view.*

class ReceiptAdapter(private val context: Context,
                     private val receipt_type: String) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    lateinit var listener: ReceiptInterface
    private var listOfSentTransactions = ArrayList<ReceiptModel>()
    private var filterListOfSentTransactions = ArrayList<ReceiptModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var layout = 0
        val viewHolder: RecyclerView.ViewHolder?
        when (viewType) {
            RECYCLER_SECTION -> {
                layout = R.layout.receipt_item
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
        val transactionModel: ReceiptModel = listOfSentTransactions[position]

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

        fun bind(invite: ReceiptModel) {
            // Attach values for each item
            recyclerHeader.text = invite.receipt_id
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to

        //        private val patientDoctor = view.p_doctor!!
        private val receipt_date = view.receipt_date!!
        private val receipt_name = view.receipt_name!!
        private val receipt_amount = view.receipt_amount!!
        private val receipt_desc = view.receipt_desc!!
        private val receipt_image = view.receipt_image!!
        private val receipt_status = view.receipt_status!!

        private var receiptModel: ReceiptModel? = null

        var sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)

        init {
            view.setOnClickListener {
                val editor = sharedPref.edit()

                val gson = Gson()
                val personString = gson.toJson(receiptModel)
                editor.putString(VISITING_HISTORY_PROFILE, personString)
                editor.putString(TRANS_TYPE, receipt_type)
                editor.apply()
                listener = context as ReceiptActivity
                listener.onReceiptClicked(personString, receipt_type)

            }
        }

        fun bind(invite: ReceiptModel) {
            receipt_date.text = invite.transaction_date
            receipt_name.text = invite.title
            receipt_desc.text = invite.description
            receipt_amount.text = "Ksh ${invite.amount_spent}"
            if (invite.status.contains("ok")) {
                receipt_status.text = "Status: Approved"
            } else {
                receipt_status.text = "Status: ${invite.status}"
            }
            receiptModel = invite

            Glide.with(context)
                    .load(invite.image)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.color.input_back)
                    .into(receipt_image)

        }

    }

    fun setContact(patients: ArrayList<ReceiptModel>) {
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
            var filteredList = java.util.ArrayList<ReceiptModel>()

            if (charSequence == null || charSequence.isEmpty()) {
                filteredList = filterListOfSentTransactions
            } else {
                val filterPattern = charSequence.toString().toLowerCase().trim { it <= ' ' }
                for (item in filterListOfSentTransactions) {
                    if (item.first_name.toLowerCase().contains(filterPattern) ||
                            item.last_name.toLowerCase().contains(filterPattern) ||
                            item.username.toLowerCase().contains(filterPattern)
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
            listOfSentTransactions = filterResults.values as java.util.ArrayList<ReceiptModel>
            notifyDataSetChanged()
        }
    }

    interface ReceiptInterface {
        fun onReceiptClicked(receipt: String, receipt_type: String)
    }
}