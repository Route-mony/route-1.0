package com.beyondthehorizon.routeapp.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.text.Html
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
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.views.transactions.main.TransactionDetailsActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.invite_friend_layout_item.view.*
import kotlinx.android.synthetic.main.invite_friend_layout_item.view.userName
import kotlinx.android.synthetic.main.recycvler_header.view.*
import kotlinx.android.synthetic.main.sent_transactions.view.*

class TransactionsAdapter(private val context: Context) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var listOfSentTransactions = ArrayList<TransactionModel>()
    private var filterListOfSentTransactions = ArrayList<TransactionModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var layout = 0
        val viewHolder: RecyclerView.ViewHolder?
        when (viewType) {
            RECYCLER_SECTION -> {
                layout = R.layout.sent_transactions
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
        val transactionModel: TransactionModel = listOfSentTransactions[position]

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
        //        private val userNameTextView: TextView
//        private val TimeTextView: TextView
        private val recyclerHeader = itemView.recyclerHeader!!

        init {
        }

        fun bind(invite: TransactionModel) {
            // Attach values for each item
            recyclerHeader.text = invite.created_at
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to

        //        private val patientDoctor = view.p_doctor!!
        private val userName = view.userName!!
        private val transTime = view.transTime!!
        private val amountTxt = view.amountTxt!!
        private val minus = view.minus!!
        private val profileImage = view.profileImage

        private var transactionModel: TransactionModel? = null

        var sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)

        init {
            view.setOnClickListener {
                val editor = sharedPref.edit()
                val gson = Gson()
                val personString = gson.toJson(transactionModel)
                editor.putString(TRANSACTION_DETAILS, personString)
                editor.apply()
                context.startActivity(Intent(context, TransactionDetailsActivity::class.java))
//                Toast.makeText(context, "Coming soon", Toast.LENGTH_LONG).show()
            }
        }

        fun bind(invite: TransactionModel) {
            userName.text = invite.details
            transTime.text = invite.created_at
            if (invite.paymentType.compareTo("received") == 0) {
                minus.setTextColor(Color.parseColor("#40CA08"))
                minus.text = "+ "
                amountTxt.text = "Ksh. ${invite.withdrawn}"
            } else {
//                amountTxt.setTextColor(Color.parseColor("#c4838d"))
                minus.visibility = View.VISIBLE
                amountTxt.text = "Ksh. ${invite.withdrawn}"
            }

            Glide.with(context)
                    .load(invite.profile_image)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .placeholder(R.drawable.ic_user)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage)
            transactionModel = invite
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

    override fun getItemViewType(position: Int): Int {
        if (listOfSentTransactions[position].type.compareTo(RECYCLER_SECTION) == 0) {
            return RECYCLER_SECTION
        } else if (listOfSentTransactions[position].type.compareTo(RECYCLER_HEADER) == 0) {
            return RECYCLER_HEADER
        }
        return -1
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