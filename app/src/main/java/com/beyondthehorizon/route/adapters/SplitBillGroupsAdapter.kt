package com.beyondthehorizon.route.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.interfaces.OnSplitBillSelectedListener
import com.beyondthehorizon.route.models.BillResponse
import kotlinx.android.synthetic.main.saved_group_item.view.*

open class SplitBillGroupsAdapter(
    context: Context,
    bills: List<BillResponse>,
    private val onSplitBillSelectedListener: OnSplitBillSelectedListener
) : RecyclerView.Adapter<SplitBillGroupsAdapter.ViewHolder>() {
    val bills = bills.reversed()
    val context = context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.saved_group_item, parent, false) as View
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return bills.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bill = bills[position]
        holder.itemView.group_date.text = bill.createdAt!!.replaceFirstChar { it.titlecase() }
        holder.itemView.group_name.text = bill.reason
        holder.itemView.group_status.text = String.format("Status: %s", bill.generalStatus)
        holder.itemView.group_amount.text = String.format("Amount: Kes %s ", bill.totalRequested)
        holder.itemView.setOnClickListener {
            onSplitBillSelectedListener.selectedSplitBill(bill)
        }
    }

}