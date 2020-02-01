package com.beyondthehorizon.routeapp.Views

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.Adapters.ContactsAdapater
import com.beyondthehorizon.routeapp.Models.Contacts
import com.beyondthehorizon.routeapp.R

class RequestFundsActivity: AppCompatActivity(){
    var contactsRecyclerView: RecyclerView? = null
    var contactsAdapater: ContactsAdapater? = null
    var map: MutableMap<String, Contacts>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun loadContacts(){
        val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "ASC")
        var dummyId = 0
        while(phones!!.moveToNext()){
            val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            map!!.put(phoneNumber, Contacts(id = dummyId, name = name, contact = phoneNumber ))
            dummyId++
            Log.d("RouteUser>>", name + "  " + phoneNumber)
        }
        Log.d("New RouteUser>>", "None")
    }

    fun prevPage() {
        super.onBackPressed()
    }
}