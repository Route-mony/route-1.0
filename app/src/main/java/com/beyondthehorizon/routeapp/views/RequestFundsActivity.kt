package com.beyondthehorizon.routeapp.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.adapters.ContactsAdapater
import com.beyondthehorizon.routeapp.databinding.ActivityRequestFundsBinding
import com.beyondthehorizon.routeapp.models.Contact
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.views.auth.LoginActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.io.ObjectInput

class RequestFundsActivity: AppCompatActivity(){
    private var contacts: MutableList<Contact> = mutableListOf()
    private var contactMap: MutableMap<String, Contact> = mutableMapOf()
    private lateinit var prefs: SharedPreferences
    private lateinit var searchView: SearchView
    private lateinit var contactsAdapater: ContactsAdapater
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    var dummyId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var binding: ActivityRequestFundsBinding = DataBindingUtil.setContentView(this, R.layout.activity_request_funds)
        recyclerView = binding.contactRecyclerView
        linearLayoutManager = LinearLayoutManager(this)
        searchView = binding.contactSearchView

        prefs = applicationContext.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)

        //loadPhoneContacts()

        loadRouteContacts()
        contacts = contactMap.values.toMutableList()

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        contactsAdapater = ContactsAdapater(this, contacts)
        recyclerView.adapter = contactsAdapater

//        binding.contactSearchView.setOnQueryTextListener()
    }

    fun loadPhoneContacts(){
        val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        while(phones!!.moveToNext()){
            val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            contactMap.put(phoneNumber, (Contact(dummyId, name, phoneNumber)))
            dummyId++
        }
    }

    fun loadRouteContacts(){
        val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
        Constants.getUserProfile(this, token)
                .setCallback { e, result ->
                    if (result != null) {
                        Log.d("ContactResponse", result.asJsonObject.toString())
                        var phone = result.get("data").asJsonObject.get("phone_number").asString
                        var name =  result.get("data").asJsonObject.get("first_name").asString
                        contactMap.put(phone, Contact(dummyId, name, phone))
                        dummyId++
                    } else {
                        Log.d("ContactResponse", "No contacts found")
                        }
                    }
        }

    fun prevPage() {
        super.onBackPressed()
    }
}