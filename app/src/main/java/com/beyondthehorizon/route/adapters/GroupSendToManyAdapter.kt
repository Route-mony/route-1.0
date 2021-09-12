package com.beyondthehorizon.route.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.interfaces.bottomsheets.EditSendToManyBottomSheetListener
import com.beyondthehorizon.route.models.contacts.MultiContactModel
import kotlinx.android.synthetic.main.send_many_item.view.*
import java.text.NumberFormat
import java.util.*

class GroupSendToManyAdapter(
    private val context: Context,
    val onEditSendToManyItem: EditSendToManyBottomSheetListener,
) :
    RecyclerView.Adapter<GroupSendToManyAdapter.ViewHolder>() {

    private var listOfSentTransactions: MutableList<MultiContactModel> = mutableListOf()
    private var filterListOfSentTransactions: MutableList<MultiContactModel> = mutableListOf()
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
        return listOfSentTransactions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bulkyRequestModel: MultiContactModel = listOfSentTransactions[position]
        holder.bind(bulkyRequestModel, position)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val reqName = view.reqName!!
        private val reqAmount = view.reqAmount!!
        private val reqNumber = view.reqNumber
        private val closeBtn = view.remove!!

        private var bulkyRequestModel: MultiContactModel? = null
        private var pst: Int? = null

        init {
            view.setOnClickListener {
                onEditSendToManyItem.onSelectGroupItemForEdit(bulkyRequestModel!!, pst!!)
            }
        }

        fun bind(invite: MultiContactModel, position: Int) {
            try {
                reqAmount.text = NumberFormat.getNumberInstance(Locale.getDefault())
                    .format(invite.amount!!.toInt())
                reqName.text = invite.username
                reqNumber.text = invite.phoneNumber
                bulkyRequestModel = invite
                pst = position
                closeBtn.setOnClickListener {
                    if (position != 0) {
                        listOfSentTransactions.removeAt(position)
                        setContact(listOfSentTransactions)
                        Toast.makeText(context, "Item removed successfully", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            } catch (ex: Exception) {
                Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun setContact(contactsModel: MutableList<MultiContactModel>) {
        listOfSentTransactions = contactsModel
        filterListOfSentTransactions = contactsModel
        notifyDataSetChanged()
    }

    fun clearList() {
        listOfSentTransactions.clear()
    }
}
