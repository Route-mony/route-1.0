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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notifications)
        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        recyclerView = binding.notificationsRecyclerView
        linearLayoutManager = LinearLayoutManager(this)
        context = applicationContext

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        notificationsAdapter = NotificationsAdapter(this, notifications)
        recyclerView.adapter = notificationsAdapter

        binding.btnRequestsNotifications.setOnClickListener{
            binding.btnRequestsNotifications.setBackgroundResource(R.drawable.button_border)
            binding.btnAllNotifications.setBackgroundResource(0)

            try {
                val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
                Constants.getFundRequests(this, "received", token).setCallback { e, result ->
                    if(result != null) {
                        getRequests(result)
                    }
                    else{
                        Toast.makeText(this, "You have no requests history", Toast.LENGTH_LONG).show()
                    }
                }
            }
            catch (e: Exception){
                Log.d("TAG", e.message)
            }
        }

        binding.btnAllNotifications.setOnClickListener{
            binding.btnAllNotifications.setBackgroundResource(R.drawable.button_border)
            binding.btnRequestsNotifications.setBackgroundResource(0)
            notificationsAdapter = NotificationsAdapter(this, notifications)
            recyclerView.adapter = notificationsAdapter
        }

        binding.arrowBack.setOnClickListener{
            onBackPressed()
        }
    }

    /**
     * Get requests
     */

    private fun getRequests(result: JsonObject){
        var statusMapper = mapOf(
                "OK" to R.drawable.ic_approved,
                "PENDING" to R.drawable.ic_pending,
                "CANCELLED" to R.drawable.ic_rejected
        )

        try {
            var requests = result.get("data").asJsonObject.get("rows").asJsonArray
            for (item: JsonElement in requests) {
                var id = item.asJsonObject.get("id").asString
                var username = item.asJsonObject.get("requester").asJsonObject.get("first_name").asString + " " +
                        item.asJsonObject.get("requester").asJsonObject.get("last_name").asString
                var phone =  item.asJsonObject.get("requester").asJsonObject.get("phone_number").asString
                var imageUrl = R.drawable.group416
                var reason = item.asJsonObject.get("reason").asString
                var amount = item.asJsonObject.get("amount").asString
                var status = item.asJsonObject.get("status").asString.toUpperCase()
                var statusIcon = statusMapper[status]
                notifications.add(Notification(id, username, phone, imageUrl, reason, amount, status, statusIcon!!))
            }

            notificationsAdapter = NotificationsAdapter(this, notifications)
            recyclerView.adapter = notificationsAdapter
        }
        catch (ex: Exception){
            Log.d("TAG", ex.message)
        }
    }
}
