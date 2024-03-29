package com.beyondthehorizon.route.adapters

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.models.BulkyRequestModel
import com.beyondthehorizon.route.utils.Constants
import kotlinx.android.synthetic.main.bulky_item.view.*

class BulkyRequestAdapter(private val context: Context, val removeOnClick: (Int) -> Unit) :
        RecyclerView.Adapter<BulkyRequestAdapter.ViewHolder>() {

    interface BulkyInterface {
        fun updateItemRmoved()
    }

    private var listOfSentTransactions = ArrayList<BulkyRequestModel>()
    private var filterListOfSentTransactions = ArrayList<BulkyRequestModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.bulky_item,
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
        val bulkyRequestModel: BulkyRequestModel = listOfSentTransactions[position]
        holder.bind(bulkyRequestModel, position)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to

        //        private val patientDoctor = view.p_doctor!!
        private val reason = view.reqReason!!
        private val quantity = view.reqQuantity!!
        private val amountTxt = view.reqAmount!!
        private val closeBtn = view.remove!!

        private var bulkyRequestModel: BulkyRequestModel? = null

        var sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)

        init {
//            closeBtn.setOnClickListener {
////                val editor = sharedPref.edit()
////                val gson = Gson()
////                val personString = gson.toJson(bulkyRequestModel)
////                editor.putString(TRANSACTION_DETAILS, personString)
////                editor.apply()
////                context.startActivity(Intent(context, TransactionDetailsActivity::class.java))
//                Toast.makeText(context, "Coming soon", Toast.LENGTH_LONG).show()
//            }
        }

        fun bind(invite: BulkyRequestModel, position: Int) {
            reason.text = invite.reason
            quantity.text = invite.quantity
            amountTxt.text = invite.amount
            bulkyRequestModel = invite
            closeBtn.setOnClickListener {
//                val editor = sharedPref.edit()
//                val gson = Gson()
//                val personString = gson.toJson(bulkyRequestModel)
//                editor.putString(TRANSACTION_DETAILS, personString)
//                editor.apply()
//                context.startActivity(Intent(context, TransactionDetailsActivity::class.java))
                if (position != 0) {
                    listOfSentTransactions.removeAt(position)
                    setContact(listOfSentTransactions)
                    removeOnClick(position)
                    Toast.makeText(context, "Item removed successfully", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    fun setContact(patients: ArrayList<BulkyRequestModel>) {
        listOfSentTransactions = patients
        filterListOfSentTransactions = patients
        Log.i("HospitalsAdapter", listOfSentTransactions.size.toString())
        notifyDataSetChanged()
    }

    fun clearList() {
        listOfSentTransactions.clear()
    }

}