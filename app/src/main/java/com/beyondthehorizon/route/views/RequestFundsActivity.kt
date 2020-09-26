package com.beyondthehorizon.route.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.adapters.ContactsAdapater
import com.beyondthehorizon.route.databinding.ActivityRequestFundsBinding
import com.beyondthehorizon.route.models.Contact
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.nav_bar_layout.*
import java.lang.Exception


class RequestFundsActivity : AppCompatActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_funds)

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
        contactMap  = mutableMapOf()
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
            val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            while (phones!!.moveToNext()) {
                val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                var cleanedPhoneNumber = phoneNumber.replace("-", "").replace(" ", "").replaceBefore("7", "0")
                var id = phoneNumber.hashCode().toString()
                contactMap.put(cleanedPhoneNumber, Contact(id, name, phoneNumber))
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun loadRouteContacts() {
        try {
            val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
            Constants.loadUserContacts(this, token).setCallback { e, result ->
                if(result != null) {
                    mapContactsToList(result.getAsJsonArray("rows"))
                }
            }
        }
        catch (e: Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun mapContactsToList(result: JsonArray) {
        loadPhoneContacts()

        if (result != null) {
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
        } else {
            Log.d("ContactResponse", "No contacts registered on route")
        }

        contacts = contactMap.values.toMutableList()
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        contactsAdapater = ContactsAdapater(this@RequestFundsActivity, contacts)
        recyclerView.adapter = contactsAdapater
    }

    fun prevPage() {
        super.onBackPressed()
    }
}
