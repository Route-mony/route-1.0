package com.beyondthehorizon.route.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.adapters.ContactsAdapater
import com.beyondthehorizon.route.databinding.ActivityRequestFundsBinding
import com.beyondthehorizon.route.models.Contact
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.CustomProgressBar
import com.beyondthehorizon.route.utils.NetworkUtils
import com.beyondthehorizon.route.utils.Utils
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.nav_bar_layout.*
import timber.log.Timber


class RequestFundsActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var networkUtils: NetworkUtils
    private lateinit var utils: Utils
    private var contacts: MutableList<Contact> = mutableListOf()
    private var contactMap: MutableMap<String, Contact> = mutableMapOf()
    private lateinit var prefs: SharedPreferences
    private lateinit var searchView: SearchView
    private lateinit var binding: ActivityRequestFundsBinding
    private lateinit var contactsAdapater: ContactsAdapater
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var dummyId = 0
    private var REQUEST_READ_CONTACTS = 79
    private lateinit var context: Context
    private lateinit var progressBar: CustomProgressBar
    private lateinit var countryLabel: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_funds)
        progressBar = CustomProgressBar(this)
        countryLabel = utils.countrySymbol
        networkUtils = NetworkUtils(this)
        utils = Utils(this)

        btn_home.setOnClickListener {
            val intent = Intent(this@RequestFundsActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@RequestFundsActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@RequestFundsActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_settings.setOnClickListener {
            val intent = Intent(this@RequestFundsActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        recyclerView = binding.contactRecyclerView
        linearLayoutManager = LinearLayoutManager(this)
        searchView = binding.contactSearchView
        contacts = mutableListOf()
        contactMap = mutableMapOf()
        contactsAdapater = ContactsAdapater(this, contacts)
        context = applicationContext

        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)

        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                loadRouteContacts()
            } else {
                requestPermission();
            }

            binding.arrowBack.setOnClickListener {
                onBackPressed()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                var pattern = newText.toLowerCase().toRegex()
                try {
                    var filteredContacts = contacts.filter { pattern.containsMatchIn(it.contact) || pattern.containsMatchIn(it.name.toLowerCase()) }
                    var adapter = ContactsAdapater(this@RequestFundsActivity, filteredContacts.toMutableList())
                    recyclerView.layoutManager = linearLayoutManager
                    recyclerView.setHasFixedSize(true)
                    contactsAdapater = ContactsAdapater(this@RequestFundsActivity, filteredContacts.toMutableList())
                    recyclerView.adapter = adapter
                } catch (ex: Exception) {
                    Toast.makeText(this@RequestFundsActivity, ex.message, Toast.LENGTH_LONG).show()
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CONTACTS),
                    REQUEST_READ_CONTACTS)
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CONTACTS),
                    REQUEST_READ_CONTACTS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_READ_CONTACTS -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()

                } else {
                    loadRouteContacts()
                }
            }
        }
    }

    private fun loadPhoneContacts() {
        try {
            val phones = this.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
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
                Constants.loadUserContacts(this, token).setCallback { e, result ->
                    binding.swipeRefresh.isRefreshing = false
                    progressBar.dialog.dismiss()
                    if (result != null && result.has("rows")) {
                        if (prefs.getString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "") == Constants.SEND_MONEY_TO_ROUTE
                                || prefs.getString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "") == Constants.REQUEST_MONEY) {
                            val users = result.getAsJsonArray("rows")
                            users.forEach { contact ->
                                val user = contact.asJsonObject
                                val phone = Utils.getFormattedPhoneNumber(user.get("phone_number").asString, countryLabel)
                                if (contactMap[phone] != null) {
                                    contactMap[phone]!!.name = String.format("%s %s", user.get("first_name"), user.get("last_name"))
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
                        Toast.makeText(this, result["errors"].asJsonArray[0].asString, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                    }
                    progressBar.dialog.dismiss()
                }
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        } else {
            internetDialog()
        }
    }

    fun prevPage() {
        super.onBackPressed()
    }

    private fun internetDialog() {
        // Initialize a new instance of
        val builder = AlertDialog.Builder(this)
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
        loadRouteContacts()
    }
}
