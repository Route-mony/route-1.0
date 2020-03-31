package com.beyondthehorizon.routeapp.views.settingsactivities

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beyondthehorizon.routeapp.R
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.routeapp.adapters.InviteFriendAdapter
import com.beyondthehorizon.routeapp.models.InviteFriend
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.views.MainActivity
import com.beyondthehorizon.routeapp.views.transactions.main.TransactionsActivity
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.activity_invite_friend.*
import kotlinx.android.synthetic.main.activity_invite_friend.back
import kotlinx.android.synthetic.main.nav_bar_layout.*
import java.lang.Exception


class InviteFriendActivity : AppCompatActivity() {

    lateinit var inviteFriendAdapter: InviteFriendAdapter
    private lateinit var prefs: SharedPreferences
    private lateinit var progressBar: ProgressDialog
    private var REQUEST_READ_CONTACTS = 79

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_friend)

        val requestType = intent.getStringExtra("TYPE")
        if (requestType.contains("SHARE")) {
            header2.visibility = View.GONE
            inner_txt.visibility = View.GONE
            txt_title.text = "Share Receipt"
        }
        progressBar = ProgressDialog(this@InviteFriendActivity)
        progressBar.setMessage("Please wait...")
        progressBar.setCanceledOnTouchOutside(false)
        btn_home.setOnClickListener {
            val intent = Intent(this@InviteFriendActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btn_settings.setOnClickListener {
            val intent = Intent(this@InviteFriendActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@InviteFriendActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        back.setOnClickListener {
            onBackPressed()
        }

        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)

        inviteFriendAdapter = InviteFriendAdapter(this@InviteFriendActivity, requestType)

        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                loadRouteContacts()
            } else {
                requestPermission()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.toLowerCase().trim { it <= ' ' }.isNotEmpty()) {
                    inviteFriendAdapter.filter.filter(newText)
                } else {
                    inviteFriendAdapter.filter.filter(newText)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return true
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

    private fun loadRouteContacts() {
        progressBar.show()
        try {
            val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
            Constants.loadUserContacts(this, token).setCallback { e, result ->
                if (result != null) {
                    mapContactsToList(result.getAsJsonArray("rows"))
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun mapContactsToList(result: JsonArray) {

        val list = ArrayList<InviteFriend>()
        inviteFriendAdapter.clearList()

        // Hash Maps
        val namePhoneMap = HashMap<String, String>()
        val sharePhoneMap = HashMap<String, String>()
        val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null)

        // Loop Through All The Numbers
        while (phones!!.moveToNext()) {

            val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            var phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            // Cleanup the phone number
            phoneNumber = phoneNumber.replace("[()\\s-]+".toRegex(), "")

            if (phoneNumber.length > 9) {
                namePhoneMap[phoneNumber.substring(phoneNumber.length - 9)] = name
            }

        }

        phones.close()

        var idList = ArrayList<String>()
        val requestType = intent.getStringExtra("TYPE")
        if (result != null) {
            for (item: JsonElement in result) {
                val phone = item.asJsonObject.get("phone_number").asString
                val id = item.asJsonObject.get("id").asString


                if (requestType.contains("Invite")) {
                    if (namePhoneMap.keys.contains(phone.substring(phone.length - 9))) {
                        namePhoneMap.remove(phone.substring(phone.length - 9))
                    }
                } else {
                    if (namePhoneMap.keys.contains(phone.substring(phone.length - 9))) {

                        Log.e("FragmentActivity.TAG", "Name :")
                        idList.add(id)
//                        sharePhoneMap[phone.substring(phone.length - 9)] = namePhoneMap.get(phone.substring(phone.length - 9))

                        sharePhoneMap[phone.substring(phone.length - 9)] = namePhoneMap[phone.substring(phone.length - 9)].toString()
                    }
                }

            }

            // Get The Contents of Hash Map in Log

            if (requestType.contains("Invite")) {
                for (entry in namePhoneMap.entries) {
                    val keyPhone = entry.key
//                val idPhone = entry.

                    val valueUserName = entry.value

                    list.add(InviteFriend(valueUserName, "0$keyPhone", ""))
                    inviteFriendsRecycler.apply {
                        layoutManager = LinearLayoutManager(this@InviteFriendActivity)
                        adapter = inviteFriendAdapter
                    }
                    inviteFriendAdapter.setContact(list)
                }
            } else {
                var i = 0
                for (entry in sharePhoneMap.entries) {
                    val keyPhone = entry.key
                    val valueUserName = entry.value

                    list.add(InviteFriend(valueUserName, "0$keyPhone", idList[i]))
                    i++

                    inviteFriendsRecycler.apply {
                        layoutManager = LinearLayoutManager(this@InviteFriendActivity)
                        adapter = inviteFriendAdapter
                    }
                    inviteFriendAdapter.setContact(list)
                }

            }
            progressBar.dismiss()
        } else {
            Log.d("ContactResponse", "No contacts registered on route")
        }

    }
}
