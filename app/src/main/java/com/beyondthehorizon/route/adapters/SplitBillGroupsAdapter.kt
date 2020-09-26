package com.beyondthehorizon.route.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.models.BillResponse
import com.beyondthehorizon.route.utils.Constants.*
import com.beyondthehorizon.route.views.split.bill.SplitBillsDetailsActivity
import kotlinx.android.synthetic.main.saved_group_item.view.*
import java.lang.Boolean.TRUE

open class SplitBillGroupsAdapter(context: Context, bills: List<BillResponse>) : RecyclerView.Adapter<SplitBillGroupsAdapter.ViewHolder>() {
    val bills = bills.reversed()
    val context = context
    val editor = context.getSharedPreferences(REG_APP_PREFERENCES, 0).edit()

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
        holder.itemView.group_date.text = bills[position].date.capitalize()
        holder.itemView.group_name.text = bills[position].group
        holder.itemView.group_status.text = "Status: ${bills[position].status}"
        holder.itemView.group_amount.text = "Amount: Kes ${bills[position].amount}"
        Log.d("RECIPIENTS", bills[position].recipient.length().toString())

        holder.itemView.setOnClickListener {
            val intent = Intent(context, SplitBillsDetailsActivity::class.java)
            editor.putString(GROUP_ID, bills[position].id)
            editor.putBoolean(EXISTING_GROUP, TRUE)
            editor.apply()
            context.startActivity(intent)
        }
    }

}