package com.beyondthehorizon.routeapp.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.models.Contact
import com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES
import com.beyondthehorizon.routeapp.views.FundAmountActivity
import com.beyondthehorizon.routeapp.views.NotificationActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_contact.view.*


class ContactsHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {

    var prefs: SharedPreferences.Editor = context.getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
    var intent = Intent(context, FundAmountActivity::class.java)
    var context = context
    /**
     * Set view with values available
     */
    fun setValues (value: Contact) {
        itemView.username.text = value.name
        itemView.contact.text = value.contact
        Picasso.get().load(value.avatar).into(itemView.profile_image)

        itemView.setOnClickListener{
            try {
                prefs.putString("Id", value.id)
                prefs.putString("Username", value.name)
                prefs.putString("Phone", value.contact)
                prefs.apply()

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(intent)
            }
            catch (ex: Exception){
                Log.d("TAG", ex.message)
            }
        }
    }
}