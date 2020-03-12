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
import com.beyondthehorizon.routeapp.models.TransactionModel
import kotlinx.android.synthetic.main.invite_friend_layout_item.view.*
import kotlinx.android.synthetic.main.invite_friend_layout_item.view.userName
import kotlinx.android.synthetic.main.sent_transactions.view.*

class TransactionsAdapter(private val context: Context) :
        RecyclerView.Adapter<TransactionsAdapter.ViewHolder>(), Filterable {

    private var listOfSentTransactions = ArrayList<TransactionModel>()
    private var filterListOfSentTransactions = ArrayList<TransactionModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.sent_transactions,
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
        val transactionModel: TransactionModel = listOfSentTransactions[position]
        holder.bind(transactionModel)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to

        //        private val patientDoctor = view.p_doctor!!
        private val userName = view.userName!!
        private val transTime = view.transTime!!
        private val amountTxt = view.amountTxt!!

        init {
            view.setOnClickListener {
                //                val editor = sharedPref.edit()
//                val gson = Gson()
//                val personString = gson.toJson(Patient)
//                editor.putString(VISITING_HISTORY_PROFILE, personString)
//                editor.apply()
//                context.startActivity(Intent(context, HealthRecordsActivity::class.java))
                Toast.makeText(context, "Coming soon", Toast.LENGTH_LONG).show()
            }
        }

        fun bind(invite: TransactionModel) {
            userName.text = invite.details
            transTime.text = invite.created_at
            amountTxt.text = invite.withdrawn

        }

    }

    fun setContact(patients: ArrayList<TransactionModel>) {
        listOfSentTransactions = patients
        filterListOfSentTransactions = patients
        Log.i("HospitalsAdapter", listOfSentTransactions.size.toString())
    }

    fun clearList() {
        listOfSentTransactions.clear()
    }

    override fun getFilter(): Filter {
        return filteredProvidersList
    }

    private val filteredProvidersList = object : Filter() {

        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            var filteredList = java.util.ArrayList<TransactionModel>()

            if (charSequence == null || charSequence.isEmpty()) {
                filteredList = filterListOfSentTransactions
            } else {
                val filterPattern = charSequence.toString().toLowerCase().trim { it <= ' ' }
                for (item in filterListOfSentTransactions) {
                    if (item.balance.toLowerCase().contains(filterPattern) ||
                            item.reference.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            listOfSentTransactions = filterResults.values as java.util.ArrayList<TransactionModel>
            notifyDataSetChanged()
        }
    }
}