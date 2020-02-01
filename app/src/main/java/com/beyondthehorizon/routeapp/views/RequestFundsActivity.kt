package com.beyondthehorizon.routeapp.views

import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.routeapp.adapters.ContactsAdapater
import com.beyondthehorizon.routeapp.models.Contact
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityRequestFundsBinding

class RequestFundsActivity: AppCompatActivity(){
    var contacts: MutableList<Contact> = mutableListOf()
    var contactMap: MutableMap<String, Contact> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var binding: ActivityRequestFundsBinding = DataBindingUtil.setContentView(this, R.layout.activity_request_funds)
        var recyclerView = binding.contactRecyclerView
        var layoutManager = LinearLayoutManager(this)

        loadPhoneContacts()

        contacts = contactMap.values.toMutableList()

        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = ContactsAdapater(this, contacts)
    }

    fun loadPhoneContacts(){
        val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        var dummyId = 0
        while(phones!!.moveToNext()){
            val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            contactMap.put(phoneNumber, (Contact(dummyId, name, phoneNumber, "@drawable/group416.png")))
            dummyId++
        }
    }

    fun loadRouteContacts(){

    }

    fun prevPage() {
        super.onBackPressed()
    }
}