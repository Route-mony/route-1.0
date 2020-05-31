package com.beyondthehorizon.routeapp.adapters

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.bottomsheets.EditSendToManyBottomSheet
import com.beyondthehorizon.routeapp.models.MultiContactModel
import com.beyondthehorizon.routeapp.utils.Constants
import com.google.gson.Gson
import kotlinx.android.synthetic.main.send_many_item.view.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class GroupSendToManyAdapter(private val context: Context, val mListener: SendToManyInterface, val removeOnClick: (Int) -> Unit) :
        RecyclerView.Adapter<GroupSendToManyAdapter.ViewHolder>() {

    interface SendToManyInterface {
        fun updateItem(item: String, itemPosition: String)
    }

    private var listOfSentTransactions = ArrayList<MultiContactModel>()
    private var filterListOfSentTransactions = ArrayList<MultiContactModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.send_many_item,
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
        val bulkyRequestModel: MultiContactModel = listOfSentTransactions[position]
        holder.bind(bulkyRequestModel, position)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to

        //        private val patientDoctor = view.p_doctor!!
        private val reqName = view.reqName!!
        private val reqAmount = view.reqAmount!!
        private val closeBtn = view.remove!!

        private var bulkyRequestModel: MultiContactModel? = null
        private var pst: Int? = null

        var sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)

        init {
            view.setOnClickListener {
                val gson = Gson()
                val personString = gson.toJson(bulkyRequestModel)
                mListener.updateItem(personString, pst.toString())

            }
        }

        fun bind(invite: MultiContactModel, position: Int) {
            reqAmount.text = NumberFormat.getNumberInstance(Locale.getDefault()).format(invite.amount.toInt())
            reqName.text = invite.username
            bulkyRequestModel = invite
            pst = position
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

    fun setContact(patients: ArrayList<MultiContactModel>) {
        listOfSentTransactions = patients
        filterListOfSentTransactions = patients
        Log.i("HospitalsAdapter", listOfSentTransactions.size.toString())
        notifyDataSetChanged()
    }

    fun clearList() {
        listOfSentTransactions.clear()
    }

}