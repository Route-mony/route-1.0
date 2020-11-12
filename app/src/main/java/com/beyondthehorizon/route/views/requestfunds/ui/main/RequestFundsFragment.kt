package com.beyondthehorizon.route.views.requestfunds.ui.main

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
import com.beyondthehorizon.route.adapters.ContactsAdapater
import com.beyondthehorizon.route.databinding.FragmentRequestFundsBinding
import com.beyondthehorizon.route.models.Contact
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.CustomProgressBar
import com.beyondthehorizon.route.utils.NetworkUtils
import com.beyondthehorizon.route.utils.Utils
import com.beyondthehorizon.route.views.ConfirmFundRequestActivity
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 */
class RequestFundsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var networkUtils: NetworkUtils
    private lateinit var utils: Utils
    private var contacts: MutableList<Contact> = mutableListOf()
    private var contactMap: MutableMap<String, Contact> = mutableMapOf()
    private var routeContactMap: MutableMap<String, Contact> = mutableMapOf()
    private lateinit var prefs: SharedPreferences
    private lateinit var searchView: SearchView
    private lateinit var parentIntent: Intent
    private lateinit var childIntent: Intent
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var binding: FragmentRequestFundsBinding
    private lateinit var transactionType: String
    private lateinit var contactsAdapater: ContactsAdapater
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var REQUEST_READ_CONTACTS = 79
    private lateinit var token: String
    private lateinit var progressBar: CustomProgressBar
    private lateinit var countryLabel: String;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_request_funds, container, false)
        networkUtils = NetworkUtils(requireContext())
        utils = Utils(requireContext())

        val view = binding.root
        recyclerView = binding.contactRecyclerView
        linearLayoutManager = LinearLayoutManager(activity)
        searchView = binding.contactSearchView
        contacts = mutableListOf()
        contactMap = mutableMapOf()
        contactsAdapater = ContactsAdapater(requireActivity(), contacts)
        progressBar = CustomProgressBar(requireContext())
        countryLabel = utils.countrySymbol

        prefs = requireActivity().getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        editor = prefs.edit()
        parentIntent = requireActivity().intent
        childIntent = Intent(requireActivity(), ConfirmFundRequestActivity::class.java)

        token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")

        transactionType = prefs.getString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, "").toString()

        try {
            if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                recyclerView.layoutManager = linearLayoutManager
                recyclerView.setHasFixedSize(true)
                contactsAdapater = ContactsAdapater(requireActivity(), contacts)
                recyclerView.adapter = contactsAdapater
                loadRouteContacts()
            } else {
                requestPermission();
            }
            progressBar.dialog.dismiss()
        } catch (e: Exception) {
            Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                var pattern = newText.removePrefix("+").toLowerCase().toRegex()
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
                val phoneNumber = Utils.getFormattedPhoneNumber(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)), countryLabel)
                if (phoneNumber.isNotEmpty()) {
                    contactMap[phoneNumber] = Contact(phoneNumber.hashCode().toString(), name, phoneNumber)
                }
            }
        } catch (e: Exception) {
            Timber.d(e.message.toString())
        }
    }

    private fun loadRouteContacts() {
        loadPhoneContacts()
        if (networkUtils.isNetworkAvailable) {
            try {
                progressBar.show("Loading contact...")
                binding.swipeRefresh.isRefreshing = true
                val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
                Constants.loadUserContacts(requireActivity(), token).setCallback { e, result ->
                    binding.swipeRefresh.isRefreshing = false
                    progressBar.dialog.dismiss()
                    if (result != null && result.has("rows")) {
                        if (prefs.getString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "") == Constants.SEND_MONEY_TO_ROUTE
                                || prefs.getString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "") == Constants.REQUEST_MONEY) {
                            val users = result.getAsJsonArray("rows")
                            contacts.clear()
                            users.forEach { contact ->
                                val user = contact.asJsonObject
                                val phone = Utils.getFormattedPhoneNumber(user.get("phone_number").asString, countryLabel)
                                if (contactMap[phone] != null) {
                                    contactMap[phone]!!.name = String.format("%s %s", user.get("first_name").asString, user.get("last_name").asString)
                                    contactMap[phone]!!.contact = user.get("phone_number").asString
                                    contactMap[phone]!!.avatar = user.get("image").asString
                                    contactMap[phone]!!.accountNumber = user.get("wallet_account").asJsonObject.get("wallet_account").asString
                                    contacts.add(contactMap[phone]!!)
                                }
                            }
                        } else {
                            contacts = contactMap.values.toMutableList()
                        }
                        contactsAdapater.updateContacts(contacts)
                    } else if (result.has("errors")) {
                        Toast.makeText(requireContext(), result["errors"].asJsonArray[0].asString, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                    progressBar.dialog.dismiss()
                }
            } catch (e: Exception) {
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
            }
        } else {
            internetDialog()
        }
    }

    private fun internetDialog() {
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

    override fun onRefresh() {
        loadRouteContacts();
    }

    companion object {
        private val TAG = this.javaClass.simpleName
    }
}