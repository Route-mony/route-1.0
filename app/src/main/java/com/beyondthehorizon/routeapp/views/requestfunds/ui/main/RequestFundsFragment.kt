package com.beyondthehorizon.routeapp.views.requestfunds.ui.main

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.beyondthehorizon.routeapp.utils.Constants.MY_ALL_ROUTE_CONTACTS
import com.beyondthehorizon.routeapp.utils.Constants.MY_ROUTE_CONTACTS
import com.beyondthehorizon.routeapp.utils.CustomProgressBar
import com.beyondthehorizon.routeapp.utils.NetworkUtils
import com.beyondthehorizon.routeapp.views.ConfirmFundRequestActivity
import com.beyondthehorizon.routeapp.views.MainActivity
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


/**
 * A simple [Fragment] subclass.
 */
class RequestFundsFragment : Fragment() {
    private lateinit var networkUtils: NetworkUtils
    private var contacts: MutableList<Contact> = mutableListOf()
    private var contactMap: MutableMap<String, Contact> = mutableMapOf()
    private var routeContactMap: MutableMap<String, Contact> = mutableMapOf()
    private lateinit var prefs: SharedPreferences
    private lateinit var searchView: SearchView
    private lateinit var parentIntent: Intent
    private lateinit var cardStatus: String
    private lateinit var childIntent: Intent
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var binding: FragmentRequestFundsBinding
    private lateinit var transactionType: String
    private lateinit var contactsAdapater: ContactsAdapater
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var dummyId = 0
    private lateinit var mobileNumber: String
    private var REQUEST_READ_CONTACTS = 79
    private lateinit var token: String
    private lateinit var tvNoInternet: TextView
    private lateinit var rvReceived: RecyclerView
    val progressBar = CustomProgressBar()

    //    private lateinit var context: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_request_funds, container, false)
        networkUtils = NetworkUtils(requireContext())

        val view = binding.root
        recyclerView = binding.contactRecyclerView
        linearLayoutManager = LinearLayoutManager(activity)
        searchView = binding.contactSearchView
        contacts = mutableListOf()
        contactMap = mutableMapOf()
        contactsAdapater = ContactsAdapater(requireActivity(), contacts)

        prefs = requireActivity().getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        editor = prefs.edit()
        parentIntent = requireActivity().intent
        childIntent = Intent(requireActivity(), ConfirmFundRequestActivity::class.java)
        progressBar.show(requireActivity(), "Loading contact...")

        var transactionMessage = ""
        token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")

        transactionType = prefs.getString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, "").toString()

        try {
            if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                if (prefs.getString(MY_ROUTE_CONTACTS, "")!!.isNotEmpty()) {

                    val gson = Gson()
                    val string = prefs.getString(MY_ROUTE_CONTACTS, "")
                    val type: Type = object : TypeToken<MutableList<Contact>>() {}.type
                    contacts = gson.fromJson(string, type)

                    recyclerView.layoutManager = linearLayoutManager
                    recyclerView.setHasFixedSize(true)
                    contactsAdapater = ContactsAdapater(requireActivity(), contacts)
                    recyclerView.adapter = contactsAdapater

                } else {
                    loadRouteContacts()
                }
            } else {
                requestPermission();
            }
            progressBar.dialog.dismiss()
        } catch (e: Exception) {
            Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                var pattern = newText.toLowerCase().toRegex()
                try {
                    var filteredContacts = contacts.filter { pattern.containsMatchIn(it.contact) || pattern.containsMatchIn(it.name.toLowerCase()) }
                    var adapter = ContactsAdapater(requireActivity(), filteredContacts.toMutableList())
                    recyclerView.layoutManager = linearLayoutManager
                    recyclerView.setHasFixedSize(true)
                    contactsAdapater = ContactsAdapater(requireActivity(), filteredContacts.toMutableList())
                    recyclerView.adapter = adapter
                } catch (ex: Exception) {
                    Toast.makeText(requireActivity(), ex.message, Toast.LENGTH_LONG).show()
                }
                progressBar.dialog.dismiss()
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            loadRouteContacts()
        }
        return view
    }


    private fun requestPermission() {
        progressBar.dialog.dismiss()
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS),
                    REQUEST_READ_CONTACTS)
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS),
                    REQUEST_READ_CONTACTS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        progressBar.dialog.dismiss()
        when (requestCode) {
            REQUEST_READ_CONTACTS -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(requireActivity(), "Permission Denied", Toast.LENGTH_SHORT).show()

                } else {

                    loadRouteContacts()

                }
            }
        }
    }

    private fun loadPhoneContacts() {
        try {
            val phones = requireActivity().contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            while (phones!!.moveToNext()) {
                val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                var cleanedPhoneNumber = phoneNumber.replace("-", "").replace(" ", "").replaceBefore("7", "0")
                var id = phoneNumber.hashCode().toString()
                contactMap.put(cleanedPhoneNumber, Contact(id, name, phoneNumber))
                progressBar.dialog.dismiss()
            }
        } catch (e: Exception) {
            Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
            progressBar.dialog.dismiss()
        }
    }

    private fun loadRouteContacts() {
        if (networkUtils.isNetworkAvailable) {
            try {
                val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
                Constants.loadUserContacts(requireActivity(), token).setCallback { e, result ->

                    if (result != null) {
                        progressBar.dialog.dismiss()
                        if (prefs.getString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "") == Constants.SEND_MONEY_TO_ROUTE
                                || prefs.getString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "") == Constants.REQUEST_MONEY) {

                            if (prefs.getString(MY_ROUTE_CONTACTS, "")!!.isNotEmpty()) {

                                val gson = Gson()
                                val string = prefs.getString(MY_ROUTE_CONTACTS, "")
                                val type: Type = object : TypeToken<MutableList<Contact>>() {}.type
                                contacts = gson.fromJson(string, type)

                                recyclerView.layoutManager = linearLayoutManager
                                recyclerView.setHasFixedSize(true)
                                contactsAdapater = ContactsAdapater(requireActivity(), contacts)
                                recyclerView.adapter = contactsAdapater

                            } else {
                                mapRouteContactsToList(result.getAsJsonArray("rows"))
                            }
                        } else {
                            if (prefs.getString(MY_ROUTE_CONTACTS, "")!!.isNotEmpty()) {

                                val gson = Gson()
                                val string = prefs.getString(MY_ALL_ROUTE_CONTACTS, "")
                                val type: Type = object : TypeToken<MutableList<Contact>>() {}.type
                                contacts = gson.fromJson(string, type)

                                recyclerView.layoutManager = linearLayoutManager
                                recyclerView.setHasFixedSize(true)
                                contactsAdapater = ContactsAdapater(requireActivity(), contacts)
                                recyclerView.adapter = contactsAdapater

                            } else {
                                mapContactsToList(result.getAsJsonArray("rows"))
                            }
                        }

                    } else {
                        progressBar.dialog.dismiss()
                        Toast.makeText(requireActivity(), "Error Loading contacts", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
            }
        }
        else{
            internetDialog()
        }
    }

    private fun mapContactsToList(result: JsonArray) {
        loadPhoneContacts()
        for (item: JsonElement in result) {
            var phone = item.asJsonObject.get("phone_number").asString.replace("-", "").replace(" ", "").replaceBefore("7", "0")
            var accountNumber = item.asJsonObject.get("wallet_account").asJsonObject.get("wallet_account").asString

            if (contactMap.keys.contains(phone)) {
                var id = item.asJsonObject.get("id").asString
                var avatar = R.drawable.group416
                contactMap.getValue(phone).id = id
                contactMap.getValue(phone).contact = item.asJsonObject.get("phone_number").asString
                contactMap.getValue(phone).avatar = avatar
                contactMap.getValue(phone).accountNumber = accountNumber
            }
        }

        contacts = contactMap.values.toMutableList()
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        contactsAdapater = ContactsAdapater(requireActivity(), contacts)
        recyclerView.adapter = contactsAdapater
        progressBar.dialog.dismiss()
        val gson = Gson()
        val json: String = gson.toJson(contacts)
        editor.putString(MY_ALL_ROUTE_CONTACTS, json)
        editor.apply()
    }

    private fun mapRouteContactsToList(result: JsonArray) {
        loadPhoneContacts()
        for (item: JsonElement in result) {
            var phone = item.asJsonObject.get("phone_number").asString.replace("-", "").replace(" ", "").replaceBefore("7", "0")
            var accountNumber = item.asJsonObject.get("wallet_account").asJsonObject.get("wallet_account").asString

            if (contactMap.keys.contains(phone)) {
                var id = item.asJsonObject.get("id").asString
                var avatar = R.drawable.group416
                contactMap.getValue(phone).id = id
                contactMap.getValue(phone).contact = item.asJsonObject.get("phone_number").asString
                contactMap.getValue(phone).avatar = avatar
                contactMap.getValue(phone).accountNumber = accountNumber

                routeContactMap.put(
                        phone,
                        Contact(id,
                                contactMap.getValue(phone).name,
                                item.asJsonObject.get("phone_number").asString,
                                avatar,
                                accountNumber))
            }
        }

        contacts = routeContactMap.values.toMutableList()
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        contactsAdapater = ContactsAdapater(requireActivity(), contacts)
        recyclerView.adapter = contactsAdapater
        binding.swipeRefresh.isRefreshing = false
        progressBar.dialog.dismiss()
        val gson = Gson()
        val json: String = gson.toJson(contacts)
        editor.putString(MY_ROUTE_CONTACTS, json)
        editor.apply()

    }

    private fun internetDialog(){
        // Initialize a new instance of
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("No Connection")
        builder.setMessage("No internet connection")
        builder.setPositiveButton("Retry") { dialog, _ ->
            dialog.dismiss()
            loadRouteContacts()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        return
    }
}