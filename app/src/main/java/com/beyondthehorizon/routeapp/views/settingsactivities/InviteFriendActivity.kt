package com.beyondthehorizon.routeapp.views.settingsactivities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beyondthehorizon.routeapp.R
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.routeapp.adapters.InviteFriendAdapter
import com.beyondthehorizon.routeapp.models.InviteFriend
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.views.MainActivity
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.activity_invite_friend.*
import kotlinx.android.synthetic.main.activity_invite_friend.back
import kotlinx.android.synthetic.main.nav_bar_layout.*
import java.lang.Exception


class InviteFriendActivity : AppCompatActivity() {

    lateinit var inviteFriendAdapter: InviteFriendAdapter
    private lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_friend)

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

        back.setOnClickListener {
            onBackPressed()
        }

        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)

        inviteFriendAdapter = InviteFriendAdapter(this@InviteFriendActivity)
        loadRouteContacts()

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

    private fun loadRouteContacts() {
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

//        Log.e("AllergiesFragment", result.toString())
        val list = ArrayList<InviteFriend>()
        inviteFriendAdapter.clearList()

        // Hash Maps
        val namePhoneMap = HashMap<String, String>()
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
        if (result != null) {
            for (item: JsonElement in result) {
                val phone = item.asJsonObject.get("phone_number").asString
                if (namePhoneMap.keys.contains(phone.substring(phone.length - 9))) {
                    namePhoneMap.remove(phone.substring(phone.length - 9))
                }
            }
            // Get The Contents of Hash Map in Log
            for (entry in namePhoneMap.entries) {
                val keyPhone = entry.key

                val valueUserName = entry.value
                Log.d("FragmentActivity.TAG", "Name :$valueUserName")

                list.add(InviteFriend(valueUserName, "0$keyPhone"))

                inviteFriendsRecycler.apply {
                    layoutManager = LinearLayoutManager(this@InviteFriendActivity)
                    adapter = inviteFriendAdapter
                }
                inviteFriendAdapter.setContact(list)
            }
        } else {
            Log.d("ContactResponse", "No contacts registered on route")
        }

    }
}
