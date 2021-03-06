package com.beyondthehorizon.routeapp.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.models.Contact
import com.beyondthehorizon.routeapp.utils.Constants.PHONE_NUMBER
import com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES
import com.beyondthehorizon.routeapp.views.FundAmountActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_contact.view.*


class ContactsHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    var editor: SharedPreferences.Editor = context.getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
    private lateinit var prefs: SharedPreferences
    private lateinit var requestType: String
    var intent = Intent(context, FundAmountActivity::class.java)
    var context = context

    /**
     * Set view with values available
     */
    fun setValues(value: Contact) {
        itemView.username.text = value.name
        itemView.contact.text = value.contact
        Picasso.get().load(value.avatar).into(itemView.profile_image)

        itemView.setOnClickListener {
            try {
                prefs = context.getSharedPreferences(REG_APP_PREFERENCES, 0)
                editor.putString("Id", value.id)
                editor.putString("Username", value.name)
                editor.putString(PHONE_NUMBER, value.contact)
                editor.putString("accountNumber", value.accountNumber)
                editor.putString("walletAccountNumber", value.accountNumber)
                editor.apply()
                intent.putExtra(PHONE_NUMBER, value.contact)
                context.startActivity(intent)
            } catch (ex: Exception) {
                Log.d("TAG", ex.message)
            }
        }
    }
}
