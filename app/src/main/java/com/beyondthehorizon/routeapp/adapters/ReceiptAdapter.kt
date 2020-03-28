package com.beyondthehorizon.routeapp.adapters

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
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.bottomsheets.ReceiptDetailsBottomModel
import com.beyondthehorizon.routeapp.models.ReceiptModel
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.TRANS_TYPE
import com.beyondthehorizon.routeapp.utils.Constants.VISITING_HISTORY_PROFILE
import com.beyondthehorizon.routeapp.views.receipt.ReceiptActivity
import com.beyondthehorizon.routeapp.views.transactions.main.TransactionDetailsActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.invite_friend_layout_item.view.*
import kotlinx.android.synthetic.main.invite_friend_layout_item.view.userName
import kotlinx.android.synthetic.main.receipt_item.view.*
import kotlinx.android.synthetic.main.sent_transactions.view.*

class ReceiptAdapter(private val context: Context,
                     private val receipt_type: String) :
        RecyclerView.Adapter<ReceiptAdapter.ViewHolder>(), Filterable {

    lateinit var listener: ReceiptInterface
    private var listOfSentTransactions = ArrayList<ReceiptModel>()
    private var filterListOfSentTransactions = ArrayList<ReceiptModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.receipt_item,
                        parent,
                        false
                )
        )
    }

    override fun getItemCount(): Int {
        return if (listOfSentTransactions.size == 0) {
            0
        } else {
            listOfSentTransactions.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transactionModel: ReceiptModel = listOfSentTransactions[position]
        holder.bind(transactionModel)
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
            receipt_date.text = invite.created_at
            receipt_name.text = invite.title
            receipt_desc.text = invite.description
            receipt_amount.text = "Ksh ${invite.amount_spent}"
            if (invite.status.contains("ok")) {
                receipt_status.text = "Status: Approved"
            } else {
                receipt_status.text = "Status: ${invite.status}"
            }
            receiptModel = invite
            val img = invite.image

            Glide.with(context)
                    .load(invite.image)
                    .centerCrop()
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