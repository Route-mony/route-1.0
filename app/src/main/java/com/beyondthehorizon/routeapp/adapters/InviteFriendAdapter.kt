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
import com.beyondthehorizon.routeapp.models.InviteFriend
import kotlinx.android.synthetic.main.invite_friend_layout_item.view.*
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import android.R.attr.name
import android.content.SharedPreferences
import com.beyondthehorizon.routeapp.models.TransactionModel
import com.beyondthehorizon.routeapp.utils.Constants
import com.google.gson.Gson
import android.R.id.message
import android.R.attr.name
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.widget.EditText
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.share_receipt_to_admin.view.*
import androidx.core.content.ContextCompat.startActivity
import android.R.attr.name
import com.beyondthehorizon.routeapp.views.MainActivity
import androidx.core.content.ContextCompat.startActivity
import android.R.attr.name
import android.net.Uri


class InviteFriendAdapter(private val context: Context, private val theView: String) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

//        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.invite_friend_layout_item -> ViewHolderInvite(
                    LayoutInflater.from(context).inflate(R.layout.invite_friend_layout_item, parent, false)
            )

            R.layout.share_receipt -> ViewHolderShareReceipt(
                    LayoutInflater.from(context).inflate(R.layout.share_receipt, parent, false)
            )

            else -> throw IllegalArgumentException("Unsupported layout") // in case populated with a model we don't know how to display.
        }
    }

    override fun getItemCount(): Int {
        return if (listOfContacts.size == 0) {
            0
        } else {
            listOfContacts.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val inviteFriend: InviteFriend = listOfContacts[position]

        when (holder) {
            is ViewHolderInvite -> {
                holder.bind(inviteFriend)
            }

            is ViewHolderShareReceipt -> {
//                val adModel = element as AdModel
                // bind AdViewHolder
                holder.bind(inviteFriend)
            }

        }
    }

    private var listOfContacts = ArrayList<InviteFriend>()
    private var filterListOfContacts = ArrayList<InviteFriend>()

    inner class ViewHolderInvite(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to

        //        private val patientDoctor = view.p_doctor!!
        private val userName = view.userName!!
        private val phoneNumber = view.phoneNumber!!
        private val inviteBtn = view.inviteBtn!!
        lateinit var inviteFriend: InviteFriend

        init {
            inviteBtn.setOnClickListener {
                val shareBody = "Hello checkout this awesome app! Route App"
//                val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
//                sharingIntent.type = "text/plain"
//                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Invite Friend")
//                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
//                context.startActivity(Intent.createChooser(sharingIntent, "Invite Friend To Route App"))

                val uri = Uri.parse("smsto:${inviteFriend.phone}")
                val intent = Intent(Intent.ACTION_SENDTO, uri)
                intent.putExtra("sms_body", shareBody)
                context.startActivity(intent)

            }
        }

        fun bind(invite: InviteFriend) {
            userName.text = invite.username
            phoneNumber.text = invite.phone
            inviteFriend = invite
        }

    }

    inner class ViewHolderShareReceipt(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to

        //        private val patientDoctor = view.p_doctor!!
        private val userName = view.userName!!
        private val phoneNumber = view.phoneNumber!!
        private val inviteBtn = view.inviteBtn!!
        private var personString = ""
        private var inviteFriend: InviteFriend? = null

        var pref: SharedPreferences =
                context.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)

        val token = "Bearer " + pref.getString(Constants.USER_TOKEN, "")
        val gson = Gson()

        private val transactionModel: TransactionModel = gson.fromJson<TransactionModel>(pref.getString(Constants.TRANSACTION_DETAILS, ""),
                TransactionModel::class.java!!)
        private val progressBar = ProgressDialog(context)

        init {
            inviteBtn.setOnClickListener {
                // Initialize a new instance of
                //before inflating the custom alert dialog layout, we will get the current activity viewgroup
//                val viewGroup = context.findViewById(android.R.id.content)
                progressBar.setMessage("Please wait...")
                val editor = pref.edit()
                //then we will inflate the custom alert dialog xml that we created
//                val dialogView = LayoutInflater.from(context).inflate(R.layout.share_receipt_to_admin, null, false)
//                //Now we need an AlertDialog.Builder object
//                val builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
//                //setting the view of the builder to our custom view that we already inflated trans_type
//                builder.setView(dialogView)
//                //finally creating the alert dialog and displaying it
//                val alertDialog = builder.create()
//                alertDialog.setCanceledOnTouchOutside(false)
//                alertDialog.show()
//                dialogView.trans_type.text = "Share Receipt with ${inviteFriend!!.username}?"
//
//                dialogView.cancel_action.setOnClickListener {
//                    alertDialog.dismiss()
//                }
//
//                dialogView.yes_action.setOnClickListener {
                // Set the alert dialog title
                val builder2 = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
                builder2.setTitle("Share Receipt")
                // Display a message on alert dialog
                builder2.setMessage("Share Receipt with ${inviteFriend!!.username}?")


                // Set a positive button and its click listener on alert dialog
                builder2.setPositiveButton("Ok") { dialog2, which ->

                    Log.e("InviteFriendAdapter", transactionModel.withdrawn)
                    postUserReceipt(context, token, pref.getString(SHARE_RECEIPT_TITLE, ""),
                            inviteFriend!!.id, transactionModel.withdrawn, transactionModel.description,
                            pref.getString(SHARE_RECEIPT_TO_ID, ""), transactionModel.created_at

                    ).setCallback { _, result ->
                        Log.e("InviteFriendAdapter", result.toString())

                        progressBar.dismiss()

                        val builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
                        if (result.get("status").asString.contains("success")) {
                            builder.setMessage(result.get("data").asJsonObject.get("message").asString)

                            // Set a positive button and its click listener on alert dialog
                            builder.setPositiveButton("Ok") { dialog, which ->
                                // Do something when user press the positive button
                                editor.remove(SHARE_RECEIPT_TO_ID)
                                editor.remove(SHARE_RECEIPT_TITLE)
                                editor.apply()
                                dialog.dismiss()
                                dialog2.dismiss()

                                val intent = Intent(context, TransactionsActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            }
                            // Finally, make the alert dialog using builder
                            val dialog: AlertDialog = builder.create()
                            dialog.setCanceledOnTouchOutside(false)
                            // Display the alert dialog on app interface
                            dialog.show()
                        } else {
                            // Set the alert dialog title
                            builder.setTitle("Unable to Share Receipt")
                            // Display a message on alert dialog
                            builder.setMessage("Something went Wrong, Try again")
                            // Set a positive button and its click listener on alert dialog
                            builder.setPositiveButton("Ok") { dialog, which ->
                                // Do something when user press the positive button
                                dialog.dismiss()
                                dialog2.dismiss()
                            }
                            // Finally, make the alert dialog using builder
                            val dialog: AlertDialog = builder.create()
                            dialog.setCanceledOnTouchOutside(false)
                            dialog.show()
                        }

                    }
                }
                builder2.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
// Finally, make the alert dialog using builder
                val dialog2: AlertDialog = builder2.create()
                // Display the alert dialog on app interface
                dialog2.show()
//                }
            }
        }

        fun bind(invite: InviteFriend) {
            userName.text = invite.username
            phoneNumber.text = invite.phone
            personString = invite.id
            inviteFriend = invite
        }

    }


    override fun getItemViewType(position: Int): Int {

        if (theView.contains("Invite")) {
            return R.layout.invite_friend_layout_item
        }
        return R.layout.share_receipt

    }

    fun setContact(patients: ArrayList<InviteFriend>) {
        listOfContacts = patients
        filterListOfContacts = patients
        Log.i("HospitalsAdapter", listOfContacts.size.toString())
    }

    fun clearList() {
        listOfContacts.clear()
    }

    override fun getFilter(): Filter {
        return filteredProvidersList
    }

    private val filteredProvidersList = object : Filter() {

        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            var filteredList = java.util.ArrayList<InviteFriend>()

            if (charSequence == null || charSequence.isEmpty()) {
                filteredList = filterListOfContacts
            } else {
                val filterPattern = charSequence.toString().toLowerCase().trim { it <= ' ' }
                for (item in filterListOfContacts) {
                    if (item.username.toLowerCase().contains(filterPattern) ||
                            item.phone.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            listOfContacts = filterResults.values as java.util.ArrayList<InviteFriend>
            notifyDataSetChanged()
        }
    }

}