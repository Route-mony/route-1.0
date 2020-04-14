package com.beyondthehorizon.routeapp.views.requestfunds.ui.main

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.adapters.ContactsAdapater
import com.beyondthehorizon.routeapp.databinding.FragmentRequestFundsBinding
import com.beyondthehorizon.routeapp.models.Contact
import com.beyondthehorizon.routeapp.utils.Constants
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import java.lang.Exception


/**
 * A simple [Fragment] subclass.
 */
class RequestFundsFragment : Fragment() {
    private var contacts: MutableList<Contact> = mutableListOf()
    private var contactMap: MutableMap<String, Contact> = mutableMapOf()
    private lateinit var prefs: SharedPreferences
    private lateinit var searchView: SearchView
    private lateinit var binding: FragmentRequestFundsBinding
    private lateinit var contactsAdapater: ContactsAdapater
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var dummyId = 0
    private var REQUEST_READ_CONTACTS = 79
    //    private lateinit var context: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//        binding = DataBindingUtil.inflate(R.layout.fragment_request_funds, container, false)

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_request_funds, container, false)

        val view = binding.root
        recyclerView = binding.contactRecyclerView
        linearLayoutManager = LinearLayoutManager(activity)
        searchView = binding.contactSearchView
        contacts = mutableListOf()
        contactMap = mutableMapOf()
        contactsAdapater = ContactsAdapater(activity!!, contacts)

        prefs = activity!!.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)

        try {
            if (ActivityCompat.checkSelfPermission(activity!!, android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                loadRouteContacts()
            } else {
                requestPermission();
            }

        } catch (e: Exception) {
            Toast.makeText(activity!!, e.message, Toast.LENGTH_LONG).show()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                var pattern = newText.toLowerCase().toRegex()
                try {
                    var filteredContacts = contacts.filter { pattern.containsMatchIn(it.contact) || pattern.containsMatchIn(it.name.toLowerCase()) }
                    var adapter = ContactsAdapater(activity!!, filteredContacts.toMutableList())
                    recyclerView.layoutManager = linearLayoutManager
                    recyclerView.setHasFixedSize(true)
                    contactsAdapater = ContactsAdapater(activity!!, filteredContacts.toMutableList())
                    recyclerView.adapter = adapter
                } catch (ex: Exception) {
                    Toast.makeText(activity!!, ex.message, Toast.LENGTH_LONG).show()
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })


        return view
    }


    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.READ_CONTACTS),
                    REQUEST_READ_CONTACTS)
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.READ_CONTACTS),
                    REQUEST_READ_CONTACTS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_READ_CONTACTS -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(activity!!, "Permission Denied", Toast.LENGTH_SHORT).show()

                } else {
                    loadRouteContacts()
                }
            }
        }
    }

    private fun loadPhoneContacts() {
        try {
            val phones = activity!!.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            while (phones!!.moveToNext()) {
                val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                var cleanedPhoneNumber = phoneNumber.replace("-", "").replace(" ", "").replaceBefore("7", "0")
                var id = phoneNumber.hashCode().toString()
                contactMap.put(cleanedPhoneNumber, Contact(id, name, phoneNumber))
            }
        } catch (e: Exception) {
            Toast.makeText(activity!!, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun loadRouteContacts() {
        try {
            val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
            Constants.loadUserContacts(activity!!, token).setCallback { e, result ->
                if (result != null) {
                    mapContactsToList(result.getAsJsonArray("rows"))
                }
            }
        } catch (e: Exception) {
            Toast.makeText(activity!!, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun mapContactsToList(result: JsonArray) {
        loadPhoneContacts()

        if (result != null) {
            for (item: JsonElement in result) {
                var phone = item.asJsonObject.get("phone_number").asString.replace("-", "").replace(" ", "").replaceBefore("7", "0")
                var accountNumber = item.asJsonObject.get("wallet_account").asJsonObject.get("wallet_account").toString()

//                Log.e("RequestFundsActivity", item.asJsonObject.get("wallet_account").asJsonObject.get("wallet_account").toString())

                if (contactMap.keys.contains(phone)) {
                    var id = item.asJsonObject.get("id").asString
                    var avatar = R.drawable.group416
                    contactMap.getValue(phone).id = id
                    contactMap.getValue(phone).contact = item.asJsonObject.get("phone_number").asString
                    contactMap.getValue(phone).avatar = avatar
                    contactMap.getValue(phone).accountNumber = accountNumber
                }
            }
        } else {
            Log.d("ContactResponse", "No contacts registered on route")
        }

        contacts = contactMap.values.toMutableList()
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        contactsAdapater = ContactsAdapater(activity!!, contacts)
        recyclerView.adapter = contactsAdapater
    }



}
