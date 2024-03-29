package com.beyondthehorizon.route.views.multicontactschoice.ui.main

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.adapters.MultiChoiceContactsAdapter
import com.beyondthehorizon.route.databinding.FragmentRequestFundsBinding
import com.beyondthehorizon.route.models.Contact
import com.beyondthehorizon.route.models.MultiContactModel
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.Constants.*
import com.beyondthehorizon.route.utils.NetworkUtils
import com.beyondthehorizon.route.views.ConfirmFundRequestActivity
import com.beyondthehorizon.route.views.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_request_funds.*
import org.json.JSONObject
import timber.log.Timber
import java.lang.reflect.Type


/**
 * A simple [Fragment] subclass.
 */
class MultiChoiceContactsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var networkUtils: NetworkUtils
    private lateinit var contacts: ArrayList<MultiContactModel>
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
    private lateinit var contactsAdapater: MultiChoiceContactsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var dummyId = 0
    private lateinit var mobileNumber: String
    private var REQUEST_READ_CONTACTS = 79
    private lateinit var token: String

    //    private lateinit var context: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_request_funds, container, false)
        networkUtils = NetworkUtils(requireContext())

        val view = binding.root
        recyclerView = binding.contactRecyclerView
        linearLayoutManager = LinearLayoutManager(activity)
        searchView = binding.contactSearchView

        prefs = requireActivity().getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        editor = prefs.edit()
        parentIntent = requireActivity().intent
        childIntent = Intent(requireActivity(), ConfirmFundRequestActivity::class.java)
        var transactionMessage = ""
        token = "Bearer " + prefs.getString(USER_TOKEN, "")

        transactionType = prefs.getString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, "").toString()

        try {
            if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {

                //SEND TO MANY BY ROUTE CONTACTS
                if (prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "")!!.compareTo(SEND_MONEY_TO_ROUTE) == 0) {
                    contacts = ArrayList()
                    //CHECK IF WE HAVE CACHED ANY ROUTE CONTACTS FROM DB BEFORE
                    if (prefs.getString(MY_ROUTE_CONTACTS_NEW, "")!!.isNotEmpty()) {
                        val gson = Gson()
                        val string = prefs.getString(MY_ROUTE_CONTACTS_NEW, "")
                        val type: Type = object : TypeToken<ArrayList<MultiContactModel>>() {}.type
                        contacts = gson.fromJson(string, type)
                        recyclerView.layoutManager = linearLayoutManager
                        recyclerView.setHasFixedSize(true)
                        contactsAdapater = MultiChoiceContactsAdapter(requireActivity(), contacts)
                        recyclerView.adapter = contactsAdapater
                    } else {
                        //CHECK IF WE HAVE CACHED ANY ROUTE CONTACTS FROM DB BEFORE IF NOT LOAD CONTACTS FROM BACKEND
                        loadRegisteredContacts()
                    }
                } else {
                    contacts = ArrayList()
                    //SEND TO MANY BY ANY MOBILE CONTACTS
                    //CHECK IF WE HAVE CACHED ANY ANY CONTACTS FROM DB BEFORE
                    if (prefs.getString(MY_ALL_CONTACTS_NEW, "")!!.isNotEmpty()) {
                        val gson = Gson()
                        val string = prefs.getString(MY_ALL_CONTACTS_NEW, "")
                        val type: Type = object : TypeToken<ArrayList<MultiContactModel>>() {}.type
                        contacts = gson.fromJson(string, type)
                        recyclerView.layoutManager = linearLayoutManager
                        recyclerView.setHasFixedSize(true)
                        contactsAdapater = MultiChoiceContactsAdapter(requireActivity(), contacts)
                        recyclerView.adapter = contactsAdapater
                    } else {
                        //CHECK IF WE HAVE CACHED ANY  CONTACTS FROM DB BEFORE IF NOT LOAD CONTACTS FROM BACKEND
                        loadRegisteredContacts()
                    }
                }
            } else {
                requestPermission();
            }

        } catch (e: Exception) {
            Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
        }

        binding.contactSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                if (!newText.toLowerCase().trim().isEmpty()) {
                contactsAdapater.getFilter().filter(newText)
//                }
                return false
            }
        })
        binding.swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = true
            loadRegisteredContacts()
        }

        return view
    }


    private fun requestPermission() {
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
        when (requestCode) {
            REQUEST_READ_CONTACTS -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireActivity(), "Permission Denied", Toast.LENGTH_SHORT).show()
                } else {
                    loadRegisteredContacts()
                }
            }
        }
    }

    private fun loadRegisteredContacts() {
        contacts = ArrayList()
        try {
            //CHECK INTERNET CONNECTION
            if (!networkUtils.isNetworkAvailable) {
                // Initialize a new instance of
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("No Connection")
                builder.setMessage("No internet connection")
                builder.setPositiveButton("Retry") { dialog, _ ->
                    dialog.dismiss()
                    loadRegisteredContacts()
                }
                builder.setNegativeButton("Cancel") { dialog, _ ->
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    dialog.dismiss()
                    startActivity(intent)
                    requireActivity().finish()
                }
                val dialog: AlertDialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()
                return
            } else {
                val phones = requireActivity().contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val myContactsList = ArrayList<MultiContactModel>()
                while (phones!!.moveToNext()) {
                    val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val data = MultiContactModel(
                            "",
                            name,
                            phoneNumber,
                            "",
                            "",
                            is_route = false,
                            is_selected = false
                    )
                    if (!myContactsList.contains(data)) {
                        myContactsList.add(data)
                    }
                }

                val gson = Gson()
                val json: String = gson.toJson(myContactsList)
                val token = "Bearer " + prefs.getString(USER_TOKEN, "")

                getRegisteredRouteContacts(requireActivity(), token, json).setCallback { e, result ->
                    swipeRefresh.isRefreshing = false
                    if (e != null) {
                        val snackbar = Snackbar
                                .make(frameLayout, "Unable to load contacts ", Snackbar.LENGTH_INDEFINITE)
                        snackbar.setAction("Try again") {
                            snackbar.dismiss()
                            loadRegisteredContacts()
                        }
                        snackbar.show()
                        return@setCallback
                    }
                    if (result != null) {
                        Timber.e("CHECKPROVIDER %s", result)
                        if (result.getAsJsonObject("data").get("contacts").asJsonArray.size() == 0) {
                            return@setCallback
                        }

                        val gsonn = Gson()
                        val jsonn: String = gsonn.toJson(result.getAsJsonObject("data").get("contacts"))
                        editor.putString(MY_ALL_CONTACTS_NEW, jsonn)
                        editor.apply()
                        if (prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "")!!.compareTo(SEND_MONEY_TO_ROUTE) == 0) {
                            for (item: JsonElement in result.getAsJsonObject("data").get("contacts").asJsonArray) {
                                val issueObj = JSONObject(item.toString())
                                if (issueObj.getBoolean("is_route")) {
                                    contacts.add(MultiContactModel(
                                            issueObj.get("id").toString(),
                                            issueObj.get("username").toString(),
                                            issueObj.get("phone_number").toString(),
                                            issueObj.get("image").toString(),
                                            issueObj.get("amount").toString(),
                                            issueObj.getBoolean("is_route"),
                                            issueObj.getBoolean("is_selected")
                                    ))
                                }

                                val json2: String = gsonn.toJson(contacts)
                                editor.putString(Constants.MY_ROUTE_CONTACTS_NEW, json2)
                                editor.apply()
                            }
                        } else {
                            val type: Type = object : TypeToken<ArrayList<MultiContactModel>>() {}.type
                            contacts = gson.fromJson(jsonn, type)
                            editor.putString(MY_ALL_CONTACTS_NEW, jsonn)
                            editor.apply()
                        }

                        recyclerView.layoutManager = linearLayoutManager
                        recyclerView.setHasFixedSize(true)
                        contactsAdapater = MultiChoiceContactsAdapter(requireActivity(), contacts)
                        recyclerView.adapter = contactsAdapater
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onRefresh() {
        loadRegisteredContacts();
    }
}