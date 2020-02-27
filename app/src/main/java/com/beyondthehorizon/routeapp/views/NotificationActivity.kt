package com.beyondthehorizon.routeapp.views

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.adapters.NotificationsAdapter
import com.beyondthehorizon.routeapp.databinding.ActivityNotificationsBinding
import com.beyondthehorizon.routeapp.models.Notification
import com.beyondthehorizon.routeapp.utils.Constants
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.Exception

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationsBinding
    private var notifications: MutableList<Notification> = mutableListOf()
    private lateinit var prefs: SharedPreferences
    private lateinit var notificationsAdapter: NotificationsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var context: Context
    private lateinit var filteredNotifications: MutableList<Notification>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notifications)
        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        recyclerView = binding.notificationsRecyclerView
        linearLayoutManager = LinearLayoutManager(this)
        context = applicationContext

        binding.btnReceivedNotifications.setOnClickListener {
            binding.btnReceivedNotifications.setBackgroundResource(R.drawable.button_border)
            binding.btnSentNotifications.setBackgroundResource(0)

            try {
                filterRequests("received")
            } catch (e: Exception) {
                Log.d("TAG", e.message)
            }
        }

        binding.btnSentNotifications.setOnClickListener {
            binding.btnSentNotifications.setBackgroundResource(R.drawable.button_border)
            binding.btnReceivedNotifications.setBackgroundResource(0)

            try {
                filterRequests("sent")
            } catch (e: Exception) {
                Log.d("TAG", e.message)
            }
        }

        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }
        if(loadSentRequests() && loadReceivedRequests()) {
            filterRequests("sent")
        }
    }

    /**
     * Get requests
     */

    private fun getRequests(result: JsonObject, type: String) {
        var statusMapper = mapOf(
                "ok" to R.drawable.ic_approved,
                "pending" to R.drawable.ic_pending,
                "cancelled" to R.drawable.ic_rejected
        )
        var userType = ""
        when(type){
            "received" -> userType = "requester"
            "sent" -> userType = "recipient"
        }
        try {
            var requests = result.get("data").asJsonObject.get("rows").asJsonArray
            for (item: JsonElement in requests) {
                var id = item.asJsonObject.get("id").asString
                var username = item.asJsonObject.get(userType).asJsonObject.get("first_name").asString + " " +
                        item.asJsonObject.get(userType).asJsonObject.get("last_name").asString
                var phone = item.asJsonObject.get(userType).asJsonObject.get("phone_number").asString
                var imageUrl = R.drawable.group416
                var reason = item.asJsonObject.get("reason").asString
                var amount = item.asJsonObject.get("amount").asString
                var status = item.asJsonObject.get("status").asString.toLowerCase()
                var statusIcon = statusMapper[status]
                notifications.add(Notification(id, username, phone, imageUrl, reason, amount, status, statusIcon!!, type))
            }
        } catch (ex: Exception) {
            Log.d("TAG", ex.message)
        }
    }

    private fun loadSentRequests():Boolean {
        try {
            val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
            Constants.getFundRequests(this, "sent", token).setCallback { e, result ->
                if (result != null) {
                    getRequests(result, "sent")
                }
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message)
        }
        return true
    }

    private fun loadReceivedRequests():Boolean {
        try {
            val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
            Constants.getFundRequests(this, "received", token).setCallback { e, result ->
                if (result != null) {
                    getRequests(result, "received")
                }
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message)
        }
        return  true
    }

    private fun filterRequests(keyword: String){
        recyclerView.layoutManager = linearLayoutManager
        filteredNotifications = notifications.filter { it.type == keyword }.toMutableList()
        recyclerView.setHasFixedSize(true)
        notificationsAdapter = NotificationsAdapter(this, filteredNotifications)
        recyclerView.adapter = notificationsAdapter
    }
}
